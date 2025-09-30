package com.xiaoyv.bangumi.shared.core.types.list

import androidx.annotation.IntDef

@IntDef(
    ListTopicType.UNKNOWN,
    ListTopicType.SUBJECT_ALL,
    ListTopicType.SUBJECT_TARGET,
    ListTopicType.GROUP_TARGET,
    ListTopicType.GROUP_ALL,
    ListTopicType.SEARCH,
)
@Retention(AnnotationRetention.SOURCE)
annotation class ListTopicType {
    companion object Companion {
        const val UNKNOWN = 0
        const val SUBJECT_ALL = 1
        const val SUBJECT_TARGET = 2
        const val GROUP_TARGET = 3
        const val GROUP_ALL = 4
        const val SEARCH = 5
    }
}