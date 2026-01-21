package com.xiaoyv.bangumi.shared.ui.component.image

import androidx.annotation.IntRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Image
import org.jetbrains.skia.ImageInfo

actual fun rgbaToImageBitmap(width: Int, height: Int, rgba: ByteArray): ImageBitmap? {
    if (width <= 0 || height <= 0) return null

    val imageInfo = ImageInfo(
        width,
        height,
        ColorType.RGBA_8888,
        ColorAlphaType.PREMUL
    )

    // 使用 Skia 直接从字节创建 Image
    return Image.makeRaster(imageInfo, rgba, width * 4).toComposeImageBitmap()
}

actual fun Modifier.fastBlur(radius: Dp): Modifier {
    return blur(radius)
}

@Composable
actual fun BlurImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier,
    transform: (AsyncImagePainter.State) -> AsyncImagePainter.State,
    onState: ((AsyncImagePainter.State) -> Unit)?,
    alignment: Alignment,
    contentScale: ContentScale,
    alpha: Float,
    colorFilter: ColorFilter?,
    filterQuality: FilterQuality,
    clipToBounds: Boolean,
    radius: Dp,
    @IntRange(0, 25) androidRadius: Int,
    androidSampling: Float,
) {
    AsyncImage(
        model = model,
        contentDescription = contentDescription,
        modifier = modifier.then(Modifier.blur(radius)),
        transform = transform,
        onState = onState,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        clipToBounds = clipToBounds
    )
}
