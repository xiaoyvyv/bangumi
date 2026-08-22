package com.xiaoyv.bangumi.shared.core.types.pixiv

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * Pixiv 排行榜内容类型
 */
@Immutable
@Serializable
enum class PixivRankingContentType(val value: String, val label: String) {
    ALL("all", "综合"),
    ILLUST("illust", "插画"),
    UGOIRA("ugoira", "动图"),
    MANGA("manga", "漫画");

    companion object {
        fun fromValue(value: String): PixivRankingContentType =
            entries.find { it.value == value } ?: ALL
    }
}
