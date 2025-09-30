package com.xiaoyv.bangumi.features.settings.network.business

import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [SettingsNetworkEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class SettingsNetworkEvent {
    sealed class UI : SettingsNetworkEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : SettingsNetworkEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data class OnUpdate(val settings: ComposeSetting.NetworkConfig) : Action()
    }
}