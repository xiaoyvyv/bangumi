package com.xiaoyv.bangumi.shared.core.utils

import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

private val units = arrayOf("B", "KB", "MB", "GB", "TB", "PB")

/**
 * 保留小数
 */
fun Double.toFixed(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return (this * factor).roundToInt() / factor
}

fun Float.toFixed(decimals: Int): Float {
    return toDouble().toFixed(decimals).toFloat()
}

fun Double.toTrimString(): String {
    return if (this == toLong().toDouble()) toLong().toString() else toString()
}

fun Long.formatFileSize(): String {
    if (this <= 0L) return "0 B"
    val digitGroups = (log10(toDouble()) / log10(1024.0)).toInt()
    val size = this / 1024.0.pow(digitGroups.toDouble())
    val rounded = (size * 10).toInt() / 10.0
    return buildString {
        append(rounded)
        append(" ")
        append(units[digitGroups])
    }
}

fun String?.toLongValue(default: Long = 0): Long {
    return orEmpty().toLongOrNull() ?: default
}

fun String?.toDoubleValue(default: Double = .0): Double {
    return orEmpty().toDoubleOrNull() ?: default
}

fun Int.formatShort(decimals: Int): String {
    return toLong().formatShort(decimals)
}

fun Long.formatShort(decimals: Int): String {
    val absValue = abs(this)
    val sign = if (this < 0) "-" else ""
    return when {
        absValue >= 1_000_000_000 -> sign + (absValue / 1_000_000_000.0).toFixed(decimals) + "B"
        absValue >= 1_000_000 -> sign + (absValue / 1_000_000.0).toFixed(decimals) + "M"
        absValue >= 1_000 -> sign + (absValue / 1_000.0).toFixed(decimals) + "K"
        else -> this.toString()
    }
}