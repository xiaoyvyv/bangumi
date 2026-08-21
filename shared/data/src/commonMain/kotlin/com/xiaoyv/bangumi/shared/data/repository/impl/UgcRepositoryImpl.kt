package com.xiaoyv.bangumi.shared.data.repository.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.login_first_tip
import com.xiaoyv.bangumi.shared.core.exception.ApiHttpException
import com.xiaoyv.bangumi.shared.core.types.RakuenFlagType
import com.xiaoyv.bangumi.shared.core.types.RakuenType
import com.xiaoyv.bangumi.shared.core.types.TopicType
import com.xiaoyv.bangumi.shared.core.types.list.ListBlogType
import com.xiaoyv.bangumi.shared.core.types.list.ListIndexType
import com.xiaoyv.bangumi.shared.core.utils.awaitAll
import com.xiaoyv.bangumi.shared.core.utils.defaultJson
import com.xiaoyv.bangumi.shared.core.utils.runResult
import com.xiaoyv.bangumi.shared.core.utils.toApiPage
import com.xiaoyv.bangumi.shared.data.api.client.BgmApiClient
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.manager.bbcodeToHtml
import com.xiaoyv.bangumi.shared.data.model.request.list.blog.ListBlogParam
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexParam
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexRelatedParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogEntry.Companion.optImageUrl
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeDollarItem
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroupHomepage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeNewReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.reaction.ComposeReaction
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeStatus
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTopicDetail
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndex
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndexFocus
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndexRelated
import com.xiaoyv.bangumi.shared.data.model.response.bgm.rakuen.ComposeRakuenTopic
import com.xiaoyv.bangumi.shared.data.parser.bgm.BlogParser
import com.xiaoyv.bangumi.shared.data.parser.bgm.GroupParser
import com.xiaoyv.bangumi.shared.data.parser.bgm.IndexParser
import com.xiaoyv.bangumi.shared.data.parser.bgm.TopicParser
import com.xiaoyv.bangumi.shared.data.repository.UgcRepository
import com.xiaoyv.bangumi.shared.data.repository.datasource.createNetworkOffsetLimitPagingPager
import com.xiaoyv.bangumi.shared.data.repository.datasource.createNetworkPageLimitPagingPager
import com.xiaoyv.bangumi.shared.data.repository.datasource.createPagingConfig
import io.ktor.client.statement.bodyAsText
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.json.jsonObject
import org.jetbrains.compose.resources.getString

class UgcRepositoryImpl(
    private val client: BgmApiClient,
    private val pagingConfig: PagingConfig,
    private val topicParser: TopicParser,
    private val blogParser: BlogParser,
    private val indexParser: IndexParser,
    private val groupParser: GroupParser,
    private val userManager: UserManager,
) : UgcRepository {

    override fun fetchRaKuenPager(@RakuenType type: String, filter: String?): Pager<Int, ComposeRakuenTopic> {
        return createNetworkPageLimitPagingPager(
            pagingConfig = pagingConfig,
            onlyOnePage = true,
            keySelector = { it.key },
            onLoadData = {
                // 登录检测
                if (type == RakuenType.MY_GROUP && !userManager.isLogin) {
                    throw ApiHttpException(401, getString(Res.string.login_first_tip))
                }

                client.requestNextTopicApi {
                    val rakuenTopics = getRakuenTopicList(type, 200).result

                    val groupTopicIds = rakuenTopics
                        .filter { item -> item.type == RakuenType.GROUP || item.type == RakuenType.MY_GROUP }
                        .map { it.id }

                    val maxGroupTopicId = groupTopicIds.maxOrNull()
                    val newGroupTopicIds = groupTopicIds.sortedDescending().take(5)

                    val subjectTopicIds = rakuenTopics
                        .filter { item -> item.type == RakuenType.SUBJECT }
                        .map { it.id }

                    val maxSubjectTopicId = subjectTopicIds.maxOrNull()
                    val newSubjectTopicIds = subjectTopicIds.sortedDescending().take(5)

                    rakuenTopics.map { item ->
                        val flags = mutableListOf<String>()

                        val id = item.id

                        // 小组话题：坟贴、新帖、火标记
                        if ((item.type == RakuenType.GROUP || item.type == RakuenType.MY_GROUP) && maxGroupTopicId != null) {
                            if (id < maxGroupTopicId - 10000) {
                                flags.add(RakuenFlagType.TYPE_OLDEST)
                            } else if (id < maxGroupTopicId - 4000) {
                                flags.add(RakuenFlagType.TYPE_OLD)
                            }

                            if (newGroupTopicIds.contains(id)) {
                                flags.add(RakuenFlagType.TYPE_NEW)
                            }
                            if (item.replyCount >= 100) {
                                flags.add(RakuenFlagType.TYPE_HOT)
                            }
                        }

                        // 条目话题：坟贴、新帖、火标记
                        if (item.type == RakuenType.SUBJECT && maxSubjectTopicId != null) {
                            if (id < maxSubjectTopicId - 5000) {
                                flags.add(RakuenFlagType.TYPE_OLDEST)
                            } else if (id < maxSubjectTopicId - 1000) {
                                flags.add(RakuenFlagType.TYPE_OLD)
                            }

                            if (newSubjectTopicIds.contains(id)) {
                                flags.add(RakuenFlagType.TYPE_NEW)
                            }
                            if (item.replyCount >= 50) {
                                flags.add(RakuenFlagType.TYPE_HOT)
                            }
                        }

                        item.copy(flags = flags.toPersistentList())
                    }
                }.getOrThrow()
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
                            zip.data2.map { ComposeBlogDisplay(blog = it.normalized(), user = zip.data1) }
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

    override fun fetchIndexRelatePager(param: ListIndexRelatedParam): Pager<Int, ComposeIndexRelated> {
        return createNetworkOffsetLimitPagingPager(
            pagingConfig = pagingConfig,
            keySelector = { it.id },
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

    override suspend fun fetchIndexFocus(): Result<List<ComposeIndexFocus>> = client.requestWebApi {
        with(indexParser) {
            fetchIndexHomepage()
                .fetchIndexFocusConverted()
        }
    }

    override suspend fun fetchTopicDetail(id: Long, @TopicType type: String): Result<ComposeTopicDetail> = runResult {
        with(topicParser) {
            if (type == TopicType.TYPE_BLOG) {
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
                content = it.msg.bbcodeToHtml(),
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