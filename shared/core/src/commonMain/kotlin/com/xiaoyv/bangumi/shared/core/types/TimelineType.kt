package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef

@IntDef(
    TimelineType.UNKNOWN,
    TimelineType.DAILY,
    TimelineType.WIKI,
    TimelineType.COLLECTION,
    TimelineType.PROGRESS,
    TimelineType.STATUS,
    TimelineType.BLOG,
    TimelineType.INDEX,
    TimelineType.PERSON,
)
@Retention(AnnotationRetention.SOURCE)
annotation class TimelineType {
    companion object {
        const val UNKNOWN = 0
        const val DAILY = 1        // 日常行为
        const val WIKI = 2         // 维基操作
        const val COLLECTION = 3   // 收藏条目
        const val PROGRESS = 4     // 收视进度
        const val STATUS = 5       // 状态
        const val BLOG = 6         // 日志
        const val INDEX = 7      // 目录
        const val PERSON = 8       // 人物

        fun string(@TimelineType type: Int): String {
            return when (type) {
                DAILY -> "日常行为"
                WIKI -> "维基操作"
                COLLECTION -> "收藏条目"
                PROGRESS -> "收视进度"
                STATUS -> "状态"
                BLOG -> "日志"
                INDEX -> "目录"
                PERSON -> "人物"
                else -> ""
            }
        }
    }
}
