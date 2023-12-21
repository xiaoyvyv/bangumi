package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

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
                TYPE_ALL -> "全部"
                TYPE_COLLECT -> "收藏"
                TYPE_COMMENT -> "评论"
                TYPE_DATELINE -> "时间"
                TYPE_TITLE -> "名称"
                else -> "全部"
            }
        }
    }
}