package com.xiaoyv.common.config.annotation

import androidx.annotation.IntDef

/**
 * Class: [CollectionType]
 *
 * @author why
 * @since 1/3/24
 */
@IntDef(
    CollectionType.TYPE_BLOG,
    CollectionType.TYPE_TOPIC
)
@Retention(AnnotationRetention.SOURCE)
annotation class CollectionType {
    companion object {
        const val TYPE_TOPIC = 1
        const val TYPE_BLOG = 2
    }
}
