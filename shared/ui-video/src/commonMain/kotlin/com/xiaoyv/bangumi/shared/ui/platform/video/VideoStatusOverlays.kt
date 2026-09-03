package com.xiaoyv.bangumi.shared.ui.platform.video

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_load_error
import com.xiaoyv.bangumi.core_resource.resources.video_paused
import com.xiaoyv.bangumi.core_resource.resources.video_reload
import org.jetbrains.compose.resources.stringResource

/**
 * 居中快进/快退 10 秒悬浮操作按钮组。
 *
 * @param visible 是否可见。
 * @param onSkipBackward 点击快退 10 秒回调。
 * @param onSkipForward 点击快进 10 秒回调。
 */
@Composable
internal fun BoxScope.VideoSkipControls(
    visible: Boolean,
    onSkipBackward: () -> Unit,
    onSkipForward: () -> Unit,
) {
    AnimatedVisibility(
        modifier = Modifier.align(Alignment.Center),
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(maxWidth * 0.25f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                VideoSkipButton(
                    icon = Icons.Default.Replay10,
                    onClick = onSkipBackward,
                )
                VideoSkipButton(
                    icon = Icons.Default.Forward10,
                    onClick = onSkipForward,
                )
            }
        }
    }
}

/**
 * 快进/快退圆形图标按钮。
 */
@Composable
private fun VideoSkipButton(
    icon: ImageVector,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = Modifier
            .size(48.dp)
            .background(Color.Black.copy(alpha = 0.3f), CircleShape),
        onClick = onClick,
    ) {
        Icon(
            modifier = Modifier.size(32.dp),
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
        )
    }
}

/**
 * 视频处于暂停状态时的居中提示胶囊。
 *
 * @param visible 是否可见。
 */
@Composable
internal fun BoxScope.VideoPausedOverlay(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        modifier = Modifier.align(Alignment.Center),
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Text(
            text = stringResource(Res.string.video_paused),
            modifier = Modifier
                .background(Color.Black, RoundedCornerShape(VideoMarginHalf))
                .padding(horizontal = VideoMargin, vertical = VideoMarginHalf),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

/**
 * 视频加载/缓冲中的居中圆形进度条。
 *
 * @param visible 是否处于加载或缓冲中。
 */
@Composable
internal fun BoxScope.VideoLoadingOverlay(visible: Boolean) {
    if (visible) {
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .background(Color.Black.copy(alpha = 0.25f), CircleShape)
                .padding(4.dp)
                .size(32.dp),
            color = Color.White,
        )
    }
}

/**
 * 视频加载失败时的错误提示与重试面板。
 *
 * @param visible 是否发生播放错误。
 * @param onRetry 点击重试按钮的回调。
 */
@Composable
internal fun BoxScope.VideoErrorOverlay(
    visible: Boolean,
    onRetry: () -> Unit,
) {
    if (visible) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.72f)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(Res.string.global_load_error),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
            )
            Button(
                modifier = Modifier.padding(top = VideoMargin),
                onClick = onRetry,
            ) {
                Text(stringResource(Res.string.video_reload))
            }
        }
    }
}
