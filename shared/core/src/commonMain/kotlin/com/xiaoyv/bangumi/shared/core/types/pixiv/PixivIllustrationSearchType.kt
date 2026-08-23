package com.xiaoyv.bangumi.shared.core.types.pixiv

import androidx.annotation.StringDef

/**
 * Pixiv 插画搜索端点支持的作品类型。
 */
@StringDef(
    PixivIllustrationSearchType.ALL,
    PixivIllustrationSearchType.ILLUST_AND_UGOIRA,
    PixivIllustrationSearchType.ILLUST,
    PixivIllustrationSearchType.UGOIRA,
)
@Retention(AnnotationRetention.SOURCE)
annotation class PixivIllustrationSearchType {
    companion object {
        const val ALL = ""
        const val ILLUST_AND_UGOIRA = "illust_and_ugoira"
        const val ILLUST = "illust"
        const val UGOIRA = "ugoira"
    }
}
