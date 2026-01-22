package com.xiaoyv.bangumi.features.sign.sign_in.business

import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeLoginResult

/**
 * [SignInSideEffect]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class SignInSideEffect {
    data class OnLoginResult(val loginInfo: ComposeLoginResult) : SignInSideEffect()
}