package com.xiaoyv.bangumi.shared.data.repository

import androidx.paging.Pager
import com.xiaoyv.bangumi.shared.core.types.TimelineCat
import com.xiaoyv.bangumi.shared.core.types.TimelineTarget
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeId
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimeline

interface TimelineRepository {
    fun fetchTimelineDisplayPager(
        @TimelineTarget target: String,
        @TimelineCat type: Int,
        username: String
    ): Pager<Int, ComposeTimeline>

    suspend fun fetchTimelineReplies(timelineId: Long): Result<List<ComposeReply>>

    suspend fun submitCreateTimeline(content: String, turnstileToken: String): Result<ComposeId>

    suspend fun submitTimelineReaction(timelineId: Long, value: String?): Result<Unit>

    suspend fun submitDeleteTimeline(timelineId: Long): Result<Unit>
}
