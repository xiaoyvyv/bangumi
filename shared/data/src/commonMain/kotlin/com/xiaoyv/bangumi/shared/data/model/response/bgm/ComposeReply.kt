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

private const val REPLY_STATE_NORMAL = 0
private const val REPLY_STATE_DELETED = 6

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
            content = when {
                state != REPLY_STATE_NORMAL && content.isEmpty() -> {
                    "评论不见了（Code:$state）"
                }

                else -> content.bbcodeToHtml()
            },
            replies = replies.normalizedReplies()
        )
    }

    override val children: SerializeList<ComposeReply> get() = replies

    companion object {
        val Empty = ComposeReply()
    }
}

/**
 * 标准化评论树，并过滤服务端标记为删除的记录。
 *
 * 被删除评论仍有有效子回复时，子回复会上提到当前层级，避免一并丢失。
 * 非正常状态的空内容评论会保留，并使用状态码生成占位文案。
 *
 * @return 已转换内容且不包含删除状态节点的评论列表。
 */
fun Iterable<ComposeReply>.normalizedReplies(): SerializeList<ComposeReply> {
    return flatMap { reply ->
        if (reply.state == REPLY_STATE_DELETED) {
            reply.replies.normalizedReplies()
        } else {
            listOf(reply.normalized())
        }
    }.toImmutableList()
}
