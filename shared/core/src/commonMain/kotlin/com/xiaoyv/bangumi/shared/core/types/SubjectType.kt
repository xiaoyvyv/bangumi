package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_all
import com.xiaoyv.bangumi.core_resource.resources.global_anime
import com.xiaoyv.bangumi.core_resource.resources.global_book
import com.xiaoyv.bangumi.core_resource.resources.global_game
import com.xiaoyv.bangumi.core_resource.resources.global_music
import com.xiaoyv.bangumi.core_resource.resources.global_real
import com.xiaoyv.bangumi.core_resource.resources.type_action_listen
import com.xiaoyv.bangumi.core_resource.resources.type_action_play
import com.xiaoyv.bangumi.core_resource.resources.type_action_read
import com.xiaoyv.bangumi.core_resource.resources.type_action_see
import org.jetbrains.compose.resources.StringResource

/**
 * [SubjectType]
 *
 * - 1 为 书籍
 * - 2 为 动画
 * - 3 为 音乐
 * - 4 为 游戏
 * - 6 为 三次元
 *
 * @author why
 * @since 2025/1/16
 */
@IntDef(
    SubjectType.BOOK,
    SubjectType.ANIME,
    SubjectType.MUSIC,
    SubjectType.GAME,
    SubjectType.REAL,
    SubjectType.UNKNOWN,
)
@Retention(AnnotationRetention.SOURCE)
annotation class SubjectType {
    companion object {
        const val UNKNOWN = 0
        const val BOOK = 1
        const val ANIME = 2
        const val MUSIC = 3
        const val GAME = 4
        const val REAL = 6

        fun string(@SubjectType type: Int): StringResource {
            return when (type) {
                BOOK -> Res.string.global_book
                ANIME -> Res.string.global_anime
                MUSIC -> Res.string.global_music
                GAME -> Res.string.global_game
                REAL -> Res.string.global_real
                else -> Res.string.global_all
            }
        }

        fun action(@SubjectType mediaType: Int): StringResource {
            return when (mediaType) {
                UNKNOWN -> Res.string.type_action_see
                ANIME -> Res.string.type_action_see
                BOOK -> Res.string.type_action_read
                MUSIC -> Res.string.type_action_listen
                GAME -> Res.string.type_action_play
                REAL -> Res.string.type_action_see
                else -> Res.string.type_action_see
            }
        }
    }
}
