package com.xiaoyv.bangumi.shared.ui.component.layout

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.abs
import kotlin.math.roundToInt

private enum class BgmCollapsingSlot {
    TOP_BAR,
    COLLAPSE,
    CONTENT,
    OVERLAY
}

@Composable
fun BgmCollapsingScaffold(
    modifier: Modifier = Modifier,
    state: ScrollState = rememberScrollState(),
    windowInsets: WindowInsets = WindowInsets.navigationBars,
    collapse: @Composable BoxScope.(pinPadding: PaddingValues) -> Unit,
    topBar: @Composable (BoxScope.(Float) -> Unit)? = null,
    overlay: @Composable (() -> Unit)? = null,
    // 折叠进度 (0f: 展开, 1f: 折叠)
    content: @Composable BoxScope.(scrollProgress: Float) -> Unit,
) {
    val density = LocalDensity.current
    var minHeightPx by rememberSaveable { mutableIntStateOf(0) }
    var maxHeightPx by rememberSaveable { mutableIntStateOf(0) }

    // Header 区域当前的垂直偏移量。范围: 0 (展开) 到 -(maxHeightPx - minHeightPx) (折叠)
    val offsetLimit by remember(minHeightPx, maxHeightPx) {
        mutableIntStateOf(-(maxHeightPx - minHeightPx).coerceAtLeast(0))
    }
    var currentOffset by rememberSaveable(maxHeightPx) { mutableFloatStateOf(0f) }

    // 计算滚动进度 (0f: 展开, 1f: 折叠)
    val scrollProgress by remember(currentOffset, offsetLimit) {
        derivedStateOf {
            if (offsetLimit == 0) 0f else ((abs(currentOffset) / abs(offsetLimit) * 1000).toLong() / 1000f).coerceIn(0f, 1f)
        }
    }
    // --- 核心滚动逻辑 (Header 优先折叠，内容优先展开) ---
    val nestedScrollConnection = remember(offsetLimit) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y // 向上是负值，向下是正值

                // 1. 向上滚动 (delta < 0)：实现 Header 优先折叠
                if (delta < 0) {
                    // a. 优先让 Header 消费
                    val newOffset = (currentOffset + delta).coerceIn(offsetLimit.toFloat(), 0f)
                    val headerConsumed = newOffset - currentOffset
                    currentOffset = newOffset

                    // 返回 Header 消费的
                    return Offset(0f, headerConsumed)
                }

                // 2. 向下滚动 (delta > 0)：直接返回 0，交给内容区域不做操作
                else if (delta > 0) {
                    return Offset.Zero
                }

                return Offset.Zero
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y

                // 只有在向下滚动 (delta > 0) 且有剩余距离 (内容已滑到顶部) 时，才让 Header 展开
                if (delta > 0 && currentOffset < 0) {
                    val newOffset = (currentOffset + delta).coerceIn(offsetLimit.toFloat(), 0f)
                    val headerConsumed = newOffset - currentOffset
                    currentOffset = newOffset
                    return Offset(0f, headerConsumed)
                }

                return Offset.Zero
            }
        }
    }


    // --- 滚动逻辑结束 ---
    SubcomposeLayout(
        modifier = modifier
            .windowInsetsPadding(windowInsets)
            .nestedScroll(nestedScrollConnection) // 整个 Scaffold 处理嵌套滚动
    ) { constraints ->

        // --- 1. 测量 TopBar 以确定 minHeightPx ---
        val topBarPlaceable = subcompose(BgmCollapsingSlot.TOP_BAR) {
            if (topBar != null) Box(Modifier.fillMaxWidth()) { topBar(scrollProgress) }
        }.firstOrNull()?.measure(constraints.copy(minHeight = 0))

        minHeightPx = topBarPlaceable?.height ?: 0
        val pinPadding = with(density) { PaddingValues(top = minHeightPx.toDp()) }

        // --- 2. 测量 Collapse 区域以确定 maxHeightPx ---
        val collapsePlaceable = subcompose(BgmCollapsingSlot.COLLAPSE) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(state, flingBehavior = rememberFlingBehavior())
            ) { collapse(pinPadding) } // 传入 pinPadding
        }.firstOrNull()?.measure(constraints.copy(minHeight = 0))

        // maxHeightPx = collapse区域的测量高度 + minHeightPx
        maxHeightPx = (collapsePlaceable?.height ?: 0)

        // --- 3. 测量内容区域 ---
        val contentConstraints = constraints.copy(minHeight = 0)
        val contentPlaceable = subcompose(BgmCollapsingSlot.CONTENT) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                // 将 ScrollState 和 Progress 传入插槽
                content(scrollProgress)
            }
        }.first().measure(contentConstraints.copy(maxHeight = contentConstraints.maxHeight - minHeightPx)) // 内容区域最大视窗高度需要减去pin bar 高度

        // --- 4. 测量 Overlay ---
        val overlayPlaceable = subcompose(BgmCollapsingSlot.OVERLAY) {
            if (overlay != null) overlay()
        }.firstOrNull()?.measure(constraints)

        // --- 5. 放置 (Layout) ---
        layout(constraints.maxWidth, constraints.maxHeight) {

            // 放置内容：内容基于内容 ScrollState 的值进行偏移，同时基于 Header 的 currentOffset 偏移
            contentPlaceable.placeRelative(
                x = 0,
                // 内容的 Y 坐标 = Header最大高度 + Header当前偏移 - 内容自身的滚动距离
                y = maxHeightPx + currentOffset.roundToInt()
            )

            // 放置 Collapse 区域：它始终和容器顶部对齐 (y=0)
            collapsePlaceable?.placeRelative(
                x = 0,
                // 要求 2: Collapse 区域始终从 y=0 开始，并随滚动而偏移
                y = currentOffset.roundToInt()
            )

            // 放置 TopBar：始终固定在顶部
            topBarPlaceable?.placeRelative(x = 0, y = 0)

            // 放置 Overlay
            overlayPlaceable?.placeRelative(x = 0, y = 0)
        }
    }
}

