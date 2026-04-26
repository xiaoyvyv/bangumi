package com.xiaoyv.bangumi.shared.ui.component.image

import androidx.annotation.IntRange
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope.Companion.DefaultFilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter.Companion.DefaultTransform
import coil3.compose.AsyncImagePainter.State
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import com.xiaoyv.bangumi.shared.core.utils.KotlinThumbHash
import com.xiaoyv.bangumi.shared.core.utils.noNull
import com.xiaoyv.bangumi.shared.ui.component.layout.state.BgmProgressIndicator
import com.xiaoyv.bangumi.shared.ui.component.space.BrushVerticalTransparentToHalfBlack
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.io.encoding.Base64

/**
 * 将 RGBA 字节数组转换为 Compose ImageBitmap
 *
 * @param width 图片宽度
 * @param height 图片高度
 * @param rgba 原始像素数据 (R, G, B, A, R, G, B, A...)
 */
expect fun rgbaToImageBitmap(width: Int, height: Int, rgba: ByteArray): ImageBitmap?


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

/**
 * Android API31及以下，使用半透明蒙层兼容实现，避免 blur 无效果更突兀问题
 */
expect fun Modifier.fastBlur(radius: Dp): Modifier

/**
 * @androidRadius   仅 Android 端 API<32 生效，Android API>=32 及其它平台都是用 radius 控制
 * @androidSampling 仅 Android 端 API<32 生效，Android API>=32 及其它平台都是用 radius 控制
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
    filterQuality: FilterQuality = FilterQuality.High,
    clipToBounds: Boolean = true,
) {
    SubcomposeAsyncImage(
        modifier = modifier
            .clip(shape)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer),
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
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    text = if (model is String && model.isNotBlank()) "404" else "Empty",
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)
                )
            }
        }
    )
}


@Composable
fun InfoImage(
    model: Any?,
    text: String? = null,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
    textPadding: Dp = LayoutPaddingHalf,
    textMaxLines: Int = 1,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    alignment: Alignment = Alignment.TopCenter,
    shape: Shape = MaterialTheme.shapes.small,
    contentDescription: String? = text,
    aspectRatio: Float = 32 / 45f,
    onClick: (() -> Unit)? = null,
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
            style = textStyle,
            maxLines = textMaxLines,
            overflow = TextOverflow.Ellipsis,
        )
    }
}