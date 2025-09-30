package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.CommentType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.utils.Node
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Immutable
@Serializable
data class ComposeComment(
    @SerialName("star") val star: Double = 0.0,
    @SerialName("user") val user: ComposeUser = ComposeUser.Empty,
    @SerialName("monoType") val monoType: Int = MonoType.UNKNOWN,
    @SerialName("type") val type: Int = CommentType.UNKNOWN,

    /**
     * - 这个ID针对人物或条目底部的吐槽是对应的用户 `data-item-user`
     * - 针对帖子评论才是评论ID `post_xxx`
     */
    @SerialName("id") val id: String = "",
    @SerialName("comment") val comment: String = "",
    @SerialName("floor") val floor: String = "",
    @SerialName("time") val time: String = "",
    @SerialName("replyParma") val replyParam: ComposeCommentSubParam = ComposeCommentSubParam.Empty,
    @SerialName("replyQuote") val replyQuote: String = "",
    @SerialName("emojiParam") val emojiParam: ComposeEmojiParam = ComposeEmojiParam(),
    @SerialName("anchored") val anchored: Boolean = false,

    /**
     * 针对媒体条目的底部吐槽
     */
    @SerialName("subjectType") val subjectType: Int = SubjectType.UNKNOWN,
    @SerialName("collectType") val collectType: Int = CollectionType.UNKNOWN,

    @SerialName("parent") val parent: ComposeComment? = null,

    /**
     * 子评论
     */
    @SerialName("children") override val children: SerializeList<ComposeComment> = persistentListOf(),


    @Transient
    val commentHtml: AnnotatedString = AnnotatedString(""),
) : Node<ComposeComment> {

    companion object {
        val Empty: ComposeComment = ComposeComment()
    }
}


@Serializable
data class ComposeCommentReply(
    @SerialName("content") val content: String = "",
    @SerialName("createdAt") val createdAt: Long = 0,
    @SerialName("creator") val creator: ComposeUser = ComposeUser.Empty,
    @SerialName("creatorID") val creatorID: Long = 0,
    @SerialName("id") val id: Long = 0,
    @SerialName("reactions") val reactions: SerializeList<ComposeReaction> = persistentListOf(),
    @SerialName("replies") val replies: SerializeList<ComposeCommentReply> = persistentListOf(),
    @SerialName("state") val state: Int = 0,
)
