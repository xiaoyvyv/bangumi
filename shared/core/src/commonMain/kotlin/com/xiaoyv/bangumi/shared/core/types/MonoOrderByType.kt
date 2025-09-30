package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.type_mono_query_collect
import com.xiaoyv.bangumi.core_resource.resources.type_mono_query_comment
import com.xiaoyv.bangumi.core_resource.resources.type_mono_query_dateline
import com.xiaoyv.bangumi.core_resource.resources.type_mono_query_title
import org.jetbrains.compose.resources.StringResource

@StringDef(
    MonoOrderByType.TYPE_COLLECT,
    MonoOrderByType.TYPE_COMMENT,
    MonoOrderByType.TYPE_DATELINE,
    MonoOrderByType.TYPE_TITLE
)
@Retention(AnnotationRetention.SOURCE)
annotation class MonoOrderByType {
    companion object {
        const val TYPE_COLLECT = "collects"
        const val TYPE_COMMENT = "comment"
        const val TYPE_DATELINE = "dateline"
        const val TYPE_TITLE = "title"

        fun string(@MonoOrderByType type: String): StringResource {
            return when (type) {
                TYPE_DATELINE -> Res.string.type_mono_query_dateline
                TYPE_COLLECT -> Res.string.type_mono_query_collect
                TYPE_COMMENT -> Res.string.type_mono_query_comment
                TYPE_TITLE -> Res.string.type_mono_query_title
                else -> Res.string.type_mono_query_dateline
            }
        }
    }
}