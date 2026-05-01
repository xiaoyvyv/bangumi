package bbob.compose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import bbob.parser.parse
import bbob.pluginhelper.TagNode
import bbob.pluginhelper.getUniqAttr
import bbob.types.NodeContent
import bbob.types.TagNodeObject

private data class InlineContext(
    val style: SpanStyle = SpanStyle(),
    val linkUrl: String? = null,
    val paragraphStyle: ParagraphStyle = ParagraphStyle(),
)

/**
 * 将 BBCode 源文本转换为 `bbcode-compose` 可渲染的元素列表。
 *
 * @param source 原始 BBCode 文本。
 * @return 转换后的元素列表。
 */
fun bbcodeToElements(
    source: String,
): List<BbCodeElement> {
    return treeToElements(parse(source))
}

/**
 * 将解析树节点转换为渲染元素列表。
 *
 * @param nodes 解析后节点列表。
 * @return 渲染元素列表。
 */
fun treeToElements(nodes: List<NodeContent>): List<BbCodeElement> {
    val result = mutableListOf<BbCodeElement>()
    val inlineBuffer = mutableListOf<NodeContent>()

    /** 将当前行内缓冲区按块级边界语义刷新为元素列表。 */
    fun flushInline(nextIsBlock: Boolean = false) {
        if (inlineBuffer.isEmpty()) return
        val parts = splitInlineBufferByNewline(inlineBuffer)
        parts.forEachIndexed { index, part ->
            when (part) {
                is InlineSegment.Text -> {
                    val annotated = buildInlineAnnotatedString(part.nodes)
                    if (annotated.text.isNotEmpty()) {
                        result += BbCodeTextElement(annotated)
                    }
                }

                is InlineSegment.NewLines -> {
                    val lineBreakCount = when {
                        index != parts.lastIndex -> (part.count - 1).coerceAtLeast(0)
                        nextIsBlock -> (part.count - 1).coerceAtLeast(0)
                        else -> part.count
                    }
                    if (lineBreakCount > 0) {
                        result += BbCodeLineBreakElement(lineBreakCount)
                    }
                }
            }
        }
        inlineBuffer.clear()
    }

    /** 将剩余行内缓冲区按行拆分并刷新为元素列表。 */
    fun flushInlineLines() {
        if (inlineBuffer.isEmpty()) return
        val parts = splitInlineBufferByNewline(inlineBuffer)
        parts.forEachIndexed { index, part ->
            when (part) {
                is InlineSegment.Text -> {
                    val annotated = buildInlineAnnotatedString(part.nodes)
                    if (annotated.text.isNotEmpty()) {
                        result += BbCodeTextElement(annotated)
                    }
                }

                is InlineSegment.NewLines -> {
                    if (index != parts.lastIndex && part.count > 1) {
                        result += BbCodeLineBreakElement(part.count - 1)
                    }
                }
            }
        }
        inlineBuffer.clear()
    }

    nodes.forEachIndexed { index, node ->
        if (node is TagNodeObject<*> && shouldTreatAsBlockNode(nodes, index, node)) {
            flushInline(nextIsBlock = true)
            result += blockNodeToElement(node)
        } else if (node is TagNodeObject<*>) {
            val element = inlineNodeToElement(node)
            if (element != null) {
                flushInline(nextIsBlock = false)
                result += element
            } else {
                inlineBuffer += node
            }
        } else {
            inlineBuffer += node
        }
    }

    flushInlineLines()
    return result
}

private sealed interface InlineSegment {
    data class Text(val nodes: List<NodeContent>) : InlineSegment

    data class NewLines(val count: Int) : InlineSegment
}

/**
 * 按换行符将行内缓冲区拆分为文本段与换行段。
 *
 * @param nodes 行内节点缓冲区。
 * @return 拆分后的行内片段列表。
 */
