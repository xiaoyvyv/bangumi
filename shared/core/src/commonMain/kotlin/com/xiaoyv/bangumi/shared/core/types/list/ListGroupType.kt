package com.xiaoyv.bangumi.shared.core.types.list

import androidx.annotation.IntDef

@IntDef(
    ListGroupType.UNKNOWN,
    ListGroupType.BROWSER,
    ListGroupType.USER,
)
@Retention(AnnotationRetention.SOURCE)
annotation class ListGroupType {
    companion object Companion {
        const val UNKNOWN = 0
        const val BROWSER = 2
        const val USER = 3
    }
}