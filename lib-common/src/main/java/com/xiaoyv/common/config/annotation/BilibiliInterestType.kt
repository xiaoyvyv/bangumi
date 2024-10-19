package com.xiaoyv.common.config.annotation

import androidx.annotation.IntDef
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.i18n

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
                TYPE_WISH -> i18n(CommonString.type_bilibili_wish)
                TYPE_DOING -> i18n(CommonString.type_bilibili_doing)
                TYPE_DONE -> i18n(CommonString.type_bilibili_done)
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
