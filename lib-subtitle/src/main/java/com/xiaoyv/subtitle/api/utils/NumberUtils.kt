package com.xiaoyv.subtitle.api.utils

object NumberUtils {
    @JvmStatic
    fun toDouble(trim: String?): Double {
        return trim.orEmpty().toDoubleOrNull() ?: 0.0
    }

    @JvmStatic
    fun toInt(value: String?): Int {
        return value.orEmpty().toIntOrNull() ?: 0
    }

    @JvmStatic
    fun toFloat(trim: String?): Float {
        return trim.orEmpty().toFloatOrNull() ?: 0f
    }
}