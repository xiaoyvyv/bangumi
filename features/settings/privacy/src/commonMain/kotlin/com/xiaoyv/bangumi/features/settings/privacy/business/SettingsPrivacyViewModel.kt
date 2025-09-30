package com.xiaoyv.bangumi.features.settings.privacy.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel

/**
 * [SettingsPrivacyViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class SettingsPrivacyViewModel(savedStateHandle: SavedStateHandle) :
    BaseViewModel<SettingsPrivacyState, SettingsPrivacySideEffect, SettingsPrivacyEvent>(savedStateHandle) {

    override fun initSate(onCreate: Boolean) = SettingsPrivacyState()

    override fun onEvent(event: SettingsPrivacyEvent) {

    }

}