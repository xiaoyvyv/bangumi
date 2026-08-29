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
    /**
     * 判断发布版本是否高于当前应用版本。
     *
     * @param currentVersionCode 当前版本号
     */
    fun isNewerThan(currentVersionCode: Long): Boolean = releaseVersionCode() > currentVersionCode

    /**
     * 从发布说明中读取工作流写入的 versionCode。
     */
    private fun releaseVersionCode(): Long =
        VERSION_CODE_REGEX.find(body)?.groupValues?.getOrNull(1)?.toLongOrNull() ?: 0L

    companion object {
        private val VERSION_CODE_REGEX = Regex("""versionCode[：:]\s*(\d+)""")

        val Empty = ComposeAppRelease()
    }
}
