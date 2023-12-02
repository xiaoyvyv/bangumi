package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Keep
@Parcelize
data class CommentFormEntity(
    @SerializedName("inputs")
    var inputs: MutableMap<String, String> = mutableMapOf(),
    @SerializedName("action")
    var action: String = ""
) : Parcelable {
    val isEmpty: Boolean
        get() = action.isBlank() && inputs.isEmpty()

    @Keep
    @Parcelize
    data class CommentParam(
        @SerializedName("type")
        var type: String = "",
        @SerializedName("topic_id")
        var topicId: Long = 0,
        @SerializedName("post_id")
        var postId: Long = 0,
        @SerializedName("sub_reply_id")
        var subReplyId: Long = 0,
        @SerializedName("sub_reply_uid")
        var subReplyUid: Long = 0,
        @SerializedName("post_uid")
        var postUid: Long = 0,
        @SerializedName("sub_post_type")
        var subPostType: Long = 0,
    ) : Parcelable
}