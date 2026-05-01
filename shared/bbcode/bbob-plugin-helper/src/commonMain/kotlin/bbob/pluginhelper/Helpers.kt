package bbob.pluginhelper

import bbob.types.NodeContent
import bbob.types.TagNodeObject
import bbob.types.TagNodeTree

/**
 * 判断对象是否为标签节点。
 *
 * @param el 待判断对象。
 * @return 是否为 [TagNodeObject]。
 */
fun isTagNode(el: Any?): Boolean = el is TagNodeObject<*>

/**
 * 判断对象是否可按字符串节点处理。
 *
 * @param el 待判断对象。
 * @return 是否为字符串或数字节点。
 */
fun isStringNode(el: Any?): Boolean = el is String || el is Number

/**
 * 判断字符串是否为换行符。
 *
 * @param el 待判断字符串。
 * @return 是否为标准换行符。
 */
fun isEOL(el: String): Boolean = el == N

/**
 * 按键遍历映射并累计结果。
 *
 * @param obj 源映射。
 * @param reduce 归并函数。
 * @param def 初始值。
 * @return 归并后的结果。
 */
private fun <Res, Def : Res, T> keysReduce(
    obj: Map<String, T>,
    reduce: (acc: Def, key: String, obj: Map<String, T>) -> Res,
    def: Def,
): Res = obj.keys.fold(def) { acc, key -> reduce(acc, key, obj) as Def }

/**
 * 递归计算节点渲染为纯文本后的长度。
 *
 * @param node 目标节点。
 * @return 节点文本长度。
 */
fun getNodeLength(node: NodeContent): Int {
    if (node is TagNodeObject<*> && node.content != null) {
        return node.content!!.sumOf { contentNode -> getNodeLength(contentNode) }
    }

    if (isStringNode(node)) {
        return node.toString().length
    }

    return 0
}

/**
 * 向标签节点内容尾部追加一个子节点。
 *
 * @param node 目标标签节点。
 * @param value 要追加的节点值。
 */
fun appendToNode(node: TagNode<*>, value: NodeContent) {
    if (node.content != null) {
        node.content!!.add(value)
    }
}

/**
 * 对属性值执行 HTML 与危险协议转义。
 *
 * @param value 原始属性值。
 * @return 安全的属性值字符串。
 */
fun escapeAttrValue(value: String): String =
    value
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#039;")
        .replace(Regex("(javascript|data|vbscript|file):", RegexOption.IGNORE_CASE)) { "${it.groupValues[1]}%3A" }

/**
 * 对普通 HTML 文本执行转义。
 *
 * @param value 原始文本。
 * @return 转义后的文本。
 */
fun escapeHTML(value: String): String = escapeAttrValue(value)

/**
 * 将单个属性转换为标签属性字符串。
 *
 * @param name 属性名。
 * @param value 属性值。
 * @return 拼接后的属性文本；不合法值返回空字符串。
 */
fun attrValue(name: String, value: Any?): String =
    when (value) {
        is Boolean -> if (value) name else ""
        is Number -> "$name=\"$value\""
        is String -> "$name=\"${escapeAttrValue(value)}\""
        is Map<*, *> -> "$name=\"${escapeAttrValue(value.toString())}\""
        is List<*> -> "$name=\"${escapeAttrValue(value.toString())}\""
        else -> ""
    }

/**
 * 将属性映射拼接为标签属性字符串。
 *
 * @param values 属性映射。
 * @return 适合直接拼入标签头部的字符串。
 */
fun attrsToString(values: Map<String, Any?>?): String {
    if (values == null) {
        return ""
    }

    return keysReduce(
        values,
        { arr, key, obj -> arr + attrValue(key, obj[key]) },
        listOf(""),
    ).joinToString(" ")
}

/**
 * 获取“唯一属性”形式的值。
 *
 * 例如 `[url=xxx]` 这类结构中，值会以 `key == value` 的形式保存。
 *
 * @param attrs 标签属性映射。
 * @return 命中的唯一属性值；未命中时返回 `null`。
 */
fun getUniqAttr(attrs: Map<String, Any?>?): Any? =
    keysReduce(
        attrs ?: emptyMap(),
        { _, key, obj -> if (obj[key] == key) obj[key] else null },
        null,
    )
