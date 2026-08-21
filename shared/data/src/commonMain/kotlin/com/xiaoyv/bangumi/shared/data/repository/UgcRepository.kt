package com.xiaoyv.bangumi.shared.data.repository

import androidx.paging.Pager
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

interface UgcRepository {
    fun fetchRaKuenPager(@RakuenType type: String, filter: String? = null): Pager<Int, ComposeRakuenTopic>

    fun fetchBlogPager(param: ListBlogParam): Pager<Int, ComposeBlogDisplay>

    fun fetchIndexPager(param: ListIndexParam): Pager<Int, ComposeIndex>

    fun fetchIndexRelatePager(param: ListIndexRelatedParam): Pager<Int, ComposeIndexRelated>

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