package com.xiaoyv.bangumi.features.settings.main.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.response.chore.ComposeBangumiStatus

/**
 * [SettingsMainState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class SettingsMainState(
    val cacheSize: String = "",
    val bangumiStatus: ComposeBangumiStatus? = null,
    val statusLoading: Boolean = false,
    val statusFailed: Boolean = false,
)
