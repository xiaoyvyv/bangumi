package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [HomeFeatureType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    HomeFeatureType.TYPE_DOLLARS,
    HomeFeatureType.TYPE_MAGI,
    HomeFeatureType.TYPE_SEARCH,
    HomeFeatureType.TYPE_ANIME_PICTURES,
    HomeFeatureType.TYPE_EMAIL,
    HomeFeatureType.TYPE_ALMANAC,
    HomeFeatureType.TYPE_WIKI,
    HomeFeatureType.TYPE_RANK,
    HomeFeatureType.TYPE_PROCESS,
)
@Retention(AnnotationRetention.SOURCE)
annotation class HomeFeatureType {
    companion object {
        const val TYPE_DOLLARS = "dollars"
        const val TYPE_MAGI = "magi"
        const val TYPE_ANIME_PICTURES = "anime-pictures"
        const val TYPE_SEARCH = "search"
        const val TYPE_RANK = "rank"
        const val TYPE_PROCESS = "process"
        const val TYPE_EMAIL = "email"
        const val TYPE_ALMANAC = "almanac"
        const val TYPE_WIKI = "wiki"
    }
}