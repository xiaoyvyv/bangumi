package com.xiaoyv.bangumi.features.settings.privacy.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel

/**
 * [SettingsPrivacyViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class SettingsPrivacyViewModel :
    BaseViewModel<SettingsPrivacyState, SettingsPrivacySideEffect, SettingsPrivacyEvent>() {

    override fun createInitialState() = SettingsPrivacyState()

    override fun onEvent(event: SettingsPrivacyEvent) {

    }

}