package com.xiaoyv.bangumi.features.pixiv.user.setting.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [PixivUserSettingEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed interface PixivUserSettingEvent {
    sealed interface UI : PixivUserSettingEvent {
        data object OnNavUp : UI
        data class OnNavScreen(val screen: Screen) : UI
    }

    sealed interface Action : PixivUserSettingEvent {
        data class OnRefresh(val loading: Boolean = false) : Action
    }
}
