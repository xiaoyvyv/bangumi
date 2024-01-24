package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [FeatureType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    FeatureType.TYPE_UNSET,
    FeatureType.TYPE_DEFAULT,
    FeatureType.TYPE_DOLLARS,
    FeatureType.TYPE_MAGI,
    FeatureType.TYPE_SYNCER,
    FeatureType.TYPE_ANIME_PICTURES,
    FeatureType.TYPE_EMAIL,
    FeatureType.TYPE_ALMANAC,
    FeatureType.TYPE_WIKI,
    FeatureType.TYPE_RANK,
    FeatureType.TYPE_PROCESS,
    FeatureType.TYPE_DETECT_ANIME,
    FeatureType.TYPE_DETECT_CHARACTER,
    FeatureType.TYPE_DISCOVER,
    FeatureType.TYPE_PROFILE,
    FeatureType.TYPE_TIMELINE,
    FeatureType.TYPE_RAKUEN,
    FeatureType.TYPE_MAGNET,
)
@Retention(AnnotationRetention.SOURCE)
annotation class FeatureType {
    companion object {
        const val TYPE_UNSET = "unset"
        const val TYPE_DEFAULT = "default"
        const val TYPE_DOLLARS = "dollars"
        const val TYPE_MAGI = "magi"
        const val TYPE_ANIME_PICTURES = "anime-pictures"
        const val TYPE_SYNCER = "syncer"
        const val TYPE_RANK = "rank"
        const val TYPE_PROCESS = "process"
        const val TYPE_EMAIL = "email"
        const val TYPE_ALMANAC = "almanac"
        const val TYPE_WIKI = "wiki"
        const val TYPE_DETECT_CHARACTER = "detect-character"
        const val TYPE_DETECT_ANIME = "detect-anime"
        const val TYPE_DISCOVER = "discover"
        const val TYPE_PROFILE = "profile"
        const val TYPE_TIMELINE = "timeline"
        const val TYPE_RAKUEN = "rakuen"
        const val TYPE_MAGNET = "magnet"

        fun name(@FeatureType type: String): String {
            return when (type) {
                TYPE_UNSET -> "未设置"
                TYPE_DOLLARS -> "Dollars"
                TYPE_MAGI -> "Magi"
                TYPE_SYNCER -> "豆哔同步"
                TYPE_ANIME_PICTURES -> "图片"
                TYPE_EMAIL -> "邮箱"
                TYPE_ALMANAC -> "年鉴"
                TYPE_WIKI -> "WIKI"
                TYPE_RANK -> "排行榜"
                TYPE_PROCESS -> "追番进度"
                TYPE_DETECT_ANIME -> "以图搜番"
                TYPE_DETECT_CHARACTER -> "以图识人"
                TYPE_DISCOVER -> "发现"
                TYPE_PROFILE -> "我的"
                TYPE_TIMELINE -> "时间胶囊"
                TYPE_RAKUEN -> "超展开"
                TYPE_MAGNET -> "搜番资源"
                else -> "添加"
            }
        }
    }
}