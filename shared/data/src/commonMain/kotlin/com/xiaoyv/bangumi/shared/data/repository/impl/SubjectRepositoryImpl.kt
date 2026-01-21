package com.xiaoyv.bangumi.shared.data.repository.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.xiaoyv.bangumi.shared.core.types.CollectionWebPath
import com.xiaoyv.bangumi.shared.core.types.EpisodeType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.PersonPositionType
import com.xiaoyv.bangumi.shared.core.types.SubjectSortBrowserType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.types.SubjectWebPath
import com.xiaoyv.bangumi.shared.core.types.list.ListSubjectType
import com.xiaoyv.bangumi.shared.core.types.list.ListTagType
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.fetchAllPages
import com.xiaoyv.bangumi.shared.core.utils.runResult
import com.xiaoyv.bangumi.shared.core.utils.substringBeforeSymbol
import com.xiaoyv.bangumi.shared.core.utils.toApiPage
import com.xiaoyv.bangumi.shared.data.api.client.BgmApiClient
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.ListSubjectParam
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.SubjectSearchBody
import com.xiaoyv.bangumi.shared.data.model.request.list.tag.ListTagParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.Airtime
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeComment
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisode
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeHomeSection
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeParade
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSubjectDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSubjectStats
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSubjectWebInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTag
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTopic
import com.xiaoyv.bangumi.shared.data.model.response.bgm.chineseNames
import com.xiaoyv.bangumi.shared.data.model.response.db.ComposeDoubanPhoto
import com.xiaoyv.bangumi.shared.data.model.response.db.ComposeDoubanSuggest
import com.xiaoyv.bangumi.shared.data.model.response.db.ComposeDoubanSuggestCard
import com.xiaoyv.bangumi.shared.data.parser.bgm.SubjectParser
import com.xiaoyv.bangumi.shared.data.parser.bgm.TopicTableParser
import com.xiaoyv.bangumi.shared.data.repository.DatabaseRepository
import com.xiaoyv.bangumi.shared.data.repository.SubjectRepository
import com.xiaoyv.bangumi.shared.data.repository.datasource.createNetworkOffsetLimitPagingPager
import com.xiaoyv.bangumi.shared.data.repository.datasource.createNetworkPageLimitPagingPager
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * [SubjectRepositoryImpl]
 *
 * @author why
 * @since 2025/1/25
 */
