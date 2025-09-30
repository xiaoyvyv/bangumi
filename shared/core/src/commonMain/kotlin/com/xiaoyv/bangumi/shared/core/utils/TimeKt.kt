package com.xiaoyv.bangumi.shared.core.utils


import com.xiaoyv.bangumi.shared.System
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

val infoYearMonthRegex by lazy { "(\\d{4}-\\d{1,2})|((\\d{4})年(\\d{1,2})月)".toRegex() }
val infoMonthDayRegex by lazy { "(\\d{4}-\\d{1,2}-\\d{1,2})|((\\d{1,2})月(\\d{1,2})日)".toRegex() }
val infoYearMonthDayRegex by lazy { "(\\d{1,2}月\\d{1,2}日)|(\\d{4}年\\d{1,2}月\\d{1,2}日)".toRegex() }

/**
 * - yyyy-MM-dd HH:mm
 * - yyyy-MM-dd HH:mm:ss
 * - yyyy-M-d HH:mm
 * - yyyy-M-d HH:mm:ss
 * - yyyy-MM-dd
 * - yyyy-M-d
 * - 以及同样的 / 分隔符（yyyy/M/d）
 */
fun String?.formatMills(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
    if (this.isNullOrBlank()) return 0

    return try {
        // 拆分日期和时间
        val parts = this.trim().split(" ")
        val datePart = parts[0]
        val timePart = parts.getOrNull(1)

        // 解析日期
        val (year, month, day) = datePart.split("-", "/")
            .map { it.toInt() }
            .let { Triple(it[0], it[1], it[2]) }

        val localDateTime = if (timePart != null) {
            // 解析时间
            val timeSegments = timePart.split(":").map { it.toInt() }
            val hour = timeSegments.getOrNull(0) ?: 0
            val minute = timeSegments.getOrNull(1) ?: 0
            val second = timeSegments.getOrNull(2) ?: 0
            LocalDateTime(year, month, day, hour, minute, second)
        } else {
            // 只有日期
            LocalDateTime(year, month, day, 0, 0, 0)
        }

        localDateTime.toInstant(timeZone).toEpochMilliseconds()
    } catch (_: Exception) {
        0
    }
}


/**
 * 秒 -> 时分秒
 */
fun Long.formatHMS(): String {
    val sec = this % 60
    val min = this / 60 % 60
    val hour = this / 60 / 60
    return hour.toString().padStart(2, '0') + ":" +
            min.toString().padStart(2, '0') + ":" +
            sec.toString().padStart(2, '0')
}


/**
 * xx 秒前，xx 分钟
 */
fun Long.formatAgo(now: Long = System.currentTimeMillis()): String {
    val diff = (now - this).coerceAtLeast(0L)

    val second = 1000L
    val minute = 60 * second
    val hour = 60 * minute
    val day = 24 * hour
    val month = 30 * day
    val year = 365 * day

    return when {
        diff < minute -> "${diff / second}秒前"
        diff < hour -> "${diff / minute}分钟前"
        diff < day -> "${diff / hour}小时前"
        diff < month -> "${diff / day}天前"
        diff < year -> "${diff / month}个月前"
        diff < 2 * year -> "${diff / year}年前"
        else -> formatDate("yyyy-MM-dd")
    }
}

/**
 * 使用 kotlinx-datetime 格式化时间戳（毫秒）
 */
fun Long.formatDate(pattern: String, timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val dateTime = instant.toLocalDateTime(timeZone)

    return pattern
        .replace("yyyy", dateTime.year.toString().padStart(4, '0'))
        .replace("MM", dateTime.month.number.toString().padStart(2, '0'))
        .replace("dd", dateTime.day.toString().padStart(2, '0'))
        .replace("HH", dateTime.hour.toString().padStart(2, '0'))
        .replace("mm", dateTime.minute.toString().padStart(2, '0'))
        .replace("ss", dateTime.second.toString().padStart(2, '0'))
}

/**
 * 是否为今天
 */
fun Long.isToday(timeZone: TimeZone = TimeZone.currentSystemDefault()): Boolean {
    val instant = Instant.fromEpochMilliseconds(this)
    val dateTime = instant.toLocalDateTime(timeZone)
    val now = Clock.System.now().toLocalDateTime(timeZone)
    return dateTime.date == now.date
}


fun currentYear() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year

/**
 * 今天星期几 1-7
 */
fun currentWeekDay() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek.isoDayNumber

/**
 * 明天星期几 1-7
 */
fun tomorrowWeekDay(): Int {
    val tomorrow = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.plus(1, DateTimeUnit.DAY)
    val tomorrowWeekDay = tomorrow.dayOfWeek.isoDayNumber
    return tomorrowWeekDay
}

fun currentTime(): LocalDateTime {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}

inline fun <T> measureBlockTimeMillis(block: () -> T): T {
    val start = System.currentTimeMillis()
    val result = block()
    val end = System.currentTimeMillis()
    debugLog { "Block executed in ${end - start} ms" }
    return result
}

/**
 * 解析 "60s ago" / "5m ago" / "1h 2m ago" / "1d ago" / "3M ago" / "2y ago"
 * 返回 Unix 时间戳（毫秒）
 *
 * 注意：M 按 30 天计算，y 按 365 天计算
 */
fun String.parseAgoToTimestamp(now: Long = System.currentTimeMillis()): Long {
    var totalMillis = 0L
    val parts = split(" ")
    for (part in parts) {
        when {
            part.endsWith("s") -> {
                val value = part.dropLast(1).toLongOrNull() ?: 0L
                totalMillis += value * 1000
            }

            part.endsWith("m") -> {
                val value = part.dropLast(1).toLongOrNull() ?: 0L
                totalMillis += value * 60_000
            }

            part.endsWith("h") -> {
                val value = part.dropLast(1).toLongOrNull() ?: 0L
                totalMillis += value * 3_600_000
            }

            part.endsWith("d") -> {
                val value = part.dropLast(1).toLongOrNull() ?: 0L
                totalMillis += value * 86_400_000
            }

            part.endsWith("M") -> { // 月，近似 30 天
                val value = part.dropLast(1).toLongOrNull() ?: 0L
                totalMillis += value * 30L * 86_400_000
            }

            part.endsWith("y") -> { // 年，近似 365 天
                val value = part.dropLast(1).toLongOrNull() ?: 0L
                totalMillis += value * 365L * 86_400_000
            }
        }
    }

    return now - totalMillis
}
