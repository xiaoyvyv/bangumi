package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeEmojiParam(
    @SerialName("enable") val enable: Boolean = false,
    @SerialName("likeType") val likeType: String = "",
    @SerialName("likeMainId") val likeMainId: String = "",
    @SerialName("likeCommentId") val likeCommentId: String = "",
) {
    companion object {
        val Empty = ComposeEmojiParam()
    }
}
