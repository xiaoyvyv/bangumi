package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [IndexTabCatType]
 *
 * @author why
 * @since 12/10/23
 */
@StringDef(
    IndexTabCatType.TYPE_ALL,
    IndexTabCatType.TYPE_ANIME,
    IndexTabCatType.TYPE_BOOK,
    IndexTabCatType.TYPE_MUSIC,
    IndexTabCatType.TYPE_GAME,
    IndexTabCatType.TYPE_REAL,
    IndexTabCatType.TYPE_CHARACTER,
    IndexTabCatType.TYPE_PERSON,
    IndexTabCatType.TYPE_EP
)
@Retention(AnnotationRetention.SOURCE)
annotation class IndexTabCatType {
    companion object {
        const val TYPE_ALL = ""
        const val TYPE_BOOK = "1"
        const val TYPE_ANIME = "2"
        const val TYPE_MUSIC = "3"
        const val TYPE_GAME = "4"
        const val TYPE_REAL = "6"
        const val TYPE_CHARACTER = "character"
        const val TYPE_PERSON = "person"
        const val TYPE_EP = "ep"
    }
}