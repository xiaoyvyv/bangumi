package com.xiaoyv.bangumi.features.settings.live2d.business

import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [SettingsLive2dEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class SettingsLive2dEvent {
    sealed class UI : SettingsLive2dEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : SettingsLive2dEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data class OnUpdate(val settings: ComposeSetting.Live2dConfig) : Action()
    }
}