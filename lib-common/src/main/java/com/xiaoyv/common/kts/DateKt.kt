package com.xiaoyv.common.kts

import androidx.annotation.IntDef
import java.util.Calendar

/**
 * 获取当前日期
 */
val currentDate: Calendar
    get() = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
    }

/**
 * 获取当前年份
 */
val currentYear: Int
    get() = currentDate.get(Calendar.YEAR)

/**
 * 月份是从 0 开始的，所以要加 1
 */
val currentMonth: Int
    get() = currentDate.get(Calendar.MONTH) + 1

/**
 * 获取当前季节
 */
val currentSeason: Int
    @SeasonType
    get() = getSeason(currentMonth)

@IntDef(
    SeasonType.TYPE_FALL,
    SeasonType.TYPE_SPRING,
    SeasonType.TYPE_WINTER,
    SeasonType.TYPE_SUMMER
)
@Retention(AnnotationRetention.SOURCE)
annotation class SeasonType {
    companion object {
        const val TYPE_SPRING = 1
        const val TYPE_SUMMER = 2
        const val TYPE_FALL = 3
        const val TYPE_WINTER = 4
    }
}

private fun getSeason(month: Int): Int {
    return when (month) {
        in 1..3 -> SeasonType.TYPE_WINTER
        in 4..6 -> SeasonType.TYPE_SPRING
        in 7..9 -> SeasonType.TYPE_SUMMER
        in 10..12 -> SeasonType.TYPE_FALL
        else -> -1
    }
}

fun Long.formatDHM(): String {
    val milliseconds = this
    val days = milliseconds / (24 * 60 * 60 * 1000)
    val hours = (milliseconds % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)
    val minutes = (milliseconds % (60 * 60 * 1000)) / (60 * 1000)
    return String.format("%02dd %02dh %02dm", days, hours, minutes)
}