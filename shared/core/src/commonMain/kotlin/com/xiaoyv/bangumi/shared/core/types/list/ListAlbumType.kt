package com.xiaoyv.bangumi.shared.core.types.list

import androidx.annotation.IntDef

@IntDef(
    ListAlbumType.UNKNOWN,
    ListAlbumType.CHARACTER_ALBUM,
    ListAlbumType.SUBJECT_PREVIEW,
    ListAlbumType.PIVIX,
    ListAlbumType.ANIME_PICTURES,
)
@Retention(AnnotationRetention.SOURCE)
annotation class ListAlbumType {
    companion object Companion {
        const val UNKNOWN = 0
        const val SUBJECT_PREVIEW = 1
        const val CHARACTER_ALBUM = 2
        const val PIVIX = 3
        const val ANIME_PICTURES = 4
    }
}