package com.xiaoyv.bangumi.shared.ui.component.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import coil3.compose.AsyncImagePainter

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
 * Text(color = imageColorState.contentColor, ...)
 * ```
 */
@Stable
class ImageColorState {
    /**
     * 背景上文字应使用的颜色
     */
    var contentColor: Color by mutableStateOf(Color.White)
        private set

    /**
     * 背景上次要文字应使用的颜色（带透明度）
     */
    val contentColorSecondary: Color
        get() = contentColor.copy(alpha = 0.75f)

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
    return remember { ImageColorState() }
}

/**
 * 从 Coil3 Image 中计算平均亮度（0.0 ~ 1.0）
 * 使用相对亮度公式: L = 0.299*R + 0.587*G + 0.114*B
 *
 * 平台实现需要从 coil3.Image 中提取像素数据进行计算
 */
expect fun computeAverageLuminance(image: coil3.Image): Float
