package com.xiaoyv.bangumi.shared.ui.component.dialog.comment

import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeNewReply

sealed class CommentSideEffect {
    data class OnSendCommentSuccess(val comment: ComposeNewReply) : CommentSideEffect()
}
