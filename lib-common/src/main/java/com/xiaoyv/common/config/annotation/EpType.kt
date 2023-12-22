package com.xiaoyv.common.config.annotation

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
