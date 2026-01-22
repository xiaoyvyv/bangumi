package com.xiaoyv.bangumi.features.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.ui.component.capsule.ContinuousCapsule
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
fun SplashRoute(onNavScreen: (Screen) -> Unit) {
    SplashScreen(onNavScreen = onNavScreen)
}

@Composable
fun SplashScreen(onNavScreen: (Screen) -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = { onNavScreen(Screen.Main) }) {
            Text(text = "Home")
        }
    }
//    Navigation()

//    LaunchedEffect(Unit) {
//        onNavScreen(Screen.Main)
//    }
}

@Composable
fun Navigation() {
    Box(modifier = Modifier.fillMaxSize()) {

        val tabCount = 4
        val density = LocalDensity.current
        val scope = rememberCoroutineScope()

        var selectedIndex by remember { mutableIntStateOf(0) }

        // Root 坐标下 Tab 中心
        val tabCenters = remember { MutableList(tabCount) { 0f } }

        // BottomBar 的中心 Y 与高度
        var barCenterY by remember { mutableFloatStateOf(0f) }
        var barHeight by remember { mutableFloatStateOf(0f) }

        /** Indicator 状态 */
        val indicatorX = remember { Animatable(0f) }
        var isDragging by remember { mutableStateOf(false) }
        var isPressed by remember { mutableStateOf(false) }

        // 按下 / 点击切换时自动吸附
        LaunchedEffect(selectedIndex) {
            val target = tabCenters.getOrNull(selectedIndex) ?: return@LaunchedEffect
            indicatorX.animateTo(
                target,
                spring(dampingRatio = 0.78f, stiffness = 420f)
            )
        }

        // 上浮视觉效果（Z 轴 scale + alpha）
        val liftScale by animateFloatAsState(
            targetValue = if (isPressed || isDragging) 1.08f else 1.0f,
            animationSpec = spring(dampingRatio = 0.65f, stiffness = 600f),
            label = "liftScale"
        )

        /** ================= Bottom Bar ================= */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(24.dp)
                .align(Alignment.BottomCenter)
                .onGloballyPositioned { coords ->
                    barHeight = coords.size.height.toFloat()
                    barCenterY = coords.positionInRoot().y + barHeight / 2f
                    if (indicatorX.value == 0f && tabCenters.any { it != 0f }) {
                        scope.launch { indicatorX.snapTo(tabCenters[selectedIndex]) }
                    }
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                repeat(tabCount) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clickable { selectedIndex = index }
                            .onGloballyPositioned { coords ->
                                val pos = coords.positionInRoot()
                                tabCenters[index] = pos.x + coords.size.width / 2f
                                if (indicatorX.value == 0f && index == selectedIndex) {
                                    scope.launch { indicatorX.snapTo(tabCenters[index]) }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Tab $index")
                    }
                }
            }
        }

        /** ================= Liquid Indicator ================= */
        if (barHeight > 0f) {
            val indicatorHeightPx = barHeight
            val baseWidthPx = with(density) { 96.dp.toPx() }

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (indicatorX.value - baseWidthPx / 2f).roundToInt(),
                            y = (barCenterY - indicatorHeightPx / 2f).roundToInt()
                        )
                    }
                    .width(with(density) { baseWidthPx.toDp() })
                    .height(with(density) { indicatorHeightPx.toDp() })
                    .graphicsLayer {
                        scaleX = liftScale
                        scaleY = liftScale
                        shape = ContinuousCapsule
                        clip = true
                    }
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val down = awaitFirstDown()
                                isPressed = true
                                isDragging = true

                                val pointerId = down.id

                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change =
                                        event.changes.firstOrNull { it.id == pointerId }
                                            ?: break

                                    if (!change.pressed) break

                                    val deltaX = change.position.x - change.previousPosition.x

                                    scope.launch { indicatorX.snapTo(indicatorX.value + deltaX) }
                                    change.consume()
                                }

                                isPressed = false
                                isDragging = false

                                // 松手吸附
                                val nearestIndex =
                                    tabCenters
                                        .mapIndexed { i, x -> i to kotlin.math.abs(x - indicatorX.value) }
                                        .minBy { it.second }
                                        .first

                                scope.launch {
                                    indicatorX.animateTo(
                                        tabCenters[nearestIndex],
                                        spring(dampingRatio = 0.78f, stiffness = 420f)
                                    )
                                }

                                selectedIndex = nearestIndex
                            }
                        }
                    }
            )
        }
    }
}
