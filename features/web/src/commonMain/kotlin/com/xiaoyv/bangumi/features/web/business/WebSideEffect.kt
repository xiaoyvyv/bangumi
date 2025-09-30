package com.xiaoyv.bangumi.features.web.business

/**
 * [WebSideEffect]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class WebSideEffect {
    data object OnNavUp : WebSideEffect()
    data object OnReload : WebSideEffect()
}