package bbob.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 直接根据 BBCode 源文本渲染 Compose 内容。
 *
 * @param source 原始 BBCode 文本。
 * @param modifier 外层修饰符。
 * @param textStyle 默认文本样式。
 * @param onLinkClick 自定义链接点击回调；为空时使用系统 `UriHandler`。
 * @param imageRenderer 自定义图片渲染器。
 */
@Composable
fun BbCode(
    source: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    onLinkClick: ((String) -> Unit)? = null,
    imageRenderer: @Composable (BbCodeImageElement) -> Unit = { element ->
        BbCodeComposeDefaults.DefaultImageRenderer(element = element, textStyle = textStyle)
    },
) {
    val elements = remember(source) {
        bbcodeToElements(source = source)
    }

    BbCode(
        elements = elements,
        modifier = modifier,
        textStyle = textStyle,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        onLinkClick = onLinkClick,
        imageRenderer = imageRenderer,
    )
}

/**
 * 直接根据元素列表渲染 Compose 内容。
 *
 * @param elements 已转换好的元素列表。
 * @param modifier 外层修饰符。
 * @param textStyle 默认文本样式。
 * @param onLinkClick 自定义链接点击回调；为空时使用系统 `UriHandler`。
 * @param imageRenderer 自定义图片渲染器。
 */
@Composable
fun BbCode(
    elements: List<BbCodeElement>,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    onLinkClick: ((String) -> Unit)? = null,
    imageRenderer: @Composable (BbCodeImageElement) -> Unit = { element ->
        BbCodeComposeDefaults.DefaultImageRenderer(element = element, textStyle = textStyle)
    },
) {
    val uriHandler = LocalUriHandler.current
    val openLink: (String) -> Unit = remember(uriHandler, onLinkClick) {
        { url ->
            if (onLinkClick != null) {
                onLinkClick(url)
            } else {
                uriHandler.openUri(url)
            }
        }
    }

    RenderElements(
        elements = elements,
        modifier = modifier,
        textStyle = textStyle,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        onLinkClick = openLink,
        imageRenderer = imageRenderer,
    )
}

/**
 * 渲染一组 BBCode 元素。
 *
 * @param elements 待渲染元素列表。
 * @param modifier 外层修饰符。
 * @param textStyle 默认文本样式。
 * @param onLinkClick 链接点击回调。
 * @param imageRenderer 图片渲染器。
 */
@Composable
private fun RenderElements(
    elements: List<BbCodeElement>,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    textStyle: TextStyle,
    onLinkClick: (String) -> Unit,
    imageRenderer: @Composable (BbCodeImageElement) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment
    ) {
        elements.forEach { element ->
            RenderElement(
                element = element,
                modifier = Modifier.fillMaxWidth(),
                textStyle = textStyle,
                horizontalAlignment = horizontalAlignment,
                verticalArrangement = verticalArrangement,
                onLinkClick = onLinkClick,
                imageRenderer = imageRenderer,
            )
        }
    }
}

/**
 * 渲染单个 BBCode 元素。
 *
 * @param element 当前待渲染元素。
 * @param modifier 外层修饰符。
 * @param textStyle 默认文本样式。
 * @param onLinkClick 链接点击回调。
 * @param imageRenderer 图片渲染器。
 */
