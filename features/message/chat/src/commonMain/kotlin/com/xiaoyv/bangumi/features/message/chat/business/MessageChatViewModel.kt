package com.xiaoyv.bangumi.features.message.chat.business

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.core.mvi.withActionLoading
import com.xiaoyv.bangumi.shared.core.types.LoadingState
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.core.utils.limit
import com.xiaoyv.bangumi.shared.core.utils.onCompletion
import com.xiaoyv.bangumi.shared.data.model.response.bgm.pm.ComposePmThread
import com.xiaoyv.bangumi.shared.data.repository.UserRepository
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.syntax.Syntax
import kotlin.time.Duration.Companion.milliseconds

/**
 * [MessageChatViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class MessageChatViewModel(
    private val args: Screen.MessageChat,
    private val userRepository: UserRepository,
) : BaseViewModel<MessageChatState, MessageChatSideEffect, MessageChatEvent.Action>() {

    init {
        viewModelScope.launch {
            while (isActive) {
                delay(5000.milliseconds)
                refresh(contentLoading = false)
            }
        }
    }

    override fun initBaseState(): UiState<MessageChatState> = initBaseLoadingState()

    override fun createInitialState() = MessageChatState(nickname = args.nickname)

    override fun onEvent(event: MessageChatEvent.Action) {
        when (event) {
            is MessageChatEvent.Action.OnRefresh -> refresh(event.loading)
            is MessageChatEvent.Action.OnSendReply -> onSendMessage(event.text)
            is MessageChatEvent.Action.OnTextChange -> onTextChange(event.text)
            is MessageChatEvent.Action.OnEnableTopicInput -> onEnableTopicInput(event.enable)
            is MessageChatEvent.Action.OnThreadChange -> onThreadChange(event.thread)
            is MessageChatEvent.Action.OnTopicInputChange -> onTopicInputChange(event.text)
        }
    }


    override suspend fun Syntax<UiState<MessageChatState>, UiSideEffect<MessageChatSideEffect>>.refreshSync() {
        userRepository.fetchUserPmMessage(args.conversationId, state.data.thread.id)
            .onFailure { reduceError { it } }
            .onSuccess { reduceData { state.copy(message = it, thread = it.currentThread) } }
    }

    private fun onTopicInputChange(text: TextFieldValue) = intent {
        reduceData { state.copy(topic = text.limit(20)) }
    }

    private fun onEnableTopicInput(enable: Boolean) = intent {
        reduceData { state.copy(topicEnable = enable) }
    }

    private fun onTextChange(text: TextFieldValue) = intent {
        reduceData { state.copy(input = text.limit(1000)) }
    }

    private fun onThreadChange(thread: ComposePmThread) = intent {
        withActionLoading { userRepository.fetchUserPmMessage(args.conversationId, thread.id) }
            .onFailure { reduceError { it } }
            .onSuccess { reduceData { state.copy(message = it, thread = thread) } }
    }

    private fun onSendMessage(text: String) = intent {
        val message = state.data.message
        val topic = if (state.data.topicEnable) state.data.topic.text else ""

        reduceData { state.copy(sending = LoadingState.Loading) }

        userRepository.submitSendMessage(text = text, topic = topic, inputs = message.inputs)
            .mapCatching {
                userRepository.fetchUserPmMessage(args.conversationId, state.data.thread.id).getOrThrow()
            }.onCompletion {
                reduceData { state.copy(sending = LoadingState.NotLoading) }
            }.onFailure {
                postToast { it.errMsg }
            }.onSuccess {
                reduceData {
                    state.copy(
                        message = it,
                        input = TextFieldValue(),
                        topic = TextFieldValue()
                    )
                }
            }
    }
}
