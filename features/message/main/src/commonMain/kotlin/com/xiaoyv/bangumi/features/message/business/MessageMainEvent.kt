package com.xiaoyv.bangumi.features.message.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [MessageMainEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class MessageMainEvent {
    sealed class UI : MessageMainEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : MessageMainEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}