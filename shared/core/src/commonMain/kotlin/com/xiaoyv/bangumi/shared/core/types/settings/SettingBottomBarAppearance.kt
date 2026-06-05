package com.xiaoyv.bangumi.shared.core.types.settings

import androidx.annotation.IntDef
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.settings_bar_appearance_liquid_glass
import com.xiaoyv.bangumi.core_resource.resources.settings_bar_appearance_material3

@IntDef(
    SettingBottomBarAppearance.MATERIAL3,
    SettingBottomBarAppearance.LIQUID_GLASS,
)
@Retention(AnnotationRetention.SOURCE)
annotation class SettingBottomBarAppearance {
    companion object {
        const val MATERIAL3 = 0
        const val LIQUID_GLASS = 1

        fun string(@SettingBottomBarAppearance value: Int) = when (value) {
            LIQUID_GLASS -> Res.string.settings_bar_appearance_liquid_glass
            else -> Res.string.settings_bar_appearance_material3
        }
    }
}
