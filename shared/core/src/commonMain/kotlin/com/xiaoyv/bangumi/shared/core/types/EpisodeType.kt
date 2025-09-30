package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef

/**
 * Class: [EpisodeType]
 *
 * @author why
 * @since 11/25/23
 */
@IntDef(
    EpisodeType.TYPE_MAIN,
    EpisodeType.TYPE_ED,
    EpisodeType.TYPE_OP,
    EpisodeType.TYPE_SP,
    EpisodeType.TYPE_MAD,
    EpisodeType.TYPE_PV,
    EpisodeType.TYPE_OTHER
)
@Retention(AnnotationRetention.SOURCE)
annotation class EpisodeType {
    companion object {
        const val TYPE_MAIN = 0
        const val TYPE_ED = 3
        const val TYPE_OP = 2
        const val TYPE_SP = 1
        const val TYPE_PV = 4
        const val TYPE_MAD = 5
        const val TYPE_OTHER = 6

        fun toAbbrType(@EpisodeType type: Int): String {
            return when (type) {
                TYPE_MAIN -> EpisodeAbbrType.TYPE_MAIN
                TYPE_ED -> EpisodeAbbrType.TYPE_ED
                TYPE_OP -> EpisodeAbbrType.TYPE_OP
                TYPE_SP -> EpisodeAbbrType.TYPE_SP
                TYPE_MAD -> EpisodeAbbrType.TYPE_MAD
                TYPE_PV -> EpisodeAbbrType.TYPE_PV
                TYPE_OTHER -> EpisodeAbbrType.TYPE_OTHER
                else -> EpisodeAbbrType.TYPE_MAIN
            }
        }
    }
}
