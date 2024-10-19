package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.i18n

@StringDef(
    MonoOrderByType.TYPE_ALL,
    MonoOrderByType.TYPE_COLLECT,
    MonoOrderByType.TYPE_COMMENT,
    MonoOrderByType.TYPE_DATELINE,
    MonoOrderByType.TYPE_TITLE
)
@Retention(AnnotationRetention.SOURCE)
annotation class MonoOrderByType {
    companion object {
        const val TYPE_ALL = ""
        const val TYPE_COLLECT = "collects"
        const val TYPE_COMMENT = "comment"
        const val TYPE_DATELINE = "dateline"
        const val TYPE_TITLE = "title"

        fun string(@MonoOrderByType type: String): String {
            return when (type) {
                TYPE_ALL -> i18n(CommonString.type_mono_query_all)
                TYPE_COLLECT -> i18n(CommonString.type_mono_query_collect)
                TYPE_COMMENT -> i18n(CommonString.type_mono_query_comment)
                TYPE_DATELINE -> i18n(CommonString.type_mono_query_dateline)
                TYPE_TITLE -> i18n(CommonString.type_mono_query_title)
                else -> i18n(CommonString.type_mono_query_all)
            }
        }
    }
}