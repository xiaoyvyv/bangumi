package com.xiaoyv.common.config.annotation

import androidx.annotation.IntDef
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.i18n

/**
 * Class: [ScoreStarType]
 *
 * @author why
 * @since 11/25/23
 */
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

        fun string(@ScoreStarType type: Int): String {
            return when (type) {
                TYPE_1 -> i18n(CommonString.type_score_1)
                TYPE_2 -> i18n(CommonString.type_score_2)
                TYPE_3 -> i18n(CommonString.type_score_3)
                TYPE_4 -> i18n(CommonString.type_score_4)
                TYPE_5 -> i18n(CommonString.type_score_5)
                TYPE_6 -> i18n(CommonString.type_score_6)
                TYPE_7 -> i18n(CommonString.type_score_7)
                TYPE_8 -> i18n(CommonString.type_score_8)
                TYPE_9 -> i18n(CommonString.type_score_9)
                TYPE_10 -> i18n(CommonString.type_score_10)
                else -> i18n(CommonString.type_score_unset)

            }
        }
    }
}
