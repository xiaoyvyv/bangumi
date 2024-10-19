package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.i18n

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
    FeatureType.TYPE_ANIME_YUC,
    FeatureType.TYPE_SCHEDULE,
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
    FeatureType.TYPE_RANK_FRIEND,
    FeatureType.TYPE_SUBTITLE
)
@Retention(AnnotationRetention.SOURCE)
annotation class FeatureType {
    companion object {
        const val TYPE_UNSET = "unset"
        const val TYPE_DEFAULT = "default"
        const val TYPE_DOLLARS = "dollars"
        const val TYPE_MAGI = "magi"
        const val TYPE_ANIME_YUC = "yuc"
        const val TYPE_SYNCER = "syncer"
        const val TYPE_RANK = "rank"
        const val TYPE_RANK_FRIEND = "rank_friend"
        const val TYPE_PROCESS = "process"
        const val TYPE_SCHEDULE = "schedule"
        const val TYPE_ALMANAC = "almanac"
        const val TYPE_WIKI = "wiki"
        const val TYPE_DETECT_CHARACTER = "detect-character"
        const val TYPE_DETECT_ANIME = "detect-anime"
        const val TYPE_DISCOVER = "discover"
        const val TYPE_PROFILE = "profile"
        const val TYPE_TIMELINE = "timeline"
        const val TYPE_RAKUEN = "rakuen"
        const val TYPE_MAGNET = "magnet"
        const val TYPE_SUBTITLE = "subtitle"

        fun name(@FeatureType type: String): String {
            return when (type) {
                TYPE_UNSET -> i18n(CommonString.type_feature_unset)
                TYPE_DOLLARS -> i18n(CommonString.type_feature_dollars)
                TYPE_MAGI -> i18n(CommonString.type_feature_magi)
                TYPE_SYNCER -> i18n(CommonString.type_feature_syncer)
                TYPE_ANIME_YUC -> i18n(CommonString.type_feature_anime_yuc)
                TYPE_SCHEDULE -> i18n(CommonString.type_feature_schedule)
                TYPE_ALMANAC -> i18n(CommonString.type_feature_almanac)
                TYPE_WIKI -> i18n(CommonString.type_feature_wiki)
                TYPE_RANK -> i18n(CommonString.type_feature_rank)
                TYPE_PROCESS -> i18n(CommonString.type_feature_process)
                TYPE_DETECT_ANIME -> i18n(CommonString.type_feature_detect_anime)
                TYPE_DETECT_CHARACTER -> i18n(CommonString.type_feature_detect_character)
                TYPE_DISCOVER -> i18n(CommonString.type_feature_discover)
                TYPE_PROFILE -> i18n(CommonString.type_feature_profile)
                TYPE_TIMELINE -> i18n(CommonString.type_feature_timeline)
                TYPE_RAKUEN -> i18n(CommonString.type_feature_rakuen)
                TYPE_MAGNET -> i18n(CommonString.type_feature_magnet)
                TYPE_RANK_FRIEND -> i18n(CommonString.type_feature_rank_friend)
                else -> i18n(CommonString.type_unknown)
            }
        }
    }
}