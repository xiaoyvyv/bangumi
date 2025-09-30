package com.xiaoyv.bangumi.shared.ui.component.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import com.xiaoyv.bangumi.shared.ui.kts.isSmallScreen

@Composable
fun AdaptiveTwoPanel(
    modifier: Modifier = Modifier,
    bias: Float = 0.5f,
    start: @Composable BoxScope.() -> Unit,
    end: @Composable BoxScope.() -> Unit,
) {
    val startWeight = bias.coerceIn(0f, 1f)

    if (isSmallScreen) {
        // 竖屏布局（上下）
        Column(modifier = modifier) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(startWeight)
                    .clipToBounds(),
                content = start
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f - startWeight)
                    .clipToBounds(),
                content = end
            )
        }
    } else {
        // 横屏布局（左右）
        Row(modifier = modifier) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(startWeight)
                    .clipToBounds(),
                content = start
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f - startWeight)
                    .clipToBounds(),
                content = end
            )
        }
    }
}
