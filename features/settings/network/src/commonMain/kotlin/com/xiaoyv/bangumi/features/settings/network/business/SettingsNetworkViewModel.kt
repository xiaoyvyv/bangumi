package com.xiaoyv.bangumi.features.settings.network.business

import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.settings_reboot_active
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.postEffect
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import org.jetbrains.compose.resources.getString

/**
 * [SettingsNetworkViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class SettingsNetworkViewModel(
    private val userManager: UserManager,
) : BaseViewModel<SettingsNetworkState, SettingsNetworkSideEffect, SettingsNetworkEvent.Action>() {

    override fun createInitialState() = SettingsNetworkState()

    override fun onEvent(event: SettingsNetworkEvent.Action) {
        when (event) {
            is SettingsNetworkEvent.Action.OnRefresh -> refresh(event.loading)
            is SettingsNetworkEvent.Action.OnUpdate -> onUpdateConfig(event.settings)
        }
    }


    private fun onUpdateConfig(settings: ComposeSetting.NetworkConfig) = intent {
        userManager.updateSettings {
            it.copy(network = settings)
        }

        postToast { getString(Res.string.settings_reboot_active) }
        postEffect { SettingsNetworkSideEffect.OnRefreshUpdateInfo }
    }
}