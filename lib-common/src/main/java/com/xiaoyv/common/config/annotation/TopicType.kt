package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [TopicType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    TopicType.TYPE_UNKNOWN,
    TopicType.TYPE_EP,
    TopicType.TYPE_GROUP,
    TopicType.TYPE_PERSON,
    TopicType.TYPE_CRT,
    TopicType.TYPE_SUBJECT,
    TopicType.TYPE_INDEX
)
@Retention(AnnotationRetention.SOURCE)
annotation class TopicType {
    companion object {
        const val TYPE_UNKNOWN = ""
        const val TYPE_EP = "ep"
        const val TYPE_GROUP = "group"
        const val TYPE_PERSON = "prsn"
        const val TYPE_CRT = "crt"
        const val TYPE_SUBJECT = "subject"
        const val TYPE_INDEX = "index"

        fun string(@TopicType type: String): String {
            return when (type) {
                TYPE_EP -> "条目章节"
                TYPE_GROUP -> "小组话题"
                TYPE_PERSON -> "现实人物"
                TYPE_CRT -> "虚拟人物"
                TYPE_SUBJECT -> "条目话题"
                TYPE_INDEX -> "目录"
                else -> "未知"
            }
        }
    }
}
