package com.xiaoyv.bangumi.features.pixiv.user.edit.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [PixivUserEditEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed interface PixivUserEditEvent {
    sealed interface UI : PixivUserEditEvent {
        data object OnNavUp : UI
        data class OnNavScreen(val screen: Screen) : UI
    }

    sealed interface Action : PixivUserEditEvent {
        data class OnRefresh(val loading: Boolean = false) : Action
        data class OnDisplayNameChanged(val value: String) : Action
        data class OnIntroductionChanged(val value: String) : Action
        data class OnWebsiteChanged(val value: String) : Action
    }
}
