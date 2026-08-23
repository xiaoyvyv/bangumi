package com.xiaoyv.bangumi.shared.core.types.pixiv

import androidx.annotation.StringDef

/**
 * Pixiv 作品搜索支持的宽高比范围。
 */
@StringDef(
    PixivIllustSearchRatio.ALL,
    PixivIllustSearchRatio.LANDSCAPE,
    PixivIllustSearchRatio.PORTRAIT,
    PixivIllustSearchRatio.SQUARE,
)
@Retention(AnnotationRetention.SOURCE)
annotation class PixivIllustSearchRatio {
    companion object {
        const val ALL = ""
        const val LANDSCAPE = "0.5-"
        const val PORTRAIT = "-0.5"
        const val SQUARE = "0.5-0.5"
    }
}
