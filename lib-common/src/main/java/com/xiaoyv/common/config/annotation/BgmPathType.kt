package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [BgmPathType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    BgmPathType.TYPE_UNKNOWN,
    BgmPathType.TYPE_CHARACTER,
    BgmPathType.TYPE_GROUP,
    BgmPathType.TYPE_PERSON,
    BgmPathType.TYPE_USER,
    BgmPathType.TYPE_BLOG,
    BgmPathType.TYPE_TOPIC,
    BgmPathType.TYPE_MESSAGE_BOX,
    BgmPathType.TYPE_SEARCH_SUBJECT,
    BgmPathType.TYPE_SEARCH_MONO
)
@Retention(AnnotationRetention.SOURCE)
annotation class BgmPathType {
    companion object {
        const val TYPE_UNKNOWN = ""
        const val TYPE_CHARACTER = "character"
        const val TYPE_GROUP = "group"
        const val TYPE_PERSON = "person"
        const val TYPE_USER = "user"
        const val TYPE_BLOG = "blog"
        const val TYPE_TOPIC = "topic"
        const val TYPE_MESSAGE_BOX = "pm"
        const val TYPE_SEARCH_SUBJECT = "subject_search"
        const val TYPE_SEARCH_MONO = "mono_search"

        fun string(@BgmPathType type: String): String {
            return when (type) {
                TYPE_SEARCH_SUBJECT -> "条目查询"
                TYPE_SEARCH_MONO -> "人物查询"
                else -> ""
            }
        }
    }
}
