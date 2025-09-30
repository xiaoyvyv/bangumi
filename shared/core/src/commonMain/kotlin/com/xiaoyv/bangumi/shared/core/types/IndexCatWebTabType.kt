package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef

@StringDef(
    IndexCatWebTabType.ALL,
    IndexCatWebTabType.SUBJECT_ANIME,
    IndexCatWebTabType.SUBJECT_BOOK,
    IndexCatWebTabType.SUBJECT_MUSIC,
    IndexCatWebTabType.SUBJECT_GAME,
    IndexCatWebTabType.SUBJECT_REAL,
    IndexCatWebTabType.CHARACTER,
    IndexCatWebTabType.PERSON,
    IndexCatWebTabType.EP,
    IndexCatWebTabType.BLOG,
    IndexCatWebTabType.TOPIC_GROUP,
    IndexCatWebTabType.TOPIC_SUBJECT
)
@Retention(AnnotationRetention.SOURCE)
annotation class IndexCatWebTabType {
    companion object Companion {
        const val ALL = ""
        const val SUBJECT_BOOK = "1"
        const val SUBJECT_ANIME = "2"
        const val SUBJECT_MUSIC = "3"
        const val SUBJECT_GAME = "4"
        const val SUBJECT_REAL = "6"
        const val CHARACTER = "character"
        const val PERSON = "person"
        const val EP = "ep"
        const val BLOG = "blog"
        const val TOPIC_GROUP = "group_topic"
        const val TOPIC_SUBJECT = "subject_topic"
    }
}
