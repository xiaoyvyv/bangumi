package com.xiaoyv.bangumi.shared.data.repository

import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.data.model.request.IndexTarget
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeId
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndex
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject

interface IndexRepository {
    suspend fun fetchUserCreatedIndex(username: String): Result<List<ComposeIndex>>

    suspend fun fetchIndexDetail(indexId: Long): Result<ComposeIndex>

    suspend fun fetchIndexComments(indexId: Long): Result<List<ComposeReply>>

    suspend fun fetchIndexIsBookmarked(indexId: Long): Result<Boolean>

    suspend fun fetchIndexSubjects(indexId: Long, @SubjectType type: Int, limit: Int, offset: Int): Result<List<ComposeSubject>>

    suspend fun submitBookmarkOrCancelIndex(indexId: Long, bookmarked: Boolean): Result<Boolean>

    suspend fun submitIndexAddRelated(indexId: Long, target: IndexTarget): Result<Unit>

    suspend fun submitIndexComment(
        indexId: Long,
        content: String,
        turnstile: String,
        replyTo: Long? = null
    ): Result<ComposeId>
}