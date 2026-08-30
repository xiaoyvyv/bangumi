package com.xiaoyv.bangumi.shared.data.model.response.bgm.pm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposePmMessage(
    @SerialName("msgId") val msgId: Long = 0,
    @SerialName("title") val title: String = "",
    @SerialName("content") val content: String = "",
    @SerialName("time") val time: String = "",
    @SerialName("user") val user: ComposeUser = ComposeUser.Empty,
) {
    val isSubjectTip get() = title.isNotBlank() && content.isBlank() && user == ComposeUser.Empty
    val isContent get() = !isSubjectTip

    companion object {
        val Empty = ComposePmMessage()
    }
}
