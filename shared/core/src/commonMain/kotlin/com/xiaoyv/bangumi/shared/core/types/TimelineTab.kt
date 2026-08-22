package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef

/**
 * [TimelineTab]
 *
 * @since 2025/5/11
 */
@IntDef(
    TimelineTab.TIMELINE_ANYONE,
    TimelineTab.TIMELINE_SELF,
    TimelineTab.TIMELINE_FRIEND
)
@Retention(AnnotationRetention.SOURCE)
annotation class TimelineTab {
    companion object {
        const val TIMELINE_ANYONE = 0
        const val TIMELINE_FRIEND = 1
        const val TIMELINE_SELF = 2
    }
}
