package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef

@StringDef(
    SubjectWebPath.ANIME,
    SubjectWebPath.BOOK,
    SubjectWebPath.MUSIC,
    SubjectWebPath.GAME,
    SubjectWebPath.REAL,
)
@Retention(AnnotationRetention.SOURCE)
annotation class SubjectWebPath {
    companion object Companion {
        const val ANIME = "anime"
        const val BOOK = "book"
        const val MUSIC = "music"
        const val GAME = "game"
        const val REAL = "real"

        fun from(@SubjectType type: Int): String {
            return when (type) {
                SubjectType.ANIME -> ANIME
                SubjectType.BOOK -> BOOK
                SubjectType.MUSIC -> MUSIC
                SubjectType.GAME -> GAME
                SubjectType.REAL -> REAL
                else -> ANIME
            }
        }
    }
}