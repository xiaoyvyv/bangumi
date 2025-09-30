package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef

/**
 * [SubjectDetailTab]
 *
 * @since 2025/5/11
 */
@IntDef(
    SubjectDetailTab.OVERVIEW,
    SubjectDetailTab.EPISODE,
    SubjectDetailTab.CHARACTER,
    SubjectDetailTab.PERSON,
    SubjectDetailTab.BLOG,
    SubjectDetailTab.TOPIC,
    SubjectDetailTab.PREVIEW,
    SubjectDetailTab.CHART,
    SubjectDetailTab.RELATED,
    SubjectDetailTab.INDEX,
)
@Retention(AnnotationRetention.SOURCE)
annotation class SubjectDetailTab {
    companion object {
        const val OVERVIEW = 0
        const val EPISODE = 1
        const val CHARACTER = 2
        const val PERSON = 3
        const val BLOG = 4
        const val TOPIC = 5
        const val PREVIEW = 6
        const val CHART = 7
        const val RELATED = 8
        const val INDEX = 9
    }
}
