package com.xiaoyv.bangumi.shared.data.repository.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.xiaoyv.bangumi.shared.core.types.IndexCatWebTabType
import com.xiaoyv.bangumi.shared.core.types.RakuenIdType
import com.xiaoyv.bangumi.shared.core.types.TimelineTab
import com.xiaoyv.bangumi.shared.core.types.TimelineTarget
import com.xiaoyv.bangumi.shared.core.types.list.ListBlogType
import com.xiaoyv.bangumi.shared.core.types.list.ListIndexType
import com.xiaoyv.bangumi.shared.core.utils.awaitAll
import com.xiaoyv.bangumi.shared.core.utils.bbcodeToHtml
import com.xiaoyv.bangumi.shared.core.utils.defaultJson
import com.xiaoyv.bangumi.shared.core.utils.parseAsHtml
import com.xiaoyv.bangumi.shared.core.utils.runResult
import com.xiaoyv.bangumi.shared.core.utils.toApiPage
import com.xiaoyv.bangumi.shared.data.api.client.BgmApiClient
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.model.request.list.blog.ListBlogParam
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogEntry.Companion.optImageUrl
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeDollarItem
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroupHomepage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeIndex
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeIndexFocus
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeNewReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReaction
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeStatus
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTimeline
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTopic
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTopicDetail
import com.xiaoyv.bangumi.shared.data.parser.bgm.BlogParser
import com.xiaoyv.bangumi.shared.data.parser.bgm.GroupParser
import com.xiaoyv.bangumi.shared.data.parser.bgm.IndexParser
import com.xiaoyv.bangumi.shared.data.parser.bgm.TimelineParser
import com.xiaoyv.bangumi.shared.data.parser.bgm.TopicParser
import com.xiaoyv.bangumi.shared.data.repository.UgcRepository
import com.xiaoyv.bangumi.shared.data.repository.datasource.createNetworkOffsetLimitPagingPager
import com.xiaoyv.bangumi.shared.data.repository.datasource.createNetworkPageLimitPagingPager
import com.xiaoyv.bangumi.shared.data.repository.datasource.createPagingConfig
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.jsonObject

