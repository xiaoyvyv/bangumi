package com.xiaoyv.bangumi.shared.ui.view.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberCombinedBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.catalog.utils.DampedDragAnimation
import com.kyant.backdrop.catalog.utils.InteractiveHighlight
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import top.yukonga.miuix.kmp.theme.LocalContentColor as MiuixLocalContentColor
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.math.abs
import kotlin.math.sign

val LocalFloatingBottomBarContentColor = staticCompositionLocalOf { Color.Unspecified }
val LocalFloatingBottomBarTabScale = staticCompositionLocalOf { { 1f } }

/**
 * 气泡当前盖住的标签下标，拖动时随气泡移动。
 */
val LocalFloatingBottomBarActiveIndex = staticCompositionLocalOf { -1 }

/**
 * 气泡是否正在被拖动。
 */
val LocalFloatingBottomBarIsDragging = staticCompositionLocalOf { false }

/**
 * 主页面液态底栏挡住内容的高度。页面里的悬浮按钮用它往上让。
 */
val LocalMainBottomBarOverlap = staticCompositionLocalOf { 0.dp }

/**
 * 滚动内容末尾的避让高度，视口仍延伸到液态底栏后方。
 */
val LocalFloatingContentBottomPadding = staticCompositionLocalOf { 0.dp }

/**
 * 合并列表原有边距与悬浮底栏避让，保留列表视口的完整高度。
 *
 * @param padding 列表原有的内容边距。
 */
@Composable
fun floatingContentPadding(padding: PaddingValues = PaddingValues()): PaddingValues {
    val direction = LocalLayoutDirection.current
    return PaddingValues(
        start = padding.calculateStartPadding(direction),
        top = padding.calculateTopPadding(),
        end = padding.calculateEndPadding(direction),
        bottom = padding.calculateBottomPadding() + LocalFloatingContentBottomPadding.current,
    )
}

/**
 * 液态底栏离窗口底部的距离，避开系统导航区域并保留悬浮间距。
 */
@Composable
fun floatingBottomBarBottomPadding(): Dp = maxOf(
    WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
    WindowInsets.captionBar.asPaddingValues().calculateBottomPadding(),
    ContentMargin,
) + ContentMarginHalf

@Immutable
class FloatingBottomBarColors(
    val containerColor: Color,
    val indicatorColor: Color,
    val contentColor: Color,
    val activeContentColor: Color,
)

object FloatingBottomBarDefaults {
    val Height: Dp = 64.dp

    @Composable
    fun colors(
        containerColor: Color = MiuixTheme.colorScheme.surfaceContainer,
        indicatorColor: Color = MiuixTheme.colorScheme.primary,
        contentColor: Color = MiuixTheme.colorScheme.onSurface,
        activeContentColor: Color = indicatorColor,
    ) = FloatingBottomBarColors(containerColor, indicatorColor, contentColor, activeContentColor)
}

enum class FloatingBottomBarMode { LiquidGlass, Blur, None }

@Composable
fun RowScope.FloatingBottomBarItem(
    onClick: () -> Unit,
    selected: Boolean = false,
    modifier: Modifier = Modifier,
    contentColor: Color = LocalFloatingBottomBarContentColor.current,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scale = LocalFloatingBottomBarTabScale.current
    Column(
        modifier
            .clip(CircleShape)
            .selectable(selected = selected, interactionSource = null, indication = null, role = Role.Tab, onClick = onClick)
            .fillMaxHeight()
            .weight(1f)
            .graphicsLayer {
                val value = scale()
                scaleX = value
                scaleY = value
            },
        verticalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CompositionLocalProvider(
            MiuixLocalContentColor provides contentColor,
            LocalFloatingBottomBarContentColor provides contentColor,
        ) { content() }
    }
}

