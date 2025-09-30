package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.System
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Class: [ComposeAuthToken]
 *
 * 注意 expiresIn 是秒级
 *
 * @author why
 * @since 12/24/23
 */
@Immutable
@Serializable
data class ComposeAuthToken(
    @SerialName("access_token")
    val accessToken: String = "",
    @SerialName("expires_in")
    val expiresIn: Long = 0,
    @SerialName("refresh_token")
    val refreshToken: String = "",
    @SerialName("token_type")
    val tokenType: String = "",
    @SerialName("user_id")
    val userId: Long = 0,
    @SerialName("saveAt")
    val saveAt: Long = System.currentTimeMillis(),
) {
    companion object {
        val Empty: ComposeAuthToken = ComposeAuthToken()
    }

    /**
     * 是否过期
     */
    val isExpire: Boolean
        get() {
            if (accessToken.isBlank()) return true
            if (refreshToken.isBlank()) return true
            return System.currentTimeMillis() > (saveAt + expiresIn * 1000)
        }
}