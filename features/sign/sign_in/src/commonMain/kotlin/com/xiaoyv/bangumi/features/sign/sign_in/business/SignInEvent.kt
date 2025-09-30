package com.xiaoyv.bangumi.features.sign.sign_in.business

import androidx.compose.ui.text.input.TextFieldValue

/**
 * [SignInEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class SignInEvent {
    sealed class UI : SignInEvent() {
        data object OnNavUp : UI()
    }

    sealed class Action : SignInEvent() {
        data object OnSignIn : Action()
        data object OnRefreshVerifyCode : Action()
        data class OnEmailChange(val email: TextFieldValue) : Action()
        data class OnPasswordChange(val password: TextFieldValue) : Action()
        data class OnCodeChange(val code: TextFieldValue) : Action()
    }
}