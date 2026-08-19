package com.xiaoyv.bangumi.features.timeline.detail.business

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
    }
}