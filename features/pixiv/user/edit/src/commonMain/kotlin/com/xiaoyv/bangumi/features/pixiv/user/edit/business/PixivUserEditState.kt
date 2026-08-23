package com.xiaoyv.bangumi.features.pixiv.user.edit.business

import androidx.compose.runtime.Immutable

/**
 * [PixivUserEditState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class PixivUserEditState(
    val displayName: String = "",
    val introduction: String = "",
    val website: String = "",
)
