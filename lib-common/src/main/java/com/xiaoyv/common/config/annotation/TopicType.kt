package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.i18n

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
                TYPE_EP -> i18n(CommonString.type_topic_ep)
                TYPE_GROUP -> i18n(CommonString.type_topic_group)
                TYPE_PERSON -> i18n(CommonString.type_topic_person)
                TYPE_CRT -> i18n(CommonString.type_topic_crt)
                TYPE_SUBJECT -> i18n(CommonString.type_topic_subject)
                TYPE_INDEX -> i18n(CommonString.type_topic_index)
                else -> i18n(CommonString.type_unknown)
            }
        }
    }
}
