package com.xiaoyv.bangumi.shared.core.utils

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode

internal const val ElementBr = "br"
internal const val ElementP = "p"
internal const val ElementDiv = "div"
internal const val ElementSpan = "span"
internal const val ElementA = "a"
internal const val ElementB = "b"
internal const val ElementU = "u"
internal const val ElementI = "i"
internal const val ElementS = "s"
internal const val ElementImg = "img"
internal const val ElementStrong = "strong"
internal const val ElementPre = "pre"

const val TagLink = "link"
const val TagMask = "mask"
const val TagImage = "image"
const val TagCode = "code"

private fun TextLayoutResult.findStringAnnotation(
    text: AnnotatedString,
    tag: String,
    position: Offset,
): AnnotatedString.Range<String>? {
    if (text.isEmpty()) return null
    val offset = getOffsetForPosition(position)

    if (tag != TagImage) {
        return text.getStringAnnotations(tag, offset, offset).firstOrNull()
    }

    val hitByBox = text.getStringAnnotations(tag, 0, text.length)
        .firstOrNull { range ->
            val start = range.start
            if (start < 0 || start >= text.length) return@firstOrNull false
            getBoundingBox(start).contains(position)
        }
    if (hitByBox != null) return hitByBox

    return text.getStringAnnotations(tag, offset, offset).firstOrNull()
}


