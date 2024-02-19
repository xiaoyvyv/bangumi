package com.xiaoyv.common.config.annotation

import androidx.annotation.IntDef

/**
 * Class: [LocalCollectionType]
 *
 * @author why
 * @since 1/3/24
 */
@IntDef(
    LocalCollectionType.TYPE_BLOG,
    LocalCollectionType.TYPE_TOPIC
)
@Retention(AnnotationRetention.SOURCE)
annotation class LocalCollectionType {
    companion object {
        const val TYPE_TOPIC = 1
        const val TYPE_BLOG = 2
    }
}
