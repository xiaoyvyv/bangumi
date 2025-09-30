package com.xiaoyv.bangumi.shared.ui.component.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun VerticalTextAutoWrap(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    lineSpacing: Dp = 0.dp,
    columnSpacing: Dp = 4.dp,
    maxCharsPerColumn: Int = 14,
    color: Color = LocalContentColor.current,
) {
    BoxWithConstraints(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(columnSpacing)) {
            text.chunked(maxCharsPerColumn).forEach { columnText ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(lineSpacing),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    columnText.forEach { char ->
                        Text(
                            text = char.toString(),
                            style = style,
                            color = color
                        )
                    }
                }
            }
        }
    }
}
