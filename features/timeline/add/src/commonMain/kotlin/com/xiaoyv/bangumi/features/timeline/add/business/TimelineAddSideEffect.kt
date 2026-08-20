package com.xiaoyv.bangumi.features.timeline.add.business

/**
 * [TimelineAddSideEffect]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class TimelineAddSideEffect {
    data class OnCreateTimelineSuccess(val id: Long) : TimelineAddSideEffect()
}