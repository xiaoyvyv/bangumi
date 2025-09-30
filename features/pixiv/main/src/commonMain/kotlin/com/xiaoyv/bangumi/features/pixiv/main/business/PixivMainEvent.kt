package com.xiaoyv.bangumi.features.pixiv.main.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [PixivMainEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class PixivMainEvent {
    sealed class UI : PixivMainEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : PixivMainEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}