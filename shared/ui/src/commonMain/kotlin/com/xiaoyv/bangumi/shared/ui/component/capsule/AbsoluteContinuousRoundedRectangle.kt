package com.xiaoyv.bangumi.shared.ui.component.capsule

import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import kotlin.math.min

@Immutable
open class AbsoluteContinuousRoundedRectangle(
    topLeft: CornerSize,
    topRight: CornerSize,
    bottomRight: CornerSize,
    bottomLeft: CornerSize,
    open val continuity: Continuity = Continuity.Default
) : CornerBasedShape(
    topStart = topLeft,
    topEnd = topRight,
    bottomEnd = bottomRight,
    bottomStart = bottomLeft
) {

    override fun createOutline(
        size: Size,
        topStart: Float,
        topEnd: Float,
        bottomEnd: Float,
        bottomStart: Float,
        layoutDirection: LayoutDirection
    ): Outline {
        // rectangle
        if (topStart + topEnd + bottomEnd + bottomStart == 0f) {
            return Outline.Rectangle(size.toRect())
        }

        val maxRadius = min(size.width, size.height) * 0.5f
        val topLeft = topStart.fastCoerceIn(0f, maxRadius)
        val topRight = topEnd.fastCoerceIn(0f, maxRadius)
        val bottomRight = bottomEnd.fastCoerceIn(0f, maxRadius)
        val bottomLeft = bottomStart.fastCoerceIn(0f, maxRadius)

        return continuity.createRoundedRectangleOutline(
            size = size,
            topLeft = topLeft,
            topRight = topRight,
            bottomRight = bottomRight,
            bottomLeft = bottomLeft
        )
    }

    override fun copy(
        topStart: CornerSize,
        topEnd: CornerSize,
        bottomEnd: CornerSize,
        bottomStart: CornerSize
    ): AbsoluteContinuousRoundedRectangle {
        return AbsoluteContinuousRoundedRectangle(
            topLeft = topStart,
            topRight = topEnd,
            bottomRight = bottomEnd,
            bottomLeft = bottomStart,
            continuity = continuity
        )
    }

    fun copy(
        topLeft: CornerSize = this.topStart,
        topRight: CornerSize = this.topEnd,
        bottomRight: CornerSize = this.bottomEnd,
        bottomLeft: CornerSize = this.bottomStart,
        continuity: Continuity = this.continuity
    ): AbsoluteContinuousRoundedRectangle {
        return AbsoluteContinuousRoundedRectangle(
            topLeft = topLeft,
            topRight = topRight,
            bottomRight = bottomRight,
            bottomLeft = bottomLeft,
            continuity = continuity
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbsoluteContinuousRoundedRectangle) return false

        if (topStart != other.topStart) return false
        if (topEnd != other.topEnd) return false
        if (bottomEnd != other.bottomEnd) return false
        if (bottomStart != other.bottomStart) return false
        if (continuity != other.continuity) return false

        return true
    }

    override fun hashCode(): Int {
        var result = topStart.hashCode()
        result = 31 * result + topEnd.hashCode()
        result = 31 * result + bottomEnd.hashCode()
        result = 31 * result + bottomStart.hashCode()
        result = 31 * result + continuity.hashCode()
        return result
    }

    override fun toString(): String {
        return "AbsoluteContinuousRoundedRectangle(topLeft=$topStart, topRight=$topEnd, bottomRight=$bottomEnd, " +
                "bottomLeft=$bottomStart, continuity=$continuity)"
    }
}

@Stable
val AbsoluteContinuousRectangle: AbsoluteContinuousRoundedRectangle = AbsoluteContinuousRectangleImpl()

@Suppress("FunctionName")
@Stable
fun AbsoluteContinuousRectangle(continuity: Continuity = Continuity.Default): AbsoluteContinuousRoundedRectangle =
    AbsoluteContinuousRectangleImpl(continuity)

@Immutable
private data class AbsoluteContinuousRectangleImpl(
    override val continuity: Continuity = Continuity.Default
) : AbsoluteContinuousRoundedRectangle(
    topLeft = ZeroCornerSize,
    topRight = ZeroCornerSize,
    bottomRight = ZeroCornerSize,
    bottomLeft = ZeroCornerSize,
    continuity = continuity
) {

    override fun toString(): String {
        return "AbsoluteContinuousRectangle(continuity=$continuity)"
    }
}

private val FullCornerSize = CornerSize(50)

@Stable
val AbsoluteContinuousCapsule: AbsoluteContinuousRoundedRectangle = AbsoluteContinuousCapsule()

