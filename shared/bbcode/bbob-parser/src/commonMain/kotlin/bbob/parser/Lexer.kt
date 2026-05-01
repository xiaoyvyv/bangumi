package bbob.parser

import bbob.pluginhelper.BACKSLASH
import bbob.pluginhelper.CLOSE_BRAKET
import bbob.pluginhelper.EQ
import bbob.pluginhelper.N
import bbob.pluginhelper.OPEN_BRAKET
import bbob.pluginhelper.QUOTEMARK
import bbob.pluginhelper.SLASH
import bbob.pluginhelper.SPACE
import bbob.pluginhelper.TAB
import bbob.types.LexerOptions
import bbob.types.LexerTokenizer

private const val EM = "!"

/**
 * 创建指定类型的令牌对象。
 *
 * @param type 令牌类型。
 * @param value 令牌值。
 * @param r 行号。
 * @param cl 列号。
 * @param p 起始偏移。
 * @param e 结束偏移。
 * @return 新的令牌实例。
 */
fun createTokenOfType(type: Int, value: String, r: Int = 0, cl: Int = 0, p: Int = 0, e: Int = 0): Token =
    Token(type, value, r, cl, p, e)

private const val STATE_WORD = 0
private const val STATE_TAG = 1
private const val STATE_TAG_ATTRS = 2
private const val TAG_STATE_NAME = 0
private const val TAG_STATE_ATTR = 1
private const val TAG_STATE_VALUE = 2
private val WHITESPACES = listOf(SPACE, TAB)
private val SPECIAL_CHARS = listOf(EQ, SPACE, TAB)
private const val END_POS_OFFSET = 2

private fun isWhiteSpace(char: String): Boolean = WHITESPACES.contains(char)
private fun isEscapeChar(char: String): Boolean = char == BACKSLASH
private fun isSpecialChar(char: String): Boolean = SPECIAL_CHARS.contains(char)
private fun isNewLine(char: String): Boolean = char == N
private fun unq(value: String): String = unquote(trimChar(value, QUOTEMARK))

/**
 * 创建 BBCode 词法分析器。
 *
 * @param buffer 待分词的原始文本。
 * @param options 词法分析配置。
 * @return 可执行的词法分析器实例。
 */
