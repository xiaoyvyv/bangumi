package com.xiaoyv.bangumi.shared.core.utils

import kotlin.math.absoluteValue
import kotlin.math.roundToInt

object KMath {
    const val PI = kotlin.math.PI

    fun max(a: Int, b: Int) = kotlin.math.max(a, b)
    fun max(a: Float, b: Float) = kotlin.math.max(a, b)
    fun round(fl: Float): Int = fl.roundToInt()
    fun cos(d: Double) = kotlin.math.cos(d)
    fun min(a: Float, b: Float) = kotlin.math.min(a, b)
    fun abs(f: Float) = f.absoluteValue
}