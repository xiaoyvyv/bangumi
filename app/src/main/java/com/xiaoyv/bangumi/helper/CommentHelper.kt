package com.xiaoyv.bangumi.helper

import androidx.fragment.app.FragmentActivity
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.parser.entity.CommentFormEntity
import com.xiaoyv.common.api.parser.entity.CommentTreeEntity
import com.xiaoyv.common.api.response.ReplyResultEntity
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.widget.reply.ReplyCommentDialog

/**
 * Class: [CommentHelper]
 *
 * @author why
 * @since 12/29/23
 */
object CommentHelper {

    /**
     * 回复弹窗
     */
    fun showCommentDialog(
        activity: FragmentActivity,
        replyForm: CommentFormEntity?,
        replyJs: String? = null,
        targetComment: CommentTreeEntity? = null,
        onReplyListener: suspend (ReplyResultEntity) -> Unit = {},
    ) {
        if (UserHelper.isLogin.not()) RouteHelper.jumpSignIn()
        if (replyForm != null && replyForm.isEmpty.not()) {
            ReplyCommentDialog.show(
                activity,
                replyForm,
                replyJs,
                targetComment,
                onPreviewCodeListener = {
                    RouteHelper.jumpPreviewBBCode(it)
                },
                onReplyListener = {
                    activity.launchUI { onReplyListener(it) }
                }
            )
        }
    }
}