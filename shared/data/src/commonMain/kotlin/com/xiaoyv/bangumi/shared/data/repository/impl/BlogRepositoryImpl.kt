package com.xiaoyv.bangumi.shared.data.repository.impl

import androidx.paging.PagingConfig
import com.xiaoyv.bangumi.shared.data.api.client.BgmApiClient
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogEntry
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.repository.BlogRepository

class BlogRepositoryImpl(
    private val client: BgmApiClient,
    private val pagingConfig: PagingConfig,
) : BlogRepository {

    override suspend fun fetchBlogDetail(blogId: Long): Result<ComposeBlogEntry> = client.requestNextBlogApi {
        getBlogEntry(blogId)
    }

    override suspend fun fetchBlogRelateSubjects(blogId: Long): Result<List<ComposeSubject>> = client.requestNextBlogApi {
        getBlogRelatedSubjects(blogId)
    }

    override suspend fun submitBlogReaction(
        commentId: Long,
        value: String?
    ): Result<Unit> {
        TODO("Not yet implemented")
    }
}