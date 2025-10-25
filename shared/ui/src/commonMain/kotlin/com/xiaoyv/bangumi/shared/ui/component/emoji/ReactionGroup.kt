package com.xiaoyv.bangumi.shared.ui.component.emoji

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.window.Popup
import com.xiaoyv.bangumi.shared.core.utils.bgmReactionKey
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReaction
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.imageResource
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random


@Composable
fun ReactionGroup(
    reactions: SerializeList<ComposeReaction>,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(LayoutPaddingHalf),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(LayoutPaddingHalf),
    itemVerticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onClick: (ComposeReaction) -> Unit = {},
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        itemVerticalAlignment = itemVerticalAlignment,
    ) {
        reactions.fastForEach { item ->
            bgmReactionKey[item.value]?.let { image ->
                var targetScale by remember { mutableStateOf(1f) }
                val scope = rememberCoroutineScope()
                val scale by animateFloatAsState(
                    targetValue = targetScale,
                    animationSpec = tween(150, easing = FastOutSlowInEasing)
                )

                // 点赞动画
                var showExplosion by remember { mutableStateOf(false) }

                Box {
                    AssistChip(
                        modifier = Modifier,
                        shape = CircleShape,
                        colors = if (item.selected) {
                            AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        } else {
                            AssistChipDefaults.assistChipColors()
                        },
                        border = if (item.selected) {
                            BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        } else {
                            AssistChipDefaults.assistChipBorder(true)
                        },
                        onClick = {
                            onClick(item)
                            scope.launch {
                                targetScale = 1.5f
                                delay(150)
                                targetScale = 1f
                            }
                            scope.launch {
                                showExplosion = true
                            }
                        },
                        leadingIcon = {
                            Image(
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .size(20.dp)
                                    .scale(scale),
                                painter = BitmapPainter(
                                    image = imageResource(image),
                                    filterQuality = FilterQuality.None
                                ),
                                contentDescription = null,
                            )

                            if (showExplosion) {
                                ParticleExplosionPopup(
                                    onFinished = { showExplosion = false }
                                )
                            }
                        },
                        label = {
                            Text(
                                text = item.total.toString(),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = if (item.selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ParticleExplosionPopup(
    duration: Int = 600,
    minDistanceDp: Dp = 30.dp,
    maxDistanceDp: Dp = 80.dp,
    minSizeDp: Dp = 4.dp,
    maxSizeDp: Dp = 8.dp,
    onFinished: () -> Unit,
) {
    Popup(alignment = Alignment.Center) {
        val density = LocalDensity.current
        val particles = remember {
            List(20) {
                Particle.random(
                    density = density,
                    minDistanceDp = minDistanceDp,
                    maxDistanceDp = maxDistanceDp,
                    minSizeDp = minSizeDp,
                    maxSizeDp = maxSizeDp
                )
            }
        }
        var progress by remember { mutableStateOf(0f) }

        // 启动动画
        LaunchedEffect(Unit) {
            animate(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = tween(duration, easing = LinearOutSlowInEasing)
            ) { value, _ ->
                progress = value
            }
            onFinished()
        }

        Canvas(modifier = Modifier.size(maxDistanceDp * 2)) { // 给内缩留空间
            translate(size.width / 2, size.height / 2) {
                val interpolation = explosionInterpolation(progress)
                particles.forEach { p ->
                    val x = p.start.x + (p.end.x - p.start.x) * interpolation
                    val y = p.start.y + (p.end.y - p.start.y) * interpolation
                    drawCircle(
                        color = p.color,
                        radius = p.size * (1 - progress), // 半径还是按原始 progress 来缩小
                        center = Offset(x, y)
                    )
                }
            }
        }

    }
}

// 定义“爆开再内缩”的插值
fun explosionInterpolation(t: Float): Float {
    return if (t < 0.8f) {
        // 前 80% 正常往外飞
        t / 0.8f
    } else {
        // 后 20% 开始内缩 (回收 30%)
        val shrinkT = (t - 0.8f) / 0.2f // [0..1]
        1f - 0.3f * shrinkT
    }
}


data class Particle(
    val start: Offset,
    val end: Offset,
    val size: Float,
    val color: Color,
) {
    companion object {
        fun random(
            density: Density,
            minDistanceDp: Dp = 50.dp,
            maxDistanceDp: Dp = 100.dp,
            minSizeDp: Dp = 4.dp,
            maxSizeDp: Dp = 8.dp,
        ): Particle {
            val minDistPx = with(density) { minDistanceDp.toPx() }
            val maxDistPx = with(density) { maxDistanceDp.toPx() }
            val minSizePx = with(density) { minSizeDp.toPx() }
            val maxSizePx = with(density) { maxSizeDp.toPx() }

            val angle = Random.nextFloat() * 2 * PI
            val distance = Random.nextFloat() * (maxDistPx - minDistPx) + minDistPx

            val end = Offset(
                (cos(angle) * distance).toFloat(),
                (sin(angle) * distance).toFloat()
            )

            val size = Random.nextFloat() * (maxSizePx - minSizePx) + minSizePx

            return Particle(
                start = Offset.Zero,
                end = end,
                size = size,
                color = Color(
                    red = Random.nextFloat(),
                    green = Random.nextFloat(),
                    blue = Random.nextFloat()
                )
            )
        }
    }
}


@Preview
@Composable
fun ReactionGroupPreview() {
    val reactions = persistentListOf(
        ComposeReaction(
            type = 0,
            mainId = 0L,
            value = "44",
            total = 0,
            emoji = "emoji"
        ), ComposeReaction(
            type = 0,
            mainId = 0L,
            value = "45",
            total = 0,
            emoji = "emoji"
        ), ComposeReaction(
            type = 0,
            mainId = 0L,
            value = "46",
            total = 0,
            emoji = "emoji"
        ), ComposeReaction(
            type = 0,
            mainId = 0L,
            value = "47",
            total = 0,
            emoji = "emoji"
        )
    )

    PreviewColumn {
        ReactionGroup(
            modifier = Modifier.padding(16.dp),
            reactions = reactions
        )
    }
}

