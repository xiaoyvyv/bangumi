package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Immutable
@Serializable
data class ComposeUnRead(
    /**
     * 未登录会返回 null
     */
    @SerialName("count") val count: Int? = null,

    @SerialName("notify_count") val notifyCount: Int = 0,
    @SerialName("notify_ignore_url") val notifyIgnoreUrl: String = "",
    @SerialName("pm_count") val pmCount: Int = 0,
    @SerialName("pm_ignore_url") val pmIgnoreUrl: String = "",
    @SerialName("pm_list") val pmList: SerializeList<Pm> = persistentListOf(),
    @SerialName("pm_url") val pmUrl: String = ""
) {
    val total get() = notifyCount + pmCount

    @Immutable
    @Serializable
    data class Pm(
        @SerialName("conversation_id") val conversationId: Long = 0,
        @SerialName("peer_name") val peerName: String = "",
        @SerialName("peer_uid") val peerUid: Long = 0,
        @SerialName("title") val title: String = "",
        @SerialName("unread_count") val unreadCount: Int = 0,
        @SerialName("url") val url: String = ""
    )

    companion object {
        val Empty = ComposeUnRead()
    }
}
