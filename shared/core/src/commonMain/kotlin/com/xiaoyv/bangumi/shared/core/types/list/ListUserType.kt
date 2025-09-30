package com.xiaoyv.bangumi.shared.core.types.list

import androidx.annotation.IntDef

@IntDef(
    ListUserType.UNKNOWN,
    ListUserType.USER_FRIEND,
    ListUserType.USER_FOLLOWER,
    ListUserType.USER_BLOCKLIST,
    ListUserType.GROUP_MEMBER,
    ListUserType.CHARACTER_COLLECT,
    ListUserType.PERSON_COLLECT,
    ListUserType.SUBJECT_COLLECT
)
@Retention(AnnotationRetention.SOURCE)
annotation class ListUserType {
    companion object Companion {
        const val UNKNOWN = 0
        const val USER_FRIEND = 1
        const val USER_FOLLOWER = 2
        const val USER_BLOCKLIST = 3
        const val GROUP_MEMBER = 4
        const val CHARACTER_COLLECT = 5
        const val PERSON_COLLECT = 6
        const val SUBJECT_COLLECT = 7
    }
}