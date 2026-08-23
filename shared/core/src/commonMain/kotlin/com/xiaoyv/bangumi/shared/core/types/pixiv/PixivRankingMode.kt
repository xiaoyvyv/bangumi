package com.xiaoyv.bangumi.shared.core.types.pixiv

import androidx.annotation.StringDef
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.pixiv_mode_daily
import com.xiaoyv.bangumi.core_resource.resources.pixiv_mode_daily_ai
import com.xiaoyv.bangumi.core_resource.resources.pixiv_mode_daily_r18
import com.xiaoyv.bangumi.core_resource.resources.pixiv_mode_female
import com.xiaoyv.bangumi.core_resource.resources.pixiv_mode_female_r18
import com.xiaoyv.bangumi.core_resource.resources.pixiv_mode_male
import com.xiaoyv.bangumi.core_resource.resources.pixiv_mode_male_r18
import com.xiaoyv.bangumi.core_resource.resources.pixiv_mode_monthly
import com.xiaoyv.bangumi.core_resource.resources.pixiv_mode_original
import com.xiaoyv.bangumi.core_resource.resources.pixiv_mode_r18g
import com.xiaoyv.bangumi.core_resource.resources.pixiv_mode_rookie
import com.xiaoyv.bangumi.core_resource.resources.pixiv_mode_weekly
import com.xiaoyv.bangumi.core_resource.resources.pixiv_mode_weekly_r18
import org.jetbrains.compose.resources.StringResource

/**
 * [PixivRankingMode] Pixiv 排行榜模式
 *
 * - [DAILY]: 日榜
 * - [WEEKLY]: 周榜
 * - [MONTHLY]: 月榜
 * - [ROOKIE]: 新人榜
 * - [ORIGINAL]: 原创榜
 * - [DAILY_AI]: AI 日榜
 * - [MALE]: 男性向
 * - [FEMALE]: 女性向
 * - [DAILY_R18]: R-18 日榜
 * - [WEEKLY_R18]: R-18 周榜
 * - [MALE_R18]: R-18 男性向
 * - [FEMALE_R18]: R-18 女性向
 * - [R18G]: R-18G
 *
 * @author why
 * @since 2025/1/12
 */
@StringDef(
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
@Retention(AnnotationRetention.SOURCE)
annotation class PixivRankingMode {
    companion object Companion {
        // 日榜
        const val DAILY = "daily"

        // 周榜
        const val WEEKLY = "weekly"

        // 月榜
        const val MONTHLY = "monthly"

        // 新人榜
        const val ROOKIE = "rookie"

        // 原创榜
        const val ORIGINAL = "original"

        // AI 日榜
        const val DAILY_AI = "daily_ai"

        // 男性向
        const val MALE = "male"

        // 女性向
        const val FEMALE = "female"

        // R-18 日榜
        const val DAILY_R18 = "daily_r18"

        // R-18 周榜
        const val WEEKLY_R18 = "weekly_r18"

        // R-18 男性向
        const val MALE_R18 = "male_r18"

        // R-18 女性向
        const val FEMALE_R18 = "female_r18"

        // R-18G
        const val R18G = "r18g"

        fun label(@PixivRankingMode mode: String): StringResource {
            return when (mode) {
                WEEKLY -> Res.string.pixiv_mode_weekly
                MONTHLY -> Res.string.pixiv_mode_monthly
                ROOKIE -> Res.string.pixiv_mode_rookie
                ORIGINAL -> Res.string.pixiv_mode_original
                DAILY_AI -> Res.string.pixiv_mode_daily_ai
                MALE -> Res.string.pixiv_mode_male
                FEMALE -> Res.string.pixiv_mode_female
                DAILY_R18 -> Res.string.pixiv_mode_daily_r18
                WEEKLY_R18 -> Res.string.pixiv_mode_weekly_r18
                MALE_R18 -> Res.string.pixiv_mode_male_r18
                FEMALE_R18 -> Res.string.pixiv_mode_female_r18
                R18G -> Res.string.pixiv_mode_r18g
                else -> Res.string.pixiv_mode_daily
            }
        }
    }
}