private fun splitInlineBufferByNewline(nodes: List<NodeContent>): List<InlineSegment> {
    val segments = mutableListOf<InlineSegment>()
    val textNodes = mutableListOf<NodeContent>()
    var newlineCount = 0

    /** 将暂存的文本节点刷新为文本片段。 */
    fun flushText() {
        if (textNodes.isNotEmpty()) {
            segments += InlineSegment.Text(textNodes.toList())
            textNodes.clear()
        }
    }

    /** 将累计的换行数量刷新为换行片段。 */
    fun flushNewlines() {
        if (newlineCount > 0) {
            segments += InlineSegment.NewLines(newlineCount)
            newlineCount = 0
        }
    }

    nodes.forEach { node ->
        when (node) {
            is String -> {
                val parts = node.split('\n')
                parts.forEachIndexed { index, part ->
                    if (part.isNotEmpty()) {
                        flushNewlines()
                        textNodes += part
                    }
                    if (index != parts.lastIndex) {
                        flushText()
                        newlineCount += 1
                    }
                }
            }

            else -> {
                flushNewlines()
                textNodes += node
            }
        }
    }

    flushText()
    flushNewlines()
    return segments
}

/**
 * 判断标签是否应作为块级元素处理。
 *
 * @param node 待判断标签节点。
 * @return 是否为块级元素。
 */
private fun isIntrinsicBlockNode(node: TagNodeObject<*>): Boolean =
    when (node.tag.toString().lowercase()) {
        "quote", "code", "list", "img", "hr", "pre" -> true
        else -> false
    }

private fun shouldTreatAsBlockNode(
    nodes: List<NodeContent>,
    index: Int,
    node: TagNodeObject<*>,
): Boolean {
    val tag = node.tag.toString().lowercase()
    if (isIntrinsicBlockNode(node)) return true
    if (tag !in setOf("spoiler", "mask")) return false
    if (!canRenderSpoilerInline(node.content.orEmpty())) return true
    return !hasInlineSiblingContent(nodes, index)
}

private fun canRenderSpoilerInline(nodes: List<NodeContent>): Boolean =
    nodes.all { node ->
        when (node) {
            null -> true
            is String, is Number -> true
            is TagNodeObject<*> -> {
                val tag = node.tag.toString().lowercase()
                if (tag in setOf("spoiler", "mask")) {
                    false
                } else if (isIntrinsicBlockNode(node) || inlineNodeToElement(node) != null) {
                    false
                } else {
                    canRenderSpoilerInline(node.content.orEmpty())
                }
            }

            else -> true
        }
    }

private fun hasInlineSiblingContent(nodes: List<NodeContent>, index: Int): Boolean =
    hasInlineContinuationContent(nodes, index - 1 downTo 0) || hasInlineContinuationContent(nodes, index + 1 until nodes.size)

private fun hasInlineContinuationContent(nodes: List<NodeContent>, range: IntProgression): Boolean =
    range.any { position -> nodeProvidesInlineContinuation(nodes[position]) }

private fun nodeProvidesInlineContinuation(node: NodeContent): Boolean =
    when (node) {
        null -> false
        is String -> node.any { !it.isWhitespace() }
        is Number -> true
        is TagNodeObject<*> -> tagProvidesInlineContinuation(node)
        else -> node.toString().isNotBlank()
    }

private fun tagProvidesInlineContinuation(node: TagNodeObject<*>): Boolean {
    val tag = node.tag.toString().lowercase()
    if (tag in setOf("spoiler", "mask", "center", "left", "right")) return false
    if (isIntrinsicBlockNode(node)) return false
    return node.content.orEmpty().any(::nodeProvidesInlineContinuation)
}

private fun shouldTreatAsStandaloneBlock(node: TagNodeObject<*>): Boolean =
    isIntrinsicBlockNode(node) || when (node.tag.toString().lowercase()) {
        "spoiler", "mask" -> !canRenderSpoilerInline(node.content.orEmpty())
        else -> false
    }

/**
 * 将块级标签节点转换为对应渲染元素。
 *
 * @param node 标签节点。
 * @return 对应的块级渲染元素。
 */
private fun blockNodeToElement(node: TagNodeObject<*>): BbCodeElement =
    when (node.tag.toString().lowercase()) {
        "quote" -> BbCodeQuoteElement(treeToElements(node.content ?: mutableListOf()))
        "code" -> BbCodeCodeBlockElement(normalizeBlockText(flattenText(node.content)))
        "pre" -> BbCodePreElement(normalizeBlockText(flattenText(node.content)))
        "img" -> BbCodeImageElement(normalizeTargetText(flattenText(node.content)), (node.attrs ?: emptyMap()).toMap())
        "spoiler" -> BbCodeSpoilerElement(treeToElements(node.content ?: mutableListOf()))
        "mask" -> BbCodeSpoilerElement(treeToElements(node.content ?: mutableListOf()), masked = true)
        "hr" -> BbCodeDividerElement
        "list" -> {
            val type = getUniqAttr(node.attrs)?.toString()
            BbCodeListElement(
                ordered = type != null,
                items = toListItems(node.content),
                type = type,
            )
        }

        else -> BbCodeTextElement(AnnotatedString(rawNodeToString(node)))
    }

