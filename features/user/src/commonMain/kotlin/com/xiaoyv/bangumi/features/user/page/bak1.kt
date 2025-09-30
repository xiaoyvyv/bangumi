package com.xiaoyv.bangumi.features.user.page


import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.features.user.business.UserEvent
import com.xiaoyv.bangumi.features.user.business.UserState
import kotlinx.coroutines.delay

@Immutable
sealed class LoadState {
    data object Loading : LoadState()
    data object Slow : LoadState()
    data object Success : LoadState()
    data object Failed : LoadState()
    data object Idle : LoadState() // 空闲状态
}

// --- 色彩定义 ---
private val tvPink = Color(0xFFF498B3)
private val tvPinkDark = Color(0xFFE4709D)
private val screenBg = Color(0xFF333333)
private val successGreen = Color(0xFF90EE90)

// --- 彩条颜色 ---
private val colorBars = listOf(
    Color(0xFFC0C0C0), Color(0xFFC0C000), Color(0xFF00C0C0),
    Color(0xFF00C000), Color(0xFFC000C0), Color(0xFFC00000), Color(0xFF0000C0)
)

@Composable
fun BangumiLoadingAnimation(
    state: LoadState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(128.dp),
        contentAlignment = Alignment.Center
    ) {
        val yOffset by animateDpAsState(
            targetValue = if (state is LoadState.Success) (-8).dp else 0.dp,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        // 1. 绘制电视机主体
        Canvas(modifier = Modifier.fillMaxSize().offset(y = yOffset)) {
            drawTvBody(this)
        }

        // 2. 绘制屏幕内容
        Box(
            modifier = Modifier
                .fillMaxSize(0.65f)
                .offset(y = yOffset),
            contentAlignment = Alignment.Center
        ) {
            val infiniteTransition = rememberInfiniteTransition()
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            val alphaLoading by animateFloatAsState(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600)
            )
            LaunchedEffect(Unit) {} // Trigger

            var currentPattern by remember { mutableStateOf(0) }
            LaunchedEffect(Unit) {
                while (true) {
                    delay(600)
                    currentPattern = (currentPattern + 1) % 3
                }
            }

            var showEmoticon by remember { mutableStateOf(false) }
            val scaleY = remember { Animatable(1.0f) }
            LaunchedEffect(Unit) {
                scaleY.animateTo(0f, tween(400, easing = FastOutSlowInEasing))
                showEmoticon = true
            }

            Crossfade(targetState = state, animationSpec = tween(300)) { currentState ->

                Canvas(modifier = Modifier.fillMaxSize()) {
                    // 背景
                    drawRect(color = screenBg)
                    // 根据状态绘制前景
                    when (currentState) {
                        LoadState.Loading -> drawLoadingScreen(alpha)
                        LoadState.Slow -> drawSlowLoadingScreen(currentPattern)
                        LoadState.Failed -> drawFailedScreen(showEmoticon, scaleY)
                        LoadState.Success -> drawSuccessScreen(alphaLoading)
                        LoadState.Idle -> { /* 只有背景 */
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}

// --- 绘制电视机主体 ---
private fun drawTvBody(scope: DrawScope) {
    val cornerRadius = scope.size.width * 0.1f
    val antennaBaseOffset = scope.size.width * 0.3f
    val antennaHeight = scope.size.height * 0.3f
    val antennaAngle = 20f // degrees

    scope.apply {
        // 主体
        drawRoundRect(
            color = tvPink,
            size = Size(width = size.width, height = size.height * 0.9f),
            topLeft = Offset(x = 0f, y = size.height * 0.1f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius)
        )
        // 天线
        withTransform({
            rotate(degrees = -antennaAngle, pivot = Offset(antennaBaseOffset, size.height * 0.1f))
        }) {
            drawLine(
                tvPinkDark,
                Offset(antennaBaseOffset, size.height * 0.1f),
                Offset(antennaBaseOffset, size.height * 0.1f - antennaHeight),
                strokeWidth = 6f
            )
            drawCircle(tvPinkDark, radius = 6f, center = Offset(antennaBaseOffset, size.height * 0.1f - antennaHeight))
        }
        withTransform({
            rotate(degrees = antennaAngle, pivot = Offset(size.width - antennaBaseOffset, size.height * 0.1f))
        }) {
            drawLine(
                tvPinkDark,
                Offset(size.width - antennaBaseOffset, size.height * 0.1f),
                Offset(size.width - antennaBaseOffset, size.height * 0.1f - antennaHeight),
                strokeWidth = 6f
            )
            drawCircle(tvPinkDark, radius = 6f, center = Offset(size.width - antennaBaseOffset, size.height * 0.1f - antennaHeight))
        }
    }
}


// --- 绘制不同状态的屏幕 ---
private fun DrawScope.drawLoadingScreen(alpha: Float) {
    val dotCount = 5
    val dotRadius = size.width * 0.05f
    size.width * 0.15f
    val totalDotWidth = (dotCount * dotRadius * 2) + ((dotCount - 1) * dotRadius)
    val startX = (size.width - totalDotWidth) / 2

    for (i in 0 until dotCount) {
        drawCircle(
            color = tvPink,
            radius = dotRadius,
            center = Offset(
                x = startX + dotRadius + i * (dotRadius * 3),
                y = size.height / 2
            ),
            alpha = alpha
        )
    }
}

private fun DrawScope.drawSlowLoadingScreen(currentPattern: Int) {
    when (currentPattern) {
        0 -> drawColorBars(this)
        1 -> drawVerticalStripes(this)
        2 -> drawCheckerboard(this)
    }
}

private fun DrawScope.drawFailedScreen(showEmoticon: Boolean, scaleY: Animatable<Float, AnimationVector1D>) {
    if (showEmoticon) {
        drawSadEmoticon(this)
    } else {
        withTransform({
            scale(1f, scaleY.value, pivot = center)
        }) {
            drawColorBars(this)
        }
    }
}

private fun DrawScope.drawSuccessScreen(alphaLoading: Float) {
    drawHappyEmoticon(this, alphaLoading)
}


// --- 绘制具体图案和表情的辅助函数 ---

private fun drawColorBars(scope: DrawScope) {
    val barWidth = scope.size.width / colorBars.size
    scope.apply {
        colorBars.forEachIndexed { index, color ->
            drawRect(
                color = color,
                topLeft = Offset(x = index * barWidth, y = 0f),
                size = Size(width = barWidth, height = size.height)
            )
        }
    }
}

private fun drawVerticalStripes(scope: DrawScope) {
    val barCount = 10
    val barWidth = scope.size.width / barCount
    scope.apply {
        for (i in 0 until barCount) {
            drawRect(
                color = if (i % 2 == 0) Color.White else Color.LightGray,
                topLeft = Offset(x = i * barWidth, y = 0f),
                size = Size(width = barWidth, height = size.height)
            )
        }
    }
}

private fun drawCheckerboard(scope: DrawScope) {
    val boxCount = 10
    val boxSize = scope.size.width / boxCount
    scope.apply {
        for (row in 0 until boxCount) {
            for (col in 0 until boxCount) {
                val color = if ((row + col) % 2 == 0) Color.White else Color.LightGray
                drawRect(
                    color = color,
                    topLeft = Offset(x = col * boxSize, y = row * boxSize),
                    size = Size(boxSize, boxSize)
                )
            }
        }
    }
}

private fun drawSadEmoticon(scope: DrawScope) {
    scope.apply {
        val path = Path()
        val strokeWidth = size.width * 0.05f
        val eyeSize = size.width * 0.15f
        val eyePadding = size.width * 0.2f
        val eyeCenterY = size.height / 2 - eyeSize * 0.2f

        // > < eyes
        path.moveTo(center.x - eyePadding, eyeCenterY - eyeSize / 2)
        path.lineTo(center.x - eyePadding + eyeSize / 2, eyeCenterY)
        path.lineTo(center.x - eyePadding, eyeCenterY + eyeSize / 2)
        path.moveTo(center.x + eyePadding, eyeCenterY - eyeSize / 2)
        path.lineTo(center.x + eyePadding - eyeSize / 2, eyeCenterY)
        path.lineTo(center.x + eyePadding, eyeCenterY + eyeSize / 2)

        // _ mouth
        val mouthY = center.y + size.height * 0.2f
        path.moveTo(center.x - eyeSize, mouthY)
        path.lineTo(center.x + eyeSize, mouthY)

        drawPath(path, color = tvPinkDark, style = Stroke(width = strokeWidth))
    }
}

private fun drawHappyEmoticon(scope: DrawScope, alpha: Float) {
    scope.apply {
        val path = Path()
        size.width * 0.05f
        val eyeSize = size.width * 0.15f
        val eyePadding = size.width * 0.2f
        val eyeCenterY = size.height / 2 - eyeSize * 0.2f

        // ^ ^ eyes
        path.moveTo(center.x - eyePadding - eyeSize / 2, eyeCenterY + eyeSize / 2)
        path.lineTo(center.x - eyePadding, eyeCenterY - eyeSize / 2)
        path.lineTo(center.x - eyePadding + eyeSize / 2, eyeCenterY + eyeSize / 2)

        path.moveTo(center.x + eyePadding - eyeSize / 2, eyeCenterY + eyeSize / 2)
        path.lineTo(center.x + eyePadding, eyeCenterY - eyeSize / 2)
        path.lineTo(center.x + eyePadding + eyeSize / 2, eyeCenterY + eyeSize / 2)

        // ▽ mouth
        val mouthY = center.y + size.height * 0.1f
        path.moveTo(center.x - eyeSize, mouthY)
        path.lineTo(center.x + eyeSize, mouthY)
        path.lineTo(center.x, mouthY + eyeSize)
        path.close()

        drawPath(path, color = successGreen, alpha = alpha)
    }
}

@Composable
fun UserTimelineScreen1(
    state: UserState,
    onUiEvent: (UserEvent.UI) -> Unit,
    onActionEvent: (UserEvent.Action) -> Unit,
) {
    var state by remember { mutableStateOf<LoadState>(LoadState.Idle) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BangumiLoadingAnimation(state)

        Spacer(Modifier.height(32.dp))

        // 控制按钮
        Column {
            Row {
                Button(onClick = { state = LoadState.Loading }) { Text("Loading") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { state = LoadState.Slow }) { Text("Slow") }
            }
            Spacer(Modifier.height(8.dp))
            Row {
                Button(onClick = { state = LoadState.Success }) { Text("Success") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { state = LoadState.Failed }) { Text("Failed") }
            }
        }
    }
}

