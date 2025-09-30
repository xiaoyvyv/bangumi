package com.xiaoyv.bangumi.features.mono.page.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [MonoPageEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class MonoPageEvent {
    sealed class UI : MonoPageEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : MonoPageEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}