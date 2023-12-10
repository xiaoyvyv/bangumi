package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

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
    }
}