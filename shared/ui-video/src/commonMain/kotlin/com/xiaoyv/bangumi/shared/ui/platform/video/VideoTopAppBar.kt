package com.xiaoyv.bangumi.shared.ui.platform.video

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 视频播放器顶部导航栏。
 *
 * @param title 视频标题。
 * @param onNavClick 返回/退出全屏按钮点击回调。
 * @param onHeightMeasured 标题栏实际渲染高度测量完成时的回调。
 * @param modifier 修饰器。
 */
@Composable
internal fun VideoTopAppBar(
    title: String,
    onNavClick: () -> Unit,
    modifier: Modifier = Modifier,
    onHeightMeasured: (Dp) -> Unit = {},
) {
    val density = LocalDensity.current

    TopAppBar(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                val height = with(density) { coordinates.size.height.toDp() }
                if (height > 0.dp) {
                    onHeightMeasured(height)
                }
            }
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.72f), Color.Transparent),
                ),
            ),
        title = { Text(title) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White,
        ),
        navigationIcon = {
            IconButton(onClick = onNavClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        },
    )
}
