package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.i18n

/**
 * Class: [MagiType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    MagiType.TYPE_ANIME,
    MagiType.TYPE_BOOK,
    MagiType.TYPE_MUSIC,
    MagiType.TYPE_GAME,
    MagiType.TYPE_REAL,
    MagiType.TYPE_UNKNOWN,
    MagiType.TYPE_OTHER,
    MagiType.TYPE_MONO,
)
@Retention(AnnotationRetention.SOURCE)
annotation class MagiType {
    companion object {
        const val TYPE_UNKNOWN = ""
        const val TYPE_ANIME = "anime"
        const val TYPE_BOOK = "book"
        const val TYPE_MUSIC = "music"
        const val TYPE_GAME = "game"
        const val TYPE_REAL = "real"
        const val TYPE_OTHER = "other"
        const val TYPE_MONO = "mono"

        fun string(@MagiType type: String): String {
            return when (type) {
                TYPE_ANIME -> i18n(CommonString.common_anime)
                TYPE_BOOK -> i18n(CommonString.common_book)
                TYPE_MUSIC -> i18n(CommonString.common_music)
                TYPE_GAME -> i18n(CommonString.common_game)
                TYPE_REAL ->i18n(CommonString.common_real)
                TYPE_OTHER -> i18n(CommonString.common_other)
                TYPE_MONO -> i18n(CommonString.common_mono)
                else -> i18n(CommonString.common_all)
            }
        }

        fun items(): List<Pair<String, String>> {
            return listOf(
                TYPE_ANIME to string(TYPE_ANIME),
                TYPE_BOOK to string(TYPE_BOOK),
                TYPE_MUSIC to string(TYPE_MUSIC),
                TYPE_GAME to string(TYPE_GAME),
                TYPE_REAL to string(TYPE_REAL),
                TYPE_UNKNOWN to string(TYPE_UNKNOWN),
                TYPE_OTHER to string(TYPE_OTHER),
                TYPE_MONO to string(TYPE_MONO),
            )
        }
    }
}