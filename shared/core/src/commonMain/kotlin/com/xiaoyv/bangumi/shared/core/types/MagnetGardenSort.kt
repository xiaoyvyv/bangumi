@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.core.types

import androidx.annotation.StringDef

/**
 * Class: [MagnetGardenSort]
 *
 * @author why
 * @since 11/25/23
 */
@StringDef(
    MagnetGardenSort.RELATED,
    MagnetGardenSort.DATE_ASC,
    MagnetGardenSort.DATE_DESC
)
@Retention(AnnotationRetention.SOURCE)
annotation class MagnetGardenSort {
    companion object Companion {
        const val RELATED = "rel"
        const val DATE_ASC = "date-asc"
        const val DATE_DESC = "date-desc"

        fun string(@MagnetGardenSort type: String): String {
            return when (type) {
                RELATED -> "相关度"
                DATE_ASC -> "从旧到新"
                DATE_DESC -> "从新到旧"
                else -> "相关度"
            }
        }
    }
}