@Suppress("FunctionName")
@Stable
fun AbsoluteContinuousCapsule(continuity: Continuity = Continuity.Default): AbsoluteContinuousRoundedRectangle =
    AbsoluteContinuousCapsuleImpl(continuity)

@Immutable
private data class AbsoluteContinuousCapsuleImpl(
    override val continuity: Continuity = Continuity.Default
) : AbsoluteContinuousRoundedRectangle(
    topLeft = FullCornerSize,
    topRight = FullCornerSize,
    bottomRight = FullCornerSize,
    bottomLeft = FullCornerSize,
    continuity = continuity
) {

    override fun createOutline(
        size: Size,
        topStart: Float,
        topEnd: Float,
        bottomEnd: Float,
        bottomStart: Float,
        layoutDirection: LayoutDirection
    ): Outline = continuity.createCapsuleOutline(size)

    override fun toString(): String {
        return "AbsoluteContinuousCapsule(continuity=$continuity)"
    }
}

@Stable
fun AbsoluteContinuousRoundedRectangle(
    corner: CornerSize,
    continuity: Continuity = Continuity.Default
): AbsoluteContinuousRoundedRectangle =
    AbsoluteContinuousRoundedRectangle(
        topLeft = corner,
        topRight = corner,
        bottomRight = corner,
        bottomLeft = corner,
        continuity = continuity
    )

@Stable
fun AbsoluteContinuousRoundedRectangle(
    size: Dp,
    continuity: Continuity = Continuity.Default
): AbsoluteContinuousRoundedRectangle =
    AbsoluteContinuousRoundedRectangle(
        corner = CornerSize(size),
        continuity = continuity
    )

@Stable
fun AbsoluteContinuousRoundedRectangle(
    @FloatRange(from = 0.0) size: Float,
    continuity: Continuity = Continuity.Default
): AbsoluteContinuousRoundedRectangle =
    AbsoluteContinuousRoundedRectangle(
        corner = CornerSize(size),
        continuity = continuity
    )

@Stable
fun AbsoluteContinuousRoundedRectangle(
    @IntRange(from = 0, to = 100) percent: Int,
    continuity: Continuity = Continuity.Default
): AbsoluteContinuousRoundedRectangle =
    AbsoluteContinuousRoundedRectangle(
        corner = CornerSize(percent),
        continuity = continuity
    )

@Stable
fun AbsoluteContinuousRoundedRectangle(
    topLeft: Dp = 0f.dp,
    topRight: Dp = 0f.dp,
    bottomRight: Dp = 0f.dp,
    bottomLeft: Dp = 0f.dp,
    continuity: Continuity = Continuity.Default
): AbsoluteContinuousRoundedRectangle =
    AbsoluteContinuousRoundedRectangle(
        topLeft = CornerSize(topLeft),
        topRight = CornerSize(topRight),
        bottomRight = CornerSize(bottomRight),
        bottomLeft = CornerSize(bottomLeft),
        continuity = continuity
    )

@Stable
fun AbsoluteContinuousRoundedRectangle(
    @FloatRange(from = 0.0) topLeft: Float = 0f,
    @FloatRange(from = 0.0) topRight: Float = 0f,
    @FloatRange(from = 0.0) bottomRight: Float = 0f,
    @FloatRange(from = 0.0) bottomLeft: Float = 0f,
    continuity: Continuity = Continuity.Default
): AbsoluteContinuousRoundedRectangle =
    AbsoluteContinuousRoundedRectangle(
        topLeft = CornerSize(topLeft),
        topRight = CornerSize(topRight),
        bottomRight = CornerSize(bottomRight),
        bottomLeft = CornerSize(bottomLeft),
        continuity = continuity
    )

@Stable
fun AbsoluteContinuousRoundedRectangle(
    @IntRange(from = 0, to = 100) topLeftPercent: Int = 0,
    @IntRange(from = 0, to = 100) topRightPercent: Int = 0,
    @IntRange(from = 0, to = 100) bottomRightPercent: Int = 0,
    @IntRange(from = 0, to = 100) bottomLeftPercent: Int = 0,
    continuity: Continuity = Continuity.Default
): AbsoluteContinuousRoundedRectangle =
    AbsoluteContinuousRoundedRectangle(
        topLeft = CornerSize(topLeftPercent),
        topRight = CornerSize(topRightPercent),
        bottomRight = CornerSize(bottomRightPercent),
        bottomLeft = CornerSize(bottomLeftPercent),
        continuity = continuity
    )
