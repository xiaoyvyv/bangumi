package com.xiaoyv.bangumi.shared.data.repository

import com.xiaoyv.bangumi.shared.data.model.request.bgm.IndexCreateParam
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexParam
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexRelatedParam
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeId
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndex
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndexRelated
import com.xiaoyv.bangumi.shared.data.repository.datasource.MemoryPagingController

interface IndexRepository {
    fun fetchIndexPager(param: ListIndexParam): MemoryPagingController<ComposeIndex, Long>

    fun fetchIndexRelatePager(param: ListIndexRelatedParam): MemoryPagingController<ComposeIndexRelated, Long>

    suspend fun fetchUserCreatedIndex(username: String): Result<List<ComposeIndex>>

    suspend fun fetchIndexDetail(indexId: Long): Result<ComposeIndex>

    suspend fun fetchIndexComments(indexId: Long): Result<List<ComposeReply>>

    suspend fun fetchIndexIsBookmarked(indexId: Long): Result<Boolean>

    suspend fun submitBookmarkOrCancelIndex(indexId: Long, bookmarked: Boolean): Result<Boolean>

    suspend fun submitIndexAddRelated(indexId: Long, param: IndexCreateParam): Result<Unit>

    suspend fun submitIndexComment(
        indexId: Long,
        content: String,
        turnstile: String,
        replyTo: Long? = null
    ): Result<ComposeId>

}