/**
 * 将需要特殊处理的行内标签直接转换为渲染元素。
 *
 * @param node 标签节点。
 * @return 若命中内联特殊分支则返回对应元素，否则返回 `null`。
 */
private fun inlineNodeToElement(node: TagNodeObject<*>): BbCodeElement? {
    if (node.tag.toString().lowercase() != "url") return null

    val url = normalizeTargetText(getUniqAttr(node.attrs)?.toString() ?: flattenText(node.content))
    val singleChild = node.content.orEmpty().singleOrNull() as? TagNodeObject<*> ?: return null
    if (singleChild.tag.toString().lowercase() != "img") return null

    return BbCodeImageElement(
        url = normalizeTargetText(flattenText(singleChild.content)),
        attrs = (singleChild.attrs ?: emptyMap()).toMap(),
        linkUrl = url,
    )
}

/**
 * 将节点列表压平为纯文本。
 *
 * 不支持的标签会回退为其原始 BBCode 字符串。
 *
 * @param nodes 节点列表。
 * @return 拼接后的纯文本。
 */
private fun flattenText(nodes: List<NodeContent>?): String =
    nodes.orEmpty().joinToString(separator = "") { node ->
        when (node) {
            null -> ""
            is String, is Number -> node.toString()
            is TagNodeObject<*> -> rawNodeToString(node)
            else -> node.toString()
        }
    }

/**
 * 规范化代码块或预格式化文本，去除首尾包裹换行。
 *
 * @param text 原始文本。
 * @return 处理后的文本。
 */
private fun normalizeBlockText(text: String): String =
    text
        .removePrefix("\n")
        .removeSuffix("\n")

/**
 * 规范化标签目标值，例如 `img/url/email` 的目标文本。
 *
 * @param text 原始目标文本。
 * @return 去除首尾空白后的结果。
 */
private fun normalizeTargetText(text: String): String = text.trim()

/**
 * 过滤仅用于源码排版的空白换行节点。
 *
 * @param nodes 原始节点列表。
 * @return 去除布局噪音后的节点列表。
 */
private fun normalizeInlineLayoutNodes(nodes: List<NodeContent>?): List<NodeContent> =
    nodes.orEmpty().filterNot { node ->
        node is String && node.isBlank() && node.contains('\n')
    }

/**
 * 将标签节点回退为原始 BBCode 文本。
 *
 * @param node 标签节点。
 * @return 节点对应的原始 BBCode 字符串。
 */
private fun rawNodeToString(node: TagNodeObject<*>): String =
    if (node is TagNode<*>) {
        node.toString()
    } else {
        val attrs = node.attrs ?: emptyMap<String, Any?>()
        val attrsString = attrs.entries.joinToString(separator = " ", prefix = if (attrs.isNotEmpty()) " " else "") { (key, value) ->
            if (value == true) key else "$key=\"$value\""
        }
        val content = flattenText(node.content)
        if (node.content == null) "[${node.tag}$attrsString]"
        else "[${node.tag}$attrsString]$content[/${node.tag}]"
    }

/**
 * 将 `list` 标签内容拆分为列表项元素集合。
 *
 * @param content `list` 标签内部原始节点。
 * @return 每个列表项对应的渲染元素列表。
 */
private fun toListItems(content: List<NodeContent>?): List<List<BbCodeElement>> {
    if (content.isNullOrEmpty()) return emptyList()

    val items = mutableListOf<MutableList<NodeContent>>()

    /** 新建一个列表项并按需写入首个节点。 */
    fun newItem(initial: NodeContent = null) {
        val item = mutableListOf<NodeContent>()
        if (initial != null) {
            item += initial
        }
        items += item
    }

    for (node in content) {
        if (items.isEmpty() && isIgnorableListBoundaryNode(node)) {
            continue
        }

        if (node is String && node.startsWith("*")) {
            newItem(node.drop(1))
            continue
        }

        if (node is TagNodeObject<*> && node.tag.toString() == "*") {
            newItem()
            continue
        }

        if (items.isEmpty()) {
            newItem(node)
        } else {
            items.last() += node
        }
    }

    return items
        .map { treeToElements(it) }
        .filterNot(::isEmptyElementGroup)
}

