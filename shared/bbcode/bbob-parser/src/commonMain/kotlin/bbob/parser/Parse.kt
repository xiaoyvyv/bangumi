package bbob.parser

import bbob.pluginhelper.CLOSE_BRAKET
import bbob.pluginhelper.OPEN_BRAKET
import bbob.pluginhelper.TagNode
import bbob.pluginhelper.isTagNode
import bbob.types.LexerOptions
import bbob.types.NodeContent
import bbob.types.ParseError
import bbob.types.ParseOptions
import bbob.types.TagNodeTree
import bbob.types.TagPosition

private fun <Type> createList(): NodeList<Type> = NodeList()

/**
 * 将 BBCode 字符串解析为节点列表。
 *
 * @param input 原始 BBCode 文本。
 * @param opts 解析配置。
 * @return 解析得到的节点列表。
 */
fun parse(input: String, opts: ParseOptions = ParseOptions()): MutableList<NodeContent> {
    val options = opts
    val openTag = options.openTag ?: OPEN_BRAKET
    val closeTag = options.closeTag ?: CLOSE_BRAKET
    val onlyAllowTags = (options.onlyAllowTags ?: emptyList()).filter { it.isNotBlank() }.map { it.lowercase() }
    val caseFreeTags = options.caseFreeTags == true
    var tokenizer: bbob.types.LexerTokenizer? = null
    val nodes = createList<TagNode<*>>()
    val nestedNodes = createList<NodeContent>()
    var activeTagNode: TagNode<String>? = null
    var activeTagNodesAttrName: String? = null
    val nestedTagsMap = mutableSetOf<String>()

    /** 统一处理大小写敏感与不敏感场景下的标签值。 */
    fun getValue(tokenValue: String): String = if (caseFreeTags) tokenValue.lowercase() else tokenValue

    /** 判断令牌对应标签是否应按嵌套标签处理。 */
    fun isTokenNested(token: Token): Boolean {
        val tokenValue = token.getValue()
        val value = getValue(tokenValue)
        val nested = tokenizer?.isTokenNested(value) == true
        if (!nestedTagsMap.contains(value) && nested) {
            nestedTagsMap.add(value)
            return true
        }
        return nestedTagsMap.contains(value)
    }

    /** 判断指定标签名是否已被标记为嵌套标签。 */
    fun isTagNested(tagName: String): Boolean = nestedTagsMap.contains(getValue(tagName))

    /** 判断标签是否在允许解析的白名单中。 */
    fun isTagAllowed(value: String): Boolean =
        if (onlyAllowTags.isNotEmpty()) {
            onlyAllowTags.contains(value.lowercase())
        } else {
            true
        }

    /** 清空当前正在构建的活动标签上下文。 */
    fun activeTagNodeFlush() {
        activeTagNode = null
        activeTagNodesAttrName = null
    }

    /** 获取当前节点内容应写入的目标列表。 */
    fun getNodesContent(): MutableList<NodeContent> {
        val lastNestedNode = nestedNodes.last()
        if (lastNestedNode != null && isTagNode(lastNestedNode)) {
            return (lastNestedNode as TagNode<*>).content ?: mutableListOf<NodeContent>().also { (lastNestedNode).content = it }
        }
        @Suppress("UNCHECKED_CAST")
        return nodes.ref() as MutableList<NodeContent>
    }

    /** 将标签节点按原始 BBCode 文本形式追加到目标列表。 */
    fun nodesAppendAsString(targetNodes: TagNodeTree, node: TagNode<*>?, isNested: Boolean = true) {
        if (targetNodes != null && node != null) {
            targetNodes.add(node.toTagStart(openTag, closeTag))
            if (node.content != null && node.content!!.isNotEmpty()) {
                node.content!!.forEach { item -> targetNodes.add(item) }
                if (isNested) {
                    targetNodes.add(node.toTagEnd(openTag, closeTag))
                }
            }
        }
    }

    /** 按当前上下文将节点追加到结果树中。 */
    fun nodesAppend(node: NodeContent) {
        val targetNodes = getNodesContent()
        if (isTagNode(node)) {
            node as TagNode<*>
            if (isTagAllowed(node.tag.toString())) {
                targetNodes.add(node.toTagNode())
            } else {
                nodesAppendAsString(targetNodes, node)
            }
        } else if (node != null) {
            targetNodes.add(node)
        }
    }

    /** 处理开始标签令牌。 */
    fun tagHandleStart(token: Token) {
        activeTagNodeFlush()
        val tagNode = TagNode.create(token.getValue(), mutableMapOf(), mutableListOf(), TagPosition(token.getStart(), token.getEnd()))
        val nested = isTokenNested(token)
        activeTagNode = tagNode
        if (nested) {
            nestedNodes.push(tagNode)
        } else {
            nodesAppend(tagNode)
        }
    }

    /** 处理结束标签令牌。 */
    fun tagHandleEnd(token: Token) {
        val tagName = token.getValue().drop(1)
        val lastNestedNode = nestedNodes.flush()
        activeTagNodeFlush()

        if (lastNestedNode != null) {
            if (isTagNode(lastNestedNode)) {
                (lastNestedNode as TagNode<*>).setEndPos(TagPosition(token.getStart(), token.getEnd()))
            }
            nodesAppend(lastNestedNode)
        } else if (!isTagNested(tagName)) {
            nodesAppend(token.toText(openTag, closeTag))
        } else {
            options.onError?.invoke(ParseError(token.getValue(), token.getLine(), token.getColumn()))
        }
    }

    /** 处理普通文本、属性与非标签令牌。 */
    fun nodeHandle(token: Token) {
        val tokenValue = token.getValue()
        val nested = isTagNested(token.toText())

        if (activeTagNode != null) {
            when (token.type) {
                TYPE_ATTR_NAME -> {
                    activeTagNodesAttrName = tokenValue
                    if (tokenValue.isNotEmpty()) {
                        activeTagNode!!.attr(tokenValue, "")
                    }
                }

                TYPE_ATTR_VALUE -> {
                    if (activeTagNodesAttrName != null) {
                        activeTagNode!!.attr(activeTagNodesAttrName!!, tokenValue)
                        activeTagNodesAttrName = null
                    } else {
                        activeTagNode!!.attr(tokenValue, tokenValue)
                    }
                }

                TYPE_SPACE, TYPE_NEW_LINE, TYPE_WORD -> {
                    if (nested) {
                        activeTagNode!!.append(tokenValue)
                    } else {
                        nodesAppend(tokenValue)
                    }
                }

                TYPE_TAG -> nodesAppend(token.toText(openTag, closeTag))
            }
        } else if (token.isText()) {
            nodesAppend(tokenValue)
        } else if (token.isTag()) {
            nodesAppend(token.toText(openTag, closeTag))
        }
    }

    /** 词法分析阶段的统一令牌分发入口。 */
    fun onToken(tokenAny: Any?) {
        val token = tokenAny as? Token ?: return
        if (token.isTag()) {
            if (token.isStart()) {
                tagHandleStart(token)
            }
            if (token.isEnd()) {
                tagHandleEnd(token)
            }
        } else {
            nodeHandle(token)
        }
    }

    val lexer = options.createTokenizer ?: { source: String, lexerOptions: LexerOptions -> createLexer(source, lexerOptions) }

    val lexerOptions = LexerOptions().also {
        it.onToken = ::onToken
        it.openTag = openTag
        it.closeTag = closeTag
        it.onlyAllowTags = options.onlyAllowTags
        it.contextFreeTags = options.contextFreeTags
        it.caseFreeTags = options.caseFreeTags
        it.enableEscapeTags = options.enableEscapeTags
        it.whitespaceInTags = options.whitespaceInTags
    }

    tokenizer = lexer(input, lexerOptions)
    tokenizer.tokenize()

    do {
        val node = nestedNodes.flush()
        if (node != null && isTagNode(node) && isTagNested((node as TagNode<*>).tag.toString())) {
            nodesAppendAsString(getNodesContent(), node, false)
        } else if (node != null) {
            nodesAppend(node)
        }
    } while (nestedNodes.has())

    @Suppress("UNCHECKED_CAST")
    return nodes.ref() as MutableList<NodeContent>
}
