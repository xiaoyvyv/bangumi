package com.xiaoyv.bangumi.features.main.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [MainEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class MainEvent {
    sealed class UI : MainEvent() {
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : MainEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}