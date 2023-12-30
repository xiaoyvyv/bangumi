package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [MediaDetailType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    MediaDetailType.TYPE_OVERVIEW,
    MediaDetailType.TYPE_CHAPTER,
    MediaDetailType.TYPE_CHARACTER,
    MediaDetailType.TYPE_MAKER,
    MediaDetailType.TYPE_RATING,
    MediaDetailType.TYPE_BLOG,
    MediaDetailType.TYPE_TOPIC,
    MediaDetailType.TYPE_STATS,
)
@Retention(AnnotationRetention.SOURCE)
annotation class MediaDetailType {
    companion object {
        const val TYPE_OVERVIEW = ""
        const val TYPE_CHAPTER = "/ep"
        const val TYPE_CHARACTER = "/characters"
        const val TYPE_MAKER = "/persons"
        const val TYPE_RATING = "/comments"
        const val TYPE_BLOG = "/reviews"
        const val TYPE_TOPIC = "/board"
        const val TYPE_STATS = "/stats"
    }
}
