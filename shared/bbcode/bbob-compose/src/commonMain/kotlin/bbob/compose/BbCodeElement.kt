package bbob.compose

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.AnnotatedString

/**
 * `bbcode-compose` 渲染层使用的元素抽象。
 */
sealed interface BbCodeElement

/**
 * 行内富文本元素。
 *
 * @property text 经过样式与注解处理后的文本。
 */
data class BbCodeTextElement(
    val text: AnnotatedString,
) : BbCodeElement

/**
 * 引用块元素。
 *
 * @property children 引用内部的子元素列表。
 */
data class BbCodeQuoteElement(
    val children: List<BbCodeElement>,
) : BbCodeElement

/**
 * 代码块元素。
 *
 * @property text 代码块原始文本。
 */
data class BbCodeCodeBlockElement(
    val text: String,
) : BbCodeElement

/**
 * 图片元素。
 *
 * @property url 图片地址。
 * @property attrs 图片标签属性。
 * @property linkUrl 包裹图片的点击链接，未设置时表示图片本身不可点击。
 */
data class BbCodeImageElement(
    val url: String,
    val attrs: Map<String, Any?> = emptyMap(),
    val linkUrl: String? = null,
) : BbCodeElement

/**
 * 列表元素。
 *
 * @property ordered 是否为有序列表。
 * @property items 列表项内容。
 * @property type 列表样式类型或序号类型。
 */
data class BbCodeListElement(
    val ordered: Boolean,
    val items: List<List<BbCodeElement>>,
    val type: String? = null,
) : BbCodeElement

/**
 * 对齐容器元素。
 *
 * @property alignment 文本对齐方式。
 * @property children 容器内部子元素。
 */
data class BbCodeAlignedElement(
    val alignment: TextAlign,
    val children: List<BbCodeElement>,
) : BbCodeElement

/**
 * 遮罩或剧透元素。
 *
 * @property children 容器内部子元素。
 * @property masked 是否按遮罩语义处理。
 */
data class BbCodeSpoilerElement(
    val children: List<BbCodeElement>,
    val masked: Boolean = false,
) : BbCodeElement

/**
 * 水平分隔线元素。
 */
data object BbCodeDividerElement : BbCodeElement

/**
 * 预格式化文本元素。
 *
 * @property text 预格式化文本内容。
 */
data class BbCodePreElement(
    val text: String,
) : BbCodeElement

/**
 * 额外空行元素。
 *
 * @property count 需要补充的空行数量。
 */
data class BbCodeLineBreakElement(
    val count: Int,
) : BbCodeElement
