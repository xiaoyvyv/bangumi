package com.xiaoyv.bangumi.shared.data.model.response.pixiv

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Access Token:
 *
 * - This is the short-lived key used to access protected resources on the server. It typically expires after a short period (e.g., an hour) for security reasons.
 *
 * Refresh Token:
 *
 * - This is a longer-lived token (e.g., days or weeks) used to acquire new access tokens when the original one expires. It's stored securely by the application.
 */
@Immutable
@Serializable
data class ComposePixivToken(
    @SerialName("access_token") val accessToken: String = "",
    @SerialName("token_type") val tokenType: String = "",
    @SerialName("scope") val scope: String = "",
    @SerialName("refresh_token") val refreshToken: String = "",
    @SerialName("device_token") val deviceToken: String = "",
    @SerialName("local_user") val localUser: String = "",
    @SerialName("expires_in") val expiresIn: Long = 0,

    /**
     * 本地填充，过期毫秒时间戳
     */
    @SerialName("expires_at") val expiresAt: Long = 0,
) {
    companion object {
        val Empty = ComposePixivToken()
    }
}
