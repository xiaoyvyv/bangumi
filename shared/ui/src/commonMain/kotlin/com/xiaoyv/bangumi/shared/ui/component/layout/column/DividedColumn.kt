package com.xiaoyv.bangumi.shared.ui.component.layout.column

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.dp
import kotlin.math.max

private enum class SlotsEnum {
    Content,
    Divider
}

/**
 * 支持分割线插槽与水平对齐的 DividedColumn
 *
 * @param modifier 修饰符
 * @param horizontalAlignment 子项和分割线的水平对齐方式（默认 Alignment.Start，与原生 Column 一致）
 * @param divider 分割线插槽，提供 (index: Int) 回调，index 代表当前是第几个分割线
 * @param content 内容子项
 */
@Composable
fun DividedColumn(
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    divider: @Composable (index: Int) -> Unit = {
        HorizontalDivider(color = MaterialTheme.colorScheme.background, thickness = 3.dp)
    },
    content: @Composable () -> Unit
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        val contentMeasurables = subcompose(SlotsEnum.Content, content)
        val itemCount = contentMeasurables.size

        if (itemCount == 0) {
            return@SubcomposeLayout layout(0, 0) {}
        }

        val childConstraints = constraints.copy(minHeight = 0)
        val contentPlaceables = arrayOfNulls<Placeable>(itemCount)

        var totalHeight = 0
        var maxWidth = constraints.minWidth

        for (i in 0 until itemCount) {
            val placeable = contentMeasurables[i].measure(childConstraints)
            contentPlaceables[i] = placeable
            totalHeight += placeable.height
            maxWidth = max(maxWidth, placeable.width)
        }

        val dividerCount = itemCount - 1
        val dividerPlaceables = arrayOfNulls<Placeable>(dividerCount)

        if (dividerCount > 0) {
            val dividerConstraints = constraints.copy(
                minHeight = 0,
                maxWidth = if (constraints.hasBoundedWidth) constraints.maxWidth else maxWidth
            )

            for (i in 0 until dividerCount) {
                val dividerMeasurables = subcompose(SlotsEnum.Divider to i) {
                    divider(i)
                }
                if (dividerMeasurables.isNotEmpty()) {
                    val placeable = dividerMeasurables.first().measure(dividerConstraints)
                    dividerPlaceables[i] = placeable
                    totalHeight += placeable.height
                    maxWidth = max(maxWidth, placeable.width)
                }
            }
        }

        maxWidth = maxWidth.coerceIn(constraints.minWidth, constraints.maxWidth)
        val finalHeight = totalHeight.coerceIn(constraints.minHeight, constraints.maxHeight)

        // 计算对齐并依次布局内容与分割线
        layout(maxWidth, finalHeight) {
            var currentY = 0
            for (i in 0 until itemCount) {
                // 放置内容项
                contentPlaceables[i]?.let { placeable ->
                    val x = horizontalAlignment.align(
                        size = placeable.width,
                        space = maxWidth,
                        layoutDirection = layoutDirection
                    )
                    placeable.placeRelative(x, currentY)
                    currentY += placeable.height
                }

                // 放置分割线项
                if (i < dividerCount) {
                    dividerPlaceables[i]?.let { dividerPlaceable ->
                        val x = horizontalAlignment.align(
                            size = dividerPlaceable.width,
                            space = maxWidth,
                            layoutDirection = layoutDirection
                        )
                        dividerPlaceable.placeRelative(x, currentY)
                        currentY += dividerPlaceable.height
                    }
                }
            }
        }
    }
}