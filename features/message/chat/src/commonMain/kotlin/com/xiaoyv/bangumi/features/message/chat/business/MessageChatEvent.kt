package com.xiaoyv.bangumi.features.message.chat.business

import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [MessageChatEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class MessageChatEvent {
    sealed class UI : MessageChatEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : MessageChatEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data class OnTextChange(val text: TextFieldValue) : Action()
        data class OnSendReply(val text: String) : Action()
    }
}