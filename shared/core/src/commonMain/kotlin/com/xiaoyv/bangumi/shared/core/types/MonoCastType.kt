package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef


/**
 * - 1 主角
 * - 2 配角
 * - 3 客串
 * - 4 闲角
 * - 5 旁白
 */
@IntDef(
    MonoCastType.UNKNOWN,
    MonoCastType.MAIN,
    MonoCastType.CO_STAR,
    MonoCastType.GUEST,
    MonoCastType.BLANK,
    MonoCastType.OTHER
)
@Retention(AnnotationRetention.SOURCE)
annotation class MonoCastType {
    companion object Companion {
        const val UNKNOWN = 0
        const val MAIN = 1
        const val CO_STAR = 2
        const val GUEST = 3
        const val BLANK = 4
        const val OTHER = 5

        fun string(@MonoCastType type: Int): String {
            return when (type) {
                MAIN -> "主角"
                CO_STAR -> "配角"
                GUEST -> "客串"
                BLANK -> "旁白"
                OTHER -> "闲角"
                else -> "未知"
            }
        }
    }
}
