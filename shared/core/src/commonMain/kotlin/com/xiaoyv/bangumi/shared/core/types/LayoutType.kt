package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef

/**
 * [LayoutType]
 */
@IntDef(
    LayoutType.LIST,
    LayoutType.GRID,
)
@Retention(AnnotationRetention.SOURCE)
annotation class LayoutType {
    companion object {
        const val LIST = 0
        const val GRID = 1
    }
}
