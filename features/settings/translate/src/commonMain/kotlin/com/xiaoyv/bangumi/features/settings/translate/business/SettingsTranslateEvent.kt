package com.xiaoyv.bangumi.features.settings.translate.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [SettingsTranslateEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class SettingsTranslateEvent {
    sealed class UI : SettingsTranslateEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : SettingsTranslateEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}