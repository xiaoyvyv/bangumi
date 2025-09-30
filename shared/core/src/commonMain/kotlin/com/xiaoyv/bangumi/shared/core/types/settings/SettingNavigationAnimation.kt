package com.xiaoyv.bangumi.shared.core.types.settings

import androidx.annotation.IntDef
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.settings_navigation_animation_fade
import com.xiaoyv.bangumi.core_resource.resources.settings_navigation_animation_none
import com.xiaoyv.bangumi.core_resource.resources.settings_navigation_animation_slide

/**
 * [SettingNavigationAnimation]
 *
 * @author why
 * @since 2025/1/16
 */
@IntDef(
    SettingNavigationAnimation.NONE,
    SettingNavigationAnimation.FADE,
    SettingNavigationAnimation.SLIDE
)
@Retention(AnnotationRetention.SOURCE)
annotation class SettingNavigationAnimation {
    companion object {
        const val NONE = 0
        const val FADE = 1
        const val SLIDE = 2

        fun string(@SettingNavigationAnimation theme: Int) = when (theme) {
            FADE -> Res.string.settings_navigation_animation_fade
            SLIDE -> Res.string.settings_navigation_animation_slide
            else -> Res.string.settings_navigation_animation_none
        }
    }
}
