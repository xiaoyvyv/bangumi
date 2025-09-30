package com.xiaoyv.bangumi.features.settings.main.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [SettingsMainEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class SettingsMainEvent {
    sealed class UI : SettingsMainEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : SettingsMainEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data object OnLogout : Action()
        data object OnCleanCache : Action()
    }
}