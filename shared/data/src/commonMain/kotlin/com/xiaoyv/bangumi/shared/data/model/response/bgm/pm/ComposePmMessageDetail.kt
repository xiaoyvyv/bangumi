package com.xiaoyv.bangumi.shared.data.model.response.bgm.pm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposePmMessageDetail(
    @SerialName("user") val user: ComposeUser = ComposeUser.Empty,
    @SerialName("threads") val threads: SerializeList<ComposePmThread> = persistentListOf(),
    @SerialName("messages") val messages: SerializeList<ComposePmMessage> = persistentListOf(),
    @SerialName("currentThread") val currentThread: ComposePmThread = ComposePmThread.Empty,
    @SerialName("form") val inputs: SerializeMap<String, String> = persistentMapOf()
) {
    companion object {
        val Empty = ComposePmMessageDetail()
    }
}
