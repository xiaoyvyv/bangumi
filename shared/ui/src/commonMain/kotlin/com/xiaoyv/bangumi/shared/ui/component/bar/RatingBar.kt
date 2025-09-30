package com.xiaoyv.bangumi.shared.ui.component.bar

import androidx.annotation.FloatRange
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.rounded.StarHalf
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.ui.component.text.StarColor
import com.xiaoyv.bangumi.shared.ui.theme.BgmIcons
import com.xiaoyv.bangumi.shared.ui.theme.BgmIconsMirrored
import kotlin.math.roundToInt

private class HalfCutoutShape(private val ratio: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        return Outline.Rectangle(Rect(Offset.Zero, Size((size.width * ratio), size.height)))
    }
}

@Composable
fun RatingBar(
    @FloatRange(from = 0.0, to = 10.0) value: Double,
    modifier: Modifier = Modifier,
    activeColor: Color = Color(0xFFFFAA00),
    inactiveColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
    starSize: Dp = 24.dp,
) {
    val fiveScaleValue = value / 2

    Row(modifier = modifier) {
        for (i in 1..5) {
            val starValue = i.toDouble()
            val fillRatio = when {
                fiveScaleValue >= starValue -> 1.0f
                fiveScaleValue > starValue - 1 -> (fiveScaleValue - (starValue - 1)).toFloat()
                else -> 0.0f
            }

            Box(
                modifier = Modifier.size(starSize),
                contentAlignment = Alignment.Center
            ) {
                // 底层灰色星（未激活部分）
                Icon(
                    imageVector = BgmIcons.Star,
                    contentDescription = null,
                    tint = inactiveColor,
                    modifier = Modifier.size(starSize)
                )

                // 上层彩色心（激活部分）
                if (fillRatio > 0f) {
                    Icon(
                        imageVector = BgmIcons.Star,
                        contentDescription = null,
                        tint = activeColor,
                        modifier = Modifier.size(starSize).run {
                            if (fillRatio == 1f) this else clip(HalfCutoutShape(fillRatio.toFloat()))
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun RatingSeekBar(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    stars: Int = 5,
    starSize: Dp = 24.dp,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    Row(
        modifier = modifier
            .wrapContentWidth()
            .pointerInput(onRatingChanged) {
                detectHorizontalDragGestures { change, _ ->
                    val x = change.position.x.coerceIn(0f, size.width.toFloat())
                    val widthPerStar = size.width / stars
                    // 每个星分 2 分，从0开始
                    val newRating = ((x / widthPerStar) * 2).roundToInt().coerceIn(0, stars * 2)
                    onRatingChanged(newRating)
                }
            },
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        for (i in 1..stars) {
            val icon = when {
                i * 2 <= rating -> BgmIcons.Star
                i * 2 - 1 == rating -> BgmIconsMirrored.StarHalf
                else -> BgmIcons.StarBorder
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(starSize)
                    .clip(CircleShape)
                    .clickable { onRatingChanged(i * 2) },
                tint = StarColor
            )
        }
    }
}
