package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.LongDef


/**
 */
@LongDef(
    PersonPositionType.UNKNOWN,
    PersonPositionType.MAIN,
)
@Retention(AnnotationRetention.SOURCE)
annotation class PersonPositionType {
    companion object {
        const val UNKNOWN = 0L
        const val MAIN = 1L
    }
}
