package com.xiaoyv.bangumi.features.settings.block.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel

/**
 * [SettingsBlockViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class SettingsBlockViewModel(savedStateHandle: SavedStateHandle) :
    BaseViewModel<SettingsBlockState, SettingsBlockSideEffect, SettingsBlockEvent>(savedStateHandle) {

    override fun initSate(onCreate: Boolean) = SettingsBlockState()

    override fun onEvent(event: SettingsBlockEvent) {

    }

}