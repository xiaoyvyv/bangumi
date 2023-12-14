package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [ReportType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(

)
@Retention(AnnotationRetention.SOURCE)
annotation class ReportType {
    companion object {
        const val TYPE_1 = "1"
        const val TYPE_2 = "2"
        const val TYPE_3 = "3"
        const val TYPE_4 = "4"
        const val TYPE_5= "5"
        const val TYPE_USER = "6"
        const val TYPE_7 = "7"
        const val TYPE_8 = "8"
        const val TYPE_BLOG = "9"
    }
}
