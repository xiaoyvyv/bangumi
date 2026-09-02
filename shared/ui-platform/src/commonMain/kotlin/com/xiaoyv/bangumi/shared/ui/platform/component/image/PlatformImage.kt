package com.xiaoyv.bangumi.shared.ui.platform.component.image

import androidx.annotation.IntRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter.Companion.DefaultTransform
import coil3.compose.AsyncImagePainter.State

/**
 * 将 RGBA 字节数组转换为 Compose ImageBitmap
 *
 * @param width 图片宽度
 * @param height 图片高度
 * @param rgba 原始像素数据 (R, G, B, A, R, G, B, A...)
 */
expect fun rgbaToImageBitmap(width: Int, height: Int, rgba: ByteArray): ImageBitmap?

/**
 * Android API31及以下，使用半透明蒙层兼容实现，避免 blur 无效果更突兀问题
 */
expect fun Modifier.fastBlur(radius: Dp): Modifier

/**
 * 显示带模糊效果的图片。
 *
 * @param model 图片数据。
 * @param contentDescription 无障碍描述。
 * @param modifier 布局修饰符。
 * @param transform 图片状态转换器。
 * @param onState 图片状态回调。
 * @param alignment 图片对齐方式。
 * @param contentScale 图片缩放方式。
 * @param alpha 图片透明度。
 * @param colorFilter 图片颜色滤镜。
 * @param filterQuality 图片滤镜质量。
 * @param clipToBounds 是否裁剪超出边界的内容。
 * @param radius 模糊半径。
 * @param androidRadius 仅 Android API < 32 生效。
 * @param androidSampling 仅 Android API < 32 生效。
 */
@Composable
expect fun BlurImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    transform: (State) -> State = DefaultTransform,
    onState: ((State) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Crop,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DefaultFilterQuality,
    clipToBounds: Boolean = true,
    radius: Dp = 50.dp,
    @IntRange(0, 25) androidRadius: Int = 5,
    androidSampling: Float = 5f,
)
