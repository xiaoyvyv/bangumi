package com.xiaoyv.bangumi.shared.ui.component.dialog.comment

import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.shared.core.mvi.BaseMviViewModel
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.TopicType
import com.xiaoyv.bangumi.shared.core.utils.BBCode
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.extractHtmlText
import com.xiaoyv.bangumi.shared.core.utils.insertBBCode
import com.xiaoyv.bangumi.shared.core.utils.onCompletion
import com.xiaoyv.bangumi.shared.core.utils.sanitizeImageUrl
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.repository.BlogRepository
import com.xiaoyv.bangumi.shared.data.repository.ChoreRepository
import com.xiaoyv.bangumi.shared.data.repository.IndexRepository
import com.xiaoyv.bangumi.shared.data.repository.MonoRepository
import com.xiaoyv.bangumi.shared.data.repository.TimelineRepository
import com.xiaoyv.bangumi.shared.data.repository.TopicRepository
import io.github.vinceglb.filekit.PlatformFile


class CommentViewModel(
    private val dialogAnchor: CommentDialogAnchor,
    private val choreRepository: ChoreRepository,
    private val topicRepository: TopicRepository,
    private val monoRepository: MonoRepository,
    private val blogRepository: BlogRepository,
    private val indexRepository: IndexRepository,
    private val timelineRepository: TimelineRepository,
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

        val comment = state.comment.text.trim()
        val payload = state.anchor.createSubmitPayload(comment)

        val result = when (val target = state.anchor.target) {
            is CommentTarget.Topic -> when (target.type) {
                TopicType.TYPE_GROUP -> topicRepository.submitGroupComment(
                    topicId = target.id,
                    content = payload.content,
                    turnstile = state.turnstile,
                    replyTo = payload.replyTo,
                )

                TopicType.TYPE_SUBJECT -> topicRepository.submitSubjectComment(
                    topicId = target.id,
                    content = payload.content,
                    turnstile = state.turnstile,
                    replyTo = payload.replyTo,
                )

                TopicType.TYPE_EP -> topicRepository.submitSubjectEpisodeComment(
                    episodeId = target.id,
                    content = payload.content,
                    turnstile = state.turnstile,
                    replyTo = payload.replyTo,
                )

                TopicType.TYPE_PERSON -> monoRepository.submitMonoComment(
                    type = MonoType.PERSON,
                    monoId = target.id,
                    content = payload.content,
                    turnstile = state.turnstile,
                    replyTo = payload.replyTo,
                )

                TopicType.TYPE_CRT -> monoRepository.submitMonoComment(
                    type = MonoType.CHARACTER,
                    monoId = target.id,
                    content = payload.content,
                    turnstile = state.turnstile,
                    replyTo = payload.replyTo,
                )

                TopicType.TYPE_INDEX -> indexRepository.submitIndexComment(
                    indexId = target.id,
                    content = payload.content,
                    turnstile = state.turnstile,
                    replyTo = payload.replyTo,
                )

                TopicType.TYPE_BLOG -> blogRepository.submitBlogComment(
                    blogId = target.id,
                    content = payload.content,
                    turnstile = state.turnstile,
                    replyTo = payload.replyTo,
                )

                else -> Result.failure(IllegalStateException())
            }

            is CommentTarget.Timeline -> timelineRepository.submitTimelineReply(
                timelineId = target.id,
                content = payload.content,
                turnstile = state.turnstile,
                replyTo = payload.replyTo,
            )
        }

        result
            .onFailure { reduce { state.copy(sending = false) } }
            .onSuccess {
                reduce { state.copy(sending = false, comment = TextFieldValue()) }
                postSideEffect(CommentSideEffect.OnSendCommentSuccess(it.id))
            }
    }
}

private data class CommentSubmitPayload(
    val content: String,
    val replyTo: Long?,
)

private fun CommentDialogAnchor.createSubmitPayload(comment: String): CommentSubmitPayload {
    val reply = this.reply
    if (reply == ComposeReply.Empty) return CommentSubmitPayload(content = comment, replyTo = null)
    if (reply.relatedID == 0L) return CommentSubmitPayload(content = comment, replyTo = reply.id)

    // ComposeReply content is normalized HTML. Submit only plain text inside the new BBCode quote.
    val quote = reply.content
        .extractHtmlText(excludeQuoteBlocks = true)
        .trim()
        .replace("[/quote]", "[/ quote]", ignoreCase = true)
        .let { content ->
            if (content.length > REPLY_QUOTE_MAX_LENGTH) {
                content.take(REPLY_QUOTE_MAX_LENGTH) + "..."
            } else {
                content
            }
        }

    return CommentSubmitPayload(
        content = "[quote]${quote}[/quote]\n$comment",
        replyTo = reply.relatedID,
    )
}

private const val REPLY_QUOTE_MAX_LENGTH = 100
