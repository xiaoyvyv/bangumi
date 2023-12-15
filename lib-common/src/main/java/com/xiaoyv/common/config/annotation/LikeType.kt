package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [LikeType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    LikeType.TYPE_TOPIC
)
@Retention(AnnotationRetention.SOURCE)
annotation class LikeType {
    companion object {
        const val TYPE_TOPIC = "8"
    }
}
