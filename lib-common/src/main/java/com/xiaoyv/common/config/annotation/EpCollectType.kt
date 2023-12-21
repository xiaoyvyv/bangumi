package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [EpCollectType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    EpCollectType.TYPE_WATCHED,
    EpCollectType.TYPE_QUEUE,
    EpCollectType.TYPE_DROP,
    EpCollectType.TYPE_REMOVE
)
@Retention(AnnotationRetention.SOURCE)
annotation class EpCollectType {
    companion object {
        const val TYPE_WATCHED = "watched"
        const val TYPE_QUEUE = "queue"
        const val TYPE_DROP = "drop"
        const val TYPE_REMOVE = "remove"
    }
}
