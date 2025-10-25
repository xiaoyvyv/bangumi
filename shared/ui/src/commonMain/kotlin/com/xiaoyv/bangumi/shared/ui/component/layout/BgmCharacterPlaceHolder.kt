package com.xiaoyv.bangumi.shared.ui.component.layout

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.ic_bgm_character
import com.xiaoyv.bangumi.shared.data.parser.RobotSpeech
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun BgmCharacterPlaceHolder(
    modifier: Modifier = Modifier,
    index: Int = Random.nextInt(0, 6),
    text: String = if (LocalInspectionMode.current) "" else RobotSpeech.instance.random,
) {
    Column(
        modifier = modifier.padding(top = 80.dp, bottom = 120.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(LayoutPadding)
    ) {
        // 无限往复动画
        val infiniteTransition = rememberInfiniteTransition(label = "shake")
        val rotation by infiniteTransition.animateFloat(
            initialValue = -5f,
            targetValue = 5f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "rotation"
        )

        Image(
            modifier = Modifier
                .width(80.dp)
                .aspectRatio(32 / 60f)
                .graphicsLayer {
                    rotationZ = rotation
                    transformOrigin = TransformOrigin(0.5f, 1f)
                },
            alignment = horizontalSliceAlignment(
                parts = 7,
                index = index,
                offsetX = with(LocalDensity.current) { 3.dp.toPx() }
            ),
            painter = painterResource(Res.drawable.ic_bgm_character),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = LayoutPadding),
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}


@Composable
private fun horizontalSliceAlignment(
    parts: Int,
    index: Int,
    offsetX: Float = 0f,
    offsetY: Float = 0f,
): Alignment {
    val safeIndex = index.coerceIn(0, parts - 1)
    return remember(parts, safeIndex, offsetX, offsetY) {
        Alignment { size, space, _ ->
            val sliceWidth = size.width / parts
            val offsetX = -safeIndex * sliceWidth + offsetX.roundToInt()
            val offsetY = (space.height - size.height) / 2 + offsetY.roundToInt()
            IntOffset(offsetX, offsetY)
        }
    }
}


@Preview
@Composable
fun PreviewBgmCharacterPlaceHolder() {
    PreviewColumn(modifier = Modifier.fillMaxWidth()) {
        BgmCharacterPlaceHolder(
            modifier = Modifier.fillMaxWidth(),
            index = 2
        )
    }
}