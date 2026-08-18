@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef

@IntDef(
    CommentType.UNKNOWN,
    CommentType.GROUP,
    CommentType.SUBJECT,
    CommentType.SUBJECT_TOPIC,
    CommentType.EP,
    CommentType.BLOG_COMMENT,
    CommentType.MONO
)
@Retention(AnnotationRetention.SOURCE)
annotation class CommentType {
    companion object Companion {
        const val UNKNOWN = 0

        /**
         * 支持贴贴的数据
         */
        const val GROUP = 8
        const val SUBJECT = 40
        const val SUBJECT_TOPIC = 10
        const val EP = 11
        const val BLOG_COMMENT = 21

        /**
         * 不支持贴贴的，这个值本地取的
         */
        const val MONO = 2

        fun isSupportRection(@CommentType type: Int): Boolean {
            return type == GROUP || type == SUBJECT || type == SUBJECT_TOPIC || type == EP
        }

        fun fromRakuenIdType(@TopicType type: String): Int {
            return when (type) {
                TopicType.TYPE_SUBJECT -> SUBJECT_TOPIC
                TopicType.TYPE_GROUP -> GROUP
                TopicType.TYPE_EP -> EP
                TopicType.TYPE_BLOG -> BLOG_COMMENT
                TopicType.TYPE_PERSON,
                TopicType.TYPE_CRT,
                    -> MONO

                else -> UNKNOWN
            }
        }
    }
}