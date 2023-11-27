package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [BrowserSortType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    BrowserSortType.TYPE_RANK,
    BrowserSortType.TYPE_DATE,
    BrowserSortType.TYPE_TITLE,
    BrowserSortType.TYPE_RATE,
    BrowserSortType.TYPE_DEFAULT
)
@Retention(AnnotationRetention.SOURCE)
annotation class BrowserSortType {
    companion object {
        const val TYPE_RANK = "rank"
        const val TYPE_DATE = "date"
        const val TYPE_TITLE = "title"

        const val TYPE_RATE = "rate"
        const val TYPE_DEFAULT = ""
    }
}