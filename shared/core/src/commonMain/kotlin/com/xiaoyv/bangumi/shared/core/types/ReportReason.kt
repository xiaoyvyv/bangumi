package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef

/**
 * 举报原因
 *
 * - 1 = 辱骂、人身攻击
 * - 2 = 刷屏、无关内容
 * - 3 = 政治相关
 * - 4 = 违法信息
 * - 5 = 泄露隐私
 * - 6 = 涉嫌刷分
 * - 7 = 引战
 * - 8 = 广告
 * - 9 = 剧透
 * - 99 = 其他
 *
 * @author why
 */
@IntDef(
    ReportReason.UNKNOWN,
    ReportReason.ABUSE,
    ReportReason.SPAM,
    ReportReason.POLITICAL,
    ReportReason.ILLEGAL,
    ReportReason.PRIVACY,
    ReportReason.CHEAT_SCORE,
    ReportReason.FLAME,
    ReportReason.ADVERTISEMENT,
    ReportReason.SPOILER,
    ReportReason.OTHER
)
@Retention(AnnotationRetention.SOURCE)
annotation class ReportReason {

    companion object {

        const val UNKNOWN = 0

        /** 辱骂、人身攻击 */
        const val ABUSE = 1

        /** 刷屏、无关内容 */
        const val SPAM = 2

        /** 政治相关 */
        const val POLITICAL = 3

        /** 违法信息 */
        const val ILLEGAL = 4

        /** 泄露隐私 */
        const val PRIVACY = 5

        /** 涉嫌刷分 */
        const val CHEAT_SCORE = 6

        /** 引战 */
        const val FLAME = 7

        /** 广告 */
        const val ADVERTISEMENT = 8

        /** 剧透 */
        const val SPOILER = 9

        /** 其他 */
        const val OTHER = 99
    }
}