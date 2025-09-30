package com.xiaoyv.bangumi.shared.ui.component.space

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val LayoutPadding: Dp = 16.dp
val LayoutPaddingHalf: Dp = 8.dp
val LayoutGridWidth = 136.dp

val BrushVerticalTransparentToHalfBlack = Brush.verticalGradient(
    colors = listOf(
        Color.Transparent,
        Color.Black.copy(alpha = 0.75f)
    )
)

val BrushSubjectBanner = Brush.horizontalGradient(
    colors = listOf(
        Color.Black.copy(alpha = 0.5f),
        Color.Transparent,
        Color.Transparent,
        Color.Transparent,
        Color.Transparent,
        Color.Black.copy(alpha = 0.5f),
    )
)

val BrushVerticalHalfBlackToTransparent = Brush.verticalGradient(
    colors = listOf(
        Color.Black.copy(alpha = 0.5f),
        Color.Transparent,
    )
)

val BrushVerticalTransparentToHalfRed = Brush.verticalGradient(
    colors = listOf(
        Color.Transparent,
        Color.Red.copy(1f),
    )
)

