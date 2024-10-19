package com.xiaoyv.common.config.annotation

import androidx.annotation.IntDef
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.i18n

/**
 * Class: [SubjectType]
 *
 * @author why
 * @since 11/25/23
 */
@IntDef(
    SubjectType.TYPE_NONE,
    SubjectType.TYPE_BOOK,
    SubjectType.TYPE_ANIME,
    SubjectType.TYPE_MUSIC,
    SubjectType.TYPE_GAME,
    SubjectType.TYPE_REAL
)
@Retention(AnnotationRetention.SOURCE)
annotation class SubjectType {
    companion object {
        const val TYPE_NONE = 0
        const val TYPE_BOOK = 1
        const val TYPE_ANIME = 2
        const val TYPE_MUSIC = 3
        const val TYPE_GAME = 4
        const val TYPE_REAL = 6

        fun string(@SubjectType type: Int?): String {
            return when (type) {
                TYPE_BOOK -> i18n(CommonString.common_book)
                TYPE_ANIME -> i18n(CommonString.common_anime)
                TYPE_MUSIC -> i18n(CommonString.common_music)
                TYPE_GAME -> i18n(CommonString.common_game)
                TYPE_REAL -> i18n(CommonString.common_real)
                else -> i18n(CommonString.common_other)
            }
        }
    }
}
