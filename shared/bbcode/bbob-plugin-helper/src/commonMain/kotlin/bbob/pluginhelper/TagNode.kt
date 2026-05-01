package bbob.pluginhelper

import bbob.types.NodeContent
import bbob.types.TagNodeObject
import bbob.types.TagNodeTree
import bbob.types.TagPosition

/**
 * 将标签名和属性映射拼装为起始标签中的属性文本。
 *
 * @param tag 标签名。
 * @param params 属性映射。
 * @return 序列化后的标签及属性字符串。
 */
private fun getTagAttrs(tag: String, params: Map<String, Any?>): String {
    val uniqAttr = getUniqAttr(params)

    if (uniqAttr != null) {
        val tagAttr = attrValue(tag, uniqAttr)
        val attrs = params.toMutableMap()
        attrs.remove(uniqAttr.toString())
        val attrsStr = attrsToString(attrs)
        return "$tagAttr$attrsStr"
    }

    return "$tag${attrsToString(params)}"
}

/**
 * 将单个节点转换为 BBCode 字符串。
 *
 * @param node 目标节点。
 * @param openTag 左标签分隔符。
 * @param closeTag 右标签分隔符。
 * @return 节点字符串。
 */
private fun toString(node: NodeContent, openTag: String, closeTag: String): String =
    if (node is TagNode<*>) {
        node.toString(openTag, closeTag)
    } else {
        node.toString()
    }

/**
 * 将节点树递归拼接为 BBCode 字符串。
 *
 * @param content 节点树内容。
 * @param openTag 左标签分隔符。
 * @param closeTag 右标签分隔符。
 * @return 序列化后的字符串；空内容时返回 `null`。
 */
private fun nodeTreeToString(content: TagNodeTree, openTag: String, closeTag: String): String? {
    if (content is List<*>) {
        return content.fold("") { r, node ->
            if (node != null) {
                r + toString(node, openTag, closeTag)
            } else {
                r
            }
        }
    }

    if (content != null) {
        return toString(content, openTag, closeTag)
    }

    return null
}

/**
 * BBCode 标签节点实现。
 *
 * @param TagValue 标签名类型。
 * @property tag 标签名。
 * @property attrs 标签属性。
 * @property content 子节点内容。
 * @property start 起始位置信息。
 * @property end 结束位置信息。
 */
open class TagNode<TagValue>(
    override val tag: TagValue,
    override var attrs: MutableMap<String, Any?>? = mutableMapOf(),
    override var content: TagNodeTree = null,
    override var start: TagPosition? = null,
    override var end: TagPosition? = null,
) : TagNodeObject<TagValue> {

    val length: Int
        get() = getNodeLength(this)

    /**
     * 读取或写入节点属性。
     *
     * @param name 属性名。
     * @param value 传入时写入属性；不传时仅返回当前值。
     * @return 属性当前值。
     */
    fun attr(name: String, value: Any? = Unit): Any? {
        val target = attrs ?: mutableMapOf<String, Any?>().also { attrs = it }
        if (value !== Unit) {
            target[name] = value
        }
        return target[name]
    }

    /**
     * 向节点内容尾部追加子节点。
     *
     * @param value 要追加的节点内容。
     */
    fun append(value: NodeContent) {
        appendToNode(this, value)
    }

    /**
     * 设置节点起始位置。
     *
     * @param value 起始位置。
     */
    fun setStartPos(value: TagPosition) {
        start = value
    }

    /**
     * 设置节点结束位置。
     *
     * @param value 结束位置。
     */
    fun setEndPos(value: TagPosition) {
        end = value
    }

    /**
     * 生成标签起始文本。
     *
     * @param openTag 左标签分隔符。
     * @param closeTag 右标签分隔符。
     * @return 序列化后的起始标签文本。
     */
    fun toTagStart(openTag: String = OPEN_BRAKET, closeTag: String = CLOSE_BRAKET): String {
        val tagAttrs = getTagAttrs(tag.toString(), attrs ?: mutableMapOf())
        return "$openTag$tagAttrs$closeTag"
    }

    /**
     * 生成标签结束文本。
     *
     * @param openTag 左标签分隔符。
     * @param closeTag 右标签分隔符。
     * @return 序列化后的结束标签文本。
     */
    fun toTagEnd(openTag: String = OPEN_BRAKET, closeTag: String = CLOSE_BRAKET): String =
        "$openTag$SLASH$tag$closeTag"

    /**
     * 创建当前节点的浅拷贝。
     *
     * @return 新的标签节点实例。
     */
    fun toTagNode(): TagNode<TagValue> = TagNode(tag, attrs, content, start, end)

    /**
     * 将节点序列化为 BBCode 字符串。
     *
     * @param openTag 左标签分隔符。
     * @param closeTag 右标签分隔符。
     * @return 序列化后的 BBCode 文本。
     */
    fun toString(openTag: String = OPEN_BRAKET, closeTag: String = CLOSE_BRAKET): String {
        val innerContent = if (content != null) nodeTreeToString(content, openTag, closeTag) else ""
        val tagStart = toTagStart(openTag, closeTag)

        if (content == null || (content is List<*> && content!!.isEmpty())) {
            return tagStart
        }

        return "$tagStart$innerContent${toTagEnd(openTag, closeTag)}"
    }

    override fun toString(): String = toString(OPEN_BRAKET, CLOSE_BRAKET)

    /**
     * 将节点转换为 JSON 风格的键值映射。
     *
     * @return 节点映射结构。
     */
    override fun toJson(): Map<String, Any?> = mapOf(
        "tag" to tag,
        "attrs" to attrs,
        "content" to content,
        "start" to start,
        "end" to end,
    )

    companion object {
        /**
         * 创建字符串标签名的节点实例。
         *
         * @param tag 标签名。
         * @param attrs 属性集合。
         * @param content 子节点内容。
         * @param start 起始位置。
         * @return 新的标签节点。
         */
        fun create(
            tag: String,
            attrs: MutableMap<String, Any?> = mutableMapOf(),
            content: TagNodeTree = null,
            start: TagPosition? = null,
        ): TagNode<String> = TagNode(tag, attrs, content, start)

        /**
         * 判断节点是否为指定标签类型。
         *
         * @param node 目标节点。
         * @param type 目标标签名。
         * @return 是否匹配指定类型。
         */
        fun isOf(node: TagNode<*>, type: String): Boolean = node.tag == type
    }
}
