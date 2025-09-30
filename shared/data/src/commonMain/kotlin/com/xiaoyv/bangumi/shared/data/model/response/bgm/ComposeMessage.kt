package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import com.xiaoyv.bangumi.shared.core.types.MessageBoxType
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Immutable
@Serializable
data class ComposeMessage(
    val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val time: String = "",
    val user: ComposeUser = ComposeUser.Empty,
    val unread: Boolean = false,
    @field:MessageBoxType val type: String = MessageBoxType.TYPE_INBOX,

    @Transient
    val contentHtml: AnnotatedString = AnnotatedString(""),
)
