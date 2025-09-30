package com.xiaoyv.bangumi.features.settings.main.business

import androidx.compose.runtime.Immutable

/**
 * [SettingsMainState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class SettingsMainState(
    val cacheSize: String = "",
)
