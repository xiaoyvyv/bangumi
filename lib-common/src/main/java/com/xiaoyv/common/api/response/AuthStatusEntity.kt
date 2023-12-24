package com.xiaoyv.common.api.response
import android.os.Parcelable

import kotlinx.parcelize.Parcelize

import androidx.annotation.Keep

import com.google.gson.annotations.SerializedName


/**
 * Class: [AuthStatusEntity]
 *
 * @author why
 * @since 12/24/23
 */
@Keep
@Parcelize
data class AuthStatusEntity(
    @SerializedName("access_token")
    var accessToken: String? = null,
    @SerializedName("client_id")
    var clientId: String? = null,
    @SerializedName("expires")
    var expires: Int = 0,
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("info")
    var info: String? = null,
    @SerializedName("type")
    var type: String? = null,
    @SerializedName("user_id")
    var userId: Int = 0
) : Parcelable