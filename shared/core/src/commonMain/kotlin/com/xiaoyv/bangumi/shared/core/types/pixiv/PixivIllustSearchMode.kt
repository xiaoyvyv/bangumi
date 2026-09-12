package com.xiaoyv.bangumi.shared.core.types.pixiv

import androidx.annotation.StringDef

/**
 * Pixiv 作品搜索支持的匹配模式。
 *
 * 不同模式决定搜索关键字会匹配作品的哪些字段，以及标签是否要求完全匹配。
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

        /**
         * 标签部分匹配。
         *
         * 搜索关键字会与作品标签进行部分匹配。
         *
         * 例如搜索 `cat` 时，可能匹配：
         * - `cat`
         * - `catgirl`
         * - `black_cat`
         *
         * 对应 Pixiv 参数：`s_tag`
         */
        const val TAG_PARTIAL = "s_tag"

        /**
         * 标签完全匹配。
         *
         * 搜索关键字必须与作品标签完全一致。
         *
         * 例如搜索 `cat` 时：
         * - `cat` 可以匹配
         * - `catgirl` 不会匹配
         *
         * 对应 Pixiv 参数：`s_tag_full`
         */
        const val TAG_FULL = "s_tag_full"

        /**
         * 仅搜索标签。
         *
         * 只根据作品标签进行搜索，不匹配标题或作品简介。
         *
         * 该模式与普通标签搜索相比，匹配范围更加严格，
         * 具体匹配行为由 Pixiv 搜索接口决定。
         *
         * 对应 Pixiv 参数：`s_tag_only`
         */
        const val TAG_ONLY = "s_tag_only"

        /**
         * 搜索标签、标题和作品简介。
         *
         * 搜索关键字会同时匹配：
         * - 标签（Tag）
         * - 标题（Title）
         * - 作品简介（Caption）
         *
         * 相比仅搜索标签，该模式的搜索范围更广。
         *
         * 对应 Pixiv 参数：`s_tag_tc`
         */
        const val TAG_TITLE_AND_CAPTION = "s_tag_tc"

        /**
         * 搜索标题和作品简介。
         *
         * 搜索关键字会匹配：
         * - 标题（Title）
         * - 作品简介（Caption）
         *
         * 不使用标签作为主要匹配字段。
         *
         * 对应 Pixiv 参数：`s_tc`
         */
        const val TITLE_AND_CAPTION = "s_tc"
    }
}