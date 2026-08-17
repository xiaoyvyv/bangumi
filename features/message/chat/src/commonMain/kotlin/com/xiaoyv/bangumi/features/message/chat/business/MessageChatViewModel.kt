package com.xiaoyv.bangumi.features.message.chat.business

import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.PageStatus
import org.orbitmvi.orbit.syntax.Syntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.types.LoadingState
import com.xiaoyv.bangumi.shared.core.utils.limit
import com.xiaoyv.bangumi.shared.core.utils.onCompletion
import com.xiaoyv.bangumi.shared.data.repository.UserRepository
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * [MessageChatViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class MessageChatViewModel(
    savedStateHandle: SavedStateHandle,
    private val args: Screen.MessageChat,
    private val userRepository: UserRepository,
) : BaseViewModel<MessageChatState, MessageChatSideEffect, MessageChatEvent.Action>(savedStateHandle) {

    init {
        viewModelScope.launch {
            while (isActive) {
                delay(5000)
                refresh(loading = false)
            }
        }
    }

    override fun initBaseState(): UiState<MessageChatState> = UiState(data = createInitialState(), status = PageStatus.Loading)

    override fun createInitialState() = MessageChatState()

    override fun onEvent(event: MessageChatEvent.Action) {
        when (event) {
            is MessageChatEvent.Action.OnRefresh -> refresh(event.loading)
            is MessageChatEvent.Action.OnSendReply -> onSendReply(event.text)
            is MessageChatEvent.Action.OnTextChange -> onTextChange(event.text)
        }
    }

    override suspend fun Syntax<UiState<MessageChatState>, UiSideEffect<MessageChatSideEffect>>.refreshSync() {
        userRepository.fetchUserMessageDetail(args.id)
            .onFailure { reduceError { it } }
            .onSuccess { reduceData { state.copy(message = it) } }
    }

    private fun onTextChange(text: TextFieldValue) = intent {
        reduceData { state.copy(input = text.limit(1000)) }
    }

    /**
     * related=374261&msg_receivers=whystart&current_msg_id=374261&formhash=4ed0c8cf&msg_title=Re%3Awhystart&msg_body=%E4%BD%A0%E5%A5%BD&chat=on&submit=%E5%9B%9E%E5%A4%8D
     *
     */
    private fun onSendReply(text: String) = intent {
        val message = state.data.message

        reduceData { state.copy(sending = LoadingState.Loading) }

        userRepository.submitSendMessage(
            relatedId = message.related,
            username = message.msgReceivers,
            currentMsgId = message.currentMsgId,
            title = message.title,
            text = text,
            newChat = false
        ).onCompletion {
            reduceData { state.copy(sending = LoadingState.NotLoading) }
        }.onSuccess {
            reduceData { state.copy(message = it, input = TextFieldValue()) }
        }
    }
}