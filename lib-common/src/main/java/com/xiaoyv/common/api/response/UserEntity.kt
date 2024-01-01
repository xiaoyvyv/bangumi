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
    var avatar: String = "",
    @SerializedName("userId")
    var id: String = "",
    @SerializedName("nickname")
    var nickname: String = "",
    @SerializedName("sign")
    var sign: String = "",
    @SerializedName("roomPic")
    var roomPic: String = "",
    @SerializedName("summary")
    var summary: String = "",
    @SerializedName("username")
    var username: String = "",
    @SerializedName("formHash")
    var formHash: String = "",
    @SerializedName("online")
    var online: String = "",
    @SerializedName("isEmpty")
    var isEmpty: Boolean = false,
) : Parcelable