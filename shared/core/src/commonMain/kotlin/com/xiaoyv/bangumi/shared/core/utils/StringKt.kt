package com.xiaoyv.bangumi.shared.core.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.PlatformSpanStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import io.ktor.http.URLBuilder
import io.ktor.http.Url

val infoEpsRegex by lazy { "(\\d+\\s*话)".toRegex() }

fun String.substringBeforeSymbol(): String {
    val regex = Regex("^[^\\p{Punct}\\s]+")
    return regex.find(this)?.value.orEmpty().ifBlank { this }
}

fun List<String>.withChinese(): List<String> {
    val regex = Regex("[\\u4e00-\\u9fff]")
    return this.filter { regex.containsMatchIn(it) }
}

fun String.substringBeforeAnyPunctuation(): String {
    val regex = Regex("\\p{P}") // 匹配任意 Unicode 标点
    val match = regex.find(this)
    return if (match != null) {
        this.substring(0, match.range.first)
    } else {
        this // 没有标点就返回原字符串
    }
}

fun String.substringAfterAnyPunctuation(): String {
    val regex = Regex("\\p{P}") // 匹配任意 Unicode 标点
    val match = regex.find(this)
    return if (match != null) {
        this.substring(match.range.last + 1)
    } else {
        this // 没有标点就返回原字符串
    }
}


fun String?.pixivOriginalUrl(): String {
    return orEmpty()
        .replace("""/c/[^/]+/""".toRegex(), "/")
        .replace("""/(img-master|custom-thumb)/""".toRegex(), "/img-original/")
        .replace("""_square\d+|_master\d+|_custom\d+""".toRegex(), "")
}

fun String?.pixivNormalUrl(): String {
    return pixivOriginalUrl()
        .replace("/img-original/", "/img-master/")
        .replace("""\.(jpg|png|gif)$""".toRegex()) {
            "_master1200.${it.groupValues.getOrNull(1) ?: "jpg"}"
        }
}

fun String?.toUrl() = if (isNullOrBlank()) URLBuilder().build() else Url(this)
fun String?.trimStr() = orEmpty().trim()

fun String.uppercaseFirstChar(): String {
    if (this.isEmpty()) return this
    return this[0].uppercaseChar() + substring(1)
}

/**
 * 适配 Html 主题
 */
@Composable
fun AnnotatedString.applyTheme(
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    linkColor: Color = MaterialTheme.colorScheme.primary,
    showMaskRanges: Collection<AnnotatedString.Range<String>> = emptySet(),
): AnnotatedString {
    val string = this@applyTheme

    return remember(string, showMaskRanges.size) {
        buildAnnotatedString {
            append(string.text)

            // SpanStyles
            string.spanStyles.forEach { span ->
                // 替换默认颜色
                val style = when (span.item.color) {
                    Color.Blue -> span.item.copy(color = linkColor)
                    Color.Unspecified -> span.item.copy(color = contentColor)
                    else -> span.item
                }

                // 判断是否为 mask 样式
                val isMask = style.color != Color.Unspecified &&
                        style.background != Color.Unspecified &&
                        style.color == style.background

                // 判断 mask 是否和任意 show range 有交集
                val intersects = showMaskRanges.any { r -> span.start < r.end && span.end > r.start }
                if (isMask && intersects) {
                    // 清掉色
                    addStyle(
                        style.copy(background = Color.Unspecified, color = Color.Unspecified),
                        span.start,
                        span.end
                    )
                } else {
                    addStyle(style, span.start, span.end)
                }
            }

            // ParagraphStyles 原样保留
            string.paragraphStyles.forEach { p ->
                addStyle(p.item, p.start, p.end)
            }

            // Annotations
            string.getStringAnnotations(0, string.length).forEach { ann ->
                val intersects = showMaskRanges.any { r -> ann.start < r.end && ann.end > r.start }
                val isMask = ann.tag == "mask"

                if (!(isMask && intersects)) {
                    addStringAnnotation(ann.tag, ann.item, ann.start, ann.end)
                }
            }
        }
    }
}

/**
 * 计算星星的数量
 */
fun Int.generateRatingStars(starMax: Int = 5, scoreMax: Int = 10): String {
    // 计算星星的数量
    val stars = (this * starMax / scoreMax.toFloat()).toInt()

    // 构建星星字符串
    val ratingStars = buildString {
        for (i in 1..starMax) {
            if (i <= stars) {
                append("★")  // 添加星星
            } else {
                append("☆")  // 添加空星
            }
        }
    }
    return ratingStars
}

fun <T> Iterable<T>.joinToString(
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((T) -> CharSequence)? = null
): String {
    return joinTo(StringBuilder(), separator, prefix, postfix, limit, truncated, transform).toString()
}

fun AnnotatedString.Builder.addUrl(
    text: String,
    style: SpanStyle? = null,
    focusedStyle: SpanStyle? = null,
    hoveredStyle: SpanStyle? = null,
    pressedStyle: SpanStyle? = null,
    listener: LinkInteractionListener?,
) {
    val start = length
    append(text)
    addLink(
        LinkAnnotation.Clickable(
            text,
            TextLinkStyles(style, focusedStyle, hoveredStyle, pressedStyle),
            listener
        ), start, start + text.length
    )
}

inline fun <R : Any> AnnotatedString.Builder.withSpanStyle(
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    fontStyle: FontStyle? = null,
    fontSynthesis: FontSynthesis? = null,
    fontFamily: FontFamily? = null,
    fontFeatureSettings: String? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    baselineShift: BaselineShift? = null,
    textGeometricTransform: TextGeometricTransform? = null,
    localeList: LocaleList? = null,
    background: Color = Color.Unspecified,
    textDecoration: TextDecoration? = null,
    shadow: Shadow? = null,
    platformStyle: PlatformSpanStyle? = null,
    drawStyle: DrawStyle? = null,
    color: Color = Color.Unspecified,
    block: AnnotatedString.Builder.() -> R,
): R = withStyle(
    style = SpanStyle(
        fontSize = fontSize,
        fontWeight = fontWeight,
        fontStyle = fontStyle,
        fontSynthesis = fontSynthesis,
        fontFamily = fontFamily,
        fontFeatureSettings = fontFeatureSettings,
        letterSpacing = letterSpacing,
        baselineShift = baselineShift,
        textGeometricTransform = textGeometricTransform,
        localeList = localeList,
        background = background,
        textDecoration = textDecoration,
        shadow = shadow,
        platformStyle = platformStyle,
        drawStyle = drawStyle,
        color = color,
    ),
    block = block
)