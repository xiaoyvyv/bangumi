package com.xiaoyv.common.config.annotation

import androidx.annotation.IntDef

/**
 * Class: [EpCollectType]
 *
 * @author why
 * @since 11/25/23
 */
@IntDef(
    EpCollectType.TYPE_NONE,
    EpCollectType.TYPE_WISH,
    EpCollectType.TYPE_COLLECT,
    EpCollectType.TYPE_DROPPED
)
@Retention(AnnotationRetention.SOURCE)
annotation class EpCollectType {
    companion object {
        const val TYPE_NONE = 0
        const val TYPE_WISH = 1
        const val TYPE_COLLECT = 2
        const val TYPE_DROPPED = 3

        fun toInterestType(type: Int): String {
            return when (type) {
                TYPE_NONE -> InterestType.TYPE_UNKNOWN
                TYPE_WISH -> InterestType.TYPE_WISH
                TYPE_COLLECT -> InterestType.TYPE_COLLECT
                TYPE_DROPPED -> InterestType.TYPE_DROPPED
                else -> InterestType.TYPE_UNKNOWN
            }
        }
    }
}
