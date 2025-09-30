package com.xiaoyv.bangumi.features.settings.translate.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel

/**
 * [SettingsTranslateViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class SettingsTranslateViewModel(savedStateHandle: SavedStateHandle) :
    BaseViewModel<SettingsTranslateState, SettingsTranslateSideEffect, SettingsTranslateEvent>(savedStateHandle) {

    override fun initSate(onCreate: Boolean) = SettingsTranslateState()

    override fun onEvent(event: SettingsTranslateEvent) {

    }

}