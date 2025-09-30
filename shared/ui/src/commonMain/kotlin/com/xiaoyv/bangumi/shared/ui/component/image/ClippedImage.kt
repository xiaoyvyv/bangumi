package com.xiaoyv.bangumi.shared.ui.component.image

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlin.math.roundToInt

@Composable
fun ClippedImage(
    source: ImageBitmap?,
    rect: Rect,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    if (source != null && rect != Rect.Zero && rect.width > 0 && rect.height > 0) {
        Canvas(modifier = modifier.clipToBounds()) {
            val srcOffset = IntOffset(rect.left.toInt(), rect.top.toInt())
            val srcSize = IntSize(rect.width.toInt(), rect.height.toInt())

            val dstSize = size // canvas size in px

            // 源区域大小（像素）
            val srcSizeFloat = Size(rect.width, rect.height)
            val scaleFactor = contentScale.computeScaleFactor(srcSizeFloat, dstSize)

            val scaledWidth = srcSizeFloat.width * scaleFactor.scaleX
            val scaledHeight = srcSizeFloat.height * scaleFactor.scaleY

            val offsetX = ((dstSize.width - scaledWidth) / 2).coerceAtLeast(0f)
            val offsetY = ((dstSize.height - scaledHeight) / 2).coerceAtLeast(0f)

            drawImage(
                image = source,
                srcOffset = srcOffset,
                srcSize = srcSize,
                dstSize = IntSize(scaledWidth.roundToInt(), scaledHeight.roundToInt()),
                dstOffset = IntOffset(offsetX.roundToInt(), offsetY.roundToInt())
            )
        }
    }
}

