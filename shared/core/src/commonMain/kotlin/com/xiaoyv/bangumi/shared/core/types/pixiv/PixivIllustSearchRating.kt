package com.xiaoyv.bangumi.shared.core.types.pixiv

import androidx.annotation.StringDef

/**
 * Pixiv 作品搜索支持的内容分级。
 */
@StringDef(
    PixivIllustSearchRating.ALL,
    PixivIllustSearchRating.SAFE,
    PixivIllustSearchRating.R18,
)
@Retention(AnnotationRetention.SOURCE)
annotation class PixivIllustSearchRating {
    companion object {
        const val ALL = "all"
        const val SAFE = "safe"
        const val R18 = "r18"
    }
}
