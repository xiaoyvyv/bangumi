package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef

/**
 * [SortType]
 *
 * @author why
 * @since 2025/1/16
 */
@IntDef(
    SortType.NEWEST,
    SortType.OLDEST,
    SortType.HOT,
)
@Retention(AnnotationRetention.SOURCE)
annotation class SortType {
    companion object {
        const val NEWEST = 0
        const val OLDEST = 1
        const val HOT = 2
    }
}
