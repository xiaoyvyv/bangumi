package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef


/**
 * - 1 = 个人
 * - 2 = 公司
 * - 3 = 组合
 */
@IntDef(
    PersonType.UNKNOWN,
    PersonType.MAIN,
    PersonType.COMPANY,
    PersonType.BAND
)
@Retention(AnnotationRetention.SOURCE)
annotation class PersonType {
    companion object {
        const val UNKNOWN = 0
        const val MAIN = 1
        const val COMPANY = 2
        const val BAND = 3

        fun string(type: Int): String {
            return when (type) {
                MAIN -> "个人"
                COMPANY -> "公司"
                BAND -> "组合"
                else -> "未知"
            }
        }
    }
}
