package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef

@IntDef(
    HomeTab.HOME,
    HomeTab.MONO,
    HomeTab.BLOG,
    HomeTab.INDEX,
    HomeTab.GROUP
)
@Retention(AnnotationRetention.SOURCE)
annotation class HomeTab {
    companion object {
        const val HOME = 0
        const val MONO = 1
        const val BLOG = 2
        const val INDEX = 3
        const val GROUP = 4
    }
}