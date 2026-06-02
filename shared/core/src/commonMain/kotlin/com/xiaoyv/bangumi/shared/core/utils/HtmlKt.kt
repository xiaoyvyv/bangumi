package com.xiaoyv.bangumi.shared.core.utils

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
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

suspend fun PointerInputScope.awaitHtmlEvent(
    text: AnnotatedString,
    onTextLayoutResult: () -> TextLayoutResult?,
    onClickLink: (AnnotatedString.Range<String>) -> Unit,
    onClickMask: (AnnotatedString.Range<String>) -> Unit,
    onClickImage: (AnnotatedString.Range<String>) -> Unit,
) {
    awaitPointerEventScope {
        while (true) {
            // 等待第一次按下
            val down = awaitFirstDown(requireUnconsumed = false)
            val layout = onTextLayoutResult() ?: continue
            val offset = layout.getOffsetForPosition(down.position)
            val linkAnnotation = text.getStringAnnotations(TagLink, offset, offset).firstOrNull()
            val maskAnnotation = text.getStringAnnotations(TagMask, offset, offset).firstOrNull()
            val imageAnnotation = layout.findStringAnnotation(text, TagImage, down.position)

            // 检测是否点击了链接
            if (linkAnnotation != null) {
                val up = waitForUpOrCancellation()
                if (up != null) {
                    val upOffset = layout.getOffsetForPosition(up.position)
                    val upAnnotation = text.getStringAnnotations(TagLink, upOffset, upOffset).firstOrNull()
                    if (upAnnotation?.item == linkAnnotation.item) {
                        onClickLink(linkAnnotation)
                        up.consume()
                    }
                }
            }

            // 检测是否点击了Mask
            if (maskAnnotation != null) {
                val up = waitForUpOrCancellation()
                if (up != null) {
                    val upOffset = layout.getOffsetForPosition(up.position)
                    val upAnnotation = text.getStringAnnotations(TagMask, upOffset, upOffset).firstOrNull()
                    if (upAnnotation?.item == maskAnnotation.item) {
                        onClickMask(maskAnnotation)
                        up.consume()
                    }
                }
            }


            // 检测是否点击了图片
            if (imageAnnotation != null) {
                val up = waitForUpOrCancellation()
                if (up != null) {
                    val upAnnotation = layout.findStringAnnotation(text, TagImage, up.position)
                    if (upAnnotation?.item == imageAnnotation.item) {
                        onClickImage(imageAnnotation)
                        up.consume()
                    }
                }
            }
        }
    }
}

fun String.parseAsHtml(
    linkColor: Color = Color.Blue,
    requiresHtmlDecode: Boolean = true,
): AnnotatedString{
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
                        val smileId = node.attr("smileid").orEmpty().trim().lowercase()
                        val src = node.attr("src").orEmpty().trim()
                        val alt = node.attr("alt").orEmpty()

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
                        val className = node.attr("class").orEmpty().trim().lowercase()
                        val style = node.attr("style").orEmpty()
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
                if (node is Node) node.childNodes().forEach(::traverse)
            }
        }
    }

    val html = this.preHandleHtml()
    val document = Ksoup.parse(html)
    document.body()?.childNodes()?.forEach(::traverse) ?: traverse(document)
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
 * BBCode to Html
 */