fun String.parseAsHtml(linkColor: Color = Color.Blue): AnnotatedString {
    if (isBlank()) return AnnotatedString("")

    val string = AnnotatedString.Builder()
    val spanStylePushedStack = mutableListOf<Boolean>()
    val paragraphStylePushedStack = mutableListOf<Boolean>()
    val maskPushedStack = mutableListOf<Boolean>()

    fun traverse(node: Node) {
        when (node) {
            is TextNode -> {
                val text = node.text()
                if (text.isNotEmpty()) string.append(text)
            }

            is Element -> {
                val name = node.tagName().trim().lowercase()
                when (name) {
                    ElementImg -> {
                        val smileId = node.attr("smileid").trim().lowercase()
                        val src = node.attr("src").trim()
                        val alt = node.attr("alt")

                        when {
                            smileId.isNotBlank() && bgmEmojis.containsKey(smileId) -> {
                                string.appendInlineContent(smileId, alt.ifBlank { smileId })
                            }

                            src.isNotBlank() -> {
                                val url = src.sanitizeImageUrl()
                                string.pushStringAnnotation(TagImage, url)
                                string.pushStyle(
                                    ParagraphStyle(
                                        lineHeightStyle = LineHeightStyle(
                                            alignment = LineHeightStyle.Alignment.Top,
                                            trim = LineHeightStyle.Trim.Both
                                        )
                                    )
                                )
                                string.appendInlineContent(TagImage, url)
                                string.pop()
                                string.pop()
                            }

                            alt.isNotBlank() -> string.append(alt)
                        }
                    }

                    ElementPre -> {
                        string.pushStringAnnotation(TagCode, TagCode)
                        string.pushStyle(
                            ParagraphStyle(
                                lineHeight = 24.sp,
                                textIndent = TextIndent(firstLine = 8.sp, restLine = 8.sp)
                            )
                        )
                        string.pushStyle(
                            SpanStyle(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.White,
                            )
                        )

                        node.childNodes().forEach(::traverse)

                        string.pop()
                        string.pop()
                        string.pop()
                    }

                    ElementDiv,
                    ElementP,
                        -> {
                        val style = node.attr("style").orEmpty()
                        val paragraphStyle = parseParagraphStyleAttributes(style)
                        if (paragraphStyle != null) {
                            string.pushStyle(paragraphStyle)
                            paragraphStylePushedStack.add(true)
                        } else {
                            paragraphStylePushedStack.add(false)
                        }

                        node.childNodes().forEach(::traverse)

                        if (paragraphStylePushedStack.removeLastOrNull() == true) {
                            string.pop()
                        }
                    }

                    ElementSpan -> {
                        val className = node.attr("class").trim().lowercase()
                        val style = node.attr("style")
                        val rawSpanStyle = parseSpanStyleAttributes(style)

                        val isMask = className == "text_mask"
                        if (isMask) {
                            string.pushStringAnnotation(TagMask, "")
                            maskPushedStack.add(true)
                        } else {
                            maskPushedStack.add(false)
                        }

                        val spanStyle = when {
                            rawSpanStyle != null -> rawSpanStyle
                            isMask -> SpanStyle(background = Color.Black, color = Color.Black)
                            className == "keyword" -> SpanStyle(
                                color = Color.Green.copy(green = 0.8f),
                                fontWeight = FontWeight.Medium
                            )

                            else -> null
                        }

                        if (spanStyle != null) {
                            string.pushStyle(spanStyle)
                            spanStylePushedStack.add(true)
                        } else {
                            spanStylePushedStack.add(false)
                        }

                        node.childNodes().forEach(::traverse)

                        if (spanStylePushedStack.removeLastOrNull() == true) {
                            string.pop()
                        }
                        if (maskPushedStack.removeLastOrNull() == true) {
                            string.pop()
                        }
                    }

                    ElementB,
                    ElementStrong,
                        -> {
                        string.pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                        node.childNodes().forEach(::traverse)
                        string.pop()
                    }

                    ElementBr -> {
                        string.appendLine()
                    }

                    ElementA -> {
                        val hrefRaw = node.attr("href").orEmpty().trim()
                        val href = hrefRaw.removePrefix("/")
                        string.pushStringAnnotation(TagLink, href)
                        string.pushStyle(
                            SpanStyle(
                                color = linkColor,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                        node.childNodes().forEach(::traverse)
                        string.pop()
                        string.pop()
                    }

                    ElementU -> {
                        string.pushStyle(
                            SpanStyle(
                                color = Color.Unspecified,
                                textDecoration = TextDecoration.Underline
                            )
                        )
                        node.childNodes().forEach(::traverse)
                        string.pop()
                    }

                    ElementI -> {
                        string.pushStyle(
                            SpanStyle(
                                color = Color.Unspecified,
                                fontStyle = FontStyle.Italic
                            )
                        )
                        node.childNodes().forEach(::traverse)
                        string.pop()
                    }

                    ElementS -> {
                        string.pushStyle(
                            SpanStyle(
                                color = Color.Unspecified,
                                textDecoration = TextDecoration.LineThrough,
                            )
                        )
                        node.childNodes().forEach(::traverse)
                        string.pop()
                    }

                    else -> node.childNodes().forEach(::traverse)
                }
            }

            else -> {
                node.childNodes().forEach(::traverse)
            }
        }
    }

    val html = this.preHandleHtml()
    val document = Ksoup.parse(html)
    document.body().childNodes().forEach(::traverse)
    return string.toAnnotatedString()
}

fun parseParagraphStyleAttributes(style: String): ParagraphStyle? {
    if (style.isBlank()) return null

    val styles = style.split(";")
        .mapNotNull { it.split(":").takeIf { kv -> kv.size == 2 } }
        .associate { it[0].trim().lowercase() to it[1].trim().lowercase() }

    var textAlign: TextAlign = TextAlign.Unspecified
    var textDirection: TextDirection = TextDirection.Unspecified
    var lineHeight: TextUnit = TextUnit.Unspecified
    var textIndent: TextIndent? = null
    var lineBreak: LineBreak = LineBreak.Unspecified
    var hyphens: Hyphens = Hyphens.Unspecified

    // text-align
    styles["text-align"]?.let {
        textAlign = when (it) {
            "left" -> TextAlign.Left
            "right" -> TextAlign.Right
            "center" -> TextAlign.Center
            "justify" -> TextAlign.Justify
            else -> TextAlign.Unspecified
        }
    }

    // direction
    styles["direction"]?.let {
        textDirection = when (it) {
            "ltr" -> TextDirection.Ltr
            "rtl" -> TextDirection.Rtl
            else -> TextDirection.Unspecified
        }
    }

    // line-height
    styles["line-height"]?.let {
        lineHeight = when {
            it == "normal" -> TextUnit.Unspecified
            it.endsWith("px") -> it.removeSuffix("px").toFloatOrNull()?.sp ?: TextUnit.Unspecified
            it.endsWith("sp") -> it.removeSuffix("sp").toFloatOrNull()?.sp ?: TextUnit.Unspecified
            it.endsWith("%") -> TextUnit.Unspecified

            else -> it.toFloatOrNull()?.sp ?: TextUnit.Unspecified
        }
    }

    // text-indent
    styles["text-indent"]?.let {
        val indent = it.removeSuffix("px").removeSuffix("sp").toFloatOrNull()
        if (indent != null) {
            textIndent = TextIndent(firstLine = indent.sp)
        }
    }

    // line-break
    styles["line-break"]?.let {
        lineBreak = when (it) {
            "normal" -> LineBreak.Simple
            "anywhere" -> LineBreak.Heading
            else -> LineBreak.Unspecified
        }
    }

    // hyphens
    styles["hyphens"]?.let {
        hyphens = when (it) {
            "auto" -> Hyphens.Auto
            "none" -> Hyphens.None
            else -> Hyphens.Unspecified
        }
    }

    // 如果所有属性都是默认值，返回 null
    if (
        textAlign == TextAlign.Unspecified &&
        textDirection == TextDirection.Unspecified &&
        lineHeight == TextUnit.Unspecified &&
        textIndent == null &&
        lineBreak == LineBreak.Unspecified &&
        hyphens == Hyphens.Unspecified
    ) return null

    return ParagraphStyle(
        textAlign = textAlign,
        textDirection = textDirection,
        lineHeight = lineHeight,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Top,
            trim = LineHeightStyle.Trim.Both
        ),
        textIndent = textIndent,
        lineBreak = lineBreak,
        hyphens = hyphens,
    )
}


fun parseSpanStyleAttributes(style: String): SpanStyle? {
    if (style.isBlank()) return null

    val styles = style.split(";")
        .mapNotNull { it.split(":").takeIf { kv -> kv.size == 2 } }
        .associate { it[0].trim().lowercase() to it[1].trim().lowercase() }

    var color: Color? = null
    var backgroundColor: Color? = null
    var fontWeight: FontWeight? = null
    var fontStyle: FontStyle? = null
    var fontSize: Float? = null
    var textDecoration: TextDecoration? = null

    styles["color"]?.let {
        color = parseHtmlHexColor(it)
    }

    styles["background-color"]?.let {
        backgroundColor = parseHtmlHexColor(it)
    }

    styles["font-weight"]?.let {
        fontWeight = when (it) {
            "bold" -> FontWeight.Bold
            "normal" -> FontWeight.Normal
            else -> it.toIntOrNull()?.let { w -> FontWeight(w.coerceIn(1, 1000)) }
        }
    }

    styles["font-style"]?.let {
        fontStyle = when (it) {
            "italic" -> FontStyle.Italic
            "normal" -> FontStyle.Normal
            else -> null
        }
    }

    styles["font-size"]?.let {
        fontSize = it.removeSuffix("px").toFloatOrNull()
    }

    styles["text-decoration"]?.let {
        textDecoration = when {
            "underline" in it -> TextDecoration.Underline
            "line-through" in it -> TextDecoration.LineThrough
            else -> null
        }
    }

    // 如果没有任何属性被设置，则返回 null，避免无效 pushStyle
    if (
        color == null &&
        backgroundColor == null &&
        fontWeight == null &&
        fontStyle == null &&
        textDecoration == null &&
        fontSize == null
    ) return null

    return SpanStyle(
        color = color ?: Color.Unspecified,
        background = backgroundColor ?: Color.Unspecified,
        fontWeight = fontWeight,
        fontStyle = fontStyle,
        fontSize = fontSize?.sp ?: TextUnit.Unspecified,
        textDecoration = textDecoration
    )
}

/**
 * 处理 Html
 */
fun String?.preHandleHtml(): String {
    return orEmpty()
        .replace("src=\"//", "src=\"https://")
        .replace("src=\"/(?!/)".toRegex(), "src=\"https://bgm.tv/")
        .replace("group/topic/350677", "group/topic/391651")
}

/**
 * 提取 HTML 中可见的纯文本。
 *
 * @param excludeQuoteBlocks 是否排除 `blockquote` 和 `.quote` 引用块。
 */
fun String.extractHtmlText(excludeQuoteBlocks: Boolean = false): String {
    val document = Ksoup.parse(preHandleHtml())
    if (excludeQuoteBlocks) {
        document.select("blockquote, .quote").remove()
    }
    return document.body().text()
}
