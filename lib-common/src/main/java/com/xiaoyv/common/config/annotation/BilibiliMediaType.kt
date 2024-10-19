package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.i18n

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
                TYPE_ANIME -> i18n(CommonString.type_bilibili_media_anime)
                TYPE_REAL -> i18n(CommonString.type_bilibili_media_real)
                else -> ""
            }
        }

        fun toSubjectType(@BilibiliMediaType type: String?): Int {
            return when (type) {
                TYPE_ANIME -> SubjectType.TYPE_ANIME
                TYPE_REAL -> SubjectType.TYPE_REAL
                else -> SubjectType.TYPE_NONE
            }
        }
    }
}
