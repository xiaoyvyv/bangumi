@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef

@StringDef(
    RakuenFlagType.TYPE_UNKNOWN,
    RakuenFlagType.TYPE_NEW,
    RakuenFlagType.TYPE_OLD,
    RakuenFlagType.TYPE_OLDEST,
    RakuenFlagType.TYPE_HOT
)
@Retention(AnnotationRetention.SOURCE)
annotation class RakuenFlagType {
    companion object Companion {
        const val TYPE_UNKNOWN = ""
        const val TYPE_NEW = "new"
        const val TYPE_OLD = "old"
        const val TYPE_OLDEST = "oldest"
        const val TYPE_HOT = "hot"
    }
}