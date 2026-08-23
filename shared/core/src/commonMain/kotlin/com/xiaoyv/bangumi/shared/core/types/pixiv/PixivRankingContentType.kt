package com.xiaoyv.bangumi.shared.core.types.pixiv

import androidx.annotation.StringDef

/**
 * [PixivRankingContentType] Pixiv 排行榜内容类型
 *
 * - [ALL]: 综合
 * - [ILLUST]: 插画
 * - [UGOIRA]: 动图
 * - [MANGA]: 漫画
 *
 * @author why
 * @since 2025/1/12
 */
@StringDef(
    PixivRankingContentType.ALL,
    PixivRankingContentType.ILLUST,
    PixivRankingContentType.UGOIRA,
    PixivRankingContentType.MANGA,
)
@Retention(AnnotationRetention.SOURCE)
annotation class PixivRankingContentType {
    companion object Companion {
        // 综合
        const val ALL = "all"

        // 插画
        const val ILLUST = "illust"

        // 动图
        const val UGOIRA = "ugoira"

        // 漫画
        const val MANGA = "manga"

        /**
         * 获取不同内容类型支持的排行榜模式
         */
        fun getSupportedModes(@PixivRankingContentType contentType: String): List<String> {
            return when (contentType) {
                ILLUST -> listOf(
                    PixivRankingMode.DAILY,
                    PixivRankingMode.WEEKLY,
                    PixivRankingMode.MONTHLY,
                    PixivRankingMode.ROOKIE,
                    PixivRankingMode.DAILY_R18,
                    PixivRankingMode.WEEKLY_R18,
                    PixivRankingMode.R18G,
                )

                UGOIRA -> listOf(
                    PixivRankingMode.DAILY,
                    PixivRankingMode.WEEKLY,
                    PixivRankingMode.DAILY_R18,
                    PixivRankingMode.WEEKLY_R18,
                )

                MANGA -> listOf(
                    PixivRankingMode.DAILY,
                    PixivRankingMode.WEEKLY,
                    PixivRankingMode.MONTHLY,
                    PixivRankingMode.ROOKIE,
                    PixivRankingMode.DAILY_R18,
                    PixivRankingMode.WEEKLY_R18,
                    PixivRankingMode.R18G,
                )

                else -> listOf(
                    PixivRankingMode.DAILY,
                    PixivRankingMode.WEEKLY,
                    PixivRankingMode.MONTHLY,
                    PixivRankingMode.ROOKIE,
                    PixivRankingMode.ORIGINAL,
                    PixivRankingMode.DAILY_AI,
                    PixivRankingMode.MALE,
                    PixivRankingMode.FEMALE,
                    PixivRankingMode.DAILY_R18,
                    PixivRankingMode.WEEKLY_R18,
                    PixivRankingMode.MALE_R18,
                    PixivRankingMode.FEMALE_R18,
                    PixivRankingMode.R18G,
                )
            }
        }
    }
}
