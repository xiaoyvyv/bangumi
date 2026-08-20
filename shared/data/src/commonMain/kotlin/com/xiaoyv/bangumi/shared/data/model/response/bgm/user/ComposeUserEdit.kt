package com.xiaoyv.bangumi.shared.data.model.response.bgm.user

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * [ComposeUserEdit]
 *
 * @author why
 * @since 2025/1/16
 */
@Immutable
@Serializable
data class ComposeUserEdit(
    val avatar: String = "",
    val nickname: String = "",
    val sign: String = "",
    val timezone: String = "",
    val site: String = "",
    val intro: String = "",
) {
    companion object {
        val Empty = ComposeUserEdit()
    }
}

@Immutable
@Serializable
data class ComposeUserServicesEdit(
    val internetPsn: String = "",
    val internetXbox: String = "",
    val internetSteam: String = "",
    val internetPixi: String = "",
    val internetGithub: String = "",
    val internetTwitter: String = "",
    val internetIns: String = "",
) {
    companion object {
        val Empty = ComposeUserServicesEdit()
    }
}
