package com.xiaoyv.common.config.annotation

import androidx.annotation.IntDef
import androidx.annotation.StringDef

/**
 * Class: [EpType]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    EpType.TYPE_MAIN,
    EpType.TYPE_ED,
    EpType.TYPE_OP,
    EpType.TYPE_SP,
    EpType.TYPE_MAD,
    EpType.TYPE_PV,
    EpType.TYPE_OTHER
)
@Retention(AnnotationRetention.SOURCE)
annotation class EpType {
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

        fun toEpType(@EpApiType epApiType: Int): String {
            return when (epApiType) {
                TYPE_MAIN -> EpType.TYPE_MAIN
                TYPE_ED -> EpType.TYPE_ED
                TYPE_OP -> EpType.TYPE_OP
                TYPE_SP -> EpType.TYPE_SP
                TYPE_MAD -> EpType.TYPE_MAD
                TYPE_PV -> EpType.TYPE_PV
                TYPE_OTHER -> EpType.TYPE_OTHER
                else -> EpType.TYPE_MAIN
            }
        }
    }
}
