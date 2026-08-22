package com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ComposePixivCommentReply(
    @SerialName("id") val id: Long = 0,
    @SerialName("userId") val userId: Long = 0,
    @SerialName("userName") val userName: String = "",
    @SerialName("isDeletedUser") val isDeletedUser: Boolean = false,
    @SerialName("img") val img: String = "",
    @SerialName("comment") val comment: String = "",
    @SerialName("stampId") val stampId: Long = 0,
    @SerialName("stampLink") val stampLink: String = "",
    @SerialName("commentDate") val commentDate: String = "",
    @SerialName("commentRootId") val commentRootId: Long = 0,
    @SerialName("commentParentId") val commentParentId: Long = 0,
    @SerialName("commentUserId") val commentUserId: Long = 0,
    @SerialName("replyToUserId") val replyToUserId: Long = 0,
    @SerialName("replyToUserName") val replyToUserName: String = "",
    @SerialName("editable") val editable: Boolean = false,
    @SerialName("hasReplies") val hasReplies: Boolean = false
) {
    companion object {
        val Empty = ComposePixivCommentReply()
    }
}

@Immutable
@Serializable
data class ComposePixivCommentsBody(
    @SerialName("comments") val comments: SerializeList<ComposePixivCommentReply> = persistentListOf(),
    @SerialName("hasNext") val hasNext: Boolean = false
) {
    companion object {
        val Empty = ComposePixivCommentsBody()
    }
}

@Immutable
@Serializable
data class ComposePixivPostCommentBody(
    @SerialName("comment_id") val commentId: Long = 0,
    @SerialName("comment") val comment: String = "",
    @SerialName("user_id") val userId: Long = 0,
    @SerialName("user_name") val userName: String = "",
    @SerialName("stamp_id") val stampId: Long = 0,
    @SerialName("parent_id") val parentId: Long = 0
) {
    companion object {
        val Empty = ComposePixivPostCommentBody()
    }
}
