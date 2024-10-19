package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.i18n

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
    BgmPathType.TYPE_FRIEND,
    BgmPathType.TYPE_SCORE
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
        const val TYPE_SCORE = "score"
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
                TYPE_SEARCH_SUBJECT -> i18n(CommonString.type_path_search_subject)
                TYPE_SEARCH_MONO -> i18n(CommonString.type_path_search_mono)
                TYPE_SEARCH_TAG -> i18n(CommonString.type_path_search_tag)
                TYPE_CHARACTER -> i18n(CommonString.type_path_character)
                TYPE_GROUP -> i18n(CommonString.type_path_group)
                TYPE_PERSON -> i18n(CommonString.type_path_person)
                TYPE_USER -> i18n(CommonString.type_path_user)
                TYPE_BLOG -> i18n(CommonString.type_path_blog)
                TYPE_TOPIC -> i18n(CommonString.type_path_topic)
                TYPE_SUBJECT -> i18n(CommonString.type_path_subject)
                TYPE_MESSAGE_BOX -> i18n(CommonString.type_path_message_box)
                TYPE_INDEX -> i18n(CommonString.type_path_index)
                TYPE_FRIEND -> i18n(CommonString.type_path_friend)
                TYPE_EP -> i18n(CommonString.type_path_ep)
                TYPE_TIMELINE -> i18n(CommonString.type_path_timeline)
                else -> ""
            }
        }
    }
}
