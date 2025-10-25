package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef

/**
 * - 0 = 条目
 * - 1 = 角色
 * - 2 = 人物
 * - 3 = 剧集
 * - 4 = 日志
 * - 5 = 小组话题
 * - 6 = 条目讨论
 */
@IntDef(
    IndexCatType.SUBJECT,
    IndexCatType.CHARACTER,
    IndexCatType.PERSON,
    IndexCatType.EP,
    IndexCatType.BLOG,
    IndexCatType.GROUP_TOPIC,
    IndexCatType.SUBJECT_TOPIC,
)
@Retention(AnnotationRetention.SOURCE)
annotation class IndexCatType {
    companion object {
        const val SUBJECT = 0
        const val CHARACTER = 1
        const val PERSON = 2
        const val EP = 3
        const val BLOG = 4
        const val GROUP_TOPIC = 5
        const val SUBJECT_TOPIC = 6
    }
}
