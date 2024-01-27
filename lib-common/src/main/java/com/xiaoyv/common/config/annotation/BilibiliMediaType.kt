package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [BilibiliMediaType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    BilibiliMediaType.TYPE_ANIME,
    BilibiliMediaType.TYPE_REAL,
    BilibiliMediaType.TYPE_UNKNOWN,
)
@Retention(AnnotationRetention.SOURCE)
annotation class BilibiliMediaType {
    companion object {
        const val TYPE_UNKNOWN = ""
        const val TYPE_ANIME = "1"
        const val TYPE_REAL = "2"

        fun string(@BilibiliMediaType type: String): String {
            return when (type) {
                TYPE_ANIME -> "动画"
                TYPE_REAL -> "影视"
                else -> ""
            }
        }
    }
}
