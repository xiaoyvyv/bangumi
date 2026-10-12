package com.xiaoyv.bangumi.shared.core.types.settings

import androidx.annotation.IntDef
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.settings_ui_style_material3
import com.xiaoyv.bangumi.core_resource.resources.settings_ui_style_miuix

/**
 * 应用界面的组件风格。
 */
@IntDef(
    SettingUiStyle.MATERIAL3,
    SettingUiStyle.MIUIX,
)
@Retention(AnnotationRetention.SOURCE)
annotation class SettingUiStyle {
    companion object {
        const val MATERIAL3 = 0
        const val MIUIX = 1

        /**
         * 获取界面风格的显示名称。
         *
         * @param value 当前界面风格。
         */
        fun string(@SettingUiStyle value: Int) = when (value) {
            MIUIX -> Res.string.settings_ui_style_miuix
            else -> Res.string.settings_ui_style_material3
        }
    }
}
