package com.xiaoyv.common.helper.theme

import androidx.annotation.IntDef

/**
 * Class: [ThemeType]
 *
 * @author why
 * @since 11/25/23
 */
@IntDef(
    ThemeType.TYPE_LIGHT,
    ThemeType.TYPE_DARK,
    ThemeType.TYPE_SYSTEM,
    ThemeType.TYPE_WALLPAPER
)
@Retention(AnnotationRetention.SOURCE)
annotation class ThemeType {
    companion object {
        const val TYPE_LIGHT = 0
        const val TYPE_DARK = 1
        const val TYPE_SYSTEM = 2
        const val TYPE_WALLPAPER = 3
    }
}