/**
 * 判断列表边界节点是否应被忽略。
 *
 * @param node 待判断节点。
 * @return 是否仅为空白边界节点。
 */
private fun isIgnorableListBoundaryNode(node: NodeContent): Boolean =
    when (node) {
        null -> true
        is String -> node.isBlank()
        else -> false
    }

/**
 * 判断元素组是否等价于空内容。
 *
 * @param elements 目标元素组。
 * @return 是否为空。
 */
private fun isEmptyElementGroup(elements: List<BbCodeElement>): Boolean =
    elements.isEmpty() || elements.all { element ->
        when (element) {
            is BbCodeLineBreakElement -> true
            is BbCodeTextElement -> element.text.text.isBlank()
            else -> false
        }
    }

/**
 * 构建行内富文本对象。
 *
 * @param nodes 行内节点列表。
 * @return Compose `AnnotatedString`。
 */
private fun buildInlineAnnotatedString(nodes: List<NodeContent>): AnnotatedString = buildAnnotatedString {
    appendInlineChildren(nodes, InlineContext())
}

/**
 * 追加单个行内节点。
 *
 * @param node 目标节点。
 * @param context 当前样式上下文。
 */
private fun AnnotatedString.Builder.appendInlineNode(node: NodeContent, context: InlineContext) {
    when (node) {
        null -> Unit
        is String, is Number -> appendStyledText(node.toString(), context)
        is TagNodeObject<*> -> appendTagNode(node, context)
        else -> appendStyledText(node.toString(), context)
    }
}

/**
 * 追加一组行内子节点。
 *
 * @param nodes 子节点列表。
 * @param context 当前样式上下文。
 */
private fun AnnotatedString.Builder.appendInlineChildren(nodes: List<NodeContent>, context: InlineContext) {
    val textBuffer = StringBuilder()

    /** 将累计的纯文本缓冲区一次性写入富文本构建器。 */
    fun flushTextBuffer() {
        if (textBuffer.isNotEmpty()) {
            appendStyledText(textBuffer.toString(), context)
            textBuffer.clear()
        }
    }

    nodes.forEach { node ->
        when (node) {
            null -> Unit
            is String, is Number -> textBuffer.append(node.toString())
            is TagNodeObject<*> -> {
                flushTextBuffer()
                appendTagNode(node, context)
            }

            else -> textBuffer.append(node.toString())
        }
    }

    flushTextBuffer()
}

/**
 * 以当前样式上下文追加文本，并写入样式与链接注解。
 *
 * @param text 待追加文本。
 * @param context 当前样式上下文。
 */
private fun AnnotatedString.Builder.appendStyledText(text: String, context: InlineContext) {
    val start = length
    append(text)
    val end = length
    if (start == end) return
    val style = if (text.isBlank()) {
        context.style.copy(textDecoration = null)
    } else {
        context.style
    }
    addStyle(style, start, end)
    context.linkUrl?.let { addStringAnnotation(BbCodeComposeDefaults.UrlAnnotationTag, it, start, end) }
}

/**
 * 追加单个标签节点，并根据标签语义转换样式或回退文本。
 *
 * @param node 标签节点。
 * @param context 当前样式上下文。
 */
