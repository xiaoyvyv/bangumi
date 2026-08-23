package com.xiaoyv.bangumi.features.pixiv.illust.main.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [PixivIllustEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class PixivIllustEvent {
    sealed class UI : PixivIllustEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : PixivIllustEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}
