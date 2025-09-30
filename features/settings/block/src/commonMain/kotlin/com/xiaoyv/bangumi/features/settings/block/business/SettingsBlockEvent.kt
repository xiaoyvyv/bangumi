package com.xiaoyv.bangumi.features.settings.block.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [SettingsBlockEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class SettingsBlockEvent {
    sealed class UI : SettingsBlockEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : SettingsBlockEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}