package com.xiaoyv.bangumi.shared.ui.component.capsule

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.util.fastCoerceAtLeast

@Stable
fun ContinuousRoundedRectangle.concentricInset(padding: Dp): ContinuousRoundedRectangle {
    return ConcentricContinuousRoundedRectangle(this, padding)
}

@Stable
fun ContinuousRoundedRectangle.concentricOutset(padding: Dp): ContinuousRoundedRectangle {
    return ConcentricContinuousRoundedRectangle(this, -padding)
}

@Stable
fun AbsoluteContinuousRoundedRectangle.concentricInset(padding: Dp): AbsoluteContinuousRoundedRectangle {
    return ConcentricAbsoluteContinuousRoundedRectangle(this, padding)
}

@Stable
fun AbsoluteContinuousRoundedRectangle.concentricOutset(padding: Dp): AbsoluteContinuousRoundedRectangle {
    return ConcentricAbsoluteContinuousRoundedRectangle(this, -padding)
}

@Immutable
private data class ConcentricContinuousRoundedRectangle(
    val containerShape: ContinuousRoundedRectangle,
    val padding: Dp
) : ContinuousRoundedRectangle(
    topStart = ConcentricCornerSize(containerShape.topStart, padding),
    topEnd = ConcentricCornerSize(containerShape.topEnd, padding),
    bottomEnd = ConcentricCornerSize(containerShape.bottomEnd, padding),
    bottomStart = ConcentricCornerSize(containerShape.bottomStart, padding),
    continuity = containerShape.continuity
)

@Immutable
private data class ConcentricAbsoluteContinuousRoundedRectangle(
    val containerShape: AbsoluteContinuousRoundedRectangle,
    val padding: Dp
) : AbsoluteContinuousRoundedRectangle(
    topLeft = ConcentricCornerSize(containerShape.topStart, padding),
    topRight = ConcentricCornerSize(containerShape.topEnd, padding),
    bottomRight = ConcentricCornerSize(containerShape.bottomEnd, padding),
    bottomLeft = ConcentricCornerSize(containerShape.bottomStart, padding),
    continuity = containerShape.continuity
)

@Immutable
private data class ConcentricCornerSize(
    private val containerCornerSize: CornerSize,
    private val padding: Dp
) : CornerSize {

    override fun toPx(shapeSize: Size, density: Density): Float {
        return (containerCornerSize.toPx(shapeSize, density) - with(density) { padding.toPx() })
            .fastCoerceAtLeast(0f)
    }
}
