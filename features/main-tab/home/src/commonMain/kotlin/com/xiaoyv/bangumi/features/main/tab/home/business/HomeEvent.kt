package com.xiaoyv.bangumi.features.main.tab.home.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [HomeEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class HomeEvent {
    sealed class UI : HomeEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : HomeEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data object OnRefreshIndexHomepage : Action()
    }
}