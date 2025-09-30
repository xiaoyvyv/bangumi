package com.xiaoyv.bangumi.features.settings.live2d.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting

/**
 * [SettingsLive2dViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class SettingsLive2dViewModel(
    savedStateHandle: SavedStateHandle,
    private val userManager: UserManager,
) : BaseViewModel<SettingsLive2dState, SettingsLive2dSideEffect, SettingsLive2dEvent.Action>(savedStateHandle) {

    override fun initSate(onCreate: Boolean) = SettingsLive2dState()

    override fun onEvent(event: SettingsLive2dEvent.Action) {
        when (event) {
            is SettingsLive2dEvent.Action.OnRefresh -> refresh(event.loading)
            is SettingsLive2dEvent.Action.OnUpdate -> onUpdateConfig(event.settings)
        }
    }

    private fun onUpdateConfig(settings: ComposeSetting.Live2dConfig) = action {
        userManager.updateSettings {
            it.copy(live2d = settings)
        }
    }
}