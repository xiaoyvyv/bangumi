package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef

@IntDef(
    EpisodeActionMenu.REMOVE,
    EpisodeActionMenu.WISH,
    EpisodeActionMenu.DONE,
    EpisodeActionMenu.SKIP_TO,
    EpisodeActionMenu.DROP
)
@Retention(AnnotationRetention.SOURCE)
annotation class EpisodeActionMenu {
    companion object {
        const val REMOVE = 0
        const val WISH = 1
        const val DONE = 2
        const val SKIP_TO = 3
        const val DROP = 4
    }
}