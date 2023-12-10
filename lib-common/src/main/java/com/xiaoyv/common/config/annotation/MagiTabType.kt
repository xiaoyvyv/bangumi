package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [MagiTabType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    MagiTabType.TYPE_QUESTION,
    MagiTabType.TYPE_HISTORY,
    MagiTabType.TYPE_RANK
)
@Retention(AnnotationRetention.SOURCE)
annotation class MagiTabType {
    companion object {
        const val TYPE_QUESTION = "question"
        const val TYPE_HISTORY = "history"
        const val TYPE_RANK = "rank"
    }
}
