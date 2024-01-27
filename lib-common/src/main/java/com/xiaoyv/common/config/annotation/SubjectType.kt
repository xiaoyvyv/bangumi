package com.xiaoyv.common.config.annotation

import androidx.annotation.IntDef

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
                TYPE_BOOK -> "书籍"
                TYPE_ANIME -> "动画"
                TYPE_MUSIC -> "音乐"
                TYPE_GAME -> "游戏"
                TYPE_REAL -> "三次元"
                else -> "其它"
            }
        }
    }
}
