package com.xiaoyv.bangumi.shared.ui.component.dialog.comment

sealed class CommentSideEffect {
    data class OnSendCommentSuccess(val replyId: Long) : CommentSideEffect()
}
