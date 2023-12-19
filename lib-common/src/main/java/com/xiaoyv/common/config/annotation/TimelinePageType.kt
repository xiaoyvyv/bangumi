package com.xiaoyv.common.config.annotation

import androidx.annotation.IntDef

/**
 * Class: [TimelinePageType]
 *
 * @author why
 * @since 11/25/23
 */
@IntDef(
    TimelinePageType.TYPE_ALL,
    TimelinePageType.TYPE_FRIEND,
    TimelinePageType.TYPE_MINE
)
@Retention(AnnotationRetention.SOURCE)
annotation class TimelinePageType {
    companion object {
        const val TYPE_ALL = 0
        const val TYPE_FRIEND = 1
        const val TYPE_MINE = 2
    }
}