private fun AnnotatedString.Builder.appendTagNode(node: TagNodeObject<*>, context: InlineContext) {
    val tag = node.tag.toString().lowercase()
    val nextContext = when (tag) {
        "b" -> context.copy(style = context.style.merge(SpanStyle(fontWeight = FontWeight.Bold)))
        "i" -> context.copy(style = context.style.merge(SpanStyle(fontStyle = FontStyle.Italic)))
        "u" -> context.copy(
            style = context.style.merge(
                SpanStyle(
                    textDecoration = mergeDecoration(
                        context.style.textDecoration,
                        TextDecoration.Underline
                    )
                )
            )
        )

        "s" -> context.copy(
            style = context.style.merge(
                SpanStyle(
                    textDecoration = mergeDecoration(
                        context.style.textDecoration,
                        TextDecoration.LineThrough
                    )
                )
            )
        )

        "color" -> {
            val color = parseColor(getUniqAttr(node.attrs)?.toString())
            context.copy(style = context.style.merge(SpanStyle(color = color ?: Color.Unspecified)))
        }

        "size" -> {
            val size = parseFontSize(getUniqAttr(node.attrs)?.toString())
            context.copy(style = context.style.merge(SpanStyle(fontSize = size)))
        }

        "font" -> {
            val fontFamily = parseFontFamily(getUniqAttr(node.attrs)?.toString())
            context.copy(style = context.style.merge(SpanStyle(fontFamily = fontFamily)))
        }

        "style" -> context.copy(style = context.style.merge(spanStyleFromStyleTag(node.attrs)))
        "code" -> context.copy(style = context.style.merge(SpanStyle(fontFamily = FontFamily.Monospace)))
        "url" -> {
            val url = normalizeTargetText(getUniqAttr(node.attrs)?.toString() ?: flattenText(node.content))
            context.copy(
                style = context.style.merge(
                    SpanStyle(
                        color = BbCodeComposeDefaults.LinkColor,
                        textDecoration = mergeDecoration(context.style.textDecoration, TextDecoration.Underline),
                    ),
                ),
                linkUrl = url,
            )
        }

        "email" -> {
            val address = normalizeTargetText(getUniqAttr(node.attrs)?.toString() ?: flattenText(node.content))
            val mailto = if (address.startsWith("mailto:", ignoreCase = true)) address else "mailto:$address"
            context.copy(
                style = context.style.merge(
                    SpanStyle(
                        color = BbCodeComposeDefaults.LinkColor,
                        textDecoration = mergeDecoration(context.style.textDecoration, TextDecoration.Underline),
                    ),
                ),
                linkUrl = mailto,
            )
        }

        "center" -> context.copy(paragraphStyle = context.paragraphStyle.merge(ParagraphStyle(textAlign = TextAlign.Center)))
        "left" -> context.copy(paragraphStyle = context.paragraphStyle.merge(ParagraphStyle(textAlign = TextAlign.Left)))
        "right" -> context.copy(paragraphStyle = context.paragraphStyle.merge(ParagraphStyle(textAlign = TextAlign.Right)))

        else -> null
    }

    if (nextContext != null) {
        val start = length
        if (tag in setOf("url", "email", "font") && node.content.orEmpty().all { it == null || it is String || it is Number }) {
            appendStyledText(flattenText(node.content), nextContext)
        } else {
            val childNodes = if (tag in setOf("center", "left", "right")) {
                normalizeInlineLayoutNodes(node.content)
            } else {
                node.content.orEmpty()
            }
            appendInlineChildren(childNodes, nextContext)
        }
        val end = length
        if (end > start && nextContext.paragraphStyle != context.paragraphStyle) {
            addStyle(nextContext.paragraphStyle, start, end)
        }
        return
    }

    if (isIntrinsicBlockNode(node)) {
        appendStyledText(" ", context)
        return
    }

    if (tag == "spoiler" || tag == "mask") {
        val childNodes = node.content.orEmpty()
        if (canRenderSpoilerInline(childNodes)) {
            val start = length
            appendInlineChildren(childNodes, context)
            val end = length
            if (end > start) {
                addStringAnnotation(
                    tag = if (tag == "mask") BbCodeComposeDefaults.MaskAnnotationTag else BbCodeComposeDefaults.SpoilerAnnotationTag,
                    annotation = "$start:$end",
                    start = start,
                    end = end,
                )
            }
            return
        }
    }

    appendStyledText(rawNodeToString(node), context)
}

/**
 * 合并文本装饰样式。
 *
 * @param current 当前装饰。
 * @param next 要追加的装饰。
 * @return 合并后的装饰结果。
 */
private fun mergeDecoration(current: TextDecoration?, next: TextDecoration): TextDecoration =
    if (current == null) {
        next
    } else {
        current + next
    }

/**
 * 将 `[style]` 标签属性映射为 Compose `SpanStyle`。
 *
 * @param attrs 标签属性。
 * @return 对应的文本样式。
 */