fun createLexer(buffer: String, options: LexerOptions = LexerOptions()): LexerTokenizer {
    var row = 0
    var prevCol = 0
    var col = 0
    var stateMode = STATE_WORD
    var tagMode = TAG_STATE_NAME
    var contextFreeTag = ""
    val tokens = mutableListOf<Token>()
    val openTag = options.openTag ?: OPEN_BRAKET
    val closeTag = options.closeTag ?: CLOSE_BRAKET
    val escapeTags = options.enableEscapeTags == true
    val contextFreeTags = (options.contextFreeTags ?: emptyList()).filter { it.isNotBlank() }.map { it.lowercase() }
    val caseFreeTags = options.caseFreeTags == true
    val nestedMap = mutableMapOf<String, Boolean>()
    val onToken = options.onToken ?: {}
    val reservedChars = listOf(closeTag, openTag, QUOTEMARK, BACKSLASH, SPACE, TAB, EQ, N, EM)
    val notCharTokens = listOf(openTag, SPACE, TAB, N)
    /** 判断字符是否属于标签解析时保留字符。 */
    fun isCharReserved(char: String): Boolean = reservedChars.contains(char)
    /** 判断字符是否可直接归入普通文本令牌。 */
    fun isCharToken(char: String): Boolean = !notCharTokens.contains(char)
    /** 判断字符是否允许在转义模式下被转义。 */
    fun isEscapableChar(char: String): Boolean = char == openTag || char == closeTag || char == BACKSLASH
    val onSkip = {
        col++
        Unit
    }

    /** 生成指定标签名对应的结束标签文本。 */
    fun toEndTag(tagName: String): String = "$openTag$SLASH$tagName$closeTag"

    /** 判断标签是否会在后文出现对应的结束标签。 */
    fun isTokenNested(tokenValue: String): Boolean {
        val value = toEndTag(tokenValue)
        nestedMap[value]?.let { return it }
        val buf = if (caseFreeTags) buffer.lowercase() else buffer
        val valToFind = if (caseFreeTags) value.lowercase() else value
        val status = buf.indexOf(valToFind) > -1
        nestedMap[value] = status
        return status
    }

    /** 维护当前上下文无关标签的活动状态。 */
    fun setupContextFreeTag(name: String, isClosingTag: Boolean = false) {
        if (contextFreeTag.isNotEmpty() && isClosingTag) {
            contextFreeTag = ""
        }
        val tagName = name.lowercase()
        if (contextFreeTag.isEmpty() && isTokenNested(name) && contextFreeTags.contains(tagName)) {
            contextFreeTag = tagName
        }
    }

    val chars = createCharGrabber(buffer, CharGrabberOptions(onSkip))

    /** 产出令牌并同步位置信息。 */
    fun emitToken(type: Int, value: String, startPos: Int = 0, endPos: Int = 0) {
        val token = createTokenOfType(type, value, row, prevCol, startPos, endPos)
        onToken(token)
        prevCol = col
        tokens.add(token)
    }

    /** 解析标签内容中的名称、属性名和属性值状态转换。 */
    fun nextTagState(tagChars: CharGrabber, isSingleValueTag: Boolean, masterStartPos: Int): Int {
        if (tagMode == TAG_STATE_ATTR) {
            val validAttrName = { char: String -> char != EQ && !isWhiteSpace(char) }
            val name = tagChars.grabWhile(validAttrName)
            val isEnd = tagChars.isLast()
            val isValue = tagChars.getCurr() != EQ
            tagChars.skip()
            if (isEnd || isValue) {
                emitToken(TYPE_ATTR_VALUE, unq(name))
            } else {
                emitToken(TYPE_ATTR_NAME, name)
            }
            if (isEnd) return TAG_STATE_NAME
            if (isValue) return TAG_STATE_ATTR
            return TAG_STATE_VALUE
        }

        if (tagMode == TAG_STATE_VALUE) {
            var stateSpecial = false
            val validAttrValue = fun(char: String): Boolean {
                val isQM = char == QUOTEMARK
                val prevChar = tagChars.getPrev()
                val nextChar = tagChars.getNext()
                val isPrevSlash = prevChar == BACKSLASH
                val isNextEQ = nextChar == EQ
                val isWS = isWhiteSpace(char)
                val isNextWS = nextChar != null && isWhiteSpace(nextChar)

                if (stateSpecial && isSpecialChar(char)) {
                    return true
                }

                if (isQM && !isPrevSlash) {
                    stateSpecial = !stateSpecial
                    if (!stateSpecial && !(isNextEQ || isNextWS)) {
                        return false
                    }
                }

                if (!isSingleValueTag) {
                    return !isWS
                }

                return true
            }

            val name = tagChars.grabWhile(validAttrValue)
            tagChars.skip()
            emitToken(TYPE_ATTR_VALUE, unq(name))
            if (tagChars.getPrev() == QUOTEMARK) {
                prevCol++
            }
            if (tagChars.isLast()) return TAG_STATE_NAME
            return TAG_STATE_ATTR
        }

        val start = masterStartPos + tagChars.getPos() - 1
        val validName = { char: String -> char != EQ && !isWhiteSpace(char) && !tagChars.isLast() }
        val name = tagChars.grabWhile(validName)
        emitToken(TYPE_TAG, name, start, masterStartPos + tagChars.getLength() + 1)
        setupContextFreeTag(name)
        tagChars.skip()
        prevCol++
        if (isSingleValueTag) return TAG_STATE_VALUE
        val hasEQ = tagChars.includes(EQ)
        return if (hasEQ) TAG_STATE_ATTR else TAG_STATE_VALUE
    }

    /** 处理标签起始状态。 */
    fun stateTag(): Int {
        val currChar = chars.getCurr()
        val nextChar = chars.getNext()
        val isNextCharReserved = nextChar != null && isCharReserved(nextChar)
        chars.skip()
        val substr = chars.substrUntilChar(closeTag)
        val hasInvalidChars = substr.isEmpty() || substr.contains(openTag)
        val isLastChar = chars.isLast()
        val hasSpace = substr.contains(SPACE)
        val isSpaceRestricted = hasSpace && options.whitespaceInTags == false

        if (isNextCharReserved || hasInvalidChars || isLastChar || isSpaceRestricted) {
            emitToken(TYPE_WORD, currChar)
            return STATE_WORD
        }

        val isNoAttrsInTag = !substr.contains(EQ)
        val isClosingTag = substr.firstOrNull()?.toString() == SLASH

        if (isNoAttrsInTag || isClosingTag) {
            val startPos = chars.getPos() - 1
            val name = chars.grabWhile({ char: String -> char != closeTag })
            val endPos = startPos + name.length + END_POS_OFFSET
            chars.skip()
            emitToken(TYPE_TAG, name, startPos, endPos)
            setupContextFreeTag(name, isClosingTag)
            return STATE_WORD
        }

        return STATE_TAG_ATTRS
    }

    /** 处理标签属性状态。 */
    fun stateAttrs(): Int {
        val startPos = chars.getPos()
        val tagStr = chars.grabWhile({ char: String -> char != closeTag }, true)
        val tagGrabber = createCharGrabber(tagStr, CharGrabberOptions(onSkip))
        val eqParts = tagStr.split(EQ)
        val tagName = eqParts[0]
        val isEndTag = tagName.firstOrNull()?.toString() == SLASH
        val isSingleAttrTag = !tagName.contains(SPACE)
        val isSingleValueTag = !isEndTag && isSingleAttrTag
        tagMode = TAG_STATE_NAME

        while (tagGrabber.hasNext()) {
            tagMode = nextTagState(tagGrabber, isSingleValueTag, startPos)
        }

        chars.skip()
        return STATE_WORD
    }

    /** 处理普通文本状态。 */
    fun stateWord(): Int {
        if (isNewLine(chars.getCurr())) {
            emitToken(TYPE_NEW_LINE, chars.getCurr())
            chars.skip()
            col = 0
            prevCol = 0
            row++
            return STATE_WORD
        }

        if (isWhiteSpace(chars.getCurr())) {
            val word = chars.grabWhile(::isWhiteSpace)
            emitToken(TYPE_SPACE, word)
            return STATE_WORD
        }

        if (chars.getCurr() == openTag) {
            if (contextFreeTag.isNotEmpty()) {
                val fullTagName = toEndTag(contextFreeTag)
                val foundTag = chars.grabN(fullTagName.length)
                val isContextFreeEnded = foundTag.equals(fullTagName, ignoreCase = true)
                if (isContextFreeEnded) {
                    return STATE_TAG
                }
            } else if (chars.includes(closeTag)) {
                return STATE_TAG
            }

            emitToken(TYPE_WORD, chars.getCurr())
            chars.skip()
            prevCol++
            return STATE_WORD
        }

        if (escapeTags) {
            if (isEscapeChar(chars.getCurr())) {
                val currChar = chars.getCurr()
                val nextChar = chars.getNext()
                chars.skip()
                if (nextChar != null && isEscapableChar(nextChar)) {
                    chars.skip()
                    emitToken(TYPE_WORD, nextChar)
                    return STATE_WORD
                }
                emitToken(TYPE_WORD, currChar)
                return STATE_WORD
            }

            val isChar = { char: String -> isCharToken(char) && !isEscapeChar(char) }
            val word = chars.grabWhile(isChar)
            emitToken(TYPE_WORD, word)
            return STATE_WORD
        }

        val word = chars.grabWhile(::isCharToken)
        emitToken(TYPE_WORD, word)
        return STATE_WORD
    }

    return object : LexerTokenizer {
        override fun tokenize(): List<bbob.types.TokenData<String>> {
            stateMode = STATE_WORD
            while (chars.hasNext()) {
                stateMode = when (stateMode) {
                    STATE_TAG -> stateTag()
                    STATE_TAG_ATTRS -> stateAttrs()
                    else -> stateWord()
                }
            }
            return tokens
        }

        override fun isTokenNested(tokenValue: String): Boolean = isTokenNested(tokenValue)
    }
}
