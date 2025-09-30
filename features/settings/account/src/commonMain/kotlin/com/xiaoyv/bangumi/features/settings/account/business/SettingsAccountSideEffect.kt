package com.xiaoyv.bangumi.features.settings.account.business

/**
 * [SettingsAccountSideEffect]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class SettingsAccountSideEffect {
    data object OnNavUp : SettingsAccountSideEffect()
}