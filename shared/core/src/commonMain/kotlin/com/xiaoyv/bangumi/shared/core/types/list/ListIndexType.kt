package com.xiaoyv.bangumi.shared.core.types.list

import androidx.annotation.IntDef

@IntDef(
    ListIndexType.UNKNOWN,
    ListIndexType.SEARCH,
    ListIndexType.BROWSER,
    ListIndexType.USER_CREATE,
    ListIndexType.USER_COLLECTION,
    ListIndexType.SUBJECT_RELATED,
    ListIndexType.CHARACTER_RELATED,
    ListIndexType.PERSON_RELATED,
)
@Retention(AnnotationRetention.SOURCE)
annotation class ListIndexType {
    companion object Companion {
        const val UNKNOWN = 0
        const val SEARCH = 1
        const val BROWSER = 2
        const val USER_CREATE = 3
        const val USER_COLLECTION = 4
        const val SUBJECT_RELATED = 5
        const val CHARACTER_RELATED = 6
        const val PERSON_RELATED = 7
    }
}