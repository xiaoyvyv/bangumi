package com.xiaoyv.common.config.annotation

import androidx.annotation.IntDef
import androidx.annotation.StringDef

/**
 * Class: [EpAbbrType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    EpAbbrType.TYPE_MAIN,
    EpAbbrType.TYPE_ED,
    EpAbbrType.TYPE_OP,
    EpAbbrType.TYPE_SP,
    EpAbbrType.TYPE_MAD,
    EpAbbrType.TYPE_PV,
    EpAbbrType.TYPE_OTHER
)
@Retention(AnnotationRetention.SOURCE)
annotation class EpAbbrType {
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

@IntDef(
    EpApiType.TYPE_MAIN,
    EpApiType.TYPE_ED,
    EpApiType.TYPE_OP,
    EpApiType.TYPE_SP,
    EpApiType.TYPE_MAD,
    EpApiType.TYPE_PV,
    EpApiType.TYPE_OTHER
)
@Retention(AnnotationRetention.SOURCE)
annotation class EpApiType {
    companion object {
        const val TYPE_MAIN = 0
        const val TYPE_ED = 3
        const val TYPE_OP = 2
        const val TYPE_SP = 1
        const val TYPE_PV = 4
        const val TYPE_MAD = 5
        const val TYPE_OTHER = 6

        fun toAbbrType(@EpApiType epApiType: Int): String {
            return when (epApiType) {
                TYPE_MAIN -> EpAbbrType.TYPE_MAIN
                TYPE_ED -> EpAbbrType.TYPE_ED
                TYPE_OP -> EpAbbrType.TYPE_OP
                TYPE_SP -> EpAbbrType.TYPE_SP
                TYPE_MAD -> EpAbbrType.TYPE_MAD
                TYPE_PV -> EpAbbrType.TYPE_PV
                TYPE_OTHER -> EpAbbrType.TYPE_OTHER
                else -> EpAbbrType.TYPE_MAIN
            }
        }
    }
}
