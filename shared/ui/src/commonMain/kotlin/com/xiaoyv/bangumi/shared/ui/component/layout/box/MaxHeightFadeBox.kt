package com.xiaoyv.bangumi.shared.ui.component.layout.box

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun MaxHeightFadeBox(
    modifier: Modifier = Modifier,
    maxHeight: Dp,
    fadeHeight: Dp = 56.dp,
    surfaceColor: Color = MaterialTheme.colorScheme.surface,
    overlay: @Composable BoxScope.() -> Unit = {},
    content: @Composable () -> Unit,
) {
    SubcomposeLayout(modifier = modifier.clipToBounds()) { constraints ->
        val maxHeightPx = maxHeight.roundToPx()
        val fadeHeightPx = fadeHeight.roundToPx()

        val contentPlaceable = subcompose("content", content)
            .first()
            .measure(constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity))

        val isOverflow = contentPlaceable.height > maxHeightPx

        val layoutHeight = contentPlaceable.height
            .coerceAtMost(maxHeightPx)
            .coerceIn(constraints.minHeight, constraints.maxHeight)

        val overlayPlaceable = if (isOverflow) {
            subcompose("overlay") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(fadeHeight)
                        .background(Brush.verticalGradient(colors = listOf(Color.Transparent, surfaceColor))),
                    content = overlay
                )
            }.first().measure(
                Constraints(
                    minWidth = constraints.minWidth,
                    maxWidth = constraints.maxWidth,
                    minHeight = fadeHeightPx,
                    maxHeight = fadeHeightPx
                )
            )
        } else {
            null
        }

        layout(
            width = contentPlaceable.width.coerceIn(
                constraints.minWidth,
                constraints.maxWidth
            ),
            height = layoutHeight
        ) {
            contentPlaceable.placeRelative(0, 0)

            overlayPlaceable?.placeRelative(
                x = 0,
                y = layoutHeight - overlayPlaceable.height
            )
        }
    }
}