fun bbcodeToHtml(bbcode: String, handleWarp: Boolean): String {
    var html = bbcode

    if (handleWarp) {
        html = html.trim().replace("\n", "<br>")
    }

    // BBCode 表情替换
    html = html.replace(
        Regex("\\(((?:bgm\\d+)|(?:blake_\\d+)|(?:musume_\\d+))\\)", RegexOption.IGNORE_CASE)
    ) { match ->
        val rawToken = match.groupValues[1].trim()
        val token = rawToken.lowercase()
        val smileId = if (token.startsWith("bgm")) {
            val number = token.removePrefix("bgm")
            bgmEmojis.values.find { emoji -> emoji.number.toString() == number }?.smileId.orEmpty()
        } else {
            token
        }

        if (smileId.isBlank()) match.value
        else "<img src=\"\" smileid=\"$smileId\" class=\"smile\" alt=\"($rawToken)\">"
    }

    // 基础样式
    html = html.replace(Regex("\\[b]([\\s\\S]+?)\\[/b]", RegexOption.IGNORE_CASE), "<b>$1</b>")
    html = html.replace(Regex("\\[i]([\\s\\S]+?)\\[/i]", RegexOption.IGNORE_CASE), "<i>$1</i>")
    html = html.replace(Regex("\\[u]([\\s\\S]+?)\\[/u]", RegexOption.IGNORE_CASE), "<u>$1</u>")
    html = html.replace(Regex("\\[s]([\\s\\S]+?)\\[/s]", RegexOption.IGNORE_CASE), "<s>$1</s>")
    html = html.replace(Regex("\\[code]([\\s\\S]+?)\\[/code]", RegexOption.IGNORE_CASE), "<pre>$1</pre>")
    html = html.replace(Regex("\\[center]([\\s\\S]+?)\\[/center]", RegexOption.IGNORE_CASE), "<span style=\"text-align:center\">$1</span>")
    html = html.replace(Regex("\\[left]([\\s\\S]+?)\\[/left]", RegexOption.IGNORE_CASE), "<span style=\"text-align:left\">$1</span>")
    html = html.replace(Regex("\\[right]([\\s\\S]+?)\\[/right]", RegexOption.IGNORE_CASE), "<span style=\"text-align:right\">$1</span>")

    // 马赛克 (背景色+前景色一样)
    html = html.replace(
        Regex("\\[mask]([\\s\\S]+?)\\[/mask]", RegexOption.IGNORE_CASE),
        "<span class=\"text_mask\" style=\"background-color:black;color:black\">$1</span>"
    )

    // 颜色
    html = html.replace(
        Regex("\\[color=(.+?)]([\\s\\S]+?)\\[/color]", RegexOption.IGNORE_CASE),
        "<span style=\"color:$1\">$2</span>"
    )

    // 字体大小
    html = html.replace(
        Regex("\\[size=(\\d+)]([\\s\\S]+?)\\[/size]", RegexOption.IGNORE_CASE),
        "<span style=\"font-size:$1px;\">$2</span>"
    )

    // 处理 [quote]
    html = html.replace(Regex("\\[quote(?:=\\d+)?]([\\s\\S]+?)\\[/quote]", RegexOption.IGNORE_CASE)) { match ->
        val content = match.groupValues[1]
        "<span class=\"quote\" style=\"font-size:12px;font-weight:bold;color:blue\">「$content」</span>"
    }

    // 处理 [user]
    html = html.replace(Regex("\\[user(?:=\\d+)?]([\\s\\S]+?)\\[/user]", RegexOption.IGNORE_CASE)) { match ->
        val username = match.groupValues[1]
        "<span style=\"font-weight:bold;color:blue\">$username</span>"
    }

    // 链接
    // [url]xxx[/url]
    html = html.replace(Regex("\\[url]([\\s\\S]+?)\\[/url]", RegexOption.IGNORE_CASE)) { match ->
        val inner = match.groupValues[1].trim()
        if (inner.startsWith("<a", ignoreCase = true)) {
            inner
        } else {
            "<a href=\"$inner\">$inner</a>"
        }
    }
    // [url=xxx]文字[/url]
    html = html.replace(Regex("\\[url=(.+?)]([\\s\\S]+?)\\[/url]", RegexOption.IGNORE_CASE)) { match ->
        val target = match.groupValues[1].trim()
        val text = match.groupValues[2].trim()

        if (target.startsWith("<a", ignoreCase = true)) {
            // 已经是完整 <a> 标签，尝试提取 href
            val hrefRegex = Regex("href\\s*=\\s*\"([^\"]+)\"", RegexOption.IGNORE_CASE)
            val href = hrefRegex.find(target)?.groupValues?.get(1) ?: text
            "<a href=\"$href\">$text</a>"
        } else {
            // 普通 [url=http://xxx]文字[/url]
            "<a href=\"$target\">$text</a>"
        }
    }


    // 图片
    // [img]xxx[/img]
    html = html.replace(Regex("\\[img]([\\s\\S]+?)\\[/img]", RegexOption.IGNORE_CASE)) { match ->
        val inner = match.groupValues[1].trim()
        if (inner.startsWith("<a", ignoreCase = true)) {
            // 提取 href
            val hrefRegex = Regex("href\\s*=\\s*\"([^\"]+)\"", RegexOption.IGNORE_CASE)
            val href = hrefRegex.find(inner)?.groupValues?.get(1)
            if (href != null) "<img src=\"$href\">" else ""
        } else {
            // 普通情况
            "<img src=\"$inner\">"
        }
    }

    return html
}
