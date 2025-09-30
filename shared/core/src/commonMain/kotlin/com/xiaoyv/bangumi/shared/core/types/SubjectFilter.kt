package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef

/**
 * [SubjectFilter]
 *
 * @author why
 * @since 2025/1/16
 */
@IntDef(
    SubjectFilter.TYPE,
    SubjectFilter.DATE,
    SubjectFilter.RATING,
    SubjectFilter.RANK,
    SubjectFilter.NSFW
)
@Retention(AnnotationRetention.SOURCE)
annotation class SubjectFilter {
    companion object {
        const val TYPE = 1
        const val DATE = 2
        const val RATING = 3
        const val RANK = 4
        const val NSFW = 6
    }
}
