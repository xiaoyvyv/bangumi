package com.xiaoyv.bangumi.shared.data.repository

import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogEntry
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject

interface BlogRepository {

    suspend fun fetchBlogDetail(blogId: Long): Result<ComposeBlogEntry>

    suspend fun fetchBlogRelateSubjects(blogId: Long): Result<List<ComposeSubject>>

    suspend fun submitBlogReaction(commentId: Long, value: String?): Result<Unit>
}