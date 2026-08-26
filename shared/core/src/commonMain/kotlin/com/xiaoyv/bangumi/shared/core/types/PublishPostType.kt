package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef

/**
 * [PublishPostType]
 */
@IntDef(
    value = [
        PublishPostType.TIMELINE_STATUS,
        PublishPostType.TOPIC_GROUP,
        PublishPostType.TOPIC_SUBJECT,
        PublishPostType.BLOG,
        PublishPostType.COMMENT_CHARACTER,
        PublishPostType.COMMENT_PERSON,
        PublishPostType.COMMENT_EP,
        PublishPostType.COMMENT_INDEX
    ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class PublishPostType {
    companion object {
        const val TIMELINE_STATUS = 1
        const val TOPIC_GROUP = 2
        const val TOPIC_SUBJECT = 3
        const val BLOG = 4
        const val COMMENT_CHARACTER = 5
        const val COMMENT_PERSON = 6
        const val COMMENT_EP = 7
        const val COMMENT_INDEX = 8
    }
}
