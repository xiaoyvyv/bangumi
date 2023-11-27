package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [CollectType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
   CollectType.TYPE_WISH,
   CollectType.TYPE_COLLECT,
   CollectType.TYPE_DO,
   CollectType.TYPE_ON_HOLD,
   CollectType.TYPE_DROPPED
)
@Retention(AnnotationRetention.SOURCE)
annotation class CollectType {
    companion object {
        const val TYPE_WISH = "wish"
        const val TYPE_COLLECT = "collect"
        const val TYPE_DO = "do"
        const val TYPE_ON_HOLD = "on_hold"
        const val TYPE_DROPPED = "dropped"
    }
}
