package com.xiaoyv.bangumi.features.settings.privacy.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [SettingsPrivacyEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class SettingsPrivacyEvent {
    sealed class UI : SettingsPrivacyEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : SettingsPrivacyEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}