@file:Suppress("ArrayInDataClass")

package com.xiaoyv.bangumi.features.sign.sign_in.business

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.shared.core.types.LoadingState
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeLoginResult
import kotlinx.collections.immutable.persistentMapOf

/**
 * [SignInState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class SignInState(
    val email: TextFieldValue = TextFieldValue(),
    val password: TextFieldValue = TextFieldValue(),
    val code: TextFieldValue = TextFieldValue(),
    val codeImage: ByteArray = byteArrayOf(),
    val codeState: LoadingState = LoadingState.NotLoading,
    val otherForms: SerializeMap<String, String> = persistentMapOf(),
    val loginResult: ComposeLoginResult = ComposeLoginResult.Empty,
    val loggingRunning: Boolean = false,
) {
    /**
     * 是否可以登录
     */
    val enableLogin: Boolean
        get() = email.text.trim().isNotBlank()
                && password.text.trim().isNotBlank()
                && code.text.trim().isNotBlank()
}
