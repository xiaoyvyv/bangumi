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
    BgmPathType.TYPE_SEARCH_MONO,
    BgmPathType.TYPE_SEARCH_TAG,
    BgmPathType.TYPE_SUBJECT,
    BgmPathType.TYPE_INDEX,
    BgmPathType.TYPE_EP,
    BgmPathType.TYPE_TIMELINE,
    BgmPathType.TYPE_FRIEND
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
        const val TYPE_SUBJECT = "subject"
        const val TYPE_MESSAGE_BOX = "pm"
        const val TYPE_INDEX = "index"
        const val TYPE_FRIEND = "friends"
        const val TYPE_EP = "ep"
        const val TYPE_TIMELINE = "timeline"
        const val TYPE_SEARCH_SUBJECT = "subject_search"
        const val TYPE_SEARCH_MONO = "mono_search"
        const val TYPE_SEARCH_TAG = "tag"

        fun string(@BgmPathType type: String): String {
            return when (type) {
                TYPE_SEARCH_SUBJECT -> "条目查询"
                TYPE_SEARCH_MONO -> "人物查询"
                TYPE_SEARCH_TAG -> "标签查询"
                TYPE_CHARACTER -> "角色"
                TYPE_GROUP -> "小组"
                TYPE_PERSON -> "人物"
                TYPE_USER -> "用户"
                TYPE_BLOG -> "日志"
                TYPE_TOPIC -> "话题"
                TYPE_SUBJECT -> "条目"
                TYPE_MESSAGE_BOX -> "短信"
                TYPE_INDEX -> "目录"
                TYPE_FRIEND -> "好友"
                TYPE_EP -> "章节"
                TYPE_TIMELINE -> "时间线"
                else -> ""
            }
        }
    }
}
