package com.xiaoyv.common.api.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Class: [AuthTokenEntity]
 *
 * 注意 expiresIn 是秒级
 *
 * @author why
 * @since 12/24/23
 */
@Keep
@Parcelize
data class AuthTokenEntity(
    @SerializedName("access_token")
    var accessToken: String? = null,
    @SerializedName("expires_in")
    var expiresIn: Long = 0,
    @SerializedName("refresh_token")
    var refreshToken: String? = null,
    @SerializedName("token_type")
    var tokenType: String? = null,
    @SerializedName("user_id")
    var userId: Long = 0,
    @SerializedName("saveAt")
    var saveAt: Long = System.currentTimeMillis(),
) : Parcelable {

    /**
     * 是否过期
     */
    val isExpire: Boolean
        get() {
            if (accessToken.isNullOrBlank()) return true
            if (refreshToken.isNullOrBlank()) return true
            return System.currentTimeMillis() > (saveAt + expiresIn * 1000)
        }
}
