package com.xiaoyv.bangumi.shared.data.repository.impl

import androidx.paging.PagingConfig
import com.xiaoyv.bangumi.shared.core.utils.runResult
import com.xiaoyv.bangumi.shared.data.api.client.BgmApiClient
import com.xiaoyv.bangumi.shared.data.model.request.CreateCommentParam
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeId
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogEntry
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.repository.BlogRepository

class BlogRepositoryImpl(
    private val client: BgmApiClient,
    private val pagingConfig: PagingConfig,
) : BlogRepository {

    override suspend fun fetchBlogDetail(blogId: Long): Result<ComposeBlogEntry> = client.requestNextBlogApi {
        getBlogEntry(blogId).normalized()
    }

    override suspend fun fetchBlogComments(blogId: Long): Result<List<ComposeReply>> = client.requestNextBlogApi {
        getBlogComments(blogId).map { it.normalized() }
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
}