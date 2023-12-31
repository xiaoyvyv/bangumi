package com.xiaoyv.common.api.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Class: [UserEntity]
 *
 * @author why
 * @since 11/26/23
 */
@Keep
@Parcelize
data class UserEntity(
    @SerializedName("avatar")
    var avatar: Avatar? = null,
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("nickname")
    var nickname: String? = null,
    @SerializedName("sign")
    var sign: String? = null,
    @SerializedName("roomPic")
    var roomPic: String? = null,
    @SerializedName("summary")
    var summary: String? = null,
    @SerializedName("user_group")
    var userGroup: Int = 0,
    @SerializedName("username")
    var username: String? = null,

    @SerializedName("formHash")
    var formHash: String? = null,
    @SerializedName("online")
    var online: String? = null,
    @SerializedName("isEmpty")
    var isEmpty: Boolean = false,
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