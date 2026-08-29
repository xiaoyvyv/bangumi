package com.xiaoyv.bangumi.shared.data.model.response.chore

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * GitHub 应用发布信息。
 */
@Immutable
@Serializable
data class ComposeAppRelease(
    @SerialName("tag_name") val tagName: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("body") val body: String = "",
    @SerialName("html_url") val htmlUrl: String = "",
    @SerialName("prerelease") val isPreRelease: Boolean = false,
    @SerialName("published_at") val publishedAt: String = "",
) {
    companion object {
        val Empty = ComposeAppRelease()
    }
}
