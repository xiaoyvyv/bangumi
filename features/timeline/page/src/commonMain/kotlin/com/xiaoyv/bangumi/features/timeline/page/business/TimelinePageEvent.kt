package com.xiaoyv.bangumi.features.timeline.page.business

import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReaction
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimeline
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [TimelinePageEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class TimelinePageEvent {
    sealed class UI : TimelinePageEvent() {
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : TimelinePageEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data class OnClickRecation(val timeline: ComposeTimeline, val reaction: ComposeReaction) : Action()
        data class OnDeleteTimeline(val timeline: ComposeTimeline) : Action()
    }
}