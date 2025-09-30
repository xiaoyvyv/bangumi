package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef
import androidx.compose.runtime.Composable
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.type_collection_score_1
import com.xiaoyv.bangumi.core_resource.resources.type_collection_score_10
import com.xiaoyv.bangumi.core_resource.resources.type_collection_score_2
import com.xiaoyv.bangumi.core_resource.resources.type_collection_score_3
import com.xiaoyv.bangumi.core_resource.resources.type_collection_score_4
import com.xiaoyv.bangumi.core_resource.resources.type_collection_score_5
import com.xiaoyv.bangumi.core_resource.resources.type_collection_score_6
import com.xiaoyv.bangumi.core_resource.resources.type_collection_score_7
import com.xiaoyv.bangumi.core_resource.resources.type_collection_score_8
import com.xiaoyv.bangumi.core_resource.resources.type_collection_score_9
import com.xiaoyv.bangumi.core_resource.resources.type_collection_score_unset
import org.jetbrains.compose.resources.stringResource

@IntDef(
    ScoreStarType.TYPE_0,
    ScoreStarType.TYPE_1,
    ScoreStarType.TYPE_2,
    ScoreStarType.TYPE_3,
    ScoreStarType.TYPE_4,
    ScoreStarType.TYPE_5,
    ScoreStarType.TYPE_6,
    ScoreStarType.TYPE_7,
    ScoreStarType.TYPE_8,
    ScoreStarType.TYPE_9,
    ScoreStarType.TYPE_10,
)
@Retention(AnnotationRetention.SOURCE)
annotation class ScoreStarType {
    companion object {
        const val TYPE_0 = 0
        const val TYPE_1 = 1
        const val TYPE_2 = 2
        const val TYPE_3 = 3
        const val TYPE_4 = 4
        const val TYPE_5 = 5
        const val TYPE_6 = 6
        const val TYPE_7 = 7
        const val TYPE_8 = 8
        const val TYPE_9 = 9
        const val TYPE_10 = 10

        @Composable
        fun string(@ScoreStarType type: Int): String {
            return when (type) {
                TYPE_1 -> stringResource(Res.string.type_collection_score_1)
                TYPE_2 -> stringResource(Res.string.type_collection_score_2)
                TYPE_3 -> stringResource(Res.string.type_collection_score_3)
                TYPE_4 -> stringResource(Res.string.type_collection_score_4)
                TYPE_5 -> stringResource(Res.string.type_collection_score_5)
                TYPE_6 -> stringResource(Res.string.type_collection_score_6)
                TYPE_7 -> stringResource(Res.string.type_collection_score_7)
                TYPE_8 -> stringResource(Res.string.type_collection_score_8)
                TYPE_9 -> stringResource(Res.string.type_collection_score_9)
                TYPE_10 -> stringResource(Res.string.type_collection_score_10)
                else -> stringResource(Res.string.type_collection_score_unset)

            }
        }
    }
}