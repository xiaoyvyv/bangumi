package com.xiaoyv.bangumi.features.settings.block.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel

/**
 * [SettingsBlockViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class SettingsBlockViewModel :
    BaseViewModel<SettingsBlockState, SettingsBlockSideEffect, SettingsBlockEvent>() {

    override fun createInitialState() = SettingsBlockState()

    override fun onEvent(event: SettingsBlockEvent) {

    }

}