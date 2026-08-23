package com.xiaoyv.bangumi.features.pixiv.login.business

/**
 * [PixivLoginSideEffect]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class PixivLoginSideEffect {
    data class OnOpenWebLogin(val codeChallenge: String) : PixivLoginSideEffect()
    data object OnLoginSuccess : PixivLoginSideEffect()
}
