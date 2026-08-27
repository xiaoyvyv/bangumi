package com.xiaoyv.bangumi.shared.ui.component.live2d

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped

/**
 * 看板娘说话对话气泡组件
 */
@Composable
fun Live2DSpeechBubble(
    state: Live2DSpeechState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // 给阴影在 AnimatedVisibility 的布局范围内预留空间，
    // 避免进入 / 退出动画过程中被边界裁剪。
    val shadowSpace = 20.dp

    AnimatedVisibility(
        modifier = modifier.offset(shadowSpace, shadowSpace),
        visible = state.isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = 220)) + slideInVertically(
            animationSpec = spring(stiffness = 380f),
            initialOffsetY = { it / 3 }
        ),
        exit = fadeOut(animationSpec = tween(durationMillis = 180)) + slideOutVertically(
            animationSpec = tween(durationMillis = 180),
            targetOffsetY = { it / 3 }
        ),
    ) {
        val bubbleBgColor = MaterialTheme.colorScheme.surfaceContainerHigh

        Box(
            modifier = Modifier
                .padding(
                    start = shadowSpace,
                    top = shadowSpace,
                    end = shadowSpace,
                    bottom = shadowSpace,
                )
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 220.dp),
                horizontalAlignment = Alignment.End
            ) {
                Surface(
                    modifier = Modifier.clickWithoutRipped {
                        state.dismiss()
                        onClick()
                    },
                    shape = MaterialTheme.shapes.medium.copy(bottomEnd = CornerSize(4.dp)),
                    color = bubbleBgColor,
                    shadowElevation = 12.dp,
                ) {
                    Text(
                        text = state.text,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }

                // 气泡指引小尾巴
                Canvas(
                    modifier = Modifier
                        .padding(end = 24.dp)
                        .size(width = 12.dp, height = 9.dp)
                ) {
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(
                            size.width * 0.2f,
                            size.height
                        )
                        close()
                    }

                    drawPath(
                        path = path,
                        color = bubbleBgColor
                    )
                }
            }
        }
    }
}