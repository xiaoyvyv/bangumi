package com.xiaoyv.bangumi.features.pixiv.user.main.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [PixivUserEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed interface PixivUserEvent {
    sealed interface UI : PixivUserEvent {
        data object OnNavUp : UI
        data class OnNavScreen(val screen: Screen) : UI
    }

    sealed interface Action : PixivUserEvent {
        data class OnRefresh(val loading: Boolean = false) : Action
    }
}
