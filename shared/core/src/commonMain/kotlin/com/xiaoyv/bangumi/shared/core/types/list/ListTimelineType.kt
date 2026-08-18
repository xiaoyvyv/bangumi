package com.xiaoyv.bangumi.shared.core.types.list

import androidx.annotation.IntDef

@IntDef(
    ListTimelineType.UNKNOWN,
    ListTimelineType.BROWSER,
    ListTimelineType.USER,
)
@Retention(AnnotationRetention.SOURCE)
annotation class ListTimelineType {
    companion object Companion {
        const val UNKNOWN = 0
        const val BROWSER = 1
        const val USER = 3
    }
}