class UgcRepositoryImpl(
    private val client: BgmApiClient,
    private val pagingConfig: PagingConfig,
    private val timelineParser: TimelineParser,
    private val topicParser: TopicParser,
    private val blogParser: BlogParser,
    private val indexParser: IndexParser,
    private val groupParser: GroupParser,
    private val userManager: UserManager,
) : UgcRepository {

    override fun fetchTimelinePager(
        @TimelineTarget target: String,
        @TimelineTab type: String,
        username: String,
    ): Pager<Int, ComposeTimeline> {
        return createNetworkPageLimitPagingPager(
            pagingConfig = pagingConfig,
            keySelector = { it.id },
            onLoadData = {
                with(timelineParser) {
                    when (target) {
                        TimelineTarget.FRIEND -> {
                            client.bgmWebApi
                                .fetchTimelineForWhole(type = type, page = it)
                                .fetchTimelineConverted(target, type)
                        }

                        TimelineTarget.USER -> {
                            awaitAll(
                                block1 = { Result.success(client.nextUserApi.getUser(username)) },
                                block2 = {
                                    Result.success(
                                        client.bgmWebApi
                                            .fetchTimelineForUser(username = username, type = type, page = it)
                                            .fetchTimelineConverted(target, type)
                                    )
                                }
                            ).map { zip -> zip.data2.map { timeline -> timeline.copy(user = zip.data1) } }
                                .getOrThrow()
                        }

                        TimelineTarget.WHOLE -> {
                            client.bgmWebApiNoCookie
                                .fetchTimelineForWhole(type = type, page = it)
                                .fetchTimelineConverted(target, type)
                        }

                        else -> emptyList()
                    }
                }
            }
        )
    }

    override fun fetchTopicPager(type: String, filter: String?): Pager<Int, ComposeTopic> {
        return createNetworkPageLimitPagingPager(
            pagingConfig = pagingConfig,
            onlyOnePage = true,
            keySelector = { it.id },
            onLoadData = {
                with(topicParser) {
                    client.bgmWebApi
                        .fetchRakuenTopic(type = type, filter = filter)
                        .fetchRakuenTopicConverted()
                }
            }
        )
    }

    override fun fetchBlogPager(param: ListBlogParam): Pager<Int, ComposeBlogDisplay> {
        return createNetworkOffsetLimitPagingPager(
            pagingConfig = pagingConfig,
            keySelector = { it.uniqueKey },
            onLoadData = { offset ->
                when (param.type) {
                    // 用户创建的日志
                    ListBlogType.USER_CREATE -> {
                        awaitAll(
                            block1 = { Result.success(client.nextUserApi.getUser(param.username)) },
                            block2 = {
                                Result.success(
                                    client.nextUserApi.getUserBlogs(
                                        username = param.username,
                                        offset = offset,
                                        limit = pagingConfig.pageSize
                                    ).result
                                )
                            }
                        ).map { zip ->
                            zip.data2.map { ComposeBlogDisplay(blog = it.opt(), user = zip.data1) }
                        }.getOrThrow()
                    }
                    // 条目相关的日志
                    ListBlogType.SUBJECT_RELATED -> client.nextSubjectApi.getSubjectReviews(
                        subjectID = param.subjectId,
                        offset = offset,
                        limit = pagingConfig.pageSize
                    ).result.optImageUrl()

                    // 全站日志浏览
                    ListBlogType.BROWSER -> with(blogParser) {
                        if (param.browser.isBlank()) {
                            client.bgmWebApi
                                .fetchBrowserBlog(page = offset.toApiPage(pagingConfig.pageSize))
                                .fetchBrowserBlogConverted()
                        } else {
                            client.bgmWebApi
                                .fetchBrowserBlog(
                                    page = offset.toApiPage(pagingConfig.pageSize),
                                    type = param.browser
                                )
                                .fetchBrowserBlogConverted()
                        }
                    }

                    else -> error("暂不支持该类型")
                }
            }
        )
    }

    override fun fetchIndexPager(param: ListIndexParam): Pager<Int, ComposeIndex> {
        return createNetworkPageLimitPagingPager(
            pagingConfig = createPagingConfig(20),
            onlyOnePage = true,
            keySelector = { it.id },
            onLoadData = { page ->
                with(indexParser) {
                    when (param.type) {
                        ListIndexType.USER_CREATE -> {
                            val info = client.nextUserApi.getUser(param.username)

                            client.bgmWebApi
                                .fetchUserIndexCreateList(username = param.username, page = page)
                                .fetchIndexListConverted()
                                .map { index -> index.copy(creator = info) }
                        }

                        ListIndexType.USER_COLLECTION -> client.requestWebApi {
                            fetchUserIndexCollectionList(username = param.username, page = page)
                                .fetchIndexListConverted()
                        }.getOrThrow()

                        ListIndexType.BROWSER -> client.requestWebApi {
                            fetchIndexBorwser(orderby = param.browserOrder, page = page)
                                .fetchIndexListConverted()
                        }.getOrThrow()

                        ListIndexType.SUBJECT_RELATED -> client.requestWebApi {
                            fetchSubjectIndex(subjectId = param.related.subjectId, page = page)
                                .fetchIndexListConverted()
                        }.getOrThrow()

                        ListIndexType.PERSON_RELATED -> client.requestWebApi {
                            fetchPersonIndices(monoId = param.related.monoId, page = page)
                                .fetchIndexListConverted()
                        }.getOrThrow()

                        ListIndexType.CHARACTER_RELATED -> client.requestWebApi {
                            fetchCharacterIndices(monoId = param.related.monoId, page = page)
                                .fetchIndexListConverted()
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

    override suspend fun fetchIndexFocus(): Result<List<ComposeIndexFocus>> = client.requestWebApi {
        with(indexParser) {
            fetchIndexHomepage()
                .fetchIndexFocusConverted()
        }
    }

    override suspend fun fetchIndexDetail(id: Long, @IndexCatWebTabType cat: String): Result<ComposeIndex> = client.requestWebApi {
        with(indexParser) {
            fetchIndexDetail(id, cat)
                .fetchIndexDetailConverted(id)
        }
    }

    override suspend fun fetchTopicDetail(id: Long, @RakuenIdType type: String): Result<ComposeTopicDetail> = runResult {
        with(topicParser) {
            if (type == RakuenIdType.TYPE_BLOG) {
                client.bgmWebApi
                    .fetchRakuenBlogDetail(id)
                    .fetchRakuenBlogDetailConverted(id)
            } else {
                client.bgmWebApi
                    .fetchRakuenTopicDetail(id, type = type)
                    .fetchRakuenTopicDetailConverted(id, type)
            }
        }
    }

    override suspend fun fetchDollarsChat(): Result<List<ComposeDollarItem>> = client.requestWebApi {
        fetchDollarChat().map {
            it.copy(
                contentHtml = bbcodeToHtml(it.msg, true).parseAsHtml(),
                avatar = "https://bgm.tv/pic/user/l/" + it.avatar
            )
        }
    }

    override suspend fun fetchGroupHomepage(): Result<ComposeGroupHomepage> = client.requestWebApi {
        with(groupParser) {
            fetchGroupHomepage()
                .fetchGroupHomepageConverted()
        }
    }


    override suspend fun submitReaction(
        type: Int,
        mainId: Long,
        id: String,
        value: String,
    ): Result<List<ComposeReaction>> = runResult {
        val response = client.bgmWebApi.submitReaction(
            type = type,
            mainId = mainId,
            id = id,
            value = value,
            gh = userManager.userInfo.formHash
        )
        val text = response.bodyAsText()
        val reactionJson = defaultJson.parseToJsonElement(text)
            .jsonObject["data"]
            .toString()

        ComposeReaction.fromJson(reactionJson).entries
            .firstOrNull()?.value
            .orEmpty()
    }

    override suspend fun submitNewReply(action: String, params: Map<String, Any>): Result<ComposeNewReply> = client.requestWebApi {
        submitNewReply(action.trimStart('/'), params = params)
    }

    override suspend fun summitDollarsChat(message: String): Result<ComposeStatus> = client.requestWebApi {
        summitDollarsChat(message = message)
    }
}