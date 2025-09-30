package com.xiaoyv.bangumi.features.settings.network.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.settings_reboot_active
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
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
    savedStateHandle: SavedStateHandle,
    private val userManager: UserManager,
) : BaseViewModel<SettingsNetworkState, SettingsNetworkSideEffect, SettingsNetworkEvent.Action>(savedStateHandle) {

    override fun initSate(onCreate: Boolean) = SettingsNetworkState()

    override fun onEvent(event: SettingsNetworkEvent.Action) {
        when (event) {
            is SettingsNetworkEvent.Action.OnRefresh -> refresh(event.loading)
            is SettingsNetworkEvent.Action.OnUpdate -> onUpdateConfig(event.settings)
        }
    }


    private fun onUpdateConfig(settings: ComposeSetting.NetworkConfig) = action {
        userManager.updateSettings {
            it.copy(network = settings)
        }

        postToast { getString(Res.string.settings_reboot_active) }
    }
}