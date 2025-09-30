package com.xiaoyv.bangumi.features.friend.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [FriendEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class FriendEvent {
    sealed class UI : FriendEvent() {
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : FriendEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}