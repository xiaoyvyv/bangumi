package com.xiaoyv.bangumi.features.settings.bar.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting

/**
 * [SettingsBarViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class SettingsBarViewModel(
    savedStateHandle: SavedStateHandle,
    private val userManager: UserManager,
) : BaseViewModel<SettingsBarState, SettingsBarSideEffect, SettingsBarEvent.Action>(savedStateHandle) {

    override fun initSate(onCreate: Boolean) = SettingsBarState()

    override fun onEvent(event: SettingsBarEvent.Action) {
        when (event) {
            is SettingsBarEvent.Action.OnRefresh -> refresh(event.loading)
            is SettingsBarEvent.Action.OnUpdate -> onUpdateConfig(event.settings)
        }
    }

    private fun onUpdateConfig(settings: ComposeSetting.HomeTabConfig) = action {
        userManager.updateSettings {
            it.copy(homeTab = settings)
        }
    }
}