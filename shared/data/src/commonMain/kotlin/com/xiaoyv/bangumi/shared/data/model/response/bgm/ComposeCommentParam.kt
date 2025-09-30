package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Immutable
@Serializable
data class ComposeCommentParam(
    @SerialName("inputs")
    val inputs: Map<String, String> = emptyMap(),
    @SerialName("action")
    val action: String = "",
) {
    val isEmpty: Boolean
        get() = action.isBlank() && inputs.isEmpty()

    companion object Companion {
        val Empty = ComposeCommentParam()
    }
}

@Immutable
@Serializable
data class ComposeCommentSubParam(
    @SerialName("type")
    val type: String = "",
    @SerialName("topic_id")
    val topicId: Long = 0,
    @SerialName("post_id")
    val postId: Long = 0,
    @SerialName("sub_reply_id")
    val subReplyId: Long = 0,
    @SerialName("sub_reply_uid")
    val subReplyUid: Long = 0,
    @SerialName("post_uid")
    val postUid: Long = 0,
    @SerialName("sub_post_type")
    val subPostType: Long = 0,
) {

    companion object Companion {
        val Empty = ComposeCommentSubParam()
    }
}
