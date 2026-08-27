package com.xiaoyv.bangumi.shared.data.repository

import com.xiaoyv.bangumi.shared.data.model.request.list.blog.ListBlogParam
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeId
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogEntry
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.repository.datasource.MemoryPagingController

interface BlogRepository {
    fun fetchBlogPager(param: ListBlogParam): MemoryPagingController<ComposeBlogDisplay, Long>

    suspend fun fetchBlogDetail(blogId: Long): Result<ComposeBlogEntry>

    suspend fun fetchBlogComments(blogId: Long): Result<List<ComposeReply>>

    suspend fun fetchBlogRelateSubjects(blogId: Long): Result<List<ComposeSubject>>

    suspend fun submitBlogReaction(commentId: Long, value: String?): Result<Unit>

    suspend fun submitBlogComment(
        blogId: Long,
        content: String,
        turnstile: String,
        replyTo: Long? = null
    ): Result<ComposeId>

    suspend fun submitCreateBlog(
        title: String,
        content: String,
        turnstile: String,
    ): Result<ComposeId>
}