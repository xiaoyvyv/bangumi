package com.xiaoyv.bangumi.shared.core.types.list

import androidx.annotation.IntDef

@IntDef(
    ListBlogType.UNKNOWN,
    ListBlogType.SEARCH,
    ListBlogType.BROWSER,
    ListBlogType.USER_CREATE,
    ListBlogType.SUBJECT_RELATED,
)
@Retention(AnnotationRetention.SOURCE)
annotation class ListBlogType {
    companion object Companion {
        const val UNKNOWN = 0
        const val SEARCH = 1
        const val BROWSER = 2
        const val USER_CREATE = 3
        const val SUBJECT_RELATED = 4
    }
}