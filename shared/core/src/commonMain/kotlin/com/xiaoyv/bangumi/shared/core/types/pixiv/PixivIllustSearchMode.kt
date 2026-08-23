package com.xiaoyv.bangumi.shared.core.types.pixiv

import androidx.annotation.StringDef

/**
 * Pixiv 作品标签搜索支持的匹配模式。
 */
@StringDef(
    PixivIllustSearchMode.TAG_PARTIAL,
    PixivIllustSearchMode.TAG_FULL,
    PixivIllustSearchMode.TAG_ONLY,
    PixivIllustSearchMode.TAG_TITLE_AND_CAPTION,
    PixivIllustSearchMode.TITLE_AND_CAPTION,
)
@Retention(AnnotationRetention.SOURCE)
annotation class PixivIllustSearchMode {
    companion object {
        const val TAG_PARTIAL = "s_tag"
        const val TAG_FULL = "s_tag_full"
        const val TAG_ONLY = "s_tag_only"
        const val TAG_TITLE_AND_CAPTION = "s_tag_tc"
        const val TITLE_AND_CAPTION = "s_tc"
    }
}
