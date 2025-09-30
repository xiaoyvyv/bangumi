package com.xiaoyv.bangumi.shared.ui.component.text

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.withSpanStyle

@Composable
fun HighlightedText(
    text: CharSequence,
    highlights: SerializeList<String>,
    modifier: Modifier = Modifier,
    highlightColor: Color = MaterialTheme.colorScheme.onSurface,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    val annotatedString = remember(text, highlights, highlightColor) {
        buildAnnotatedString {
            if (highlights.isEmpty()) append(text) else {
                // 限制文本长度，避免超长文本 OOM
                val safeText = if (text.length > 2000) text.take(2000) else text

                // 构建正则，匹配所有关键字
                val regex = Regex(highlights.joinToString("|") { Regex.escape(it) })

                var lastIndex = 0
                regex.findAll(safeText).forEach { match ->
                    val start = match.range.first
                    val end = match.range.last + 1

                    // 添加普通文字
                    if (start > lastIndex) {
                        append(safeText.subSequence(lastIndex, start))
                    }

                    // 添加高亮文字
                    withSpanStyle(color = highlightColor, fontWeight = FontWeight.Bold) {
                        append(safeText.subSequence(start, end))
                    }

                    lastIndex = end
                }

                // 添加剩余普通文字
                if (lastIndex < safeText.length) {
                    append(safeText.subSequence(lastIndex, safeText.length))
                }
            }
        }
    }

    Text(
        text = annotatedString,
        style = style,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        inlineContent = inlineContent,
        onTextLayout = onTextLayout,
    )
}
