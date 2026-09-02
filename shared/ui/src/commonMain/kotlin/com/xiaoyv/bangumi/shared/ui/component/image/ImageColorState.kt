package com.xiaoyv.bangumi.shared.ui.component.image

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import coil3.compose.AsyncImagePainter
import com.xiaoyv.bangumi.shared.ui.platform.component.image.computeAverageLuminance

/**
 * 用于背景区域根据模糊背景图的明暗自动决定文字颜色。
 *
 * - 背景偏暗 → 文字为白色
 * - 背景偏亮 → 文字为黑色
 *
 * 使用方式：
 * ```
 * val imageColorState = rememberImageColorState()
 * BlurImage(onState = imageColorState.onImageState, ...)
 * CompositionLocalProvider(LocalContentColor provides imageColorState.contentColor) {
 *     Text(...)
 * }
 * ```
 */
@Stable
class ImageColorState(initialColor: Color = Color.White) {
    /**
     * 背景上文字应使用的颜色
     */
    var contentColor: Color by mutableStateOf(initialColor)
        private set

    /**
     * 传递给 BlurImage 的 onState 回调
     */
    val onImageState: (AsyncImagePainter.State) -> Unit = { state ->
        if (state is AsyncImagePainter.State.Success) {
            val image = state.result.image
            val luminance = computeAverageLuminance(image)
            // 亮度阈值：0.5 以下认为是深色背景，使用白色文字
            contentColor = if (luminance < 0.5f) Color.White else Color.Black
        }
    }
}

/**
 * 记住一个 ImageColorState 实例
 */
@Composable
fun rememberImageColorState(): ImageColorState {
    val inDarkTheme = isSystemInDarkTheme()
    return remember(inDarkTheme) {
        ImageColorState(if (inDarkTheme) Color.White else Color.Black)
    }
}
