package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef

@StringDef(
    IndexItemType.SUBJECT_TYPE_1,
    IndexItemType.SUBJECT_TYPE_2,
    IndexItemType.SUBJECT_TYPE_3,
    IndexItemType.SUBJECT_TYPE_4,
    IndexItemType.SUBJECT_TYPE_6,
    IndexItemType.SUBJECT_TYPE_CHARACTER,
    IndexItemType.SUBJECT_TYPE_PERSON,
    IndexItemType.SUBJECT_TYPE_EP,
    IndexItemType.SUBJECT_TYPE_BLOG,
    IndexItemType.SUBJECT_TYPE_GROUP_TOPIC,
    IndexItemType.SUBJECT_TYPE_SUBJECT_TOPIC
)
@Retention(AnnotationRetention.SOURCE)
annotation class IndexItemType {
    companion object {
        const val SUBJECT_TYPE_1 = "subject_type_1"
        const val SUBJECT_TYPE_2 = "subject_type_2"
        const val SUBJECT_TYPE_3 = "subject_type_3"
        const val SUBJECT_TYPE_4 = "subject_type_4"
        const val SUBJECT_TYPE_6 = "subject_type_6"
        const val SUBJECT_TYPE_CHARACTER = "subject_type_character"
        const val SUBJECT_TYPE_PERSON = "subject_type_person"
        const val SUBJECT_TYPE_EP = "subject_type_ep"
        const val SUBJECT_TYPE_BLOG = "subject_type_blog"
        const val SUBJECT_TYPE_GROUP_TOPIC = "subject_type_group_topic"
        const val SUBJECT_TYPE_SUBJECT_TOPIC = "subject_type_subject_topic"
    }
}
