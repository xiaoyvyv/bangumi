package com.xiaoyv.bangumi.shared.ui.component.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.subject_score_empty
import com.xiaoyv.bangumi.core_resource.resources.subject_score_format
import com.xiaoyv.bangumi.shared.core.utils.toFixed
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.kts.isMediumScreen
import com.xiaoyv.bangumi.shared.ui.kts.isSmallScreen
import org.jetbrains.compose.resources.stringResource

@Composable
fun RatingBarChart(
    rating: Map<Int, Int>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            when {
                isSmallScreen -> LayoutPadding
                isMediumScreen -> LayoutPadding * 2
                else -> LayoutPadding * 4
            }
        ),
        verticalAlignment = Alignment.Bottom
    ) {
        val maxCount = rating.values.maxOrNull() ?: 1
        val totalCount = remember(rating) { rating.values.sum() }
        val ratingMap = remember(rating) {
            rating.entries.sortedByDescending { it.key }
        }

        ratingMap.forEach { (score, count) ->
            val tooltipState = rememberTooltipState(isPersistent = false)
            val percent = if (totalCount == 0) 0.0 else count / totalCount.toDouble() * 100
            val tipText = if (percent == 0.0) stringResource(Res.string.subject_score_empty) else {
                stringResource(
                    Res.string.subject_score_format,
                    percent.toFixed(2).toString(),
                    totalCount
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                TooltipBox(
                    state = tooltipState,
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip(
                            modifier = Modifier.padding(horizontal = LayoutPadding),
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            shape = MaterialTheme.shapes.small,
                            shadowElevation = 1.dp
                        ) {
                            Text(
                                modifier = Modifier.padding(LayoutPaddingHalf),
                                text = tipText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                ) {
                    var isPressed by remember { mutableStateOf(false) }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .graphicsLayer { alpha = if (isPressed) 0.75f else 1f }
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        isPressed = true
                                        try {
                                            awaitRelease()
                                        } finally {
                                            isPressed = false
                                        }
                                    }
                                )
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(maxHeight * count / maxCount)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .align(Alignment.BottomCenter)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = score.toString(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
