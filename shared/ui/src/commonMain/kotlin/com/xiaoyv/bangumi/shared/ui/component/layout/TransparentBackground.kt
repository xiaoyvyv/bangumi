package com.xiaoyv.bangumi.shared.ui.component.layout

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TransparentBackground(
    modifier: Modifier = Modifier,
    tileSize: Dp = 10.dp,
    lightColor: Color = Color(0xFFC0C0C0),
    darkColor: Color = Color(0xFFE0E0E0),
) {
    val tileSizePx = with(LocalDensity.current) { tileSize.toPx() }

    Canvas(modifier = modifier) {
        val rows = (size.height / tileSizePx).toInt() + 1
        val cols = (size.width / tileSizePx).toInt() + 1

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val isLight = (row + col) % 2 == 0
                drawRect(
                    color = if (isLight) lightColor else darkColor,
                    topLeft = Offset(x = col * tileSizePx, y = row * tileSizePx),
                    size = Size(tileSizePx, tileSizePx)
                )
            }
        }
    }
}
