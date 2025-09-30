package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef
import androidx.compose.runtime.Composable
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_unknown
import com.xiaoyv.bangumi.core_resource.resources.type_interest_doing
import com.xiaoyv.bangumi.core_resource.resources.type_interest_done
import com.xiaoyv.bangumi.core_resource.resources.type_interest_drop
import com.xiaoyv.bangumi.core_resource.resources.type_interest_hold
import com.xiaoyv.bangumi.core_resource.resources.type_interest_wish
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

/**
 * [CollectionType]
 *
 * - 1: 想看
 * - 2: 看过
 * - 3: 在看
 * - 4: 搁置
 * - 5: 抛弃
 *
 * @author why
 * @since 2025/1/16
 */
@IntDef(
    CollectionType.WISH,
    CollectionType.DONE,
    CollectionType.DOING,
    CollectionType.ASIDE,
    CollectionType.DROP,
    CollectionType.UNKNOWN,
)
@Retention(AnnotationRetention.SOURCE)
annotation class CollectionType {
    companion object {
        const val UNKNOWN = 0
        const val WISH = 1
        const val DONE = 2
        const val DOING = 3
        const val ASIDE = 4
        const val DROP = 5

        @CollectionType
        fun from(text: String): Int {
            return when (text.trim()) {
                "想看", "想玩", "想读", "想听" -> WISH
                "看过", "玩过", "读过", "听过" -> DONE
                "在看", "在玩", "在读", "在听" -> DOING
                "搁置" -> ASIDE
                "抛弃" -> DROP
                else -> UNKNOWN
            }
        }

        @Composable
        fun string(@SubjectType subjectType: Int, @CollectionType collectType: Int) =
            when (collectType) {
                WISH -> stringResource(
                    Res.string.type_interest_wish,
                    stringResource(SubjectType.action(subjectType))
                )

                DONE -> stringResource(
                    Res.string.type_interest_done,
                    stringResource(SubjectType.action(subjectType))
                )

                DOING -> stringResource(
                    Res.string.type_interest_doing,
                    stringResource(SubjectType.action(subjectType))
                )

                ASIDE -> stringResource(Res.string.type_interest_hold)
                DROP -> stringResource(Res.string.type_interest_drop)
                else -> stringResource(Res.string.global_unknown)
            }

        suspend fun stringSync(@SubjectType subjectType: Int, @CollectionType collectType: Int) =
            when (collectType) {
                WISH -> getString(
                    Res.string.type_interest_wish,
                    getString(SubjectType.action(subjectType))
                )

                DONE -> getString(
                    Res.string.type_interest_done,
                    getString(SubjectType.action(subjectType))
                )

                DOING -> getString(
                    Res.string.type_interest_doing,
                    getString(SubjectType.action(subjectType))
                )

                ASIDE -> getString(Res.string.type_interest_hold)
                DROP -> getString(Res.string.type_interest_drop)
                else -> getString(Res.string.global_unknown)
            }
    }
}
