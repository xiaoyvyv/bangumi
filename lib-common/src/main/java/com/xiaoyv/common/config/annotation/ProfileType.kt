package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [ProfileType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    ProfileType.TYPE_COLLECTION,
    ProfileType.TYPE_MONO,
    ProfileType.TYPE_BLOG,
    ProfileType.TYPE_INDEX,
    ProfileType.TYPE_GROUP,
    ProfileType.TYPE_FRIEND,
    ProfileType.TYPE_WIKI,
)
@Retention(AnnotationRetention.SOURCE)
annotation class ProfileType {
    companion object {
        const val TYPE_COLLECTION = "list"
        const val TYPE_MONO = "mono"
        const val TYPE_BLOG = "blog"
        const val TYPE_INDEX = "index"
        const val TYPE_GROUP = "groups"
        const val TYPE_FRIEND = "friends"
        const val TYPE_WIKI = "wiki"
    }
}
