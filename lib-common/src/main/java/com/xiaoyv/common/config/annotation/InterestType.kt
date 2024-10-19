package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.i18n

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

        fun string(@InterestType type: String, @MediaType mediaType: String): String {
            return when (type) {
                TYPE_WISH -> i18n(CommonString.type_interest_wish, MediaType.action(mediaType))
                TYPE_COLLECT -> i18n(CommonString.type_interest_done, MediaType.action(mediaType))
                TYPE_DO -> i18n(CommonString.type_interest_doing, MediaType.action(mediaType))
                TYPE_ON_HOLD -> i18n(CommonString.type_interest_hold)
                TYPE_DROPPED -> i18n(CommonString.type_interest_drop)
                else -> ""
            }
        }

        fun toIntType(@InterestType type: String): Int {
            return when (type) {
                TYPE_WISH -> SubjectCollectType.TYPE_WISH
                TYPE_COLLECT -> SubjectCollectType.TYPE_COLLECT
                TYPE_DO -> SubjectCollectType.TYPE_DOING
                TYPE_ON_HOLD -> SubjectCollectType.TYPE_ON_HOLD
                TYPE_DROPPED -> SubjectCollectType.TYPE_DOING
                else -> SubjectCollectType.TYPE_NONE
            }
        }
    }
}
