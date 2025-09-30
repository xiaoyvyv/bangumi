package com.xiaoyv.bangumi.features.settings.bar.business

import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [SettingsBarEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class SettingsBarEvent {
    sealed class UI : SettingsBarEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : SettingsBarEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data class OnUpdate(val settings: ComposeSetting.HomeTabConfig) : Action()
    }
}