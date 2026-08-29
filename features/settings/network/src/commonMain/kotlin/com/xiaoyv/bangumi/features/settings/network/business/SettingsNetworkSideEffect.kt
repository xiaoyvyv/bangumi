package com.xiaoyv.bangumi.features.settings.network.business

/**
 * [SettingsNetworkSideEffect]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class SettingsNetworkSideEffect {
    data object OnRefreshUpdateInfo : SettingsNetworkSideEffect()
}