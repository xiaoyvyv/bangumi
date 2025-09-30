package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef
import androidx.compose.runtime.Composable
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_unknown
import com.xiaoyv.bangumi.core_resource.resources.type_interest_done
import com.xiaoyv.bangumi.core_resource.resources.type_interest_drop
import com.xiaoyv.bangumi.core_resource.resources.type_interest_wish
import org.jetbrains.compose.resources.stringResource

/**
 * - 0 = 撤消/删除
 * - 1 = 想看
 * - 2 = 看过
 * - 3 = 抛弃
 *
 * @author why
 * @since 11/25/23
 */
@IntDef(
    CollectionEpisodeType.UNKNOWN,
    CollectionEpisodeType.WISH,
    CollectionEpisodeType.DONE,
    CollectionEpisodeType.DROPPED
)
@Retention(AnnotationRetention.SOURCE)
annotation class CollectionEpisodeType {
    companion object {
        const val UNKNOWN = 0
        const val WISH = 1
        const val DONE = 2
        const val DROPPED = 3


        @Composable
        fun string(@SubjectType subjectType: Int, @CollectionEpisodeType epStatus: Int) =
            when (epStatus) {
                WISH -> stringResource(
                    Res.string.type_interest_wish,
                    stringResource(SubjectType.action(subjectType))
                )

                DONE -> stringResource(
                    Res.string.type_interest_done,
                    stringResource(SubjectType.action(subjectType))
                )

                DROPPED -> stringResource(
                    Res.string.type_interest_drop,
                    stringResource(SubjectType.action(subjectType))
                )

                else -> stringResource(Res.string.global_unknown)
            }
    }
}
