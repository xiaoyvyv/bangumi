package com.xiaoyv.bangumi.features.settings.ui.business

import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [SettingsUiEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class SettingsUiEvent {
    sealed class UI : SettingsUiEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : SettingsUiEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data class OnUpdate(val settings: ComposeSetting.UIConfig) : Action()

    }
}