package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [EpCollectPathType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    EpCollectPathType.TYPE_WATCHED,
    EpCollectPathType.TYPE_QUEUE,
    EpCollectPathType.TYPE_DROP,
    EpCollectPathType.TYPE_REMOVE
)
@Retention(AnnotationRetention.SOURCE)
annotation class EpCollectPathType {
    companion object {
        const val TYPE_WATCHED = "watched"
        const val TYPE_QUEUE = "queue"
        const val TYPE_DROP = "drop"
        const val TYPE_REMOVE = "remove"
    }
}