private fun spanStyleFromStyleTag(attrs: Map<String, Any?>?): SpanStyle {
    if (attrs == null) return SpanStyle()

    var color: Color? = null
    var fontSize: TextUnit = TextUnit.Unspecified
    var fontFamily: FontFamily? = null

    attrs["color"]?.toString()?.let { color = parseColor(it) }
    attrs["size"]?.toString()?.let { fontSize = parseFontSize(it) }
    attrs["font"]?.toString()?.let { fontFamily = parseFontFamily(it) }

    return SpanStyle(
        color = color ?: Color.Unspecified,
        fontSize = fontSize,
        fontFamily = fontFamily,
    )
}

/**
 * 解析字体大小字符串。
 *
 * 支持 `px`、`sp`、`em` 和纯数字。
 *
 * @param value 原始大小值。
 * @return 解析后的字号；无法识别时返回 `TextUnit.Unspecified`。
 */
private fun parseFontSize(value: String?): TextUnit {
    if (value.isNullOrBlank()) return TextUnit.Unspecified

    val normalized = value.trim()
    return when {
        normalized.endsWith("px", ignoreCase = true) -> normalized.dropLast(2).toFloatOrNull()?.sp ?: TextUnit.Unspecified
        normalized.endsWith("sp", ignoreCase = true) -> normalized.dropLast(2).toFloatOrNull()?.sp ?: TextUnit.Unspecified
        normalized.endsWith("em", ignoreCase = true) -> normalized.dropLast(2).toFloatOrNull()?.em ?: TextUnit.Unspecified
        else -> normalized.toFloatOrNull()?.sp ?: TextUnit.Unspecified
    }
}

private val namedColors: Map<String, Int> = mapOf(
    "black" to 0xFF000000.toInt(),
    "silver" to 0xFFC0C0C0.toInt(),
    "gray" to 0xFF808080.toInt(),
    "grey" to 0xFF808080.toInt(),
    "white" to 0xFFFFFFFF.toInt(),
    "maroon" to 0xFF800000.toInt(),
    "red" to 0xFFFF0000.toInt(),
    "purple" to 0xFF800080.toInt(),
    "fuchsia" to 0xFFFF00FF.toInt(),
    "green" to 0xFF008000.toInt(),
    "lime" to 0xFF00FF00.toInt(),
    "olive" to 0xFF808000.toInt(),
    "yellow" to 0xFFFFFF00.toInt(),
    "navy" to 0xFF000080.toInt(),
    "blue" to 0xFF0000FF.toInt(),
    "teal" to 0xFF008080.toInt(),
    "aqua" to 0xFF00FFFF.toInt(),
    "orange" to 0xFFFFA500.toInt(),
    "transparent" to 0x00000000,
)

/**
 * 解析颜色字符串。
 *
 * 支持命名色以及 `#rgb`、`#rgba`、`#rrggbb`、`#aarrggbb`。
 *
 * @param value 原始颜色值。
 * @return 解析成功时返回颜色对象，否则返回 `null`。
 */
private fun parseColor(value: String?): Color? {
    val normalized = value?.trim()?.lowercase() ?: return null
    namedColors[normalized]?.let { return Color(it) }

    val hex = normalized.removePrefix("#")
    val expanded = when (hex.length) {
        3 -> hex.map { "$it$it" }.joinToString(separator = "").let { "ff$it" }
        4 -> hex.map { "$it$it" }.joinToString(separator = "")
        6 -> "ff$hex"
        8 -> hex
        else -> return null
    }

    return expanded.toLongOrNull(16)?.toInt()?.let(::Color)
}

/**
 * 解析字体族名称并映射为 Compose 通用字体族。
 *
 * 未命中的字体会回退为 `null`，由外层使用默认字体。
 *
 * @param value 原始字体名称。
 * @return 对应的字体族；未命中时返回 `null`。
 */
private fun parseFontFamily(value: String?): FontFamily? {
    val normalized = value
        ?.trim()
        ?.removePrefix("\"")
        ?.removeSuffix("\"")
        ?.lowercase()
        ?: return null

    return when (normalized) {
        "monospace", "monaco", "menlo", "consolas", "courier", "courier new" -> FontFamily.Monospace
        "serif", "times", "times new roman", "georgia" -> FontFamily.Serif
        "sans-serif", "sans serif", "arial", "helvetica", "verdana", "tahoma", "trebuchet ms" -> FontFamily.SansSerif
        "cursive", "comic sans ms", "brush script mt" -> FontFamily.Cursive
        else -> null
    }
}
