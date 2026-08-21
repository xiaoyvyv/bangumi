package com.xiaoyv.bangumi.shared.data.model.response.bgm

import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.utils.Node
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeDateLong
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.manager.bbcodeToHtml
import com.xiaoyv.bangumi.shared.data.model.response.bgm.reaction.ComposeReaction
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ComposeReply(
    @SerialName("id") val id: Long = 0,
    @SerialName("content") @JsonNames("comment") val content: String = "",
    @SerialName("state") val state: Int = 0,
    @SerialName("createdAt") @JsonNames("updatedAt") val createdAt: SerializeDateLong = 0,
    @SerialName("creator") @JsonNames("user") val user: ComposeUser = ComposeUser.Empty,
    @SerialName("creatorID") val creatorID: Long = 0,

    @SerialName("mainID") val mainID: Long = 0,
    @SerialName("relatedPhotoID") val relatedPhotoID: Long = 0,
    @SerialName("relatedID") val relatedID: Long = 0,
    @SerialName("reactions") val reactions: SerializeList<ComposeReaction> = persistentListOf(),
    @SerialName("replies") val replies: SerializeList<ComposeReply> = persistentListOf(),

    /**
     * 条目吐槽的额外数据
     */
    @SerialName("rate") val rate: Double = 0.0,
    @SerialName("type") @CollectionType val type: Int = CollectionType.UNKNOWN
) : Node<ComposeReply> {

    fun updateCommentById(updates: Map<Long, ComposeReply>): ComposeReply {
        val updatedSelf = updates[id] ?: this
        if (updatedSelf.replies.isEmpty()) return updatedSelf
        return updatedSelf.copy(
            replies = updatedSelf.replies
                .map { it.updateCommentById(updates) }
                .toImmutableList()
        )
    }

    fun normalized(): ComposeReply {
        return copy(
            content = content.bbcodeToHtml(),
            replies = replies.map { it.normalized() }.toImmutableList()
        )
    }

    override val children: SerializeList<ComposeReply> get() = replies

    companion object {
        val Empty = ComposeReply()
    }
}
