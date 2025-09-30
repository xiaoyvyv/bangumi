package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef

/**
 * Class: [ModeType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    ModeType.UNKNOWN,
    ModeType.ALL,
    ModeType.FRIENDS
)
@Retention(AnnotationRetention.SOURCE)
annotation class ModeType {
    companion object Companion {
        const val UNKNOWN = ""
        const val ALL = "all"
        const val FRIENDS = "friends"
    }
}
