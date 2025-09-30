package com.xiaoyv.bangumi.features.user.page

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform

@Composable
fun AnimatedKawaiiTV(modifier: Modifier = Modifier) {
    // 使用 infiniteTransition 来创建无限循环的动画
    val infiniteTransition = rememberInfiniteTransition(label = "KawaiiTVTransition")

    // 1. 电视主体摇晃动画 (-3 到 3 度)
    val bodyRotation by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BodyRotation"
    )

    // 2. 左天线抖动动画 (-5 到 10 度)
    val leftAntennaRotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1500
                -5f at 0
                10f at 700
                -5f at 1000
                -5f at 1500
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "LeftAntennaRotation"
    )

    // 3. 右天线抖动动画 (5 到 -10 度，与左边错开)
    val rightAntennaRotation by infiniteTransition.animateFloat(
        initialValue = 5f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1800
                5f at 0
                5f at 300 // 延迟启动
                -10f at 1000
                5f at 1300
                5f at 1800
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "RightAntennaRotation"
    )

    // 4. 嘴巴上下起伏动画
    val mouthOffsetY by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "MouthOffset"
    )

    Canvas(modifier = modifier) {
        val strokeWidth = size.width * 0.05f
        val gradientBrush = Brush.linearGradient(
            colors = listOf(Color(0xFFF857A6), Color(0xFFFF5858)),
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height)
        )

        // 以画布中心为原点进行绘制和旋转
        withTransform({
            translate(center.x, center.y)
            // 应用身体摇晃
            rotate(bodyRotation, pivot = Offset(0f, size.height * 0.45f))
        }) {

            // 绘制电视所有部分
            drawTvBody(gradientBrush, strokeWidth)
            drawAntennas(gradientBrush, strokeWidth, leftAntennaRotation, rightAntennaRotation)
            drawFace(gradientBrush, strokeWidth, mouthOffsetY)
        }
    }
}

private fun DrawScope.drawTvBody(brush: Brush, strokeWidth: Float) {
    val width = size.width
    val height = size.height

    // 主体
    val bodyWidth = width * 0.9f
    val bodyHeight = height * 0.7f
    drawRoundRect(
        brush = brush,
        topLeft = Offset(-bodyWidth / 2, -height * 0.35f),
        size = Size(bodyWidth, bodyHeight),
        cornerRadius = CornerRadius(width * 0.1f),
        style = Stroke(width = strokeWidth)
    )

    // 顶部凸起
    val topWidth = width * 0.3f
    val topHeight = height * 0.1f
    drawRoundRect(
        brush = brush,
        topLeft = Offset(-topWidth / 2, -height * 0.45f),
        size = Size(topWidth, topHeight),
        cornerRadius = CornerRadius(width * 0.05f),
        style = Stroke(width = strokeWidth)
    )

    // 屏幕下方作为对话框尖角
    val pointerPath = Path().apply {
        moveTo(-width * 0.15f, height * 0.35f)
        lineTo(0f, height * 0.5f)
        lineTo(width * 0.15f, height * 0.35f)
    }
    drawPath(path = pointerPath, brush = brush, style = Stroke(width = strokeWidth))
}

private fun DrawScope.drawAntennas(brush: Brush, strokeWidth: Float, leftRotation: Float, rightRotation: Float) {
    val width = size.width
    val height = size.height

    // 左天线
    withTransform({
        // 围绕天线底部旋转
        rotate(leftRotation, pivot = Offset(-width * 0.1f, -height * 0.45f))
    }) {
        drawLine(
            brush = brush,
            start = Offset(-width * 0.1f, -height * 0.45f),
            end = Offset(-width * 0.3f, -height * 0.65f),
            strokeWidth = strokeWidth
        )
    }

    // 右天线
    withTransform({
        rotate(rightRotation, pivot = Offset(width * 0.1f, -height * 0.45f))
    }) {
        drawLine(
            brush = brush,
            start = Offset(width * 0.1f, -height * 0.45f),
            end = Offset(width * 0.3f, -height * 0.65f),
            strokeWidth = strokeWidth
        )
    }
}

private fun DrawScope.drawFace(brush: Brush, strokeWidth: Float, mouthOffsetY: Float) {
    val width = size.width
    val eyeWidth = width * 0.15f
    val eyeHeight = width * 0.15f

    // 左眼 >
    val leftEyePath = Path().apply {
        moveTo(-width * 0.3f, -eyeHeight / 2)
        lineTo(-width * 0.3f + eyeWidth, 0f)
        lineTo(-width * 0.3f, eyeHeight / 2)
    }
    drawPath(path = leftEyePath, brush = brush, style = Stroke(width = strokeWidth))

    // 右眼 <
    val rightEyePath = Path().apply {
        moveTo(width * 0.3f, -eyeHeight / 2)
        lineTo(width * 0.3f - eyeWidth, 0f)
        lineTo(width * 0.3f, eyeHeight / 2)
    }
    drawPath(path = rightEyePath, brush = brush, style = Stroke(width = strokeWidth))

    // 嘴巴 ~
    val mouthPath = Path().apply {
        val startX = -width * 0.15f
        val endX = width * 0.15f
        val baseY = width * 0.18f + mouthOffsetY // 应用上下起伏

        moveTo(startX, baseY)
        cubicTo(
            x1 = startX + (endX - startX) * 0.33f, y1 = baseY - width * 0.08f,
            x2 = startX + (endX - startX) * 0.66f, y2 = baseY + width * 0.08f,
            x3 = endX, y3 = baseY
        )
    }
    drawPath(path = mouthPath, brush = brush, style = Stroke(width = strokeWidth))
}
