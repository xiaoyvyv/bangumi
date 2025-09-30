package com.xiaoyv.bangumi.features.main.tab.home.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [CalendarEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class CalendarEvent {
    sealed class UI : CalendarEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : CalendarEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data object OnChangeLayoutMode : Action()
    }
}