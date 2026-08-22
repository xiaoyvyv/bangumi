package com.xiaoyv.bangumi.features.timeline.detail.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimeline
import kotlinx.collections.immutable.persistentListOf

/**
 * [TimelineDetailState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class TimelineDetailState(
    val timeline: ComposeTimeline = ComposeTimeline(),
    val replies: SerializeList<ComposeReply> = persistentListOf(),
)
