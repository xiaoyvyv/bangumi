package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.IntDef

/**
 * Class: [ReportValueType]
 *
 * @author why
 * @since 11/25/23
 */
@IntDef(
    ReportValueType.UNKNOWN,
    ReportValueType.VALUE_1,
    ReportValueType.VALUE_2,
    ReportValueType.VALUE_3,
    ReportValueType.VALUE_4,
    ReportValueType.VALUE_5,
    ReportValueType.VALUE_6,
    ReportValueType.VALUE_7,
    ReportValueType.VALUE_8,
    ReportValueType.VALUE_9,
    ReportValueType.VALUE_99,
)
@Retention(AnnotationRetention.SOURCE)
annotation class ReportValueType {
    companion object {
        const val UNKNOWN = 0
        const val VALUE_1 = 1
        const val VALUE_2 = 2
        const val VALUE_3 = 3
        const val VALUE_4 = 4
        const val VALUE_5 = 5
        const val VALUE_6 = 6
        const val VALUE_7 = 7
        const val VALUE_8 = 8
        const val VALUE_9 = 9
        const val VALUE_99 = 99
    }
}
