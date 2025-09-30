package com.xiaoyv.bangumi.shared.ui.component.pager

import androidx.annotation.IntRange
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * [BgmGridPager]
 *
 * @since 2025/5/8
 */
@Composable
fun <T> BgmGridPager(
    items: List<T>,
    modifier: Modifier = Modifier,
    minItemSize: Dp = 38.dp,
    @IntRange(from = 1) maxRows: Int = 5,
    verticalSpacing: Dp = LayoutPaddingHalf,
    horizontalSpacing: Dp = LayoutPaddingHalf,
    contentPadding: PaddingValues = PaddingValues(),
    onInitialPage: (Int, Int) -> Int = { _, _ -> 0 },
    empty: @Composable () -> Unit = {},
    key: ((index: Int) -> Any)? = null,
    content: @Composable BoxScope.(T) -> Unit,
) {
    if (items.isEmpty()) empty() else BoxWithConstraints(modifier = modifier) {
        val screenWidth = maxWidth -
                contentPadding.calculateStartPadding(LocalLayoutDirection.current) -
                contentPadding.calculateEndPadding(LocalLayoutDirection.current)

        val columns = floor(screenWidth / (minItemSize + horizontalSpacing))
            .roundToInt()
            .coerceAtLeast(1)
        val horizontalSpacingCount = columns - 1
        val itemSize = (screenWidth - horizontalSpacing * horizontalSpacingCount) / columns
        val itemsPerPage = columns * maxRows
        val pageCount = ceil(items.size / itemsPerPage.toFloat()).roundToInt()
        val initialPage = remember { onInitialPage(itemsPerPage, pageCount) }
        val pagerState = rememberPagerState(initialPage = initialPage) { pageCount }

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            key = key,
            beyondViewportPageCount = 0,
            flingBehavior = PagerDefaults.flingBehavior(pagerState, pagerSnapDistance = PagerSnapDistance.atMost(10))
        ) { page ->
            val startIndex = page * itemsPerPage
            val endIndex = min(startIndex + itemsPerPage, items.size)
            val pageItems = items.subList(startIndex, endIndex)

            VerticalGrid(
                columns = SimpleGridCells.Fixed(columns),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                verticalArrangement = Arrangement.spacedBy(verticalSpacing),
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
            ) {
                pageItems.forEachIndexed { index, item ->
                    key(if (key != null) key(startIndex + index) else index) {
                        Box(modifier = Modifier.size(itemSize)) {
                            content(item)
                        }
                    }
                }
            }
        }
    }
}
