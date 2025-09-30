package com.xiaoyv.bangumi.shared.core.types.list

import androidx.annotation.IntDef

@IntDef(
    ListTagType.UNKNOWN,
    ListTagType.SEARCH,
    ListTagType.BROWSER
)
@Retention(AnnotationRetention.SOURCE)
annotation class ListTagType {
    companion object Companion {
        const val UNKNOWN = 0
        const val SEARCH = 1
        const val BROWSER = 2
    }
}