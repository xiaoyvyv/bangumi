package com.xiaoyv.bangumi.features.main.tab.newest.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [NewestEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class NewestEvent {
    sealed class UI : NewestEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : NewestEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data class OnChangeYear(val year: Int) : Action()
    }
}