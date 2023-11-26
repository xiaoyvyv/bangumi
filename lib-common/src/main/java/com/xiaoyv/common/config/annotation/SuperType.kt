package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [SuperType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    SuperType.TYPE_ALL,
    SuperType.TYPE_GROUP,
    SuperType.TYPE_MY_GROUP,
    SuperType.TYPE_SUBJECT,
    SuperType.TYPE_EP,
    SuperType.TYPE_MONO
)
@Retention(AnnotationRetention.SOURCE)
annotation class SuperType {
    companion object {
        const val TYPE_ALL = ""
        const val TYPE_GROUP = "group"
        const val TYPE_MY_GROUP = "my_group"
        const val TYPE_SUBJECT = "subject"
        const val TYPE_EP = "ep"
        const val TYPE_MONO = "mono"
    }
}