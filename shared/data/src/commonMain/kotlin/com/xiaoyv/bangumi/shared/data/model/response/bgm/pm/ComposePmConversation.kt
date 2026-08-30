package com.xiaoyv.bangumi.shared.data.model.response.bgm.pm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposePmConversation(
    val id: Long = 0,
    val content: String = "",
    val time: String = "",
    val user: ComposeUser = ComposeUser.Empty,
    val unread: Int = 0,
)
