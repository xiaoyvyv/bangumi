package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * Class: [ComposeLoginResult]
 *
 * @author why
 * @since 11/25/23
 */
@Immutable
@Serializable
data class ComposeLoginResult(
    val success: Boolean = false,
    val message: String = "",
    val composeUser: ComposeUser = ComposeUser.Empty,
) {
    companion object {
        val Empty = ComposeLoginResult()
    }
}