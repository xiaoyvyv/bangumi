package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef

@StringDef(
    CollectionWebSortType.RATE,
    CollectionWebSortType.DATE,
    CollectionWebSortType.COLLECT_TIME,
    CollectionWebSortType.TITLE,
)
@Retention(AnnotationRetention.SOURCE)
annotation class CollectionWebSortType {
    companion object Companion {
        const val RATE = "rate"
        const val COLLECT_TIME = "update"
        const val DATE = "date"
        const val TITLE = "title"
    }
}