package com.xiaoyv.bangumi.shared.ui.component.pager

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun rememberBgmPagerState(
    initialPage: Int = 0,
    @FloatRange(from = -0.5, to = 0.5) initialPageOffsetFraction: Float = 0f,
    onPageChange: (Int) -> Unit = {},
    pageCount: () -> Int,
): PagerState {
    val pagerState = rememberPagerState(initialPage, initialPageOffsetFraction, pageCount)
    LaunchedEffect(Unit) {
        snapshotFlow { pagerState.currentPage }.collect {
            onPageChange(it)
        }
    }
    return pagerState
}

@Composable
fun <Key : Any> BgmTabHorizontalPager(
    tabs: SerializeList<ComposeTextTab<Key>>,
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
    userScrollEnabled: Boolean = true,
    edgePadding: Dp = 0.dp,
    beyondViewportPageCount: Int = 0,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    key: ((index: Int) -> Any)? = { tabs[it].type },
    onTabSelected: (Int) -> Unit = {},
    pagerState: PagerState = rememberPagerState(
        initialPage = initialPage.coerceAtLeast(0),
        pageCount = { tabs.size }
    ),
    divider: @Composable () -> Unit = @Composable { BgmHorizontalDivider() },
    pageContent: @Composable (page: Int) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val currentPage = pagerState.currentPage.coerceAtMost(tabs.size - 1)
    var inputType by remember { mutableStateOf(PointerType.Touch) }

    Column(modifier = modifier.fillMaxSize()) {
        if (tabs.size > 1) {
            SecondaryScrollableTabRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "scrollable_tab" },
                selectedTabIndex = currentPage,
                edgePadding = edgePadding,
                divider = {},
                indicator = {
                    val indicatorHeight = 4.dp
                    val currentPage by remember { derivedStateOf { pagerState.currentPage } }
                    val fraction by remember { derivedStateOf { pagerState.currentPageOffsetFraction } }

                    Box(
                        Modifier
                            .tabIndicatorLayout { measurable, constraints, tabPositions ->
                                if (tabPositions.isEmpty()) {
                                    return@tabIndicatorLayout layout(0, 0) {}
                                }

                                val currentTab = tabPositions[currentPage]
                                val nextTab = tabPositions.getOrNull(currentPage + 1)
                                val preTab = tabPositions.getOrNull(currentPage - 1)

                                val targetWidth = when {
                                    fraction > 0 -> lerp(currentTab.contentWidth, requireNotNull(nextTab).contentWidth, fraction)
                                    fraction < 0 -> lerp(requireNotNull(preTab).contentWidth, currentTab.contentWidth, 1 + fraction)
                                    else -> currentTab.contentWidth
                                }

                                // === 偏移平滑过渡计算 (关键!) ===
                                val targetOffset = if (nextTab != null) {
                                    lerp(currentTab.left, nextTab.left, fraction)
                                } else if (preTab != null) {
                                    lerp(preTab.left, currentTab.left, 1 + fraction)
                                } else {
                                    currentTab.left
                                }

                                // 测量指示器 Box
                                val placeable = measurable.measure(
                                    constraints.copy(
                                        minWidth = targetWidth.roundToPx(),
                                        maxWidth = targetWidth.roundToPx(),
                                    )
                                )

                                // 布局并放置指示器
                                layout(targetWidth.roundToPx(), indicatorHeight.roundToPx()) {
                                    placeable.placeRelative(targetOffset.roundToPx(), 0)
                                }
                            }
                            .fillMaxWidth()
                            .height(indicatorHeight)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                            )
                    )
                },
                tabs = {
                    tabs.forEachIndexed { index, tab ->
                        val text = tab.displayText()

                        Tab(
                            modifier = Modifier.semantics { contentDescription = text },
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selected = currentPage == index,
                            onClick = {
                                scope.launch {
                                    // callback
                                    if (index != currentPage) {
                                        onTabSelected(index)
                                    }

                                    // Only use smooth animation when switching between two adjacent pages, otherwise select directly
                                    if (abs(currentPage - index) > 1) {
                                        pagerState.scrollToPage(index)
                                    } else {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                            },
                            text = {
                                Text(
                                    text = text,
                                    style = style.copy(fontWeight = if (currentPage != index) FontWeight.Medium else FontWeight.SemiBold),
                                )
                            }
                        )
                    }
                }
            )
        }

        if (tabs.size == 1) {
            pageContent(0)
        } else {
            divider()
            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                val pointer = event.changes.firstOrNull()
                                if (pointer != null) {
                                    inputType = pointer.type
                                }
                            }
                        }
                    },
                state = pagerState,
                userScrollEnabled = inputType == PointerType.Touch && userScrollEnabled,
                beyondViewportPageCount = beyondViewportPageCount,
                key = key,
                pageContent = { pageContent(it) }
            )
        }
    }
}


@Composable
fun <Key : Any> BgmChipHorizontalPager(
    tabs: SerializeList<ComposeTextTab<Key>>,
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
    userScrollEnabled: Boolean = true,
    beyondViewportPageCount: Int = 0,
    key: ((index: Int) -> Any)? = { tabs[it].type },
    onTabSelected: (Int) -> Unit = {},
    scope: CoroutineScope = rememberCoroutineScope(),
    listState: LazyListState = rememberLazyListState(),
    pagerState: PagerState = rememberBgmPagerState(
        onPageChange = { scope.launch { listState.animateScrollToItem(it) } },
        initialPage = initialPage.coerceAtLeast(0),
        pageCount = { tabs.size }
    ),
    extra: @Composable (ColumnScope.() -> Unit)? = null,
    pageContent: @Composable (page: Int) -> Unit,
) {
    Column(modifier = modifier) {
        if (tabs.size > 1) LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = LayoutPaddingHalf, horizontal = 12.dp),
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
        ) {
            itemsIndexed(tabs) { index, tab ->
                FilterChip(
                    selected = index == pagerState.currentPage,
                    label = { Text(text = tab.displayText()) },
                    onClick = {
                        onTabSelected(index)
                        scope.launch { pagerState.scrollToPage(index) }
                    }
                )
            }
        }

        if (extra != null) extra()

        if (tabs.size == 1) {
            pageContent(0)
        } else HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            key = key,
            state = pagerState,
            userScrollEnabled = userScrollEnabled,
            beyondViewportPageCount = beyondViewportPageCount,
            pageContent = { pageContent(it) }
        )
    }
}