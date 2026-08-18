package com.xiaoyv.bangumi.features.settings.translate.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel

/**
 * [SettingsTranslateViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class SettingsTranslateViewModel :
    BaseViewModel<SettingsTranslateState, SettingsTranslateSideEffect, SettingsTranslateEvent>() {

    override fun createInitialState() = SettingsTranslateState()

    override fun onEvent(event: SettingsTranslateEvent) {

    }

}