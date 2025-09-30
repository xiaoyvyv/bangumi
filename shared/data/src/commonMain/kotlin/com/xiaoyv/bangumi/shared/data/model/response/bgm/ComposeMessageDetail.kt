package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposeMessageDetail(
    val title: String = "",
    val msgReceivers: String = "",
    val related: String = "",
    val currentMsgId: String = "",
    val canReply: Boolean = false,
    val messages: SerializeList<ComposeMessage> = persistentListOf(),
) {
    companion object {
        val Empty = ComposeMessageDetail()
    }
}