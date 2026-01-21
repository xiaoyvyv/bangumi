package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_sort_bookmark
import com.xiaoyv.bangumi.core_resource.resources.global_sort_date
import com.xiaoyv.bangumi.core_resource.resources.global_sort_rank
import com.xiaoyv.bangumi.core_resource.resources.global_sort_title
import com.xiaoyv.bangumi.core_resource.resources.global_sort_trends
import com.xiaoyv.bangumi.core_resource.resources.global_unknown
import org.jetbrains.compose.resources.StringResource

/**
 * [SubjectSortBrowserType]
 *
 * - title 按照名称
 * - collects 收藏人数
 * - rank 排名由高到低
 * - trends 热门
 * - date 日期，仅在浏览条目接口有效
 *
 * @author why
 * @since 2025/1/25
 */
@StringDef(
    SubjectSortBrowserType.TITLE,
    SubjectSortBrowserType.RANK,
    SubjectSortBrowserType.COLLECTS,
    SubjectSortBrowserType.TRENDS,
    SubjectSortBrowserType.DATE,
)
@Retention(AnnotationRetention.SOURCE)
annotation class SubjectSortBrowserType {
    companion object Companion {
        const val TITLE = "title"
        const val RANK = "rank"
        const val TRENDS = "trends"
        const val COLLECTS = "collects"
        const val DATE = "date"

        fun string(@SubjectSortBrowserType type: String): StringResource {
            return when (type) {
                TITLE -> Res.string.global_sort_title
                RANK -> Res.string.global_sort_rank
                TRENDS -> Res.string.global_sort_trends
                COLLECTS -> Res.string.global_sort_bookmark
                DATE -> Res.string.global_sort_date
                else -> Res.string.global_unknown
            }
        }

        fun toSearchType(@SubjectSortBrowserType type: String): String {
            return when (type) {
                TITLE -> SubjectSortSearchType.MATCH
                RANK -> SubjectSortSearchType.RANK
                TRENDS -> SubjectSortSearchType.SCORE
                COLLECTS -> SubjectSortSearchType.COLLECTS
                else -> SubjectSortSearchType.MATCH
            }
        }
    }
}
