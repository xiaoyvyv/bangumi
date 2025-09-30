@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef

/**
 * Class: [RakuenTab]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    RakuenTab.ALL,
    RakuenTab.GROUP,
    RakuenTab.MY_GROUP,
    RakuenTab.SUBJECT,
    RakuenTab.EP,
    RakuenTab.MONO
)
@Retention(AnnotationRetention.SOURCE)
annotation class RakuenTab {
    companion object Companion {
        const val ALL = ""
        const val GROUP = "group"
        const val MY_GROUP = "my_group"
        const val SUBJECT = "subject"
        const val EP = "ep"
        const val MONO = "mono"
    }
}