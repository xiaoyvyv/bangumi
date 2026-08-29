package com.xiaoyv.bangumi.shared.core.types.settings

import androidx.annotation.IntDef

/**
 * [SettingUpdateChannel]
 *
 * @author why
 * @since 2025/1/16
 */
@IntDef(
    SettingUpdateChannel.RELEASE,
    SettingUpdateChannel.PREVIEW,
)
@Retention(AnnotationRetention.SOURCE)
annotation class SettingUpdateChannel {
    companion object {
        const val RELEASE = 0
        const val PREVIEW = 1

        fun string(@SettingUpdateChannel channel: Int): String = when (channel) {
            RELEASE -> "Release"
            PREVIEW -> "Preview"
            else -> "Unknown"
        }
    }
}
