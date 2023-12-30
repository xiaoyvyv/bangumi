package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [TopicTimeType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    TopicTimeType.TYPE_UNKNOWN,
    TopicTimeType.TYPE_NEW,
    TopicTimeType.TYPE_OLD,
    TopicTimeType.TYPE_OLDEST,
    TopicTimeType.TYPE_HOT
)
@Retention(AnnotationRetention.SOURCE)
annotation class TopicTimeType {
    companion object {
        const val TYPE_UNKNOWN = ""
        const val TYPE_NEW = "new"
        const val TYPE_OLD = "old"
        const val TYPE_OLDEST = "oldest"
        const val TYPE_HOT = "hot"
    }
}
