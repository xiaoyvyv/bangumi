package com.xiaoyv.bangumi.shared.ui.component.image

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImagePainter.Companion.DefaultTransform
import coil3.compose.AsyncImagePainter.State
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import com.xiaoyv.bangumi.shared.core.utils.KotlinThumbHash
import com.xiaoyv.bangumi.shared.core.utils.noNull
import com.xiaoyv.bangumi.shared.ui.component.layout.state.BgmProgressIndicator
import com.xiaoyv.bangumi.shared.ui.component.space.BrushVerticalTransparentToHalfBlack
import com.xiaoyv.bangumi.shared.ui.platform.component.image.rgbaToImageBitmap
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.io.encoding.Base64

@Composable
fun produceThumbHashImage(key: Any?): ImageBitmap? {
    val imageBitmap by produceState<ImageBitmap?>(key1 = key, initialValue = null) {
        value = withContext(Dispatchers.Default) {
            try {
                val text = ThumbHashGenerator.generate(key.toString())
                val hashBytes = Base64.Mime.decode(text)
                val decoded = KotlinThumbHash.thumbHashToRGBA(hashBytes)
                rgbaToImageBitmap(decoded.width, decoded.height, decoded.rgba)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
    return imageBitmap
}

@Composable
fun StateImage(
    model: Any?,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    blurLoading: Boolean = true,
    transform: (State) -> State = DefaultTransform,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Crop,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    border: BorderStroke? = null,
    filterQuality: FilterQuality = FilterQuality.High,
    clipToBounds: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer
) {
    SubcomposeAsyncImage(
        modifier = modifier
            .fillMaxSize()
            .clip(shape)
            .let { if (border != null) it.border(border, shape) else it }
            .background(containerColor),
        model = model,
        contentDescription = contentDescription,
        transform = transform,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality,
        clipToBounds = clipToBounds,
        loading = {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (blurLoading) {
                    val imageBitmap = produceThumbHashImage(model.toString())
                    if (imageBitmap != null) Image(
                        bitmap = imageBitmap,
                        contentDescription = contentDescription,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                BgmProgressIndicator(
                    modifier = Modifier
                        .size(minOf(maxWidth / 2, 40.dp))
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(3.dp),
                    strokeWidth = 2.dp
                )
            }
        },
        success = {
            SubcomposeAsyncImageContent()
        },
        error = {
            ErrorImagePlaceholder()
        }
    )
}

/**
 * 图片加载失败时展示的轻量缺图占位图。
 *
 * 图形采用通用的 Broken Image 语义，并保持透明背景，避免干扰页面中的封面布局。
 */
@Composable
private fun ErrorImagePlaceholder() {
    val color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.32f)

    Canvas(modifier = Modifier.fillMaxSize()) {
        val iconSize = minOf(size.width, size.height) * 0.34f
        val center = Offset(size.width / 2f, size.height / 2f)
        val width = iconSize
        val height = iconSize * 0.72f
        val left = center.x - width / 2f
        val top = center.y - height / 2f
        val strokeWidth = (iconSize * 0.055f).coerceAtLeast(1f)
        val cornerRadius = iconSize * 0.08f

        drawRoundRect(
            color = color,
            topLeft = Offset(left, top),
            size = Size(width, height),
            cornerRadius = CornerRadius(cornerRadius),
            style = Stroke(width = strokeWidth),
        )
        drawCircle(
            color = color,
            radius = iconSize * 0.07f,
            center = Offset(left + width * 0.72f, top + height * 0.28f),
        )
        drawLine(
            color = color,
            start = Offset(left + width * 0.16f, top + height * 0.76f),
            end = Offset(left + width * 0.4f, top + height * 0.52f),
            strokeWidth = strokeWidth,
        )
        drawLine(
            color = color,
            start = Offset(left + width * 0.4f, top + height * 0.52f),
            end = Offset(left + width * 0.62f, top + height * 0.74f),
            strokeWidth = strokeWidth,
        )
        drawLine(
            color = color,
            start = Offset(left + width * 0.62f, top + height * 0.74f),
            end = Offset(left + width * 0.78f, top + height * 0.59f),
            strokeWidth = strokeWidth,
        )
    }
}


@Composable
fun InfoImage(
    model: Any?,
    text: String? = null,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
    textPadding: Dp = ContentMarginHalf,
    textMaxLines: Int = 1,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    alignment: Alignment = Alignment.TopCenter,
    shape: Shape = MaterialTheme.shapes.small,
    contentDescription: String? = text,
    aspectRatio: Float = 32 / 45f,
    onClick: (() -> Unit)? = null,
    content: @Composable (BoxScope.() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .aspectRatio(aspectRatio)
            .clip(shape)
            .noNull(onClick) { clickable(onClick = it) }
    ) {
        StateImage(
            modifier = Modifier.fillMaxSize(),
            model = model,
            contentDescription = contentDescription,
            contentScale = contentScale,
            alignment = alignment,
            shape = shape
        )

        if (!text.isNullOrBlank()) Text(
            modifier = Modifier
                .fillMaxWidth()
                .background(BrushVerticalTransparentToHalfBlack)
                .padding(horizontal = textPadding)
                .padding(bottom = textPadding, top = textPadding * 3)
                .align(Alignment.BottomCenter),
            text = text,
            autoSize = remember(textStyle) {
                TextAutoSize.StepBased(
                    minFontSize = 6.sp,
                    maxFontSize = textStyle.fontSize,
                    stepSize = 0.5.sp,
                )
            },
            style = textStyle,
            maxLines = textMaxLines,
            overflow = TextOverflow.Ellipsis,
        )

        content?.invoke(this)
    }
}
