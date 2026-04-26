package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.CommentType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.utils.Node
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeDateLong
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.data.parser.bbcode.parseAsBbcode
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonNames

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

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ComposeReply(
    @SerialName("content") val content: String = "",
    @SerialName("createdAt") val createdAt: SerializeDateLong = 0,
    @SerialName("creator") @JsonNames("creator", "user") val user: ComposeUser = ComposeUser.Empty,
    @SerialName("creatorID") val creatorID: Long = 0,
    @SerialName("id") val id: Long = 0,
    @SerialName("mainID") val mainID: Long = 0,
    @SerialName("relatedID") val relatedID: Long = 0,
    @SerialName("reactions") val reactions: SerializeList<ComposeReaction> = persistentListOf(),
    @SerialName("replies") val replies: SerializeList<ComposeReply> = persistentListOf(),
    @SerialName("state") val state: Int = 0
) : Node<ComposeReply> {
    @Transient
    val displayContent = if (content.startsWith("[quote]")) {
        content.substringAfter("[/quote]").trim().parseAsBbcode()
    } else {
        content.trim().parseAsBbcode()
    }

    @Transient
    val displayQuote = if (content.startsWith("[quote]")) {
        content
            .substringBefore("[/quote]")
            .substringAfter("[quote]")
            .trim()
            .parseAsBbcode()
    } else {
        AnnotatedString("")
    }

    override val children: SerializeList<ComposeReply> get() = replies
}
