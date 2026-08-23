package com.xiaoyv.bangumi.shared.data.model.response.pixiv

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [ComposePixivToken] Pixiv Token 响应数据
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
@Serializable
data class ComposePixivToken(
    @SerialName("access_token") val accessToken: String = "",
    @SerialName("token_type") val tokenType: String = "",
    @SerialName("scope") val scope: String = "",
    @SerialName("refresh_token") val refreshToken: String = "",
    @SerialName("device_token") val deviceToken: String = "",
    @SerialName("user") val user: ComposePixivUser = ComposePixivUser.Empty,
    @SerialName("response") val response: ComposePixivToken? = null,
    @SerialName("expires_in") val expiresIn: Long = 0,

    // 本地填充，过期毫秒时间戳
    @SerialName("expires_at") val expiresAt: Long = 0,
) {
    // 优先读取根节点 user，若空则尝试读取 response.user
    val currentUser: ComposePixivUser
        get() = if (user.id.isNotBlank()) user else (response?.user ?: ComposePixivUser.Empty)

    companion object {
        val Empty = ComposePixivToken()
    }
}