class SubjectRepositoryImpl(
    private val client: BgmApiClient,
    private val pagingConfig: PagingConfig,
    private val subjectParser: SubjectParser,
    private val topicTableParser: TopicTableParser,
    private val databaseRepository: DatabaseRepository,
) : SubjectRepository {

    override fun fetchSubjectPager(param: ListSubjectParam): Pager<Int, ComposeSubjectDisplay> {
        return createNetworkOffsetLimitPagingPager(
            pagingConfig = pagingConfig,
            keySelector = { it.subject.id },
            onLoadData = {
                fetchSubjectList(param, it, pagingConfig.pageSize).getOrThrow()
            }
        )
    }

    override fun fetchSubjectCommentPager(subjectId: Long): Pager<Int, ComposeComment> {
        return createNetworkPageLimitPagingPager(
            keySelector = { it.id },
            pagingConfig = pagingConfig,
            onLoadData = {
                with(subjectParser) {
                    client.bgmWebApi
                        .fetchSubjectComment(subjectId, it)
                        .fetchSubjectCommentConverted()
                }
            }
        )
    }


    override fun fetchSubjectTagPager(param: ListTagParam): Pager<Int, ComposeTag> {
        return createNetworkPageLimitPagingPager(
            pagingConfig = pagingConfig,
            keySelector = { it.name },
            onlyOnePage = param.type == ListTagType.SEARCH,
            onLoadData = {
                when (param.type) {
                    ListTagType.SEARCH -> fetchSearchSubjectTags(param.search.keyword, param.search.subjectType).getOrThrow()
                    ListTagType.BROWSER -> fetchBrowserSubjectTags(param.subjectType, it).getOrThrow()
                    else -> error("不支持的标签类型: ${param.type}")
                }
            }
        )
    }

    override suspend fun fetchSearchSubjectTags(
        query: String,
        type: Int,
    ): Result<List<ComposeTag>> = client.requestWebApi {
        with(subjectParser) {
            fetchSearchSubjectTags(type = SubjectWebPath.from(type), keyword = query)
                .fetchSearchSubjectTagsConverted()
        }
    }

    override suspend fun fetchBrowserSubjectTags(type: Int, page: Int): Result<List<ComposeTag>> = client.requestWebApi {
        with(subjectParser) {
            fetchBorwserSubjectTags(type = SubjectWebPath.from(type), page = page)
                .fetchSearchSubjectTagsConverted()
        }
    }


    override fun fetchSearchSuggestion(query: String): Flow<Result<ComposeDoubanSuggest>> {
        return flow {
            if (query.isBlank()) {
                emit(Result.success(ComposeDoubanSuggest.Empty))
            } else {
                emit(runResult { client.dbApi.queryDouBanSuggestion(query) })
            }
        }
    }

    override suspend fun fetchSubjectPreview(subject: ComposeSubject): Result<ComposeDoubanPhoto> = runResult {
        val key = "DB6-${subject.id}"
        val chineseNames = subject.infobox.chineseNames(subject.nameCn)

        var suggestCard = databaseRepository.get(key, ComposeDoubanSuggestCard.Empty, ComposeDoubanSuggestCard.serializer())
        if (suggestCard == ComposeDoubanSuggestCard.Empty) {
            val cards = coroutineScope {
                awaitAll(
                    *chineseNames
                        .flatMap {
                            if (it.length > 10) listOf(it, it.substringBeforeSymbol(), it.dropLast(2))
                            else listOf(it, it.substringBeforeSymbol())
                        }
                        .map {
                            async {
                                runCatching { client.dbApi.queryDouBanSuggestion(q = it).cards }.getOrNull().orEmpty()
                            }
                        }
                        .toTypedArray()
                ).flatMap { it }
            }

            val mediaCards = cards
                .filter { it.targetType == "tv" || it.targetType == "movie" }
                .sortedBy {
                    if (subject.type == SubjectType.REAL) {
                        if (it.target.hasLinewatch) 0 else 1
                    } else {
                        if (it.target.hasLinewatch) 1 else 0
                    }
                }
            val card = mediaCards.find { chineseNames.contains(it.target.title) }
                ?: mediaCards.firstOrNull()

            if (card != null) databaseRepository.put(key, card, ComposeDoubanSuggestCard.serializer())
            suggestCard = card ?: ComposeDoubanSuggestCard.Empty
        }
        if (suggestCard == ComposeDoubanSuggestCard.Empty) ComposeDoubanPhoto.Empty else {
            client.dbApi
                .queryDouBanPhotoList(suggestCard.targetId, suggestCard.targetType)
                .copy(doubanMediaId = suggestCard.targetId, doubanMediaType = suggestCard.targetType)
        }
    }

    override suspend fun fetchCalendar(): Result<Map<String, List<ComposeHomeSection>>> = client.requestNextSubjectApi {
        getCalendar()
    }

    override suspend fun fetchTrends(
        type: Int,
        limit: Int,
    ): Result<List<ComposeHomeSection>> = client.requestNextSubjectApi {
        getTrends(type = type, limit = limit).result
    }

    override suspend fun fetchSubjectEpisodes(subjectId: Long, @EpisodeType type: Int?): Result<List<ComposeEpisode>> =
        client.requestNextSubjectApi {
            fetchAllPages(1000) { offset, limit ->
                getSubjectEpisodes(
                    subjectID = subjectId,
                    type = type,
                    offset = offset,
                    limit = limit
                ).result
            }
        }

    override suspend fun fetchSubjectStats(id: Long): Result<ComposeSubjectStats> = client.requestWebApi {
        with(subjectParser) {
            fetchSubjectStats(subjectId = id)
                .fetchSubjectStatsConverted()
        }
    }

    override suspend fun fetchSubjectDetail(subjectId: Long): Result<ComposeSubject> = client.requestNextSubjectApi {
        getSubject(subjectId)
    }

    override suspend fun fetchSubjectDetailByWeb(subjectId: Long): Result<ComposeSubjectWebInfo> = client.requestWebApi {
        with(subjectParser) {
            fetchSubjectDetail(subjectId)
                .fetchSubjectDetailConverted()
        }
    }

    override suspend fun fetchSubjectCharacter(subjectId: Long, type: Int?, offset: Int, limit: Int) = client.requestNextSubjectApi {
        getSubjectCharacters(subjectID = subjectId, type = null, limit = limit).result.map {
            ComposeMonoDisplay(type = MonoType.CHARACTER, info = it)
        }
    }

    override suspend fun fetchSubjectPerson(subjectId: Long, position: Long?, offset: Int, limit: Int) = client.requestNextSubjectApi {
        getSubjectStaffPersons(subjectID = subjectId, position = position, limit = limit).result.map {
            ComposeMonoDisplay(type = MonoType.PERSON, info = it)
        }
    }

    override suspend fun fetchSubjectTopic(subjectId: Long): Result<List<ComposeTopic>> = client.requestWebApi {
        with(topicTableParser) {
            fetchSubjectTopic(subjectId)
                .fetchSubjectTopicTableItem()
        }
    }

    override suspend fun fetchMySubjectTags(subjectId: Long): Result<List<ComposeTag>> = client.requestWebApi {
        with(subjectParser) {
            fetchSubjectDetail(subjectId)
                .fetchMySubjectTagsCovConverted()
        }
    }

    override suspend fun fetchSubjectRelated(subjectId: Long): Result<List<ComposeSubjectDisplay>> = client.requestNextSubjectApi {
        getSubjectRelations(subjectID = subjectId).result.map {
            it.copy(subject = it.subject.copy(airtime = Airtime.fromInfo(it.subject.info)))
        }
    }

    override suspend fun fetchSubjectList(param: ListSubjectParam, offset: Int, pageSize: Int) =
        when (param.type) {
            ListSubjectType.PERSON_WORK -> client.requestNextPersonApi {
                getPersonWorks(
                    personID = param.personWork.personId,
                    subjectType = param.personWork.subjectType.takeIf { it != SubjectType.UNKNOWN },
                    position = param.personWork.position.takeIf { it != PersonPositionType.UNKNOWN },
                    offset = offset,
                    limit = pagingConfig.pageSize
                ).result
            }

            ListSubjectType.SUBJECT_RELATED -> client.requestNextSubjectApi {
                getSubjectRelations(
                    subjectID = param.related.subjectId,
                    offset = offset,
                    limit = pagingConfig.pageSize
                ).result
            }

            ListSubjectType.SEARCH -> {
                debugLog { "Search:${param.search}" }
                client.requestNextSearchApi {
                    searchSubjects(
                        body = param.search,
                        offset = offset,
                        limit = pagingConfig.pageSize
                    ).result.map { ComposeSubjectDisplay(subject = it) }
                }
            }

            ListSubjectType.BROWSER -> {
                // 浏览API有TAG时有BUG，暂时用搜索接口替代
                if (param.browser.tags.isNotEmpty()) {
                    client.requestNextSearchApi {
                        searchSubjects(
                            body = SubjectSearchBody(
                                keyword = "",
                                filter = SubjectSearchBody.SubjectSearchFilter(
                                    nsfw = false,
                                    type = if (param.browser.subjectType != SubjectType.UNKNOWN) persistentListOf(param.browser.subjectType) else null,
                                    tags = param.browser.tags,
                                    date = param.browser.searchData()
                                ),
                                sort = SubjectSortBrowserType.toSearchType(param.browser.sort),
                            ),
                            offset = offset,
                            limit = pagingConfig.pageSize
                        ).result.map { ComposeSubjectDisplay(subject = it) }
                    }
                } else {
                    client.requestNextSubjectApi {
                        getSubjects(
                            type = param.browser.subjectType,
                            cat = param.browser.cat,
                            series = param.browser.series,
                            sort = param.browser.sort,
                            year = param.browser.year,
                            tags = param.browser.tags,
                            month = param.browser.month,
                            page = offset.toApiPage(pageSize),
                        ).result.map {
                            ComposeSubjectDisplay(subject = it.copy(airtime = Airtime.fromInfo(it.info)))
                        }
                    }
                }
            }

            ListSubjectType.USER_COLLECTION -> client.requestWebApi {
                with(subjectParser) {
                    fetchUserCollection(
                        username = param.collection.username,
                        type = CollectionWebPath.from(param.collection.collectionType),
                        subjectType = SubjectWebPath.from(param.collection.subjectType),
                        sortType = param.collection.collectionSort,
                        page = (offset / pageSize) + 1,
                    ).fetchUserCollectionCoverted(
                        collectionType = param.collection.collectionType,
                        subjectType = param.collection.subjectType,
                    )
                }
            }

            else -> error("不支持的列表类型: ${param.type}")
        }

    override suspend fun fetchSubjectParade(subjectId: Long): Result<ComposeParade> = client.requestJsonApi {
        fetchSubjectParade(subjectId)
    }

}