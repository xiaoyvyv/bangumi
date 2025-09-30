package com.xiaoyv.bangumi.shared.ui.component.dialog.comment

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeComment
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTopicDetail


@Immutable
data class CommentState(
    val anchor: CommentDialogAnchor = CommentDialogAnchor.Empty,
    val comment: TextFieldValue = TextFieldValue(),
    val sending: Boolean = false,
)

@Immutable
data class CommentDialogAnchor(
    val lastViewedInMillis: Long,
    val article: ComposeTopicDetail = ComposeTopicDetail.Empty,
    val reply: ComposeComment = ComposeComment.Empty,
) {
    val key = article.id.toString() + reply.id + lastViewedInMillis

    companion object {
        val Empty = CommentDialogAnchor(lastViewedInMillis = 0)
    }
}