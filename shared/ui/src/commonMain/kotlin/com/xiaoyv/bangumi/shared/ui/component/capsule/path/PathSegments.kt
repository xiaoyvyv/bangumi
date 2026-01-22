package com.xiaoyv.bangumi.shared.ui.component.capsule.path

import androidx.compose.ui.graphics.Path
import kotlin.math.PI
import kotlin.math.abs

typealias PathSegments = List<PathSegment>

fun PathSegments.toPath(): Path {
    return Path().apply {
        if (isEmpty()) return@apply
        val startPoint = first().from
        moveTo(startPoint.x.toFloat(), startPoint.y.toFloat())
        forEach { it.drawTo(this) }
    }
}

fun PathSegments.toSvg(asDocument: Boolean = false): String = buildString {
    val bounds = this@toSvg.toPath().getBounds()

    if (asDocument) {
        append("""<svg xmlns="http://www.w3.org/2000/svg" """)
        appendLine("""viewBox="${bounds.left} ${bounds.top} ${bounds.width} ${bounds.height}">""")
    }

    if (isNotEmpty()) {
        if (asDocument) {
            append("""  <path d="""")
        }

        val startPoint = this@toSvg.first().from
        append("M${startPoint.x} ${startPoint.y}")

        this@toSvg.forEach { segment ->
            val string = when (segment) {
                is PathSegment.Line -> "L${segment.to.x} ${segment.to.y}"
                is PathSegment.Arc -> {
                    val largeArcFlag = if (abs(segment.sweepAngle) > PI) 1 else 0
                    val sweepFlag = if (segment.sweepAngle > 0) 1 else 0
                    "A${segment.radius} ${segment.radius} 0 $largeArcFlag $sweepFlag ${segment.to.x} ${segment.to.y}"
                }

                is PathSegment.Circle -> {
                    val cx = segment.center.x
                    val cy = segment.center.y
                    val r = segment.radius
                    "M${cx + r} $cy A$r $r 0 1 0 ${cx - r} $cy A$r $r 0 1 0 ${cx + r} $cy Z"
                }

                is PathSegment.Cubic -> "C${segment.p1.x} ${segment.p1.y}, ${segment.p2.x} ${segment.p2.y}, ${segment.p3.x} ${segment.p3.y}"
            }
            append(string)
        }

        if (asDocument) {
            appendLine(""""/>""")
        }
    }
    if (asDocument) {
        appendLine("""</svg>""")
    }
}