@Composable
private fun RenderElement(
    element: BbCodeElement,
    modifier: Modifier = Modifier,
    textStyle: TextStyle,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    onLinkClick: (String) -> Unit,
    imageRenderer: @Composable (BbCodeImageElement) -> Unit,
) {
    when (element) {
        is BbCodeTextElement -> {
            LinkTextOnlyConsume(
                modifier = modifier,
                style = textStyle,
                text = element.text,
                onLinkClick = onLinkClick
            )
        }

        is BbCodeQuoteElement -> {
            BbCodeComposeDefaults.DefaultQuoteContainer(modifier = modifier) {
                RenderElements(
                    elements = element.children,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = textStyle,
                    verticalArrangement = verticalArrangement,
                    horizontalAlignment = horizontalAlignment,
                    onLinkClick = onLinkClick,
                    imageRenderer = imageRenderer,
                )
            }
        }

        is BbCodeCodeBlockElement -> {
            BbCodeComposeDefaults.DefaultCodeContainer(
                text = element.text,
                modifier = modifier,
                textStyle = textStyle,
            )
        }

        is BbCodeImageElement -> {
            if (element.linkUrl != null) {
                Box(modifier = modifier.clickable { onLinkClick(element.linkUrl) }) {
                    imageRenderer(element)
                }
            } else {
                imageRenderer(element)
            }
        }

        is BbCodeListElement -> {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                element.items.forEachIndexed { index, item ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        BasicText(
                            text = if (element.ordered) "${index + 1}." else "\u2022",
                            modifier = Modifier.padding(end = 8.dp),
                            style = textStyle,
                        )
                        RenderElements(
                            elements = item,
                            modifier = Modifier.weight(1f, fill = false),
                            textStyle = textStyle,
                            verticalArrangement = verticalArrangement,
                            horizontalAlignment = horizontalAlignment,
                            onLinkClick = onLinkClick,
                            imageRenderer = imageRenderer,
                        )
                    }
                }
            }
        }

        is BbCodeAlignedElement -> {
            RenderElements(
                elements = element.children,
                modifier = modifier,
                textStyle = textStyle.merge(TextStyle(textAlign = element.alignment)),
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
                onLinkClick = onLinkClick,
                imageRenderer = imageRenderer,
            )
        }

        is BbCodeSpoilerElement -> {
            BbCodeComposeDefaults.DefaultSpoilerContainer(modifier) {
                RenderElements(
                    elements = element.children,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = textStyle,
                    verticalArrangement = verticalArrangement,
                    horizontalAlignment = horizontalAlignment,
                    onLinkClick = onLinkClick,
                    imageRenderer = imageRenderer,
                )
            }
        }

        BbCodeDividerElement -> BbCodeComposeDefaults.DefaultDivider(modifier = modifier)

        is BbCodePreElement -> {
            BbCodeComposeDefaults.DefaultCodeContainer(
                text = element.text,
                modifier = modifier,
                textStyle = BbCodeComposeDefaults.preformattedTextStyle(textStyle).merge(
                    TextStyle(textAlign = TextAlign.Start),
                ),
            )
        }

        is BbCodeLineBreakElement -> {
            val lineHeight = with(LocalDensity.current) {
                when {
                    textStyle.lineHeight != TextUnit.Unspecified -> textStyle.lineHeight.toDp()
                    textStyle.fontSize != TextUnit.Unspecified -> (textStyle.fontSize * 1.2f).toDp()
                    else -> 20.sp.toDp()
                }
            }
            Spacer(modifier = modifier.height(lineHeight * element.count))
        }
    }
}


@Composable
fun LinkTextOnlyConsume(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    onLinkClick: (String) -> Unit
) {
    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val hiddenInlineSpoilerColor = MaterialTheme.colorScheme.primary
    var revealedInlineSpoilers by remember(text) { mutableStateOf(setOf<String>()) }
    val renderedText = remember(text, revealedInlineSpoilers, hiddenInlineSpoilerColor) {
        text.withInlineSpoilerMask(
            revealed = revealedInlineSpoilers,
            hiddenBackground = hiddenInlineSpoilerColor,
        )
    }

    BasicText(
        text = renderedText,
        style = style,
        onTextLayout = { layoutResult = it },
        modifier = modifier.pointerInput(renderedText) {
            awaitEachGesture {
                val down = awaitFirstDown(
                    requireUnconsumed = false,
                    pass = PointerEventPass.Initial
                )

                val layout = layoutResult ?: return@awaitEachGesture
                val offset = layout.getOffsetForPosition(down.position)

                text.getStringAnnotations(
                    tag = BbCodeComposeDefaults.MaskAnnotationTag,
                    start = offset,
                    end = offset
                ).firstOrNull() ?: text.getStringAnnotations(
                    tag = BbCodeComposeDefaults.SpoilerAnnotationTag,
                    start = offset,
                    end = offset
                ).firstOrNull()?.let { spoilerAnnotation ->
                    down.consume()
                    val up = waitForUpOrCancellation()
                    if (up != null) {
                        up.consume()
                        revealedInlineSpoilers = revealedInlineSpoilers.toggle(spoilerAnnotation.item)
                    }
                    return@awaitEachGesture
                }

                text.getStringAnnotations(
                    tag = BbCodeComposeDefaults.UrlAnnotationTag,
                    start = offset,
                    end = offset
                ).firstOrNull()?.let { link ->
                    down.consume()

                    val up = waitForUpOrCancellation()
                    if (up != null) {
                        up.consume()
                        onLinkClick(link.item)
                    }
                }
            }
        }
    )
}


private fun Set<String>.toggle(value: String): Set<String> =
    if (value in this) this - value else this + value

private fun AnnotatedString.withInlineSpoilerMask(
    revealed: Set<String>,
    hiddenBackground: Color,
): AnnotatedString =
    buildAnnotatedString {
        append(this@withInlineSpoilerMask)

        val hiddenAnnotations =
            getStringAnnotations(BbCodeComposeDefaults.SpoilerAnnotationTag, 0, length) +
                    getStringAnnotations(BbCodeComposeDefaults.MaskAnnotationTag, 0, length)

        hiddenAnnotations
            .filterNot { it.item in revealed }
            .forEach { annotation ->
                addStyle(
                    style = SpanStyle(
                        color = Color.Transparent,
                        background = Color.Unspecified,
                    ),
                    start = annotation.start,
                    end = annotation.end,
                )
                addStyle(
                    style = SpanStyle(background = hiddenBackground),
                    start = annotation.start,
                    end = annotation.end,
                )
            }
    }