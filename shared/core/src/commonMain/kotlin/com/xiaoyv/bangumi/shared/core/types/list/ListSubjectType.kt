@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.core.types.list

import androidx.annotation.IntDef

@IntDef(
    ListSubjectType.UNKNOWN,
    ListSubjectType.SEARCH,
    ListSubjectType.BROWSER,
    ListSubjectType.USER_COLLECTION,
    ListSubjectType.PERSON_WORK,
    ListSubjectType.SUBJECT_RELATED,
)
@Retention(AnnotationRetention.SOURCE)
annotation class ListSubjectType {
    companion object Companion {
        const val UNKNOWN = 0
        const val SEARCH = 1
        const val BROWSER = 2
        const val USER_COLLECTION = 3
        const val PERSON_WORK = 4
        const val SUBJECT_RELATED = 5
    }
}