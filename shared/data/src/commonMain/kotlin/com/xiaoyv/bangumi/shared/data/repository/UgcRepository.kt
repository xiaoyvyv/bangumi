package com.xiaoyv.bangumi.shared.data.repository

import androidx.paging.Pager
import com.xiaoyv.bangumi.shared.core.types.CommentType
import com.xiaoyv.bangumi.shared.core.types.IndexCatWebTabType
import com.xiaoyv.bangumi.shared.core.types.RakuenIdType
import com.xiaoyv.bangumi.shared.core.types.RakuenTab
import com.xiaoyv.bangumi.shared.core.types.TimelineTab
import com.xiaoyv.bangumi.shared.core.types.TimelineTarget
import com.xiaoyv.bangumi.shared.data.model.request.list.blog.ListBlogParam
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeDollarItem
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroupHomepage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeIndex
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeIndexFocus
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeNewReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReaction
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeStatus
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTimeline
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTopic
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTopicDetail

interface UgcRepository {
    fun fetchTimelinePager(
        @TimelineTarget target: String,
        @TimelineTab type: String,
        username: String = "",
    ): Pager<Int, ComposeTimeline>

    fun fetchTopicPager(@RakuenTab type: String, filter: String? = null): Pager<Int, ComposeTopic>

    fun fetchBlogPager(param: ListBlogParam): Pager<Int, ComposeBlogDisplay>

    fun fetchIndexPager(param: ListIndexParam): Pager<Int, ComposeIndex>

    suspend fun fetchIndexFocus(): Result<List<ComposeIndexFocus>>

    suspend fun fetchIndexDetail(id: Long, @IndexCatWebTabType cat: String): Result<ComposeIndex>

    suspend fun fetchTopicDetail(id: Long, @RakuenIdType type: String): Result<ComposeTopicDetail>

    suspend fun fetchDollarsChat(): Result<List<ComposeDollarItem>>

    suspend fun fetchGroupHomepage(): Result<ComposeGroupHomepage>

    suspend fun submitReaction(
        @CommentType type: Int,
        mainId: Long,
        id: String,
        value: String,
    ): Result<List<ComposeReaction>>

    suspend fun submitNewReply(action: String, params: Map<String, Any>): Result<ComposeNewReply>

    suspend fun summitDollarsChat(message: String): Result<ComposeStatus>
}