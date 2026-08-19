package com.xiaoyv.bangumi.shared.ui.component.dialog.comment

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.shared.core.types.TopicType
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply


@Immutable
data class CommentState(
    val anchor: CommentDialogAnchor = CommentDialogAnchor.Empty,
    val comment: TextFieldValue = TextFieldValue(),
    val sending: Boolean = false,
    val turnstile: String = ""
)

@Immutable
data class CommentDialogAnchor(
    @TopicType val targetType: String,
    val targetId: Long,
    val reply: ComposeReply = ComposeReply.Empty,
) {
    val key = targetType + "-" + targetId + "-" + reply.id

    companion object {
        val Empty = CommentDialogAnchor(TopicType.TYPE_UNKNOWN, 0)
    }
}