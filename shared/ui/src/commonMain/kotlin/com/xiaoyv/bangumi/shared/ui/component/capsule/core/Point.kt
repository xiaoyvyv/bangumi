package com.xiaoyv.bangumi.shared.ui.component.capsule.core

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.xiaoyv.bangumi.shared.ui.component.capsule.lerp
import kotlin.math.sqrt

@Immutable
data class Point(val x: Double, val y: Double) {

    @Stable
    operator fun unaryMinus(): Point {
        return Point(-x, -y)
    }

    @Stable
    operator fun minus(other: Point): Point {
        return Point(x - other.x, y - other.y)
    }

    @Stable
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }

    @Stable
    operator fun times(operand: Double): Point {
        return Point(x * operand, y * operand)
    }

    @Stable
    operator fun div(operand: Double): Point {
        return Point(x / operand, y / operand)
    }

    @Stable
    fun normalized(): Point {
        val length = sqrt(x * x + y * y)
        return if (length != 0.0) this / length else Zero
    }

    companion object {

        @Stable
        val Zero: Point = Point(0.0, 0.0)
    }
}

@Stable
fun lerp(start: Point, stop: Point, fraction: Double): Point {
    return Point(
        lerp(start.x, stop.x, fraction),
        lerp(start.y, stop.y, fraction)
    )
}
