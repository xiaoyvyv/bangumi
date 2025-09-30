package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef

/**
 * [SubjectSortSearchType]
 *
 * - match = 匹配程度
 * - heat = 收藏人数
 * - rank = 排名由高到低
 * - score = 评分
 *
 * @author why
 * @since 2025/1/25
 */
@StringDef(
    SubjectSortSearchType.RANK,
    SubjectSortSearchType.COLLECTS,
    SubjectSortSearchType.SCORE,
    SubjectSortSearchType.MATCH,
)
@Retention(AnnotationRetention.SOURCE)
annotation class SubjectSortSearchType {
    companion object Companion {
        const val MATCH = "match"
        const val SCORE = "score"
        const val COLLECTS = "heat"
        const val RANK = "rank"
    }
}

