package com.xiaoyv.common.api.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Class: [ReplyResultEntity]
 *
 * @author why
 * @since 12/2/23
 */
@Keep
@Parcelize
data class ReplyResultEntity(
    @SerializedName("posts")
    var posts: Posts? = null,
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("timestamp")
    var timestamp: Long = 0
) : Parcelable {

    @Keep
    @Parcelize
    data class Posts(
        @SerializedName("sub")
        var sub: Map<String, List<CommentEntity>>? = null
    ) : Parcelable

    @Keep
    @Parcelize
    data class CommentEntity(
        @SerializedName("avatar")
        var avatar: String? = null,
        @SerializedName("dateline")
        var dateline: String? = null,
        @SerializedName("is_self")
        var isSelf: Boolean = false,
        @SerializedName("model")
        var model: String? = null,
        @SerializedName("nickname")
        var nickname: String? = null,
        @SerializedName("pst_content")
        var pstContent: String? = null,
        @SerializedName("pst_id")
        var pstId: String? = null,
        @SerializedName("pst_mid")
        var pstMid: String? = null,
        @SerializedName("pst_uid")
        var pstUid: String? = null,
        @SerializedName("sign")
        var sign: String? = null,
        @SerializedName("username")
        var username: String? = null
    ) : Parcelable
}