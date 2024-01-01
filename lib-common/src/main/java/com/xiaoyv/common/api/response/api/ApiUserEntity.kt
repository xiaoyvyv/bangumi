package com.xiaoyv.common.api.response.api

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Class: [ApiUserEntity]
 *
 * @author why
 * @since 1/1/24
 */
@Keep
@Parcelize
data class ApiUserEntity(
    @SerializedName("avatar")
    var avatar: Avatar? = null,
    @SerializedName("id")
    var id: Long = 0,
    @SerializedName("nickname")
    var nickname: String? = null,
    @SerializedName("sign")
    var sign: String? = null,
    @SerializedName("url")
    var url: String? = null,
    @SerializedName("user_group")
    var userGroup: Int = 0,
    @SerializedName("username")
    var username: String? = null,
) : Parcelable {

    @Keep
    @Parcelize
    data class Avatar(
        @SerializedName("large")
        var large: String? = null,
        @SerializedName("medium")
        var medium: String? = null,
        @SerializedName("small")
        var small: String? = null,
    ) : Parcelable
}