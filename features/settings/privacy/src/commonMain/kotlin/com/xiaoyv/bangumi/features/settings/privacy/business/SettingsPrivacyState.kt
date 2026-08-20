package com.xiaoyv.bangumi.features.settings.privacy.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUserPrivacy

/**
 * [SettingsPrivacyState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class SettingsPrivacyState(
    val privacy: ComposeUserPrivacy = ComposeUserPrivacy.Empty,
    val loading: Boolean = false,
)
