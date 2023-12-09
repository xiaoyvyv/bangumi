package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [SearchCatType]
 *
 * @author why
 * @since 12/10/23
 */
@StringDef(
    SearchCatType.TYPE_ALL,
    SearchCatType.TYPE_ANIME,
    SearchCatType.TYPE_BOOK,
    SearchCatType.TYPE_MUSIC,
    SearchCatType.TYPE_GAME,
    SearchCatType.TYPE_REAL,
    SearchCatType.TYPE_CHARACTER,
    SearchCatType.TYPE_PERSON,
    SearchCatType.TYPE_KEYWORD
)
@Retention(AnnotationRetention.SOURCE)
annotation class SearchCatType {
    companion object {
        const val TYPE_ALL = "all"
        const val TYPE_ANIME = "2"
        const val TYPE_BOOK = "1"
        const val TYPE_MUSIC = "3"
        const val TYPE_GAME = "4"
        const val TYPE_REAL = "6"
        const val TYPE_CHARACTER = "crt"
        const val TYPE_PERSON = "prsn"

        /**
         * 用户输入关键字
         */
        const val TYPE_KEYWORD = "keyword"
    }
}