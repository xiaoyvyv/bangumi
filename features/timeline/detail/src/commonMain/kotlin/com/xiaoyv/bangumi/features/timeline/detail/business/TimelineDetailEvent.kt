package com.xiaoyv.bangumi.features.timeline.detail.business

import com.xiaoyv.bangumi.shared.data.model.response.bgm.reaction.ComposeReaction
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimeline
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [TimelineDetailEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class TimelineDetailEvent {
    sealed class UI : TimelineDetailEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : TimelineDetailEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data class OnClickReaction(
            val timeline: ComposeTimeline,
            val reaction: ComposeReaction,
        ) : Action()
        data class OnDeleteTimeline(val timeline: ComposeTimeline) : Action()
        data object OnAppendComment : Action()
    }
}
