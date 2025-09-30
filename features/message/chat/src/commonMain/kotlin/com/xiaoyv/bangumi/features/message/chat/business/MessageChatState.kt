package com.xiaoyv.bangumi.features.message.chat.business

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.shared.core.types.LoadingState
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMessageDetail

/**
 * [MessageChatState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class MessageChatState(
    val input: TextFieldValue = TextFieldValue(),
    val sending: LoadingState = LoadingState.NotLoading,
    val message: ComposeMessageDetail = ComposeMessageDetail.Empty,
)
