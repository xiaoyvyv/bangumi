@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef
import androidx.annotation.StringDef

/**
 * Class: [ProfileTab]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    ProfileTab.COLLECTION,
    ProfileTab.MONO,
    ProfileTab.BLOG,
    ProfileTab.INDEX,
    ProfileTab.GROUP,
    ProfileTab.FRIEND
)
@Retention(AnnotationRetention.SOURCE)
annotation class ProfileTab {
    companion object Companion {
        const val COLLECTION = "collection"
        const val MONO = "mono"
        const val BLOG = "blog"
        const val INDEX = "index"
        const val GROUP = "group"
        const val FRIEND = "friend"
    }
}

@IntDef(
    ProfileMenu.TIME_MACHINE,
    ProfileMenu.TIMELINE,
    ProfileMenu.BIO,
    ProfileMenu.COLLECTION,
    ProfileMenu.FRIEND,
    ProfileMenu.STATE,
)
@Retention(AnnotationRetention.SOURCE)
annotation class ProfileMenu {
    companion object Companion {
        const val TIME_MACHINE = 1
        const val TIMELINE = 2
        const val BIO = 3
        const val COLLECTION = 4
        const val FRIEND = 5
        const val STATE = 6
    }
}

