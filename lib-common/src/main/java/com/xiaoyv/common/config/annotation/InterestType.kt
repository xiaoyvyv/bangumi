package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [InterestType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    InterestType.TYPE_UNKNOWN,
    InterestType.TYPE_WISH,
    InterestType.TYPE_COLLECT,
    InterestType.TYPE_DO,
    InterestType.TYPE_ON_HOLD,
    InterestType.TYPE_DROPPED
)
@Retention(AnnotationRetention.SOURCE)
annotation class InterestType {
    companion object {
        const val TYPE_UNKNOWN = "0"
        const val TYPE_WISH = "1"
        const val TYPE_COLLECT = "2"
        const val TYPE_DO = "3"
        const val TYPE_ON_HOLD = "4"
        const val TYPE_DROPPED = "5"

        fun string(@InterestType type: String): String {
            return when (type) {
                TYPE_WISH -> "想看"
                TYPE_COLLECT -> "看过"
                TYPE_DO -> "在看"
                TYPE_ON_HOLD -> "搁置"
                TYPE_DROPPED -> "抛弃"
                else -> ""
            }
        }
    }
}
