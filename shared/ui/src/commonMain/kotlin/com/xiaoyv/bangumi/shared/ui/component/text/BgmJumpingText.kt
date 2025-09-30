package com.xiaoyv.bangumi.shared.ui.component.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


@Composable
fun BgmJumpingText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium.copy(
        fontFamily = FontFamily.Monospace,
    ),
    textAlign: TextAlign = TextAlign.Center,
) {
    val startIndex = -1
    var current by remember { mutableIntStateOf(startIndex) }
    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    // 驱动索引切换
    LaunchedEffect(layoutResult) {
        while (isActive && layoutResult != null) {
            delay(50)
            val target = current + 1
            current = if (target >= text.length) startIndex else target
        }
    }

    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            if (current >= 0) {
                append(text.substring(0, current + 1))
            }
            if (current + 1 < text.length) {
                append("—".repeat(text.length - (current + 1)))
            }
        },
        style = style,
        textAlign = textAlign,
        onTextLayout = { layoutResult = it }
    )
}
