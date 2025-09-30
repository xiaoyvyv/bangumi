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
    SettingUpdateChannel.ACTION,
)
@Retention(AnnotationRetention.SOURCE)
annotation class SettingUpdateChannel {
    companion object {
        const val RELEASE = 0
        const val ACTION = 1
        fun string(@SettingUpdateChannel channel: Int): String = when (channel) {
            RELEASE -> "Release"
            ACTION -> "Action"
            else -> "Unknown"
        }
    }
}
