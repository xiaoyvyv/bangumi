package bbob.parser

import bbob.pluginhelper.CLOSE_BRAKET
import bbob.pluginhelper.OPEN_BRAKET
import bbob.pluginhelper.SLASH
import bbob.types.TokenData

const val TYPE_ID = "t"
const val VALUE_ID = "v"
const val LINE_ID = "l"
const val COLUMN_ID = "r"
const val START_POS_ID = "s"
const val END_POS_ID = "e"
const val TYPE_WORD = 1
const val TYPE_TAG = 2
const val TYPE_ATTR_NAME = 3
const val TYPE_ATTR_VALUE = 4
const val TYPE_SPACE = 5
const val TYPE_NEW_LINE = 6

private fun getTokenValue(token: Token?): String = token?.v ?: ""
private fun getTokenLine(token: Token?): Int = token?.l ?: 0
private fun getTokenColumn(token: Token?): Int = token?.r ?: 0
private fun getStartPosition(token: Token?): Int = token?.s ?: 0
private fun getEndPosition(token: Token?): Int = token?.e ?: 0

private fun isTextToken(token: Token?): Boolean =
    token?.t == TYPE_SPACE || token?.t == TYPE_NEW_LINE || token?.t == TYPE_WORD

private fun isTagToken(token: Token?): Boolean = token?.t == TYPE_TAG

private fun isTagEnd(token: Token?): Boolean = getTokenValue(token).firstOrNull()?.code == SLASH.first().code
private fun isTagStart(token: Token?): Boolean = !isTagEnd(token)
private fun isAttrNameToken(token: Token?): Boolean = token?.t == TYPE_ATTR_NAME
private fun isAttrValueToken(token: Token?): Boolean = token?.t == TYPE_ATTR_VALUE
private fun getTagName(token: Token?): String = getTokenValue(token).let { if (isTagEnd(token)) it.drop(1) else it }
private fun tokenToText(token: Token, openTag: String = OPEN_BRAKET, closeTag: String = CLOSE_BRAKET): String =
    "$openTag${getTokenValue(token)}$closeTag"

/**
 * 解析阶段使用的令牌对象。
 *
 * @property s 在原始文本中的起始偏移。
 * @property e 在原始文本中的结束偏移。
 */
class Token(
    override val t: Int = 0,
    value: Any? = null,
    override val l: Int = 0,
    override val r: Int = 0,
    val s: Int = 0,
    val e: Int = 0,
) : TokenData<String>(t, value.toString(), l, r) {
    override val v: String = value.toString()

    /**
     * 当前令牌类型。
     */
    val type: Int
        get() = t

    /**
     * 判断令牌是否为空令牌。
     *
     * @return 是否为空。
     */
    fun isEmpty(): Boolean = t == 0

    /**
     * 判断令牌是否属于文本类型。
     *
     * @return 是否为文本令牌。
     */
    fun isText(): Boolean = isTextToken(this)

    /**
     * 判断令牌是否属于标签类型。
     *
     * @return 是否为标签令牌。
     */
    fun isTag(): Boolean = isTagToken(this)

    /**
     * 判断令牌是否为属性名。
     *
     * @return 是否为属性名令牌。
     */
    fun isAttrName(): Boolean = isAttrNameToken(this)

    /**
     * 判断令牌是否为属性值。
     *
     * @return 是否为属性值令牌。
     */
    fun isAttrValue(): Boolean = isAttrValueToken(this)

    /**
     * 判断标签令牌是否为开始标签。
     *
     * @return 是否为开始标签。
     */
    fun isStart(): Boolean = isTagStart(this)

    /**
     * 判断标签令牌是否为结束标签。
     *
     * @return 是否为结束标签。
     */
    fun isEnd(): Boolean = isTagEnd(this)

    /**
     * 获取标签名。
     *
     * @return 去除结束斜杠后的标签名。
     */
    fun getName(): String = getTagName(this)

    /**
     * 获取令牌值。
     *
     * @return 原始令牌值。
     */
    fun getValue(): String = getTokenValue(this)

    /**
     * 获取令牌所在行号。
     *
     * @return 行号。
     */
    fun getLine(): Int = getTokenLine(this)

    /**
     * 获取令牌所在列号。
     *
     * @return 列号。
     */
    fun getColumn(): Int = getTokenColumn(this)

    /**
     * 获取令牌起始偏移。
     *
     * @return 起始位置。
     */
    fun getStart(): Int = getStartPosition(this)

    /**
     * 获取令牌结束偏移。
     *
     * @return 结束位置。
     */
    fun getEnd(): Int = getEndPosition(this)

    /**
     * 将令牌还原为原始标签文本。
     *
     * @param openTag 左标签分隔符。
     * @param closeTag 右标签分隔符。
     * @return 序列化后的标签文本。
     */
    fun toText(openTag: String = OPEN_BRAKET, closeTag: String = CLOSE_BRAKET): String = tokenToText(this, openTag, closeTag)

    /**
     * 以 BBCode 文本形式输出令牌。
     *
     * @return 原始标签文本。
     */
    override fun toString(): String = toText()
}
