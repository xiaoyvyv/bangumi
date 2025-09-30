package com.xiaoyv.bangumi.shared.core.types.settings

import androidx.annotation.IntDef
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.settings_indication
import com.xiaoyv.bangumi.core_resource.resources.settings_indication_fade
import com.xiaoyv.bangumi.core_resource.resources.settings_indication_ripped

@IntDef(
    SettingIndication.NONE,
    SettingIndication.FADE,
    SettingIndication.RIPPLE
)
@Retention(AnnotationRetention.SOURCE)
annotation class SettingIndication {
    companion object {
        const val NONE = 0
        const val RIPPLE = 1
        const val FADE = 2

        fun string(@SettingIndication theme: Int) = when (theme) {
            RIPPLE -> Res.string.settings_indication_ripped
            FADE -> Res.string.settings_indication_fade
            else -> Res.string.settings_indication
        }
    }
}