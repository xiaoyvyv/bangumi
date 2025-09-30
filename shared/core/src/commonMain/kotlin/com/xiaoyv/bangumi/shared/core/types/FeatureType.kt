@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.type_feature_almanac
import com.xiaoyv.bangumi.core_resource.resources.type_feature_anime_yuc
import com.xiaoyv.bangumi.core_resource.resources.type_feature_detect_anime
import com.xiaoyv.bangumi.core_resource.resources.type_feature_detect_character
import com.xiaoyv.bangumi.core_resource.resources.type_feature_discover
import com.xiaoyv.bangumi.core_resource.resources.type_feature_dollars
import com.xiaoyv.bangumi.core_resource.resources.type_feature_magi
import com.xiaoyv.bangumi.core_resource.resources.type_feature_magnet
import com.xiaoyv.bangumi.core_resource.resources.type_feature_pixiv
import com.xiaoyv.bangumi.core_resource.resources.type_feature_process
import com.xiaoyv.bangumi.core_resource.resources.type_feature_profile
import com.xiaoyv.bangumi.core_resource.resources.type_feature_rakuen
import com.xiaoyv.bangumi.core_resource.resources.type_feature_rank
import com.xiaoyv.bangumi.core_resource.resources.type_feature_schedule
import com.xiaoyv.bangumi.core_resource.resources.type_feature_subject_browser
import com.xiaoyv.bangumi.core_resource.resources.type_feature_syncer
import com.xiaoyv.bangumi.core_resource.resources.type_feature_tag
import com.xiaoyv.bangumi.core_resource.resources.type_feature_timeline
import com.xiaoyv.bangumi.core_resource.resources.type_feature_unset
import com.xiaoyv.bangumi.core_resource.resources.type_feature_wiki
import com.xiaoyv.bangumi.core_resource.resources.type_unknown
import org.jetbrains.compose.resources.StringResource

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
    FeatureType.TYPE_NEWEST,
    FeatureType.TYPE_SCHEDULE,
    FeatureType.TYPE_ALMANAC,
    FeatureType.TYPE_WIKI,
    FeatureType.TYPE_RANK,
    FeatureType.TYPE_TRACKING,
    FeatureType.TYPE_DETECT_ANIME,
    FeatureType.TYPE_DETECT_CHARACTER,
    FeatureType.TYPE_DISCOVER,
    FeatureType.TYPE_PROFILE,
    FeatureType.TYPE_TIMELINE,
    FeatureType.TYPE_RAKUEN,
    FeatureType.TYPE_MAGNET,
    FeatureType.TYPE_SUBTITLE,
    FeatureType.TYPE_TAG,
    FeatureType.TYPE_SUBJECT_BROWSER,
    FeatureType.TYPE_PIXIV,
    FeatureType.TYPE_HOME,
    FeatureType.TYPE_SMS
)
@Retention(AnnotationRetention.SOURCE)
annotation class FeatureType {
    companion object {
        const val TYPE_UNSET = "unset"
        const val TYPE_HOME = "home"
        const val TYPE_DEFAULT = "default"
        const val TYPE_DOLLARS = "dollars"
        const val TYPE_MAGI = "magi"
        const val TYPE_NEWEST = "yuc"
        const val TYPE_SYNCER = "syncer"
        const val TYPE_RANK = "rank"
        const val TYPE_TRACKING = "process"
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
        const val TYPE_TAG = "tag"
        const val TYPE_SUBJECT_BROWSER = "subject_browser"
        const val TYPE_PIXIV = "pixiv"
        const val TYPE_SMS = "pm"

        fun name(@FeatureType type: String): StringResource {
            return when (type) {
                TYPE_UNSET -> Res.string.type_feature_unset
                TYPE_DOLLARS -> Res.string.type_feature_dollars
                TYPE_MAGI -> Res.string.type_feature_magi
                TYPE_SYNCER -> Res.string.type_feature_syncer
                TYPE_NEWEST -> Res.string.type_feature_anime_yuc
                TYPE_SCHEDULE -> Res.string.type_feature_schedule
                TYPE_ALMANAC -> Res.string.type_feature_almanac
                TYPE_WIKI -> Res.string.type_feature_wiki
                TYPE_RANK -> Res.string.type_feature_rank
                TYPE_TRACKING -> Res.string.type_feature_process
                TYPE_DETECT_ANIME -> Res.string.type_feature_detect_anime
                TYPE_DETECT_CHARACTER -> Res.string.type_feature_detect_character
                TYPE_DISCOVER -> Res.string.type_feature_discover
                TYPE_PROFILE -> Res.string.type_feature_profile
                TYPE_TIMELINE -> Res.string.type_feature_timeline
                TYPE_RAKUEN -> Res.string.type_feature_rakuen
                TYPE_MAGNET -> Res.string.type_feature_magnet
                TYPE_TAG -> Res.string.type_feature_tag
                TYPE_SUBJECT_BROWSER -> Res.string.type_feature_subject_browser
                TYPE_PIXIV -> Res.string.type_feature_pixiv
                else -> Res.string.type_unknown
            }
        }
    }
}