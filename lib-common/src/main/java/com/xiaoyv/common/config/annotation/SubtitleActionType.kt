package com.xiaoyv.common.config.annotation

import androidx.annotation.IntDef

/**
 * Class: [SubtitleActionType]
 *
 * @author why
 * @since 11/25/23
 */
@IntDef(
    SubtitleActionType.TYPE_SHARE,
    SubtitleActionType.TYPE_COPY,
    SubtitleActionType.TYPE_TRANSLATE,
)
@Retention(AnnotationRetention.SOURCE)
annotation class SubtitleActionType {
    companion object {
        const val TYPE_SHARE = 1
        const val TYPE_COPY = 2
        const val TYPE_TRANSLATE = 3
    }
}