@Composable
fun FloatingBottomBar(
    selectedIndex: () -> Int,
    onSelected: (Int) -> Unit,
    backdrop: Backdrop,
    tabsCount: Int,
    modifier: Modifier = Modifier,
    mode: FloatingBottomBarMode = FloatingBottomBarMode.LiquidGlass,
    colors: FloatingBottomBarColors = FloatingBottomBarDefaults.colors(),
    content: @Composable RowScope.() -> Unit,
) {
    val isDark = MiuixTheme.colorScheme.background.luminance() < 0.5f
    val pillShape = CircleShape
    val isLiquid = mode == FloatingBottomBarMode.LiquidGlass
    val isBlur = mode == FloatingBottomBarMode.Blur
    val containerColor = if (isLiquid) {
        if (isDark) Color(0xFF111114).copy(alpha = 0.42f) else Color.White.copy(alpha = 0.38f)
    } else {
        colors.containerColor
    }
    val tabsBackdrop = rememberLayerBackdrop()
    val combinedBackdrop = rememberCombinedBackdrop(backdrop, tabsBackdrop)
    val density = LocalDensity.current
    val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
    val scope = rememberCoroutineScope()
    var tabWidthPx by remember { mutableFloatStateOf(0f) }
    var totalWidthPx by remember { mutableFloatStateOf(0f) }
    val offsetAnimation = remember { Animatable(0f) }
    val rubberBandPx = with(density) { 4.dp.toPx() }
    val panelOffset by remember(rubberBandPx) {
        derivedStateOf {
            if (totalWidthPx == 0f) 0f else {
                val fraction = (offsetAnimation.value / totalWidthPx).fastCoerceIn(-1f, 1f)
                rubberBandPx * fraction.sign * EaseOut.transform(abs(fraction))
            }
        }
    }
    val onSelectedState = rememberUpdatedState(onSelected)
    val selectedIndexState = rememberUpdatedState(selectedIndex)
    var currentIndex by remember { mutableIntStateOf(selectedIndexState.value()) }
    val selectedIndexValue = selectedIndexState.value()
    LaunchedEffect(selectedIndexValue) {
        if (selectedIndexValue != currentIndex) currentIndex = selectedIndexValue
    }
    val dragAnimation = remember(scope, tabsCount, density, isLtr) {
        DampedDragAnimation(
            animationScope = scope,
            initialValue = selectedIndexState.value().toFloat(),
            valueRange = 0f..(tabsCount - 1).coerceAtLeast(0).toFloat(),
            visibilityThreshold = 0.001f,
            initialScale = 1f,
            pressedScale = 78f / 56f,
            onDragStarted = {},
            onDragStopped = {
                val targetIndex = targetValue.fastRoundToInt().fastCoerceIn(0, (tabsCount - 1).coerceAtLeast(0))
                currentIndex = targetIndex
                onSelectedState.value(targetIndex)
                animateToValue(targetIndex.toFloat())
                scope.launch { offsetAnimation.animateTo(0f, spring(1f, 300f, 0.5f)) }
            },
            onDrag = { _, amount ->
                if (tabWidthPx > 0) {
                    updateValue((targetValue + amount.x / tabWidthPx * if (isLtr) 1f else -1f).fastCoerceIn(0f, (tabsCount - 1).toFloat()))
                    scope.launch { offsetAnimation.snapTo(offsetAnimation.value + amount.x) }
                }
            },
        )
    }
    LaunchedEffect(dragAnimation) {
        snapshotFlow { currentIndex }.drop(1).collectLatest { dragAnimation.animateToValue(it.toFloat()) }
    }
    val activeTabIndex by remember(tabsCount, dragAnimation) {
        derivedStateOf {
            if (tabsCount <= 0) -1
            else dragAnimation.value.fastRoundToInt().fastCoerceIn(0, tabsCount - 1)
        }
    }
    val isDragging by remember(dragAnimation) {
        derivedStateOf { dragAnimation.pressProgress > 0.01f }
    }
    val highlight = remember(scope, dragAnimation, isLtr) {
        InteractiveHighlight(
            animationScope = scope,
            position = { size, _ ->
                Offset(
                    if (isLtr) (dragAnimation.value + 0.5f) * tabWidthPx + panelOffset
                    else size.width - (dragAnimation.value + 0.5f) * tabWidthPx + panelOffset,
                    size.height / 2f,
                )
            },
        )
    }
    CompositionLocalProvider(
        LocalFloatingBottomBarActiveIndex provides activeTabIndex,
        LocalFloatingBottomBarIsDragging provides isDragging,
    ) {
        Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
            CompositionLocalProvider(
                LocalFloatingBottomBarContentColor provides colors.contentColor,
                LocalFloatingBottomBarActiveIndex provides activeTabIndex,
                LocalFloatingBottomBarIsDragging provides isDragging,
            ) {
                Row(
                    Modifier
                        .selectableGroup()
                        .onGloballyPositioned {
                            totalWidthPx = it.size.width.toFloat()
                            tabWidthPx = ((totalWidthPx - with(density) { 8.dp.toPx() }) / tabsCount.coerceAtLeast(1)).coerceAtLeast(0f)
                        }
                        .graphicsLayer { translationX = panelOffset }
                        .then(
                            when {
                                isLiquid -> Modifier.drawBackdrop(
                                    backdrop = backdrop,
                                    shape = { pillShape },
                                    effects = { vibrancy(); blur(6.dp.toPx()); lens(24.dp.toPx(), 24.dp.toPx()) },
                                    highlight = { Highlight.Default.copy(alpha = 0.75f) },
                                    shadow = {
                                        Shadow(
                                            radius = 10.dp,
                                            color = Color.Black.copy(alpha = if (isDark) 0.2f else 0.1f),
                                        )
                                    },
                                    layerBlock = {
                                        val width = size.width.coerceAtLeast(1f)
                                        val scale = lerp(1f, 1f + 16.dp.toPx() / width, dragAnimation.pressProgress)
                                        scaleX = scale
                                        scaleY = scale
                                    },
                                    onDrawSurface = { drawRect(containerColor) },
                                )
                                isBlur -> Modifier.drawBackdrop(
                                    backdrop = backdrop,
                                    shape = { pillShape },
                                    effects = { blur(25.dp.toPx()) },
                                    onDrawSurface = { drawRect(containerColor.copy(alpha = 0.65f)) },
                                )
                                else -> Modifier.background(containerColor, pillShape)
                            }
                        )
                        .then(if (isLiquid) highlight.modifier else Modifier)
                        .height(FloatingBottomBarDefaults.Height)
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    content = content,
                )
            }

            if (isLiquid) {
                CompositionLocalProvider(
                    LocalFloatingBottomBarTabScale provides {
                        lerp(1f, 1.2f, dragAnimation.pressProgress)
                    },
                    LocalFloatingBottomBarContentColor provides colors.activeContentColor,
                ) {
                    Row(
                        Modifier
                            .clearAndSetSemantics {}
                            .alpha(0f)
                            .fillMaxWidth()
                            .layerBackdrop(tabsBackdrop)
                            .graphicsLayer { translationX = panelOffset }
                            .drawBackdrop(
                                backdrop = backdrop,
                                shape = { pillShape },
                                effects = {
                                    vibrancy()
                                    blur(6.dp.toPx())
                                    lens(24.dp.toPx(), 24.dp.toPx())
                                },
                                onDrawSurface = { drawRect(containerColor) },
                            )
                            .height(56.dp)
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        content = content,
                    )
                }
            }

            if (tabWidthPx > 0f) {
                val tabWidth = with(density) { tabWidthPx.toDp() }
                if (isLiquid) {
                    Box(
                        Modifier
                            .padding(horizontal = 4.dp)
                            .graphicsLayer {
                                val offset = dragAnimation.value * tabWidthPx
                                translationX = if (isLtr) offset + panelOffset else -offset + panelOffset
                            }
                            .then(highlight.gestureModifier)
                            .then(if (tabsCount > 1) dragAnimation.modifier else Modifier)
                            .drawBackdrop(
                                backdrop = combinedBackdrop,
                                shape = { pillShape },
                                effects = {
                                    val progress = dragAnimation.pressProgress
                                    lens(
                                        refractionHeight = 24.dp.toPx() * progress,
                                        refractionAmount = 24.dp.toPx() * progress,
                                        depthEffect = true,
                                    )
                                },
                                highlight = { Highlight.Default.copy(alpha = dragAnimation.pressProgress) },
                                innerShadow = {
                                    val progress = dragAnimation.pressProgress
                                    InnerShadow(
                                        radius = 8.dp * progress,
                                        alpha = progress,
                                    )
                                },
                                layerBlock = {
                                    scaleX = dragAnimation.scaleX
                                    scaleY = dragAnimation.scaleY
                                    val velocity = dragAnimation.velocity / 10f
                                    scaleX /= 1f - (velocity * 0.75f).fastCoerceIn(-0.2f, 0.2f)
                                    scaleY *= 1f - (velocity * 0.25f).fastCoerceIn(-0.2f, 0.2f)
                                },
                                onDrawSurface = {
                                    val progress = dragAnimation.pressProgress
                                    drawRect(
                                        color = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f),
                                        alpha = 1f - progress,
                                    )
                                    drawRect(Color.Black.copy(alpha = 0.03f * progress))
                                },
                            )
                            .height(56.dp)
                            .width(tabWidth),
                    )
                } else {
                    Box(
                        Modifier
                            .padding(horizontal = 4.dp)
                            .graphicsLayer {
                                val offset = dragAnimation.value * tabWidthPx
                                translationX = if (isLtr) offset + panelOffset else -offset + panelOffset
                            }
                            .then(if (tabsCount > 1) dragAnimation.modifier else Modifier)
                            .clip(pillShape)
                            .background(colors.indicatorColor.copy(alpha = 0.15f), pillShape)
                            .height(56.dp)
                            .width(tabWidth),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        CompositionLocalProvider(LocalFloatingBottomBarContentColor provides colors.activeContentColor) {
                            Row(
                                Modifier
                                    .clearAndSetSemantics {}
                                    .wrapContentWidth(Alignment.Start, unbounded = true)
                                    .requiredWidth(with(density) { (totalWidthPx - 8.dp.toPx()).toDp() })
                                    .height(56.dp)
                                    .graphicsLayer {
                                        val offset = dragAnimation.value * tabWidthPx
                                        translationX = if (isLtr) -offset else offset
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                content = content,
                            )
                        }
                    }
                }
            }
        }
    }
}
