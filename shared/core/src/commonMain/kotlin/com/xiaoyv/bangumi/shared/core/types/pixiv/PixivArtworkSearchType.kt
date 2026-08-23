package com.xiaoyv.bangumi.shared.core.types.pixiv

import androidx.annotation.StringDef

/**
 * Pixiv Ajax 作品搜索端点类型。
 */
@StringDef(
    PixivArtworkSearchType.ILLUSTRATIONS,
    PixivArtworkSearchType.MANGA,
)
@Retention(AnnotationRetention.SOURCE)
annotation class PixivArtworkSearchType {
    companion object {
        const val ILLUSTRATIONS = "illustrations"
        const val MANGA = "manga"
    }
}
