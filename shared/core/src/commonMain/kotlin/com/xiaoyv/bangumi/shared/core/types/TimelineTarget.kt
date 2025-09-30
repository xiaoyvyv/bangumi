package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef

/**
 * [TimelineTarget]
 *
 * @since 2025/5/11
 */
@StringDef(
    TimelineTarget.WHOLE,
    TimelineTarget.USER,
    TimelineTarget.FRIEND,
)
@Retention(AnnotationRetention.SOURCE)
annotation class TimelineTarget {
    companion object {
        const val WHOLE = "all"
        const val FRIEND = "friends"
        const val USER = "user"
    }
}
