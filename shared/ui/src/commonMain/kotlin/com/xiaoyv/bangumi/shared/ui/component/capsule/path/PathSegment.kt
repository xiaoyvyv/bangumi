package com.xiaoyv.bangumi.shared.ui.component.capsule.path

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import com.xiaoyv.bangumi.shared.ui.component.capsule.core.Point
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Immutable
sealed interface PathSegment {

    val from: Point
    val to: Point

    fun drawTo(path: Path)

    // -----------------------------
    // Line
    // -----------------------------
    data class Line(
        override val from: Point,
        override val to: Point
    ) : PathSegment {

        override fun drawTo(path: Path) {
            path.lineTo(
                to.x.toFloat(),
                to.y.toFloat()
            )
        }
    }

    // -----------------------------
    // Arc (multiplatform)
    // -----------------------------
    data class Arc(
        val center: Point,
        val radius: Double,
        val startAngle: Double, // radians
        val sweepAngle: Double  // radians
    ) : PathSegment {

        override val from: Point
            get() = Point(
                center.x + cos(startAngle) * radius,
                center.y + sin(startAngle) * radius
            )

        override val to: Point
            get() = Point(
                center.x + cos(startAngle + sweepAngle) * radius,
                center.y + sin(startAngle + sweepAngle) * radius
            )

        override fun drawTo(path: Path) {
            val rect = Rect(
                (center.x - radius).toFloat(),
                (center.y - radius).toFloat(),
                (center.x + radius).toFloat(),
                (center.y + radius).toFloat()
            )

            path.arcTo(
                rect = rect,
                startAngleDegrees = (startAngle * 180.0 / PI).toFloat(),
                sweepAngleDegrees = (sweepAngle * 180.0 / PI).toFloat(),
                forceMoveTo = false
            )
        }
    }

    // -----------------------------
    // Circle (multiplatform)
    // -----------------------------
    data class Circle(
        val center: Point,
        val radius: Double
    ) : PathSegment {

        override val from: Point
            get() = Point(center.x + radius, center.y)

        override val to: Point
            get() = from

        override fun drawTo(path: Path) {
            val rect = Rect(
                (center.x - radius).toFloat(),
                (center.y - radius).toFloat(),
                (center.x + radius).toFloat(),
                (center.y + radius).toFloat()
            )

            path.addOval(rect)
        }
    }

    // -----------------------------
    // Cubic Bézier
    // -----------------------------
    data class Cubic(
        val p0: Point,
        val p1: Point,
        val p2: Point,
        val p3: Point
    ) : PathSegment {

        override val from: Point
            get() = p0

        override val to: Point
            get() = p3

        override fun drawTo(path: Path) {
            path.cubicTo(
                p1.x.toFloat(), p1.y.toFloat(),
                p2.x.toFloat(), p2.y.toFloat(),
                p3.x.toFloat(), p3.y.toFloat()
            )
        }
    }
}
