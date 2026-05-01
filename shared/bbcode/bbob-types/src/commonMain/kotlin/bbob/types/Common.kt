package bbob.types

typealias StringNode = Any
typealias NodeContent = Any?
typealias PartialNodeContent = Any?
typealias TagNodeTree = MutableList<NodeContent>?

/**
 * 标签在原始文本中的起止位置。
 *
 * @property from 起始偏移。
 * @property to 结束偏移。
 */
data class TagPosition(val from: Int, val to: Int)

/**
 * BBCode 标签节点的统一抽象。
 *
 * @param TagValue 标签名类型。
 */
interface TagNodeObject<TagValue> {
    val tag: TagValue
    var attrs: MutableMap<String, Any?>?
    var content: TagNodeTree
    var start: TagPosition?
    var end: TagPosition?

    /**
     * 将当前节点转换为便于序列化的映射结构。
     *
     * @return 节点的键值映射表示。
     */
    fun toJson(): Map<String, Any?>
}
