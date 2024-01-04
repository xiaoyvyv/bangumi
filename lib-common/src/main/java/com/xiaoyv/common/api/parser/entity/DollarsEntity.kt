package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import android.webkit.URLUtil
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize


/**
 * Class: [DollarsEntity]
 *
 * @author why
 * @since 1/4/24
 */
@Keep
@Parcelize
data class DollarsEntity(
    @SerializedName("id")
    override var id: String = "",
    @SerializedName("avatar")
    var avatar: String? = null,
    @SerializedName("color")
    var color: String? = null,
    @SerializedName("is_html")
    var isHtml: Int = 0,
    @SerializedName("msg")
    var msg: String? = null,
    @SerializedName("nickname")
    var nickname: String? = null,
    @SerializedName("timestamp")
    var timestamp: Long = 0,
    @SerializedName("uid")
    var uid: Long = 0,

    var isSending: Boolean = false,
) : IdEntity, Parcelable {
    val showAvatar: String
        get() = when {
            isMine -> UserHelper.currentUser.avatar
            URLUtil.isNetworkUrl(avatar.orEmpty()) -> avatar.orEmpty()
            else -> "https://lain.bgm.tv/pic/user/l/" + avatar.orEmpty()
        }

    val isMine: Boolean
        get() = uid.toString() == UserHelper.currentUser.id || nickname == UserHelper.currentUser.nickname
}