/*
@Composable
fun BgmCollapsingScaffold(
    modifier: Modifier = Modifier,
    state: ScrollState = rememberScrollState(),
    windowInsets: WindowInsets = WindowInsets.navigationBars,
    collapse: @Composable BoxScope.(PaddingValues) -> Unit,
    topBar: @Composable (BoxScope.(Float) -> Unit)? = null,
    overlay: @Composable (BoxScope.() -> Unit)? = null,
    content: @Composable BoxScope.(Float) -> Unit,
) {
    BoxWithConstraints(modifier = modifier.windowInsetsPadding(windowInsets)) {
        var pinHeight by rememberSaveable { mutableIntStateOf(0) }
        val parentHeight = maxHeight
        val density = LocalDensity.current

        val nestedScrollConnection = remember(state) {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    // 向下（available.y > 0）：优先让子组件消费
                    if (available.y > 0) return Offset.Zero

                    // 向上（available.y < 0）：先让 外层 消费
                    val consumed = state.dispatchRawDelta(-available.y)
                    return Offset(0f, -consumed)
                }
            }
        }

        val scrollProgress by remember {
            derivedStateOf {
                if (state.maxValue > 0) state.value.toFloat() / state.maxValue else 0f
            }
        }

        Column(
            modifier = Modifier
                .matchParentSize()
                .verticalScroll(state, overscrollEffect = null)
                .nestedScroll(nestedScrollConnection)
        ) {
            val topPadding = with(density) { if (LocalInspectionMode.current) 64.dp else pinHeight.toDp() }

            Box(
                modifier = Modifier.fillMaxWidth(),
                content = { collapse(PaddingValues(top = topPadding)) }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(parentHeight - topPadding),
                content = {
                    content(scrollProgress)
                }
            )
        }

        if (topBar != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        val newHeight = it.size.height
                        if (newHeight != pinHeight) {
                            pinHeight = newHeight
                        }
                    }
            ) {
                topBar(scrollProgress)
            }
        }

        if (overlay != null) overlay()
    }
}*/
