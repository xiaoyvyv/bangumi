package com.xiaoyv.bangumi.shared.ui.component.dialog.comment

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.BaseMviViewModel
import com.xiaoyv.bangumi.shared.core.utils.BBCode
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.insertBBCode
import com.xiaoyv.bangumi.shared.core.utils.onCompletion
import com.xiaoyv.bangumi.shared.core.utils.optImageUrl
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeComment
import com.xiaoyv.bangumi.shared.data.repository.ChoreRepository
import com.xiaoyv.bangumi.shared.data.repository.UgcRepository
import io.github.vinceglb.filekit.PlatformFile


class CommentViewModel(
    stateHandle: SavedStateHandle,
    private val ugcRepository: UgcRepository,
    private val choreRepository: ChoreRepository,
    private val dialogAnchor: CommentDialogAnchor,
) : BaseMviViewModel<CommentState, CommentSideEffect, CommentEvent>(stateHandle) {

    override fun initSate(onCreate: Boolean): CommentState {
        return CommentState(anchor = dialogAnchor)
    }

    override fun onEvent(event: CommentEvent) {
        when (event) {
            is CommentEvent.OnTextChange -> onTextChange(event.value)
            is CommentEvent.SendComment -> onSendComment()
            is CommentEvent.OnImagePickResult -> onImagePickResult(event.file)
        }
    }

    private fun onTextChange(value: TextFieldValue) = intent {
        reduce { state.copy(comment = value) }
    }

    private fun onImagePickResult(file: PlatformFile) = intent {
        reduce { state.copy(sending = true) }
        choreRepository.compressImageAndUpload(file)
            .onCompletion { reduce { state.copy(sending = false) } }
            .onFailure { debugLog { it } }
            .onSuccess {
                val imageUrl = it.thumbUrl.optImageUrl()
                val code = BBCode(hint = imageUrl, code = "img")

                reduce { state.copy(comment = state.comment.insertBBCode(code, suffix = "\n")) }
            }
    }

    /**
     * 发表评论
     */
    private fun onSendComment() = intent {
        reduce { state.copy(sending = true) }

        val reply = state.anchor.reply
        val article = state.anchor.article
        val comment = state.comment.text.trim()
        val isSubComment = reply != ComposeComment.Empty

        val replyArticleParam = article.replyParam
        val params = replyArticleParam.inputs.toMutableMap()

        params["content"] = if (!isSubComment) comment else {
            buildString {
                append("[quote][b]")
                append(reply.user.nickname)
                append("[/b] 说: ")
                append(reply.commentHtml.text.take(200))
                append("[/quote]\n")
                append(comment)
            }
        }
        params["related_photo"] to "0"
        params["lastview"] = state.anchor.lastViewedInMillis.toString().take(10)

        // 如果回复的子评论
        if (isSubComment) {
            val subReplyParam = reply.replyParam
            params["topic_id"] = subReplyParam.topicId.toString()
            params["related"] = subReplyParam.postId.toString()
            params["sub_reply_uid"] = subReplyParam.subReplyUid.toString()
            params["post_uid"] = subReplyParam.postUid.toString()
        }

        ugcRepository.submitNewReply(replyArticleParam.action, params = params)
            .onFailure { reduce { state.copy(sending = false) } }
            .onSuccess {
                reduce { state.copy(sending = false, comment = TextFieldValue()) }
                postSideEffect(CommentSideEffect.OnSendCommentSuccess(it))
            }
    }
}