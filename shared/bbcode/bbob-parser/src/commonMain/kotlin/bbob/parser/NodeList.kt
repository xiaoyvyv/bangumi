package bbob.parser

/**
 * 简单的栈式节点列表封装。
 *
 * @param Value 节点值类型。
 */
class NodeList<Value> {
    private val n = mutableListOf<Value>()

    /**
     * 读取尾部元素但不移除。
     *
     * @return 最后一个元素；为空时返回 `null`。
     */
    fun last(): Value? = n.lastOrNull()

    /**
     * 判断列表是否非空。
     *
     * @return 是否至少包含一个元素。
     */
    fun has(): Boolean = n.isNotEmpty()

    /**
     * 弹出尾部元素。
     *
     * @return 被移除的元素；为空时返回 `null`。
     */
    fun flush(): Value? = if (n.isNotEmpty()) n.removeAt(n.lastIndex) else null

    /**
     * 向尾部压入一个元素。
     *
     * @param value 待压入的值。
     */
    fun push(value: Value) {
        n.add(value)
    }

    /**
     * 暴露底层可变列表引用。
     *
     * @return 底层列表。
     */
    fun ref(): MutableList<Value> = n
}
