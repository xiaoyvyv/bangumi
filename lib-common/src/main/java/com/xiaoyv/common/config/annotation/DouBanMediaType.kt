package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [DouBanMediaType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    DouBanMediaType.TYPE_MOVE,
    DouBanMediaType.TYPE_BOOK,
    DouBanMediaType.TYPE_MUSIC,
    DouBanMediaType.TYPE_GAME,
    DouBanMediaType.TYPE_DRAMA
)
@Retention(AnnotationRetention.SOURCE)
annotation class DouBanMediaType {
    companion object {
        const val TYPE_UNKNOWN = ""
        const val TYPE_MOVE = "movie"
        const val TYPE_BOOK = "book"
        const val TYPE_MUSIC = "music"
        const val TYPE_GAME = "game"
        const val TYPE_DRAMA = "drama"

        fun action(@DouBanMediaType mediaType: String): String {
            return when (mediaType) {
                TYPE_UNKNOWN -> "看"
                TYPE_MOVE -> "看"
                TYPE_BOOK -> "读"
                TYPE_MUSIC -> "听"
                TYPE_GAME -> "玩"
                TYPE_DRAMA -> "看"
                else -> "看"
            }
        }

        fun string(@DouBanMediaType type: String): String {
            return when (type) {
                TYPE_MOVE -> "影视"
                TYPE_BOOK -> "书籍"
                TYPE_MUSIC -> "音乐"
                TYPE_GAME -> "游戏"
                TYPE_DRAMA -> "舞台剧"
                else -> ""
            }
        }

        fun toSubjectType(@DouBanMediaType type: String?): Int {
            return when (type) {
                TYPE_MOVE -> SubjectType.TYPE_ANIME
                TYPE_BOOK -> SubjectType.TYPE_BOOK
                TYPE_MUSIC -> SubjectType.TYPE_MUSIC
                TYPE_GAME -> SubjectType.TYPE_GAME
                TYPE_DRAMA -> SubjectType.TYPE_REAL
                else -> SubjectType.TYPE_NONE
            }
        }
    }
}
