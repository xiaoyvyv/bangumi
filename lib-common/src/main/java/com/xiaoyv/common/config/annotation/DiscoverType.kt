package com.xiaoyv.common.config.annotation

import androidx.annotation.StringDef

/**
 * Class: [DiscoverType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    DiscoverType.TYPE_HOME,
    DiscoverType.TYPE_MONO,
    DiscoverType.TYPE_BLOG,
    DiscoverType.TYPE_INDEX,
    DiscoverType.TYPE_GROUP,
    DiscoverType.TYPE_FRIEND,
    DiscoverType.TYPE_WIKI,
)
@Retention(AnnotationRetention.SOURCE)
annotation class DiscoverType {
    companion object {
        const val TYPE_HOME = "list"
        const val TYPE_MONO = "mono"
        const val TYPE_BLOG = "blog"
        const val TYPE_INDEX = "index"
        const val TYPE_GROUP = "groups"
        const val TYPE_FRIEND = "friends"
        const val TYPE_WIKI = "wiki"
    }
}
