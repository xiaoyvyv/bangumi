package com.xiaoyv.bangumi.shared.ui.component.dialog.comment

import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.shared.core.mvi.BaseMviViewModel
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.TopicType
import com.xiaoyv.bangumi.shared.core.utils.BBCode
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.insertBBCode
import com.xiaoyv.bangumi.shared.core.utils.onCompletion
import com.xiaoyv.bangumi.shared.core.utils.sanitizeImageUrl
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.repository.BlogRepository
import com.xiaoyv.bangumi.shared.data.repository.ChoreRepository
import com.xiaoyv.bangumi.shared.data.repository.IndexRepository
import com.xiaoyv.bangumi.shared.data.repository.MonoRepository
import com.xiaoyv.bangumi.shared.data.repository.TopicRepository
import io.github.vinceglb.filekit.PlatformFile


class CommentViewModel(
    private val dialogAnchor: CommentDialogAnchor,
    private val choreRepository: ChoreRepository,
    private val topicRepository: TopicRepository,
    private val monoRepository: MonoRepository,
    private val blogRepository: BlogRepository,
    private val indexRepository: IndexRepository,
) : BaseMviViewModel<CommentState, CommentSideEffect, CommentEvent>() {

    override fun createInitialState(): CommentState {
        return CommentState(anchor = dialogAnchor)
    }

    override fun onEvent(event: CommentEvent) {
        when (event) {
            is CommentEvent.OnTextChange -> onTextChange(event.value)
            is CommentEvent.SendComment -> onSendComment()
            is CommentEvent.OnImagePickResult -> onImagePickResult(event.file)
            is CommentEvent.OnReceiveTurnstileToken -> onReceiveTurnstileToken(event.token)
        }
    }

    private fun onReceiveTurnstileToken(token: String) = intent {
        reduce { state.copy(turnstile = token) }
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
                val imageUrl = it.thumbUrl.sanitizeImageUrl()
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
        val comment = state.comment.text.trim()
        val hasReplyComment = reply != ComposeReply.Empty

        val result = when (state.anchor.targetType) {
            TopicType.TYPE_GROUP -> {
                topicRepository.submitGroupComment(
                    topicId = state.anchor.targetId,
                    content = comment,
                    turnstile = state.turnstile,
                    replyTo = if (hasReplyComment) reply.id else null
                )
            }

            TopicType.TYPE_SUBJECT -> {
                topicRepository.submitSubjectComment(
                    topicId = state.anchor.targetId,
                    content = comment,
                    turnstile = state.turnstile,
                    replyTo = if (hasReplyComment) reply.id else null
                )
            }

            TopicType.TYPE_EP -> {
                topicRepository.submitSubjectEpisodeComment(
                    episodeId = state.anchor.targetId,
                    content = comment,
                    turnstile = state.turnstile,
                    replyTo = if (hasReplyComment) reply.id else null
                )
            }

            TopicType.TYPE_PERSON -> {
                monoRepository.submitMonoComment(
                    type = MonoType.PERSON,
                    monoId = state.anchor.targetId,
                    content = comment,
                    turnstile = state.turnstile,
                    replyTo = if (hasReplyComment) reply.id else null
                )
            }

            TopicType.TYPE_CRT -> {
                monoRepository.submitMonoComment(
                    type = MonoType.CHARACTER,
                    monoId = state.anchor.targetId,
                    content = comment,
                    turnstile = state.turnstile,
                    replyTo = if (hasReplyComment) reply.id else null
                )
            }

            TopicType.TYPE_INDEX -> {
                indexRepository.submitIndexComment(
                    indexId = state.anchor.targetId,
                    content = comment,
                    turnstile = state.turnstile,
                    replyTo = if (hasReplyComment) reply.id else null
                )
            }

            TopicType.TYPE_BLOG -> {
                blogRepository.submitBlogComment(
                    blogId = state.anchor.targetId,
                    content = comment,
                    turnstile = state.turnstile,
                    replyTo = if (hasReplyComment) reply.id else null
                )
            }

            else -> {
                Result.failure(IllegalStateException())
            }
        }

        result
            .onFailure { reduce { state.copy(sending = false) } }
            .onSuccess {
                reduce { state.copy(sending = false, comment = TextFieldValue()) }
                postSideEffect(CommentSideEffect.OnSendCommentSuccess(it.id))
            }
    }
}
