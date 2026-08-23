package com.xiaoyv.bangumi.features.pixiv.tag.main.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * Events accepted by the Pixiv tag detail page.
 */
sealed interface PixivTagEvent {
    sealed interface UI : PixivTagEvent {
        data object OnNavUp : UI
        data class OnNavScreen(val screen: Screen) : UI
    }

    sealed interface Action : PixivTagEvent {
        data class OnRefresh(val loading: Boolean = false) : Action
    }
}
