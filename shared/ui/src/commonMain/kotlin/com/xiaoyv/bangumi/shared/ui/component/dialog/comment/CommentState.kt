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

/**
 * 评论弹窗的目标对象。
 */
@Immutable
sealed interface CommentTarget {
    /**
     * 弹窗实例的稳定目标标识。
     */
    val key: String

    /**
     * 话题类评论目标。
     *
     * @param type 话题目标类型。
     * @param id 话题目标 ID。
     */
    @Immutable
    data class Topic(
        @TopicType val type: String,
        val id: Long,
    ) : CommentTarget {
        override val key = "topic-$type-$id"
    }

    /**
     * 时间线评论目标。
     *
     * @param id 时间线 ID。
     */
    @Immutable
    data class Timeline(val id: Long) : CommentTarget {
        override val key = "timeline-$id"
    }
}

@Immutable
data class CommentDialogAnchor(
    val target: CommentTarget,
    val reply: ComposeReply = ComposeReply.Empty,
) {
    val key = target.key + "-" + reply.id

    companion object {
        val Empty = CommentDialogAnchor(CommentTarget.Topic(TopicType.TYPE_UNKNOWN, 0))
    }
}
