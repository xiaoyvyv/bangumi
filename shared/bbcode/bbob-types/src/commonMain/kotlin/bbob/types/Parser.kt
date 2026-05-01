package bbob.types

/**
 * 解析阶段的错误信息。
 *
 * @property tagName 出错的标签名。
 * @property lineNumber 错误所在行号。
 * @property columnNumber 错误所在列号。
 */
data class ParseError(
    val tagName: String,
    val lineNumber: Int,
    val columnNumber: Int,
)

/**
 * 词法分析输出的基础令牌结构。
 *
 * @param TokenValue 令牌值类型。
 * @property t 令牌类型。
 * @property v 令牌值。
 * @property l 行号。
 * @property r 列号。
 */
open class TokenData<TokenValue>(
    open val t: Int,
    open val v: TokenValue,
    open val l: Int,
    open val r: Int,
)

/**
 * 词法分析器接口。
 */
interface LexerTokenizer {
    /**
     * 执行分词并返回令牌列表。
     *
     * @return 分词结果。
     */
    fun tokenize(): List<TokenData<String>>

    /**
     * 判断指定标签是否应按可嵌套标签处理。
     *
     * @param tokenValue 标签名。
     * @return 是否会在后文出现对应闭合标签。
     */
    fun isTokenNested(tokenValue: String): Boolean = false
}

/**
 * 词法与语法阶段共享的公共配置。
 */
open class CommonOptions {
    var openTag: String? = null
    var closeTag: String? = null
    var onlyAllowTags: List<String>? = null
    var enableEscapeTags: Boolean? = null
    var caseFreeTags: Boolean? = null
    var whitespaceInTags: Boolean? = null
    var contextFreeTags: List<String>? = null

    /**
     * 将公共配置字段复制到目标对象。
     *
     * @param target 目标配置对象。
     */
    protected fun copyInto(target: CommonOptions) {
        target.openTag = openTag
        target.closeTag = closeTag
        target.onlyAllowTags = onlyAllowTags
        target.enableEscapeTags = enableEscapeTags
        target.caseFreeTags = caseFreeTags
        target.whitespaceInTags = whitespaceInTags
        target.contextFreeTags = contextFreeTags
    }
}

/**
 * 词法分析器配置。
 */
open class LexerOptions : CommonOptions() {
    var onToken: ((token: Any?) -> Unit)? = null

    /**
     * 复制当前词法配置。
     *
     * @return 新的词法配置实例。
     */
    open fun copy(): LexerOptions = LexerOptions().also {
        copyInto(it)
        it.onToken = onToken
    }
}

/**
 * 语法解析配置。
 */
open class ParseOptions : CommonOptions() {
    var createTokenizer: ((input: String, options: LexerOptions) -> LexerTokenizer)? = null
    var onError: ((error: ParseError) -> Unit)? = null

    /**
     * 复制当前解析配置。
     *
     * @return 新的解析配置实例。
     */
    open fun copy(): ParseOptions = ParseOptions().also {
        copyInto(it)
        it.createTokenizer = createTokenizer
        it.onError = onError
    }
}
