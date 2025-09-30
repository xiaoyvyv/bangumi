package com.xiaoyv.bangumi.features.settings.ui.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting

/**
 * [SettingsUiViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class SettingsUiViewModel(
    savedStateHandle: SavedStateHandle,
    private val userManager: UserManager,
) : BaseViewModel<SettingsUiState, SettingsUiSideEffect, SettingsUiEvent.Action>(savedStateHandle) {

    override fun initSate(onCreate: Boolean) = SettingsUiState()

    override fun onEvent(event: SettingsUiEvent.Action) {
        when (event) {
            is SettingsUiEvent.Action.OnRefresh -> refresh(event.loading)
            is SettingsUiEvent.Action.OnUpdate -> onUpdateConfig(event.settings)
        }
    }

    private fun onUpdateConfig(settings: ComposeSetting.UIConfig) = action {
        userManager.updateSettings {
            it.copy(ui = settings)
        }
    }
}