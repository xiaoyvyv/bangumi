package com.xiaoyv.bangumi.shared.data.model.response.chore

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.types.settings.SettingUpdateChannel
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
     * @param channel 更新渠道
     * @param currentVersionCode 当前版本号
     * @param currentVersionName 当前版本名称
     */
    fun isNewerThan(
        @SettingUpdateChannel channel: Int,
        currentVersionCode: Long,
        currentVersionName: String,
    ): Boolean = when (channel) {
        SettingUpdateChannel.PREVIEW -> releaseVersionCode() > currentVersionCode
        else -> compareVersion(tagName, currentVersionName) > 0
    }

    /**
     * 从发布说明中读取工作流写入的 versionCode。
     */
    private fun releaseVersionCode(): Long =
        VERSION_CODE_REGEX.find(body)?.groupValues?.getOrNull(1)?.toLongOrNull() ?: 0L

    companion object {
        private val VERSION_CODE_REGEX = Regex("""versionCode[：:]\s*(\d+)""")

        val Empty = ComposeAppRelease()

        /**
         * 比较两个语义版本名称。
         *
         * @param remoteVersion 远端版本名称
         * @param localVersion 本地版本名称
         */
        private fun compareVersion(remoteVersion: String, localVersion: String): Int {
            val remoteParts = remoteVersion.removePrefix("v").split(".")
            val localParts = localVersion.removePrefix("v").split(".")
            val partCount = maxOf(remoteParts.size, localParts.size)

            for (index in 0 until partCount) {
                val remotePart = remoteParts.getOrNull(index)?.toIntOrNull() ?: 0
                val localPart = localParts.getOrNull(index)?.toIntOrNull() ?: 0
                if (remotePart != localPart) return remotePart.compareTo(localPart)
            }
            return 0
        }
    }
}
