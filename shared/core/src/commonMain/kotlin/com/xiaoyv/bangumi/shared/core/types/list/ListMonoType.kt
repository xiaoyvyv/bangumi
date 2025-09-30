package com.xiaoyv.bangumi.shared.core.types.list

import androidx.annotation.IntDef

@IntDef(
    ListMonoType.UNKNOWN,
    ListMonoType.BROWSER,
    ListMonoType.USER_COLLECTION,
    ListMonoType.SUBJECT_CHARACTER,
    ListMonoType.SUBJECT_PERSON,
    ListMonoType.SEARCH_CHARACTER,
    ListMonoType.SEARCH_PERSON,
)
@Retention(AnnotationRetention.SOURCE)
annotation class ListMonoType {
    companion object Companion {
        const val UNKNOWN = 0
        const val BROWSER = 1
        const val USER_COLLECTION = 2
        const val SUBJECT_CHARACTER = 3
        const val SUBJECT_PERSON = 4
        const val SEARCH_PERSON = 5
        const val SEARCH_CHARACTER = 6
    }
}