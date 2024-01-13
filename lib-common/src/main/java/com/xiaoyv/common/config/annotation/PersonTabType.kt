package com.xiaoyv.common.config.annotation

import androidx.annotation.IntDef

/**
 * Class: [PersonTabType]
 *
 * @author why
 * @since 11/25/23
 */
@IntDef(
    PersonTabType.TYPE_OVERVIEW,
    PersonTabType.TYPE_CHARACTER,
    PersonTabType.TYPE_OPUS,
    PersonTabType.TYPE_COOPERATE,
    PersonTabType.TYPE_SAVE,
    PersonTabType.TYPE_PICTURE,
)
@Retention(AnnotationRetention.SOURCE)
annotation class PersonTabType {
    companion object {
        const val TYPE_OVERVIEW = 0
        const val TYPE_CHARACTER = 1
        const val TYPE_OPUS = 2
        const val TYPE_COOPERATE = 3
        const val TYPE_SAVE = 4
        const val TYPE_PICTURE = 5
    }
}
