package com.xiaoyv.bangumi.features.message.chat.business

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.shared.core.types.LoadingState
import com.xiaoyv.bangumi.shared.data.model.response.bgm.pm.ComposePmMessageDetail
import com.xiaoyv.bangumi.shared.data.model.response.bgm.pm.ComposePmThread

/**
 * [MessageChatState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class MessageChatState(
    val nickname: String = "",
    val thread: ComposePmThread = ComposePmThread.Empty,
    val topic: TextFieldValue = TextFieldValue(),
    val topicEnable: Boolean = false,

    val input: TextFieldValue = TextFieldValue(),
    val sending: LoadingState = LoadingState.NotLoading,
    val message: ComposePmMessageDetail = ComposePmMessageDetail.Empty,
)
