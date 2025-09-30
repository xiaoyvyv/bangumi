package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef

@StringDef(
    IndexHomepageType.HOT,
    IndexHomepageType.NEWEST
)
@Retention(AnnotationRetention.SOURCE)
annotation class IndexHomepageType {
    companion object {
        const val NEWEST = ""
        const val HOT = "collect"
    }
}
