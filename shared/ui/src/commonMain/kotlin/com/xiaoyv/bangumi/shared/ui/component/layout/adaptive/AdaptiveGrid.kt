package com.xiaoyv.bangumi.shared.ui.component.layout.adaptive

import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A non-lazy grid that fills the available width with equally sized adaptive columns.
 *
 * [fixedColumnCount] can be used when a window size class requires an exact column count.
 */
@Composable
@OptIn(ExperimentalGridApi::class)
fun AdaptiveGrid(
    minColumnWidth: Dp,
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 0.dp,
    verticalSpacing: Dp = horizontalSpacing,
    fixedColumnCount: Int? = null,
    content: @Composable () -> Unit,
) {
    require(minColumnWidth > 0.dp) { "minColumnWidth must be greater than zero" }
    require(horizontalSpacing >= 0.dp) { "horizontalSpacing must be non-negative" }
    require(verticalSpacing >= 0.dp) { "verticalSpacing must be non-negative" }
    require(fixedColumnCount == null || fixedColumnCount > 0) {
        "fixedColumnCount must be greater than zero"
    }

    Grid(
        config = {
            val columnCount = fixedColumnCount ?: run {
                val spacingPx = horizontalSpacing.roundToPx()
                ((constraints.maxWidth + spacingPx) /
                        (minColumnWidth.roundToPx() + spacingPx))
                    .coerceAtLeast(1)
            }
            repeat(columnCount) { column(minmax(0.dp, 1.fr)) }
            gap(row = verticalSpacing, column = horizontalSpacing)
        },
        modifier = modifier,
    ) {
        content()
    }
}
