package com.xiaoyv.common.config.annotation

import androidx.annotation.IntDef

/**
 * Class: [BilibiliInterestType]
 *
 * @author why
 * @since 11/25/23
 */
@IntDef(
    BilibiliInterestType.TYPE_ALL,
    BilibiliInterestType.TYPE_WISH,
    BilibiliInterestType.TYPE_DONE,
    BilibiliInterestType.TYPE_DOING,
)
@Retention(AnnotationRetention.SOURCE)
annotation class BilibiliInterestType {
    companion object {
        const val TYPE_ALL = 0
        const val TYPE_WISH = 1
        const val TYPE_DOING = 2
        const val TYPE_DONE = 3

        fun string(@BilibiliInterestType type: Int): String {
            return when (type) {
                TYPE_WISH -> "想看"
                TYPE_DOING -> "在看"
                TYPE_DONE -> "看过"
                else -> ""
            }
        }

        fun toInterest(@BilibiliInterestType type: Int): String {
            return when (type) {
                TYPE_WISH -> InterestType.TYPE_WISH
                TYPE_DOING -> InterestType.TYPE_DO
                TYPE_DONE -> InterestType.TYPE_COLLECT
                else -> InterestType.TYPE_UNKNOWN
            }
        }
    }
}
