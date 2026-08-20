package com.xiaoyv.bangumi.features.timeline.add.business

import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.postEffect
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.withActionLoading
import com.xiaoyv.bangumi.shared.core.utils.BBCode
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.core.utils.insertBBCode
import com.xiaoyv.bangumi.shared.core.utils.sanitizeImageUrl
import com.xiaoyv.bangumi.shared.data.repository.ChoreRepository
import com.xiaoyv.bangumi.shared.data.repository.TimelineRepository
import io.github.vinceglb.filekit.PlatformFile

/**
 * [TimelineAddViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class TimelineAddViewModel(
    private val timelineRepository: TimelineRepository,
    private val choreRepository: ChoreRepository
) : BaseViewModel<TimelineAddState, TimelineAddSideEffect, TimelineAddEvent.Action>() {

    override fun createInitialState() = TimelineAddState()

    override fun onEvent(event: TimelineAddEvent.Action) {
        when (event) {
            is TimelineAddEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is TimelineAddEvent.Action.OnContentChange -> onContentChange(event.content)
            is TimelineAddEvent.Action.OnReceiveTurnstileToken -> onReceiveTurnstileToken(event.token)
            is TimelineAddEvent.Action.OnPublish -> onPublish()
            is TimelineAddEvent.Action.OnImagePickResult -> onImagePickResult(event.path)
        }
    }


    private fun onReceiveTurnstileToken(token: String) = intent {
        reduceData { state.copy(turnstileToken = token) }
    }

    fun onContentChange(content: TextFieldValue) = intent {
        reduceData { state.copy(content = content) }
    }

    fun onImagePickResult(file: PlatformFile) = intent {
        withActionLoading { choreRepository.compressImageAndUpload(file) }
            .onFailure { postToast { it.errMsg } }
            .onSuccess {
                val imageUrl = it.thumbUrl.sanitizeImageUrl()
                val code = BBCode(hint = imageUrl, code = "img")

                reduceData {
                    state.copy(
                        content = state.content.insertBBCode(
                            prefix = if (state.content.text.isNotBlank()) "\n" else "",
                            suffix = "\n",
                            bbCode = code
                        )
                    )
                }
            }
    }

    private fun onPublish() = intent {
        withActionLoading {
            timelineRepository.submitCreateTimeline(
                content = state.content.text.trim(),
                turnstileToken = state.turnstileToken
            )
        }.onSuccess {
            postEffect { TimelineAddSideEffect.OnCreateTimelineSuccess(it.id) }
        }
    }
}