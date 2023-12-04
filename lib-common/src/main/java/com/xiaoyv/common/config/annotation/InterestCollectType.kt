package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [InterestCollectType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
   InterestCollectType.TYPE_WISH,
   InterestCollectType.TYPE_COLLECT,
   InterestCollectType.TYPE_DO,
   InterestCollectType.TYPE_ON_HOLD,
   InterestCollectType.TYPE_DROPPED
)
@Retention(AnnotationRetention.SOURCE)
annotation class InterestCollectType {
    companion object {
        const val TYPE_WISH = "wish"
        const val TYPE_COLLECT = "collect"
        const val TYPE_DO = "do"
        const val TYPE_ON_HOLD = "on_hold"
        const val TYPE_DROPPED = "dropped"
    }
}
