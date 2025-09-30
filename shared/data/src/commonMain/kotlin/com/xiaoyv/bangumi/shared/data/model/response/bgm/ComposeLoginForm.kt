package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * Class: [ComposeLoginForm]
 *
 * @author why
 * @since 11/25/23
 */
@Immutable
@Serializable
data class ComposeLoginForm(
    val forms: Map<String, String> = emptyMap(),
    val hasLogin: Boolean = false,
    val loginInfo: ComposeLoginResult = ComposeLoginResult.Empty,
)