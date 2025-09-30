package com.xiaoyv.bangumi.shared.ui.component.image

import android.os.Build
import androidx.annotation.IntRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.size.Scale
import kotlin.math.cos
import kotlin.math.sin

actual fun Modifier.fastBlur(radius: Dp): Modifier {
    if (radius <= 0.dp || radius == Dp.Unspecified) return this
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) this.blur(radius) else {
        val alpha = (radius.value / 32f).coerceIn(0f, 0.4f)
        drawWithContent {
            drawContent()
            val angleInDegrees = -45f
            val angleInRad = Math.toRadians(angleInDegrees.toDouble())
            val endX = cos(angleInRad).toFloat() * size.width
            val endY = sin(angleInRad).toFloat() * size.height

            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Black.copy(alpha = alpha), Color.White.copy(alpha = alpha)),
                    start = Offset.Zero,
                    end = Offset(endX, endY)
                )
            )
        }
    }
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
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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
    } else {
        val context = LocalContext.current

        AsyncImage(
            model = remember(model) {
                ImageRequest.Builder(context)
                    .data(model)
                    .scale(Scale.FIT)
                    .transformations(
                        BlurTransformation(
                            context,
                            radius = androidRadius.toFloat(),
                            sampling = androidSampling
                        )
                    )
                    .build()
            },
            contentDescription = contentDescription,
            modifier = modifier,
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
}