package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef

@StringDef(
    EpisodeAbbrType.TYPE_MAIN,
    EpisodeAbbrType.TYPE_ED,
    EpisodeAbbrType.TYPE_OP,
    EpisodeAbbrType.TYPE_SP,
    EpisodeAbbrType.TYPE_MAD,
    EpisodeAbbrType.TYPE_PV,
    EpisodeAbbrType.TYPE_OTHER
)
@Retention(AnnotationRetention.SOURCE)
annotation class EpisodeAbbrType {
    companion object {
        const val TYPE_MAIN = ""
        const val TYPE_ED = "ED"
        const val TYPE_OP = "OP"
        const val TYPE_SP = "SP"
        const val TYPE_MAD = "MAD"
        const val TYPE_PV = "PV"
        const val TYPE_OTHER = "O"
    }
}