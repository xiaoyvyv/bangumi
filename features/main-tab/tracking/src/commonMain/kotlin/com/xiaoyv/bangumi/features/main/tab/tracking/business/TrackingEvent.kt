package com.xiaoyv.bangumi.features.main.tab.tracking.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [TrackingEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class TrackingEvent {
    sealed class UI : TrackingEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : TrackingEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}