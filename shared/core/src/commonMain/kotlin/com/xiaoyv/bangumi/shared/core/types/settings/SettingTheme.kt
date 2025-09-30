package com.xiaoyv.bangumi.shared.core.types.settings

import androidx.annotation.IntDef
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_unknown
import com.xiaoyv.bangumi.core_resource.resources.settings_theme_dark
import com.xiaoyv.bangumi.core_resource.resources.settings_theme_light
import com.xiaoyv.bangumi.core_resource.resources.settings_theme_system

/**
 * [SettingTheme]
 *
 * @author why
 * @since 2025/1/16
 */
@IntDef(
    SettingTheme.SYSTEM,
    SettingTheme.LIGHT,
    SettingTheme.DARK,
)
@Retention(AnnotationRetention.SOURCE)
annotation class SettingTheme {
    companion object {
        const val SYSTEM = 0
        const val LIGHT = 1
        const val DARK = 2

        fun string(@SettingTheme theme: Int) = when (theme) {
            SYSTEM -> Res.string.settings_theme_system
            LIGHT -> Res.string.settings_theme_light
            DARK -> Res.string.settings_theme_dark
            else -> Res.string.global_unknown
        }
    }
}
