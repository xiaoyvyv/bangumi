package com.xiaoyv.bangumi.shared.data.repository.impl

import androidx.paging.PagingConfig
import com.xiaoyv.bangumi.shared.core.types.IndexCatWebTabType
import com.xiaoyv.bangumi.shared.core.types.list.ListIndexType
import com.xiaoyv.bangumi.shared.core.utils.toApiOffset
import com.xiaoyv.bangumi.shared.data.api.client.ApiClient
import com.xiaoyv.bangumi.shared.data.manager.app.PreferenceStore
import com.xiaoyv.bangumi.shared.data.model.request.bgm.CreateCommentParam
import com.xiaoyv.bangumi.shared.data.model.request.bgm.IndexCreateParam
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexParam
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexRelatedParam
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeId
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndex
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndexFocus
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndexRelated
import com.xiaoyv.bangumi.shared.data.model.response.bgm.loadAllData
import com.xiaoyv.bangumi.shared.data.model.response.bgm.normalizedReplies
import com.xiaoyv.bangumi.shared.data.parser.bgm.IndexParser
import com.xiaoyv.bangumi.shared.data.repository.IndexRepository
import com.xiaoyv.bangumi.shared.data.repository.datasource.MemoryPagingController
import com.xiaoyv.bangumi.shared.data.repository.datasource.createMemoryOffsetLimitPagingController
import com.xiaoyv.bangumi.shared.data.repository.datasource.createMemoryPageLimitPagingController
import com.xiaoyv.bangumi.shared.data.repository.datasource.createPagingConfig

class IndexRepositoryImpl(
    private val client: ApiClient,
    private val pagingConfig: PagingConfig,
    private val preferenceStore: PreferenceStore,
    private val indexParser: IndexParser,
) : IndexRepository {

    override fun fetchIndexPager(param: ListIndexParam): MemoryPagingController<ComposeIndex, Long> {
        return createMemoryPageLimitPagingController(
            pagingConfig = createPagingConfig(20),
            onlyOnePage = true,
            idSelector = { it.id },
            onLoadData = { page ->
                with(indexParser) {
                    when (param.type) {
                        ListIndexType.USER_CREATE -> client.requestNextUserApi {
                            getUserIndexes(
                                username = param.username,
                                offset = page.toApiOffset(pagingConfig.pageSize)
                            ).result
                        }.getOrThrow()

                        ListIndexType.USER_COLLECTION -> client.requestNextUserApi {
                            getUserIndexCollections(
                                username = param.username,
                                offset = page.toApiOffset(pagingConfig.pageSize)
                            ).result
                        }.getOrThrow()

                        ListIndexType.SUBJECT_RELATED -> client.requestNextSubjectApi {
                            getSubjectIndexes(
                                subjectID = param.related.subjectId,
                                offset = page.toApiOffset(pagingConfig.pageSize)
                            ).result
                        }.getOrThrow()

                        ListIndexType.PERSON_RELATED -> client.requestNextPersonApi {
                            getPersonIndexes(
                                personID = param.related.monoId,
                                offset = page.toApiOffset(pagingConfig.pageSize)
                            ).result
                        }.getOrThrow()

                        ListIndexType.CHARACTER_RELATED -> client.requestNextCharacterApi {
                            getCharacterIndexes(
                                characterID = param.related.monoId,
                                offset = page.toApiOffset(pagingConfig.pageSize)
                            ).result
                        }.getOrThrow()

                        ListIndexType.BROWSER -> client.requestNextIndexApi {
                            getIndexes(
                                type = param.browserType,
                                order = param.browserOrder,
                                offset = page.toApiOffset(pagingConfig.pageSize)
                            ).result
                        }.getOrThrow()

                        ListIndexType.SEARCH -> {
                            client.appApi.fetchSearchIndex(
                                keyword = param.search.keyword,
                                exact = param.search.exact,
                                page = page,
                                size = pagingConfig.pageSize
                            ).data.records.map { it.toComposeIndex() }
                        }

                        else -> error("暂不支持该类型")
                    }
                }
            }
        )
    }

    override fun fetchIndexRelatePager(param: ListIndexRelatedParam): MemoryPagingController<ComposeIndexRelated, Long> {
        return createMemoryOffsetLimitPagingController(
            pagingConfig = pagingConfig,
            idSelector = { it.id },
            onLoadData = {
                client.nextIndexApi.getIndexRelated(
                    indexID = param.indexId,
                    cat = param.cat,
                    type = param.subjectType,
                    limit = pagingConfig.pageSize,
                    offset = it,
                ).result
            }
        )
    }

    override suspend fun fetchUserCreatedIndex(username: String): Result<List<ComposeIndex>> = client.requestNextUserApi {
        loadAllData(100) { offset, limit ->
            getUserIndexes(username = username, limit = limit, offset = offset)
        }
    }

    override suspend fun fetchIndexDetail(indexId: Long): Result<ComposeIndex> = client.requestNextIndexApi {
        getIndex(indexId)
    }

    override suspend fun fetchIndexFocus(): Result<List<ComposeIndexFocus>> = client.requestWebApi {
        with(indexParser) {
            fetchIndexHomepage()
                .fetchIndexFocusConverted()
        }
    }

    override suspend fun fetchIndexComments(indexId: Long): Result<List<ComposeReply>> = client.requestNextIndexApi {
        getIndexComments(indexId).normalizedReplies()
    }


    override suspend fun fetchIndexIsBookmarked(indexId: Long): Result<Boolean> = client.requestWebApi {
        // 用 EP 类型查询网页详情页数据，判断是否收藏，一般这个比较条目少，快
        fetchIndexDetail(indexId, IndexCatWebTabType.EP).select("a.btnBlue").isNotEmpty()
    }

    override suspend fun submitBookmarkOrCancelIndex(indexId: Long, bookmarked: Boolean): Result<Boolean> = client.requestNextCollectionApi {
        if (bookmarked) {
            addIndexCollection(indexId)
        } else {
            // TODO API有问题，暂时用WEB代替
            // deleteIndexCollection(indexId)
            client.bgmWebApiNoRedirect.submitCollectionIndexRemove(indexId, preferenceStore.userInfo.formHash)
        }
        bookmarked
    }

    override suspend fun submitIndexAddRelated(indexId: Long, param: IndexCreateParam): Result<Unit> = client.requestNextIndexApi {
        putIndexRelated(indexID = indexId, param = param)
    }

    override suspend fun submitIndexComment(
        indexId: Long,
        content: String,
        turnstile: String,
        replyTo: Long?
    ): Result<ComposeId> = client.requestNextIndexApi {
        createIndexComment(
            indexID = indexId,
            param = CreateCommentParam(
                content = content,
                turnstileToken = turnstile,
                replyTo = replyTo ?: 0
            )
        )
    }
}
