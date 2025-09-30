package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef

/**
 * [MonoDetailTab]
 *
 * @since 2025/5/11
 */
@IntDef(
    MonoDetailTab.OVERVIEW,
    MonoDetailTab.WORKS,
    MonoDetailTab.CASTS,
    MonoDetailTab.COLLABS,
    MonoDetailTab.COLLECTIONS,
    MonoDetailTab.ANIME_PICTURES,
    MonoDetailTab.PIXIV,
    MonoDetailTab.ALBUM,
    MonoDetailTab.INDEX,
)
@Retention(AnnotationRetention.SOURCE)
annotation class MonoDetailTab {
    companion object {
        const val OVERVIEW = 0
        const val CASTS = 1
        const val WORKS = 2
        const val COLLABS = 3
        const val COLLECTIONS = 4
        const val ANIME_PICTURES = 5
        const val PIXIV = 6
        const val ALBUM = 7
        const val INDEX = 8
    }
}
