package com.xiaoyv.bangumi.shared.data.repository.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.xiaoyv.bangumi.shared.core.types.MonoCastType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.types.list.ListMonoType
import com.xiaoyv.bangumi.shared.core.utils.fetchAllPages
import com.xiaoyv.bangumi.shared.core.utils.toApiPage
import com.xiaoyv.bangumi.shared.data.api.client.BgmApiClient
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.ListMonoParam
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.ListPersonCastParam
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeSection
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMono
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoWebInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePersonPosition
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectDisplay
import com.xiaoyv.bangumi.shared.data.parser.bgm.MonoParser
import com.xiaoyv.bangumi.shared.data.repository.MonoRepository
import com.xiaoyv.bangumi.shared.data.repository.datasource.createNetworkOffsetLimitPagingPager

/**
 * [MonoRepositoryImpl]
 *
 * @since 2025/5/18
 */
class MonoRepositoryImpl(
    private val client: BgmApiClient,
    private val pagingConfig: PagingConfig,
    private val monoParser: MonoParser,
) : MonoRepository {

    override fun fetchMonoListPager(param: ListMonoParam): Pager<Int, ComposeMonoDisplay> {
        return createNetworkOffsetLimitPagingPager(
            pagingConfig = pagingConfig,
            keySelector = { it.key },
            onLoadData = {
                fetchMonoListByType(param, it, pagingConfig.pageSize).getOrThrow()
            }
        )
    }

    override suspend fun fetchPersonWorkPosition(monoId: Long): Result<List<ComposePersonPosition>> = client.requestWebApi {
        with(monoParser) {
            fetchPersonWorkPosition(monoId)
                .fetchPersonWorkPositionConverted()
        }
    }

    override fun fetchPersonCastPager(param: ListPersonCastParam): Pager<Int, ComposeMonoInfo> {
        return createNetworkOffsetLimitPagingPager(
            pagingConfig = pagingConfig,
            keySelector = { it.mono.id },
            onLoadData = {
                client.nextPersonApi.getPersonCasts(
                    personID = param.personId,
                    subjectType = param.subjectType.takeIf { type -> type != SubjectType.UNKNOWN },
                    type = param.voiceType.takeIf { type -> type != MonoCastType.UNKNOWN },
                    offset = it,
                    limit = pagingConfig.pageSize
                ).result
            }
        )
    }

    override suspend fun fetchPersonWorks(
        monoId: Long,
        limit: Int,
    ): Result<List<ComposeSubjectDisplay>> = client.requestNextPersonApi {
        getPersonWorks(monoId, limit = limit).result
    }

    override suspend fun fetchCharacterCasts(monoId: Long): Result<List<ComposeMonoInfo>> = client.requestNextCharacterApi {
        fetchAllPages(100) { offset, limit ->
            getCharacterCasts(
                characterID = monoId,
                offset = offset,
                limit = limit
            ).result
        }
    }

    override suspend fun fetchPersonCast(
        monoId: Long,
        limit: Int,
    ): Result<List<ComposeMonoInfo>> = client.requestNextPersonApi {
        getPersonCasts(personID = monoId, limit = limit).result
    }


    override suspend fun fetchMonoListByType(
        param: ListMonoParam,
        offset: Int,
        limit: Int,
    ): Result<List<ComposeMonoDisplay>> {
        return when (param.type) {
            ListMonoType.SUBJECT_CHARACTER -> client.requestNextSubjectApi {
                getSubjectCharacters(
                    subjectID = param.subject.subjectId,
                    type = param.subject.monoVoiceType,
                    limit = limit,
                    offset = offset
                ).result
                    .asSequence()
                    .sortedByDescending { it.mono.comment }
                    .sortedByDescending { it.order }
                    .map { ComposeMonoDisplay(type = MonoType.CHARACTER, info = it) }
                    .toList()
            }

            ListMonoType.SUBJECT_PERSON -> client.requestNextSubjectApi {
                getSubjectStaffPersons(
                    subjectID = param.subject.subjectId,
                    position = param.subject.personPosition,
                    limit = limit,
                    offset = offset
                ).result
                    .asSequence()
                    .sortedByDescending { it.mono.comment }
                    .sortedByDescending { it.order }
                    .map { ComposeMonoDisplay(type = MonoType.PERSON, info = it) }
                    .toList()
            }

            // 人物浏览
            ListMonoType.BROWSER -> client.requestWebApi {
                with(monoParser) {
                    if (param.browser.monoType == MonoType.CHARACTER) {
                        fetchBrowserMonoCharacter(
                            page = offset.toApiPage(limit),
                            type = param.browser.mutexParam.type,
                            bloodtype = param.browser.mutexParam.bloodType,
                            gender = param.browser.mutexParam.gender,
                            month = param.browser.mutexParam.month,
                            day = param.browser.mutexParam.day,
                            sort = param.browser.orderBy
                        ).fetchBrowserMonoConverted()
                    } else {
                        fetchBrowserMonoPerson(
                            page = offset.toApiPage(limit),
                            type = param.browser.mutexParam.type,
                            bloodtype = param.browser.mutexParam.bloodType,
                            gender = param.browser.mutexParam.gender,
                            month = param.browser.mutexParam.month,
                            day = param.browser.mutexParam.day,
                            sort = param.browser.orderBy
                        ).fetchBrowserMonoConverted()
                    }
                }
            }
            // 用户人物收藏
            ListMonoType.USER_COLLECTION -> client.requestNextUserApi {
                if (param.collection.monoType == MonoType.CHARACTER) {
                    getUserCharacterCollections(
                        username = param.collection.username,
                        offset = offset,
                        limit = limit
                    ).result.map {
                        ComposeMonoDisplay(
                            type = MonoType.CHARACTER,
                            info = ComposeMonoInfo(mono = it)
                        )
                    }
                } else {
                    getUserPersonCollections(
                        username = param.collection.username,
                        offset = offset,
                        limit = limit
                    ).result.map {
                        ComposeMonoDisplay(
                            type = MonoType.PERSON,
                            info = ComposeMonoInfo(mono = it)
                        )
                    }
                }
            }

            ListMonoType.SEARCH_CHARACTER -> client.requestNextSearchApi {
                searchCharacters(
                    searchCharacter = param.search,
                    offset = offset,
                    limit = limit
                ).result.map { ComposeMonoDisplay(type = MonoType.CHARACTER, info = ComposeMonoInfo(mono = it)) }
                    .toList()
            }

            ListMonoType.SEARCH_PERSON -> client.requestNextSearchApi {
                searchPersons(
                    searchPerson = param.search,
                    offset = offset,
                    limit = limit
                ).result.map { ComposeMonoDisplay(type = MonoType.PERSON, info = ComposeMonoInfo(mono = it)) }
                    .toList()
            }

            else -> error("不支持的类型: ${param.type}")
        }
    }

    override suspend fun fetchMonoDetail(monoId: Long, type: Int): Result<ComposeMono> =
        if (type == MonoType.CHARACTER) {
            client.requestNextCharacterApi { getCharacter(monoId) }
        } else {
            client.requestNextPersonApi { getPerson(monoId) }
        }

    override suspend fun fetchMonoDetailByWeb(monoId: Long, type: Int): Result<ComposeMonoWebInfo> = client.requestWebApi {
        with(monoParser) {
            if (type == MonoType.CHARACTER) {
                fetchCharacterDetail(monoId)
                    .fetchMonoDetailConverted()
            } else {
                fetchPersonDetail(monoId)
                    .fetchMonoDetailConverted()
            }
        }
    }

    override suspend fun fetchMonoHomepage(): Result<List<ComposeSection<ComposeMonoDisplay>>> = client.requestWebApi {
        with(monoParser) {
            fetchMonoHomepage()
                .fetchMonoHomepageConverted()
        }
    }


    /*
        override suspend fun fetchMonoListByType(
            param: ListMonoParam,
            page: Int,
            pageSize: Int,
        ): Result<List<ComposeMonoDisplay>> = client.requestWebApi {
            when (param.type) {

                else -> error("不支持的类型: ${param.type}")
            }
        }*/
}