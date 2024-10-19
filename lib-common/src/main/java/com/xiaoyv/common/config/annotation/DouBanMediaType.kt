package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.i18n

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
                TYPE_UNKNOWN -> i18n(CommonString.type_action_see)
                TYPE_MOVE -> i18n(CommonString.type_action_see)
                TYPE_BOOK -> i18n(CommonString.type_action_read)
                TYPE_MUSIC -> i18n(CommonString.type_action_listen)
                TYPE_GAME -> i18n(CommonString.type_action_play)
                TYPE_DRAMA -> i18n(CommonString.type_action_see)
                else -> i18n(CommonString.type_action_see)
            }
        }

        fun string(@DouBanMediaType type: String): String {
            return when (type) {
                TYPE_MOVE -> i18n(CommonString.common_anime)
                TYPE_BOOK -> i18n(CommonString.common_book)
                TYPE_MUSIC -> i18n(CommonString.common_music)
                TYPE_GAME -> i18n(CommonString.common_game)
                TYPE_DRAMA -> i18n(CommonString.common_drama)
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
