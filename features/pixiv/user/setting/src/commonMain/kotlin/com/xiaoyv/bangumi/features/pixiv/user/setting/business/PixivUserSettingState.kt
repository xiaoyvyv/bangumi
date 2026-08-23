package com.xiaoyv.bangumi.features.pixiv.user.setting.business

import androidx.compose.runtime.Immutable

/**
 * [PixivUserSettingState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class PixivUserSettingState(
    val showR18: Boolean = false,
    val showAiWorks: Boolean = true,
    val autoplayUgoira: Boolean = true,
)
