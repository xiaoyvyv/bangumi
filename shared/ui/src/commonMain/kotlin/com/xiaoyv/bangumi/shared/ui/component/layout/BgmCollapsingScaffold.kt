package com.xiaoyv.bangumi.shared.ui.component.layout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.abs
import kotlin.math.roundToInt

private enum class BgmCollapsingSlot {
    TOP_BAR,
    COLLAPSE,
    CONTENT,
    OVERLAY
}

@Stable
class BgmCollapsingScaffoldState(
    initialOffset: Float = 0f,
) {
    var currentOffset by mutableFloatStateOf(initialOffset)
        internal set

    var offsetLimit by mutableIntStateOf(0)
        internal set

    val progress: Float
        get() = if (offsetLimit == 0) 0f
        else (abs(currentOffset) / abs(offsetLimit).toFloat()).coerceIn(0f, 1f)

    val isCollapsed: Boolean
        get() = offsetLimit < 0 && currentOffset <= offsetLimit.toFloat()

    val isExpanded: Boolean
        get() = currentOffset >= 0f

    suspend fun collapse() {
        if (offsetLimit < 0 && currentOffset > offsetLimit.toFloat()) {
            val anim = Animatable(currentOffset)
            try {
                anim.animateTo(offsetLimit.toFloat()) {
                    currentOffset = value
                }
            } catch (_: CancellationException) {
                currentOffset = offsetLimit.toFloat()
            }
        }
    }

    suspend fun expand() {
        if (offsetLimit < 0 && currentOffset < 0f) {
            val anim = Animatable(currentOffset)
            try {
                anim.animateTo(0f) {
                    currentOffset = value
                }
            } catch (_: CancellationException) {
                currentOffset = 0f
            }
        }
    }

    fun collapseSnap() {
        if (offsetLimit < 0) {
            currentOffset = offsetLimit.toFloat()
        }
    }

    fun expandSnap() {
        currentOffset = 0f
    }

    companion object {
        val Saver: Saver<BgmCollapsingScaffoldState, *> = Saver(
            save = { it.currentOffset },
            restore = { BgmCollapsingScaffoldState(initialOffset = it) }
        )
    }
}

@Composable
fun rememberBgmCollapsingScaffoldState(): BgmCollapsingScaffoldState {
    return rememberSaveable(saver = BgmCollapsingScaffoldState.Saver) {
        BgmCollapsingScaffoldState()
    }
}

