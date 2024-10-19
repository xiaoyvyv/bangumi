package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.i18n

/**
 * Class: [DouBanInterestType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    DouBanInterestType.TYPE_UNKNOWN,
    DouBanInterestType.TYPE_WISH,
    DouBanInterestType.TYPE_DONE,
    DouBanInterestType.TYPE_DOING,
)
@Retention(AnnotationRetention.SOURCE)
annotation class DouBanInterestType {
    companion object {
        const val TYPE_UNKNOWN = ""
        const val TYPE_WISH = "mark"
        const val TYPE_DOING = "doing"
        const val TYPE_DONE = "done"

        fun string(@DouBanInterestType type: String, @DouBanMediaType mediaType: String): String {
            return when (type) {
                TYPE_WISH -> i18n(
                    CommonString.type_interest_wish,
                    DouBanMediaType.action(mediaType)
                )

                TYPE_DONE -> i18n(
                    CommonString.type_interest_done,
                    DouBanMediaType.action(mediaType)
                )

                TYPE_DOING -> i18n(
                    CommonString.type_interest_doing,
                    DouBanMediaType.action(mediaType)
                )

                else -> ""
            }
        }

        fun toInterest(@DouBanInterestType type: String): String {
            return when (type) {
                TYPE_WISH -> InterestType.TYPE_WISH
                TYPE_DOING -> InterestType.TYPE_DO
                TYPE_DONE -> InterestType.TYPE_COLLECT
                else -> InterestType.TYPE_UNKNOWN
            }
        }
    }
}
