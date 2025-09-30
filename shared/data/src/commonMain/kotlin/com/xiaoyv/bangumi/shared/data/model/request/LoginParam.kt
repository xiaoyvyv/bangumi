package com.xiaoyv.bangumi.shared.data.model.request

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [LoginParam]
 *
 * 等私有API完善后，可以切换为私有API登录
 *
 * ```
 * https://next.bgm.tv/p1/turnstile?redirect_uri=bangumi://turnstile
 * ```
 *
 * @author why
 * @since 2025/1/14
 */
@Immutable
@Serializable
data class LoginParam(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("code") val code: String,
    @SerialName("otherForms") val otherForms: Map<String, String>,
)
