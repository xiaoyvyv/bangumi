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
import androidx.compose.ui.unit.Constraints
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
    state: ScrollState = rememberScrollState(), // Header 内部的滚动状态
    windowInsets: WindowInsets = WindowInsets.navigationBars,
    collapse: @Composable BoxScope.(pinPadding: PaddingValues) -> Unit,
    topBar: @Composable (BoxScope.(Float) -> Unit)? = null,
    overlay: @Composable (() -> Unit)? = null,
    content: @Composable BoxScope.(scrollProgress: Float) -> Unit,
) {
    val density = LocalDensity.current
    var minHeightPx by rememberSaveable { mutableIntStateOf(0) }
    var maxHeightPx by rememberSaveable { mutableIntStateOf(0) }

    // OffsetLimit: Header 可以向上移动的最大距离 (负值)
    val offsetLimit by remember(minHeightPx, maxHeightPx) {
        mutableIntStateOf(-(maxHeightPx - minHeightPx).coerceAtLeast(0))
    }

    // currentOffset: 当前 Header 的偏移量，范围在 [offsetLimit, 0] 之间
    var currentOffset by rememberSaveable(maxHeightPx) { mutableFloatStateOf(0f) }

    // 进度计算
    val scrollProgress by remember(currentOffset, offsetLimit) {
        derivedStateOf {
            if (offsetLimit == 0) 0f else ((abs(currentOffset) / abs(offsetLimit) * 1000).toLong() / 1000f).coerceIn(0f, 1f)
        }
    }

    // --- 1. Content 区域的滚动逻辑 (手指在内容区滑动) ---
    // 需求：
    // 上拉 (Delta < 0): 头部优先折叠 -> 头部折叠完 -> 内容滚动
    // 下拉 (Delta > 0): 内容优先滚动 -> 内容滚到顶 -> 头部展开
    val contentNestedScrollConnection = remember(offsetLimit) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                // 上拉：如果头部还没完全折叠，优先消费 Delta 来折叠头部
                if (delta < 0 && currentOffset > offsetLimit) {
                    val newOffset = (currentOffset + delta).coerceAtLeast(offsetLimit.toFloat())
                    val consumed = newOffset - currentOffset
                    currentOffset = newOffset
                    return Offset(0f, consumed)
                }
                // 下拉：返回 Zero，让内容区优先滚动 (onPostScroll 会处理剩下的)
                return Offset.Zero
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                // 下拉：如果内容区已经滚到头了 (available > 0)，且头部还没完全展开，则展开头部
                if (delta > 0 && currentOffset < 0) {
                    val newOffset = (currentOffset + delta).coerceAtMost(0f)
                    val consumedByHeader = newOffset - currentOffset
                    currentOffset = newOffset
                    return Offset(0f, consumedByHeader)
                }
                return Offset.Zero
            }
        }
    }

    // --- 2. Header (Collapse) 区域的滚动逻辑 (手指在折叠区滑动) ---
    // 需求：
    // 上拉 (Delta < 0): 优先让折叠部分展示滚到底 (内部滚动) -> 再让头部折叠
    // 下拉 (Delta > 0): 优先展开头部 -> 头部展开了 -> 继续滚动折叠部分自身 (内部滚动)
    val headerNestedScrollConnection = remember(offsetLimit) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                // 下拉：如果头部被折叠了 (currentOffset < 0)，优先消费 Delta 展开头部
                if (delta > 0 && currentOffset < 0) {
                    val newOffset = (currentOffset + delta).coerceAtMost(0f)
                    val consumed = newOffset - currentOffset
                    currentOffset = newOffset
                    return Offset(0f, consumed)
                }
                // 上拉：返回 Zero，优先让 Header 内部滚动 (ScrollState) 消费。
                // 如果内部滚到底了，ScrollState 无法消费，会进入 onPostScroll
                return Offset.Zero
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                // 上拉：Header 内部已经滚到底了 (available < 0)，此时消费剩余 Delta 来折叠整个 Header 结构
                if (delta < 0 && currentOffset > offsetLimit) {
                    val newOffset = (currentOffset + delta).coerceAtLeast(offsetLimit.toFloat())
                    val consumedByHeader = newOffset - currentOffset
                    currentOffset = newOffset
                    return Offset(0f, consumedByHeader)
                }
                return Offset.Zero
            }
        }
    }

    SubcomposeLayout(modifier = modifier.windowInsetsPadding(windowInsets)) { constraints ->

        // --- 测量 TopBar ---
        val topBarPlaceable = subcompose(BgmCollapsingSlot.TOP_BAR) {
            if (topBar != null) Box(Modifier.fillMaxWidth()) { topBar(scrollProgress) }
        }.firstOrNull()?.measure(constraints.copy(minHeight = 0))

        minHeightPx = topBarPlaceable?.height ?: 0
        val pinPadding = with(density) { PaddingValues(top = minHeightPx.toDp()) }

        val collapsePlaceable = subcompose(BgmCollapsingSlot.COLLAPSE) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .nestedScroll(headerNestedScrollConnection)
                    .verticalScroll(state)
            ) {
                collapse(pinPadding)
            }
        }.firstOrNull()?.measure(constraints.copy(minHeight = 0, maxHeight = constraints.maxHeight * 50))

        // 计算最大高度，用于确定折叠范围
        maxHeightPx = collapsePlaceable?.height ?: 0

        // --- 测量 Content 区域 ---
        // 注意：这里将 contentNestedScrollConnection 挂载到了包裹容器上
        val contentConstraints = constraints.copy(minHeight = 0)
        val contentPlaceable = subcompose(BgmCollapsingSlot.CONTENT) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .nestedScroll(contentNestedScrollConnection)
            ) {
                content(scrollProgress)
            }
        }.first().measure(contentConstraints.copy(maxHeight = contentConstraints.maxHeight - minHeightPx))

        // --- 测量 Overlay ---
        val overlayPlaceable = subcompose(BgmCollapsingSlot.OVERLAY) {
            if (overlay != null) overlay()
        }.firstOrNull()?.measure(constraints)

        // --- 布局 (Layout) ---
        layout(constraints.maxWidth, constraints.maxHeight) {
            val currentOffsetPx = currentOffset.roundToInt()

            // 1. Content: 位于 Header 底部，随 offset 移动
            // y = Header总高度 + 当前偏移 (负数)
            contentPlaceable.placeRelative(
                x = 0,
                y = maxHeightPx + currentOffsetPx
            )

            // 2. Collapse: 始终从 0 开始，随 offset 移动，实现折叠效果
            collapsePlaceable?.placeRelative(
                x = 0,
                y = currentOffsetPx
            )

            // 3. TopBar: 固定不动，或者根据需要做透明度动画等
            topBarPlaceable?.placeRelative(x = 0, y = 0)

            // 4. Overlay
            overlayPlaceable?.placeRelative(x = 0, y = 0)
        }
    }
}
