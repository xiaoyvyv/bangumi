package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [MediaType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    MediaType.TYPE_UNKNOWN,
    MediaType.TYPE_ANIME,
    MediaType.TYPE_BOOK,
    MediaType.TYPE_MUSIC,
    MediaType.TYPE_GAME,
    MediaType.TYPE_REAL
)
@Retention(AnnotationRetention.SOURCE)
annotation class MediaType {
    companion object {
        const val TYPE_UNKNOWN = ""
        const val TYPE_ANIME = "anime"
        const val TYPE_BOOK = "book"
        const val TYPE_MUSIC = "music"
        const val TYPE_GAME = "game"
        const val TYPE_REAL = "real"

        fun action(@MediaType mediaType: String): String {
            return when (mediaType) {
                TYPE_UNKNOWN -> ""
                TYPE_ANIME -> "看"
                TYPE_BOOK -> "读"
                TYPE_MUSIC -> "听"
                TYPE_GAME -> "玩"
                TYPE_REAL -> "看"
                else -> ""
            }
        }
    }
}
