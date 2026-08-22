package com.xiaoyv.bangumi.shared.data.repository

import com.xiaoyv.bangumi.shared.core.types.CommentType
import com.xiaoyv.bangumi.shared.core.types.RakuenType
import com.xiaoyv.bangumi.shared.data.model.request.list.blog.ListBlogParam
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexParam
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexRelatedParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeDollarItem
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroupHomepage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeStatus
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndex
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndexFocus
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndexRelated
import com.xiaoyv.bangumi.shared.data.model.response.bgm.rakuen.ComposeRakuenTopic
import com.xiaoyv.bangumi.shared.data.model.response.bgm.reaction.ComposeReaction
import com.xiaoyv.bangumi.shared.data.repository.datasource.MemoryPagingController

interface UgcRepository {
    fun fetchRaKuenPager(@RakuenType type: String, filter: String? = null): MemoryPagingController<ComposeRakuenTopic, String>

    fun fetchBlogPager(param: ListBlogParam): MemoryPagingController<ComposeBlogDisplay, Long>

    fun fetchIndexPager(param: ListIndexParam): MemoryPagingController<ComposeIndex, Long>

    fun fetchIndexRelatePager(param: ListIndexRelatedParam): MemoryPagingController<ComposeIndexRelated, Long>

    suspend fun fetchIndexFocus(): Result<List<ComposeIndexFocus>>

    suspend fun fetchDollarsChat(): Result<List<ComposeDollarItem>>

    suspend fun fetchGroupHomepage(): Result<ComposeGroupHomepage>

    suspend fun submitReaction(
        @CommentType type: Int,
        mainId: Long,
        id: String,
        value: String,
    ): Result<List<ComposeReaction>>

    suspend fun summitDollarsChat(message: String): Result<ComposeStatus>
}
