package com.xiaoyv.bangumi.features.dollars.business

import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [DollarsEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class DollarsEvent {
    sealed class UI : DollarsEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : DollarsEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data class OnValueChange(val value: TextFieldValue) : Action()
        data object OnSendMessage : Action()
    }
}