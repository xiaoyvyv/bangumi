package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef

/**
 * [TimelineTab]
 *
 * @since 2025/5/11
 */
@StringDef(
    TimelineTab.DYNAMIC,
    TimelineTab.SPIT_OUT,
    TimelineTab.BOOKMARK,
    TimelineTab.PROGRESS,
    TimelineTab.BLOG,
    TimelineTab.MONO,
    TimelineTab.FRIEND,
    TimelineTab.GROUP,
    TimelineTab.WIKI,
    TimelineTab.INDEX,
)
@Retention(AnnotationRetention.SOURCE)
annotation class TimelineTab {
    companion object {
        const val DYNAMIC = "all"
        const val SPIT_OUT = "say"
        const val BOOKMARK = "subject"
        const val PROGRESS = "process"
        const val BLOG = "blog"
        const val MONO = "mono"
        const val FRIEND = "relation"
        const val GROUP = "group"
        const val WIKI = "wiki"
        const val INDEX = "index"
    }
}

