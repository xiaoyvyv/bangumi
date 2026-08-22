package com.xiaoyv.bangumi.shared.core.types.pixiv

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * Pixiv 排行榜模式
 */
@Immutable
@Serializable
enum class PixivRankingMode(val value: String, val label: String) {
    DAILY("daily", "日榜"),
    WEEKLY("weekly", "周榜"),
    MONTHLY("monthly", "月榜"),
    ROOKIE("rookie", "新人榜"),
    DAILY_R18("daily_r18", "R-18 日榜"),
    WEEKLY_R18("weekly_r18", "R-18 周榜"),
    MALE_R18("male_r18", "R-18 男性向"),
    FEMALE_R18("female_r18", "R-18 女性向"),
    R18G("r18g", "R-18G");

    companion object {
        fun fromValue(value: String): PixivRankingMode =
            entries.find { it.value == value } ?: DAILY
    }
}
