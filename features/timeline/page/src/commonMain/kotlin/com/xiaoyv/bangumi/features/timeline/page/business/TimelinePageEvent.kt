package com.xiaoyv.bangumi.features.timeline.page.business

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
    }
}