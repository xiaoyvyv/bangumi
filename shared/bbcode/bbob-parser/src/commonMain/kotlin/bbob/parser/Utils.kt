package bbob.parser

import bbob.pluginhelper.BACKSLASH
import bbob.pluginhelper.QUOTEMARK

/**
 * 字符抓取器配置。
 *
 * @property onSkip 每次光标前进时触发的回调。
 */
class CharGrabberOptions(
    val onSkip: (() -> Unit)? = null,
)

/**
 * 面向字符串的轻量游标工具，用于词法分析阶段按字符抓取内容。
 *
 * @property source 原始字符串。
 * @property options 抓取器配置。
 */
class CharGrabber(
    private val source: String,
    private val options: CharGrabberOptions = CharGrabberOptions(),
) {
    private var pos = 0
    private val len = source.length

    /**
     * 将光标向前移动指定字符数。
     *
     * @param num 要跳过的字符数。
     * @param silent 是否静默跳过而不触发回调。
     */
    fun skip(num: Int = 1, silent: Boolean = false) {
        pos += num
        if (!silent) {
            options.onSkip?.invoke()
        }
    }

    /**
     * 判断光标后是否还有可读字符。
     *
     * @return 是否仍有剩余字符。
     */
    fun hasNext(): Boolean = len > pos

    /**
     * 获取当前字符。
     *
     * @return 当前字符；越界时返回空字符串。
     */
    fun getCurr(): String = source.getOrNull(pos)?.toString() ?: ""

    /**
     * 获取当前光标位置。
     *
     * @return 当前偏移。
     */
    fun getPos(): Int = pos

    /**
     * 获取原始字符串长度。
     *
     * @return 字符串总长度。
     */
    fun getLength(): Int = len

    /**
     * 获取从当前光标开始的剩余字符串。
     *
     * @return 剩余文本。
     */
    fun getRest(): String = source.substring(pos)

    /**
     * 获取下一个字符。
     *
     * @return 下一个字符；不存在时返回 `null`。
     */
    fun getNext(): String? = source.getOrNull(pos + 1)?.toString()

    /**
     * 获取前一个字符。
     *
     * @return 前一个字符；不存在时返回 `null`。
     */
    fun getPrev(): String? = if (pos - 1 >= 0) source.getOrNull(pos - 1)?.toString() else null

    /**
     * 判断光标是否已经到达末尾。
     *
     * @return 是否位于最后一个位置之后。
     */
    fun isLast(): Boolean = pos == len

    /**
     * 判断从当前位置起是否还能找到指定字符串。
     *
     * @param value 目标字符串。
     * @return 是否包含目标值。
     */
    fun includes(value: String): Boolean = source.indexOf(value, pos) >= 0

    /**
     * 持续抓取满足条件的字符。
     *
     * @param condition 抓取条件。
     * @param silent 是否静默移动光标。
     * @return 抓取到的字符串。
     */
    fun grabWhile(condition: (curr: String) -> Boolean, silent: Boolean = false): String {
        var start = 0

        if (hasNext()) {
            start = pos
            while (hasNext() && condition(getCurr())) {
                skip(1, silent)
            }
        }

        return source.substring(start, pos)
    }

    /**
     * 从当前位置起抓取指定数量的字符。
     *
     * @param num 要抓取的字符数。
     * @return 抓取到的字符串。
     */
    fun grabN(num: Int = 0): String = source.substring(pos, (pos + num).coerceAtMost(source.length))

    /**
     * 截取直到遇到指定字符前的子串。
     *
     * @param char 截止字符。
     * @return 目标子串；未找到时返回空字符串。
     */
    fun substrUntilChar(char: String): String {
        val idx = source.indexOf(char, pos)
        return if (idx >= 0) source.substring(pos, idx) else ""
    }
}

/**
 * 创建字符抓取器实例。
 *
 * @param source 原始字符串。
 * @param options 抓取器配置。
 * @return 新的字符抓取器。
 */
fun createCharGrabber(source: String, options: CharGrabberOptions = CharGrabberOptions()): CharGrabber =
    CharGrabber(source, options)

/**
 * 去掉字符串首尾连续出现的指定字符。
 *
 * @param input 原始字符串。
 * @param charToRemove 需要移除的字符。
 * @return 处理后的字符串。
 */
fun trimChar(input: String, charToRemove: String): String {
    var str = input
    while (str.startsWith(charToRemove)) {
        str = str.substring(1)
    }
    while (str.endsWith(charToRemove)) {
        str = str.substring(0, str.length - 1)
    }
    return str
}

/**
 * 还原带转义双引号的字符串内容。
 *
 * @param str 原始字符串。
 * @return 去除转义后的字符串。
 */
fun unquote(str: String): String = str.replace(BACKSLASH + QUOTEMARK, QUOTEMARK)