@Composable
fun BgmCollapsingScaffold(
    modifier: Modifier = Modifier,
    state: ScrollState = rememberScrollState(),
    collapsingState: BgmCollapsingScaffoldState = rememberBgmCollapsingScaffoldState(),
    windowInsets: WindowInsets = WindowInsets.navigationBars,
    collapse: @Composable BoxScope.(pinPadding: PaddingValues) -> Unit,
    topBar: @Composable (BoxScope.(progress: () -> Float) -> Unit)? = null,
    overlay: @Composable (() -> Unit)? = null,
    content: @Composable BoxScope.(progress: () -> Float) -> Unit,
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    var minHeightPx by rememberSaveable { mutableIntStateOf(0) }
    var maxHeightPx by rememberSaveable { mutableIntStateOf(0) }

    // Header 可以向上移动的最大距离 (负值)
    val calculatedOffsetLimit = -(maxHeightPx - minHeightPx).coerceAtLeast(0)
    SideEffect {
        collapsingState.offsetLimit = calculatedOffsetLimit
    }

    val offsetLimit = collapsingState.offsetLimit

    // 当 offsetLimit 发生变化时进行边界校准
    LaunchedEffect(offsetLimit) {
        collapsingState.currentOffset = collapsingState.currentOffset.coerceIn(offsetLimit.toFloat(), 0f)
    }

    // 真实物理衰减惯性滚动 (Fling Momentum)
    val decaySpec = remember(density) { splineBasedDecay<Float>(density) }

    suspend fun performDecayFling(initialVelocity: Float): Float {
        if (offsetLimit >= 0 || initialVelocity == 0f) return initialVelocity

        val anim = Animatable(collapsingState.currentOffset)
        var lastValue = collapsingState.currentOffset
        var currentVelocity = initialVelocity

        try {
            anim.animateDecay(
                initialVelocity = initialVelocity,
                animationSpec = decaySpec
            ) {
                val delta = value - lastValue
                lastValue = value
                currentVelocity = velocity

                val oldOffset = collapsingState.currentOffset
                val newOffset = (oldOffset + delta).coerceIn(offsetLimit.toFloat(), 0f)
                collapsingState.currentOffset = newOffset

                if (newOffset == offsetLimit.toFloat() || newOffset == 0f) {
                    throw CancellationException("Animation bounds reached")
                }
            }
        } catch (_: CancellationException) {
        }

        return currentVelocity
    }

    // 进度计算使用 Lambda 闭包与 derivedStateOf
    val scrollProgressLambda: () -> Float = remember(collapsingState) {
        return@remember { collapsingState.progress }
    }

    // 计算是否完全展开到最顶端
    val isAtTop by remember(collapsingState) {
        derivedStateOf { collapsingState.isExpanded }
    }

    // 统一 NestedScrollConnection 滚动手势控制器
    val nestedScrollConnection = remember(collapsingState, density) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val currentOffset = collapsingState.currentOffset
                val offsetLimit = collapsingState.offsetLimit
                // 上滑 (delta < 0)：优先折叠 Header
                if (delta < 0 && currentOffset > offsetLimit) {
                    val newOffset = (currentOffset + delta).coerceAtLeast(offsetLimit.toFloat())
                    val consumed = newOffset - currentOffset
                    collapsingState.currentOffset = newOffset
                    return Offset(0f, consumed)
                }
                return Offset.Zero
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val currentOffset = collapsingState.currentOffset
                // 下滑 (delta > 0)：子列表滚到最顶端后，剩余下滑量展开 Header
                if (delta > 0 && currentOffset < 0) {
                    val newOffset = (currentOffset + delta).coerceAtMost(0f)
                    val consumedByHeader = newOffset - currentOffset
                    collapsingState.currentOffset = newOffset
                    return Offset(0f, consumedByHeader)
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                val velocity = available.y
                val currentOffset = collapsingState.currentOffset
                val offsetLimit = collapsingState.offsetLimit
                // 上滑惯性：若 Header 未完全收起，优先惯性折叠 Header
                if (offsetLimit < 0 && velocity < 0f && currentOffset > offsetLimit) {
                    val remainingVelocity = performDecayFling(velocity)
                    return Velocity(0f, velocity - remainingVelocity)
                }
                return Velocity.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                val velocity = available.y
                val currentOffset = collapsingState.currentOffset
                val offsetLimit = collapsingState.offsetLimit
                // 下滑惯性：Content 滚动到顶后剩余的惯性，拉出 Header
                if (offsetLimit < 0 && velocity > 0f && currentOffset < 0f) {
                    val remainingVelocity = performDecayFling(velocity)
                    return Velocity(0f, velocity - remainingVelocity)
                }
                return Velocity.Zero
            }
        }
    }

    // TopBar 专用的直接拖拽 Modifier (带真实物理惯性)
    val topBarDragModifier = Modifier
        .fillMaxWidth()
        .draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState { delta ->
                val offsetLimit = collapsingState.offsetLimit
                if (offsetLimit < 0) {
                    collapsingState.currentOffset = (collapsingState.currentOffset + delta).coerceIn(offsetLimit.toFloat(), 0f)
                }
            },
            onDragStopped = { velocity ->
                val offsetLimit = collapsingState.offsetLimit
                if (offsetLimit < 0 && velocity != 0f) {
                    coroutineScope.launch {
                        performDecayFling(velocity)
                    }
                }
            }
        )

    // Content 专用的拖拽 Modifier (保证不可滚动/短内容拖拽时也可以折叠展开 Header，带真实物理惯性)
    val contentDragModifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.surface)
        .draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState { delta ->
                val offsetLimit = collapsingState.offsetLimit
                if (offsetLimit < 0) {
                    collapsingState.currentOffset = (collapsingState.currentOffset + delta).coerceIn(offsetLimit.toFloat(), 0f)
                }
            },
            onDragStopped = { velocity ->
                val offsetLimit = collapsingState.offsetLimit
                if (offsetLimit < 0 && velocity != 0f) {
                    coroutineScope.launch {
                        performDecayFling(velocity)
                    }
                }
            }
        )

    SubcomposeLayout(
        modifier = modifier
            .windowInsetsPadding(windowInsets)
            .nestedScroll(nestedScrollConnection)
    ) { constraints ->
        val topBarPlaceable = subcompose(BgmCollapsingSlot.TOP_BAR) {
            if (topBar != null) {
                Box(modifier = topBarDragModifier) {
                    topBar(scrollProgressLambda)
                }
            }
        }.firstOrNull()?.measure(constraints.copy(minHeight = 0))

        val measuredMinHeight = topBarPlaceable?.height ?: 0
        if (minHeightPx != measuredMinHeight) {
            minHeightPx = measuredMinHeight
        }
        val pinPadding = with(density) { PaddingValues(top = minHeightPx.toDp()) }

        val collapsePlaceable = subcompose(BgmCollapsingSlot.COLLAPSE) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(state)
            ) {
                collapse(pinPadding)
            }
        }.firstOrNull()?.measure(constraints.copy(minHeight = 0, maxHeight = 262142))

        val measuredMaxHeight = collapsePlaceable?.height ?: 0
        if (maxHeightPx != measuredMaxHeight) {
            maxHeightPx = measuredMaxHeight
        }

        // --- 测量 Content 区域 ---
        val contentConstraints = constraints.copy(minHeight = 0)
        val contentPlaceable = subcompose(BgmCollapsingSlot.CONTENT) {
            CompositionLocalProvider(LocalCollapsingPullRefresh provides isAtTop) {
                Box(modifier = contentDragModifier) {
                    content(scrollProgressLambda)
                }
            }
        }.first().measure(contentConstraints.copy(maxHeight = contentConstraints.maxHeight - minHeightPx))

        // --- 测量 Overlay ---
        val overlayPlaceable = subcompose(BgmCollapsingSlot.OVERLAY) {
            if (overlay != null) overlay()
        }.firstOrNull()?.measure(constraints)

        // --- 布局 (Layout) ---
        layout(constraints.maxWidth, constraints.maxHeight) {
            val currentOffsetPx = collapsingState.currentOffset.roundToInt()

            // Content: 位于 Header 底部，随 offset 移动
            contentPlaceable.placeRelative(
                x = 0,
                y = maxHeightPx + currentOffsetPx
            )

            // Collapse: 始终从 0 开始，随 offset 移动，实现折叠效果
            collapsePlaceable?.placeRelative(
                x = 0,
                y = currentOffsetPx
            )

            // TopBar: 固定不动
            topBarPlaceable?.placeRelative(x = 0, y = 0)

            // Overlay
            overlayPlaceable?.placeRelative(x = 0, y = 0)
        }
    }
}
