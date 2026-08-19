package com.xiaoyv.bangumi.features.timeline.add.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [TimelineAddEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class TimelineAddEvent {
    sealed class UI : TimelineAddEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : TimelineAddEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}