package com.xiaoyv.bangumi.features.almanac.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [AlmanacEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class AlmanacEvent {
    sealed class UI : AlmanacEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : AlmanacEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}