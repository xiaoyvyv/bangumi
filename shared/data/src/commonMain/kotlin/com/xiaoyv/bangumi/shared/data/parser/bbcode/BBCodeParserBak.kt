package com.xiaoyv.bangumi.shared.data.parser.bbcode
/*

interface BBCodeHandler {
    fun onOpenTagName(name: String)
    fun onOpenTag(name: String, attributes: Map<String, String>, isImplied: Boolean)
    fun onCloseTag(name: String, isImplied: Boolean)
    fun onText(text: String)
    fun onEnd()
    fun onError(error: Exception)
}

class BBCodeParser(
    private val handler: BBCodeHandler,
    private val supportedTags: Set<String> = defaultSupportedTags
) {
    private data class Tag(val name: String, val autoValue: Boolean = false)

    private val stack = ArrayDeque<Tag>()
    private val textBuffer = StringBuilder()
    private var rawModeTagName: String? = null

    // 1. 定义哪些标签在没有属性时需要将其内容作为 value
    private val contentAsValueTags = setOf("url", "img")

    fun parse(input: String) {
        try {
            var i = 0
            val len = input.length
            while (i < len) {
                val c = input[i]
                when {
                    c == '\\' && i + 1 < len -> {
                        textBuffer.append(input[i + 1])
                        i += 2
                    }

                    (c == '[' || c == '<') && rawModeTagName == null -> {
                        val closeChar = if (c == '[') ']' else '>'
                        val end = input.indexOf(closeChar, i + 1)

                        if (end != -1) {
                            val content = input.substring(i + 1, end).trim()
                            val tagName = getTagName(content)
                            val isClosing = content.startsWith("/")

                            if (tagName.isNotEmpty() && isTagSupported(tagName)) {
                                val (_, attrs) = parseTagAttributes(content.removePrefix("/").removeSuffix("/"))

                                // 2. 核心逻辑：如果是 [url] 且没有 value 属性
                                if (!isClosing && contentAsValueTags.contains(tagName) && !attrs.containsKey("value")) {
                                    flushText()
                                    handler.onOpenTagName(tagName)
                                    // 标记这个标签正处于“等待内容”模式
                                    stack.addLast(Tag(tagName, autoValue = true))
                                    rawModeTagName = tagName
                                } else {
                                    flushText()
                                    handleTag(content, tagName)
                                    if (!isClosing && isRawTag(tagName)) {
                                        rawModeTagName = tagName
                                    }
                                }
                                i = end + 1
                            } else {
                                textBuffer.append(c)
                                i++
                            }
                        } else {
                            textBuffer.append(c)
                            i++
                        }
                    }

                    rawModeTagName != null -> {
                        val isPotentialEnd = (c == '[' || c == '<')
                        val closeChar = if (c == '[') ']' else '>'
                        val end = if (isPotentialEnd) input.indexOf(closeChar, i + 1) else -1

                        if (end != -1) {
                            val content = input.substring(i + 1, end).trim()
                            val tagName = getTagName(content)
                            val isClosing = content.startsWith("/")

                            if (isClosing && tagName == rawModeTagName) {
                                val lastTag = stack.lastOrNull()

                                if (lastTag?.autoValue == true && lastTag.name == tagName) {
                                    val capturedText = textBuffer.toString()

                                    // 1. 先发送带属性的 openTag (将文本作为 value 属性)
                                    handler.onOpenTag(tagName, mapOf("value" to capturedText), isImplied = false)

                                    // 2. 正常走一次 onText 流程，让外部能接收到文本显示
                                    flushText()

                                    // 3. 正常关闭标签
                                    handler.onCloseTag(tagName, isImplied = false)
                                    stack.removeLast()
                                } else {
                                    flushText()
                                    handleTag(content, tagName)
                                }

                                rawModeTagName = null
                                i = end + 1
                            } else {
                                textBuffer.append(c)
                                i++
                            }
                        } else {
                            textBuffer.append(c)
                            i++
                        }
                    }

                    else -> {
                        textBuffer.append(c)
                        i++
                    }
                }
            }
            flushText()
            closeRemainingTags()
            handler.onEnd()
        } catch (e: Exception) {
            handler.onError(e)
        }
    }

    private fun getTagName(content: String): String {
        if (content.isEmpty()) return ""
        // 移除前导 / (结束标签) 和 尾部 / (自闭合标签)
        val raw = content.removePrefix("/").removeSuffix("/").trim()
        val firstSpaceOrEq = raw.indexOfFirst { it == ' ' || it == '=' || it == '/' }
        return if (firstSpaceOrEq == -1) raw.lowercase() else raw.substring(0, firstSpaceOrEq).lowercase()
    }

    private fun handleTag(raw: String, tagName: String) {
        if (raw.startsWith("/")) {
            handleCloseTag(tagName)
            return
        }

        // 列表项 [*] 特殊逻辑
        if ((tagName == "*" || tagName == "li") && stack.lastOrNull()?.name == "*") {
            handler.onCloseTag(stack.removeLast().name, isImplied = true)
        }

        // 识别 HTML 风格的自闭合 <br/> 或像 <img> 这样的单标签
        val isSelfClosing = raw.endsWith("/") || isVoidElement(tagName)
        val (_, attrs) = parseTagAttributes(raw.removeSuffix("/"))

        handler.onOpenTagName(tagName)
        handler.onOpenTag(tagName, attrs, isImplied = false)

        if (!isSelfClosing) {
            stack.addLast(Tag(tagName))
        } else {
            handler.onCloseTag(tagName, isImplied = false)
        }
    }

    private fun parseTagAttributes(content: String): Pair<String, Map<String, String>> {
        val attrs = mutableMapOf<String, String>()
        val firstSpaceOrEq = content.indexOfFirst { it == ' ' || it == '=' }
        if (firstSpaceOrEq == -1) return content.lowercase() to attrs

        val tagName = content.substring(0, firstSpaceOrEq).lowercase()
        var remaining = content.substring(firstSpaceOrEq).trim()

        // 兼容 BBCode 的 [url=xxx] 格式
        if (remaining.startsWith("=")) {
            val firstSpace = remaining.indexOf(' ')
            if (firstSpace == -1) {
                attrs["value"] = remaining.substring(1).trimQuotes()
                return tagName to attrs
            } else {
                attrs["value"] = remaining.substring(1, firstSpace).trimQuotes()
                remaining = remaining.substring(firstSpace).trim()
            }
        }

        // 标准 key="value" 属性解析
        val attrRegex = Regex("""([\w-]+)\s*=\s*("[^"]*"|'[^']*'|[^\s>]+)""")
        attrRegex.findAll(remaining).forEach { match ->
            attrs[match.groupValues[1].lowercase()] = match.groupValues[2].trimQuotes()
        }
        return tagName to attrs
    }

    // HTML 的空元素（不需要结束标签）
    private fun isVoidElement(name: String): Boolean {
        return name == "br" || name == "img" || name == "hr" || name == "input" || name == "meta"
    }

    private fun isRawTag(name: String) = name == "code" || name == "noparse" || name == "pre"
    private fun isTagSupported(name: String) = supportedTags.contains(name)
    private fun String.trimQuotes(): String =
        if (length >= 2 && ((startsWith("\"") && endsWith("\"")) || (startsWith("'") && endsWith("'")))) substring(1, length - 1) else this

    private fun handleCloseTag(name: String) {
        val index = stack.indexOfLast { it.name == name }
        if (index == -1) return
        while (stack.size - 1 > index) {
            handler.onCloseTag(stack.removeLast().name, isImplied = true)
        }
        handler.onCloseTag(stack.removeLast().name, isImplied = false)
    }

    private fun closeRemainingTags() {
        flushText()
        while (stack.isNotEmpty()) handler.onCloseTag(stack.removeLast().name, isImplied = true)
    }

    private fun flushText() {
        if (textBuffer.isNotEmpty()) {
            handler.onText(textBuffer.toString())
            textBuffer.clear()
        }
    }
}*/
