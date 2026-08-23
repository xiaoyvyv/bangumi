package com.xiaoyv.bangumi.features.pixiv.user.main.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivUserInfoBody

/**
 * [PixivUserState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class PixivUserState(
    val userId: Long = 0,
    val userInfo: ComposePixivUserInfoBody = ComposePixivUserInfoBody.Empty,
)
