package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Immutable
@Serializable
data class ComposeNotification(
    /**
     * 未登录会返回 null
     */
    @SerialName("count") val count: Int? = null,

    @SerialName("id") val id: Long = 0,
    @SerialName("unread") val unread: Boolean = false,
    @SerialName("message") val message: String = "",
    @SerialName("link") val link: String = "",
    @SerialName("user") val user: ComposeUser = ComposeUser.Empty,


    @Transient
    val messageHtml: AnnotatedString = AnnotatedString(""),
) {
    companion object {
        val Empty = ComposeNotification()
    }
}
