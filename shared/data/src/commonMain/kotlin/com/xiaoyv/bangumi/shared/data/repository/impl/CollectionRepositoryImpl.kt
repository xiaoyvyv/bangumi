package com.xiaoyv.bangumi.shared.data.repository.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.xiaoyv.bangumi.shared.core.types.CollectionEpisodeType
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.EpisodeType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.data.api.client.BgmApiClient
import com.xiaoyv.bangumi.shared.data.manager.app.PreferenceStore
import com.xiaoyv.bangumi.shared.data.model.request.CollectionEpisodeUpdate
import com.xiaoyv.bangumi.shared.data.model.request.CollectionSubjectUpdate
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisode
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.repository.CollectionRepository
import com.xiaoyv.bangumi.shared.data.repository.SubjectRepository
import com.xiaoyv.bangumi.shared.data.repository.datasource.createNetworkOffsetLimitPagingPager
import com.xiaoyv.bangumi.shared.data.repository.datasource.createPagingConfig
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class CollectionRepositoryImpl(
    private val client: BgmApiClient,
    private val pagingConfig: PagingConfig,
    private val subjectRepository: SubjectRepository,
    private val preferenceStore: PreferenceStore,
) : CollectionRepository {

    override fun fetchMyCollectionSubjectPager(
        @SubjectType subjectType: Int,
        @CollectionType type: Int,
        fetchEpisode: Boolean,
    ): Pager<Int, ComposeSubject> {
        return createNetworkOffsetLimitPagingPager(
            pagingConfig = createPagingConfig(10),
            keySelector = { it.id },
            onLoadData = {
                fetchMyCollectionSubject(
                    subjectType = subjectType,
                    type = type,
                    fetchEpisode = fetchEpisode,
                    offset = it,
                    limit = 10,
                ).getOrThrow().result
            }
        )
    }

    override suspend fun submitBookmarkOrCancelMono(
        monoId: Long,
        @MonoType type: Int,
        bookmarked: Boolean,
    ): Result<Boolean> = client.requestNextCollectionApi {
        if (type == MonoType.PERSON) {
            if (bookmarked) addPersonCollection(monoId)
            // TODO 删除人物收藏 API 不可用，WEB 请求代替
            else client.bgmWebApiNoRedirect.submitCollectionPersonRemove(monoId, preferenceStore.userInfo.formHash)
        } else {
            if (bookmarked) addCharacterCollection(monoId)
            // TODO 删除角色收藏 API 不可用，WEB 请求代替
            else client.bgmWebApiNoRedirect.submitCollectionCharacterRemove(monoId, preferenceStore.userInfo.formHash)
        }
        bookmarked
    }

    override suspend fun submitUpdateUserEpisode(
        subjectId: Long,
        episodes: List<ComposeEpisode>,
        @CollectionEpisodeType type: Int,
    ): Result<Unit> {
        /* private api 有速率限制，暂时不用
        return client.requestNextCollectionApi {
            if (episodes.size > 1) {
                updateEpisodeProgress(episodes.last().id, UpdateEpisodeProgress(batch = true))
            } else {
                updateEpisodeProgress(episodes.first().id, UpdateEpisodeProgress(type = type))
            }
        }*/
        return client.requestJsonApi {
            // API 不支持直接更新已经存在的状态，先全部移除，再更新状态
            val episodeIds = episodes.map { it.id }
            if (type != CollectionEpisodeType.UNKNOWN) {
                // 如果批量处理的数据，有任何EP已经有收藏状态了，先清理掉，没有收藏状态则没必要直接API请求
                val needReset = episodes.any { it.collection.status != CollectionEpisodeType.UNKNOWN }
                if (needReset) {
                    submitUpdateUserEpisodeBatch(subjectId, body = CollectionEpisodeUpdate(episodeIds, CollectionEpisodeType.UNKNOWN))
                }
            }
            if (episodeIds.size == 1) {
                submitUpdateUserEpisode(episodeIds.first(), body = CollectionEpisodeUpdate(type = type))
            } else {
                submitUpdateUserEpisodeBatch(subjectId, body = CollectionEpisodeUpdate(episodeIds, type))
            }
        }
    }

    override suspend fun submitUpdateUserSubject(
        subjectId: Long,
        update: CollectionSubjectUpdate,
    ): Result<Unit> = client.requestJsonApi {
        submitUserSubjectCollection(subjectId, body = update)
    }

    override suspend fun submitRemoveSubjectCollection(subjectId: Long): Result<Unit> = client.requestWebApi(disableRedirect = true) {
        submitCollectionSubjectRemove(subjectId, formHash = preferenceStore.userInfo.formHash)
    }

    suspend fun fetchMyCollectionSubject(
        @SubjectType subjectType: Int,
        @CollectionType type: Int,
        offset: Int,
        limit: Int,
        fetchEpisode: Boolean,
    ): Result<ComposePage<ComposeSubject>> = client.requestJsonApi {
        val page = client.nextCollectionApi.getMySubjectCollections(
            subjectType = subjectType.takeIf { value -> value != SubjectType.UNKNOWN },
            type = type.takeIf { value -> value != CollectionType.UNKNOWN },
            offset = offset,
            limit = limit,
        )
        if (!fetchEpisode) return@requestJsonApi page
        if (subjectType != SubjectType.ANIME && subjectType != SubjectType.REAL) return@requestJsonApi page

        // 动画和三次元，请求章节数据，这里收藏 Api 未直接提供详细格子数据，直接并发暴力获取来渲染格子（Sai 不会骂我吧，狗头保命）
        val results = coroutineScope {
            val tasks = page.result
                .map {
                    async {
                        it.id to subjectRepository.fetchSubjectEpisodes(
                            subjectId = it.id,
                            type = EpisodeType.TYPE_MAIN,
                            offset = it.interest.epStatus.let { ep ->
                                // 这里进度列表填充的格子，总共不超过24是直接拿全部，负责以当前进度往前偏移3格
                                if (it.eps in 1..24) 0 else (ep - 3).coerceAtLeast(0)
                            },
                            limit = 40
                        ).getOrNull().orEmpty()
                    }
                }
                .toTypedArray()
            awaitAll(*tasks).associate { it }
        }
        page.copy(
            result = page.result.map { collection ->
                collection.copy(episodes = results[collection.id].orEmpty().toImmutableList())
            }
        )
    }
}