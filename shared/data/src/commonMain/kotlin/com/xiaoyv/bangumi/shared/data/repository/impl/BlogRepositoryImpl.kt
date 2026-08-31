package com.xiaoyv.bangumi.shared.data.repository.impl

import androidx.paging.PagingConfig
import com.xiaoyv.bangumi.shared.core.types.list.ListBlogType
import com.xiaoyv.bangumi.shared.core.utils.awaitAll
import com.xiaoyv.bangumi.shared.core.utils.runResult
import com.xiaoyv.bangumi.shared.data.api.client.ApiClient
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.model.request.bgm.CreateBlogEntryRequest
import com.xiaoyv.bangumi.shared.data.model.request.bgm.CreateCommentParam
import com.xiaoyv.bangumi.shared.data.model.request.list.blog.ListBlogParam
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeId
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogEntry
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogEntry.Companion.optImageUrl
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.normalizedReplies
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.repository.BlogRepository
import com.xiaoyv.bangumi.shared.data.repository.datasource.MemoryPagingController
import com.xiaoyv.bangumi.shared.data.repository.datasource.createMemoryOffsetLimitPagingController

class BlogRepositoryImpl(
    private val client: ApiClient,
    private val pagingConfig: PagingConfig,
    private val userManager: UserManager,
) : BlogRepository {
    override fun fetchBlogPager(param: ListBlogParam): MemoryPagingController<ComposeBlogDisplay, Long> {
        return createMemoryOffsetLimitPagingController(
            pagingConfig = pagingConfig,
            idSelector = { it.uniqueKey },
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
                    ListBlogType.BROWSER -> client.requestNextBlogApi {
                        getChannelBlogs(
                            type = param.browserType,
                            offset = offset,
                        ).result.map {
                            ComposeBlogDisplay(
                                id = it.id,
                                blog = it.normalized(),
                                user = it.user
                            )
                        }
                    }.getOrThrow()

                    else -> error("暂不支持该类型")
                }
            }
        )
    }

    override suspend fun fetchBlogDetail(blogId: Long): Result<ComposeBlogEntry> = client.requestNextBlogApi {
        getBlogEntry(blogId).normalized()
    }

    override suspend fun fetchBlogComments(blogId: Long): Result<List<ComposeReply>> = client.requestNextBlogApi {
        getBlogComments(blogId).normalizedReplies()
    }

    override suspend fun fetchBlogRelateSubjects(blogId: Long): Result<List<ComposeSubject>> = client.requestNextBlogApi {
        getBlogRelatedSubjects(blogId)
    }

    override suspend fun submitBlogReaction(
        commentId: Long,
        value: String?
    ): Result<Unit> = runResult {
        TODO("Not yet implemented")
    }

    override suspend fun submitBlogComment(
        blogId: Long,
        content: String,
        turnstile: String,
        replyTo: Long?
    ): Result<ComposeId> = client.requestNextBlogApi {
        createBlogComment(
            entryID = blogId,
            param = CreateCommentParam(
                content = content,
                turnstileToken = turnstile,
                replyTo = replyTo ?: 0
            )
        )
    }

    override suspend fun submitCreateBlog(
        title: String,
        content: String,
        turnstile: String,
        public: Boolean,
        tags: List<String>,
        subjectIDs: List<Long>,
    ): Result<ComposeId> = client.requestNextBlogApi {
        // TODO createBlogEntryByProxy 这里走的 web 请求，turnstileToken 映射的 formHash，后续 PrivateApi 正常了可以移除，传真实的 turnstileToken
        createBlogEntryByProxy(
            param = CreateBlogEntryRequest(
                title = title,
                content = content,
                turnstileToken = userManager.userInfo.formHash,
                public = public,
                tags = tags.takeIf { it.isNotEmpty() },
                subjectIDs = subjectIDs.takeIf { it.isNotEmpty() }
            )
        )
    }
}
