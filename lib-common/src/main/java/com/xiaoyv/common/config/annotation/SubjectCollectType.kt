package com.xiaoyv.common.config.annotation

import androidx.annotation.IntDef

/**
 * Class: [SubjectCollectType]
 *
 * @author why
 * @since 11/25/23
 */
@IntDef(
    SubjectCollectType.TYPE_NONE,
    SubjectCollectType.TYPE_WISH,
    SubjectCollectType.TYPE_COLLECT,
    SubjectCollectType.TYPE_DOING,
    SubjectCollectType.TYPE_ON_HOLD,
    SubjectCollectType.TYPE_DROPPED
)
@Retention(AnnotationRetention.SOURCE)
annotation class SubjectCollectType {
    companion object {
        const val TYPE_NONE = 0
        const val TYPE_WISH = 1
        const val TYPE_COLLECT = 2
        const val TYPE_DOING = 3
        const val TYPE_ON_HOLD = 4
        const val TYPE_DROPPED = 5
    }
}
