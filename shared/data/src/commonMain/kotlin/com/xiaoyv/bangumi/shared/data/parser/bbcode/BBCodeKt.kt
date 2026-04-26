package com.xiaoyv.bangumi.shared.data.parser.bbcode

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
    // 默认支持的标签集
    private val supportedTags: Set<String> = defaultSupportedTags
) {
    private data class Tag(val name: String, val isSynthesized: Boolean = false)

    private val stack = ArrayDeque<Tag>()
    private val textBuffer = StringBuilder()
    private var rawModeTagName: String? = null

    fun parse(input: String) {
        try {
            var i = 0
            val len = input.length
            while (i < len) {
                when (input[i]) {
                    '\\' -> {
                        if (i + 1 < len) {
                            textBuffer.append(input[i + 1])
                            i += 2
                        } else {
                            textBuffer.append('\\')
                            i++
                        }
                    }

                    '[' -> {
                        val end = input.indexOf(']', i + 1)
                        if (end != -1) {
                            val content = input.substring(i + 1, end).trim()
                            val tagName = getTagName(content)
                            val isClosing = content.startsWith("/")

                            if (rawModeTagName != null) {
                                if (isClosing && tagName == rawModeTagName) {
                                    flushText()
                                    handleTag(content, tagName, input, end + 1)
                                    rawModeTagName = null
                                    i = end + 1
                                } else {
                                    textBuffer.append('[')
                                    i++
                                }
                            } else {
                                if (tagName.isNotEmpty() && isTagSupported(tagName)) {
                                    flushText()
                                    val consumedCount = handleTag(content, tagName, input, end + 1)
                                    if (!isClosing && isRawTag(tagName)) {
                                        rawModeTagName = tagName
                                    }
                                    i = (end + 1) + consumedCount
                                } else {
                                    textBuffer.append('[')
                                    i++
                                }
                            }
                        } else {
                            textBuffer.append('[')
                            i++
                        }
                    }

                    else -> {
                        textBuffer.append(input[i])
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

    private fun isRawTag(name: String): Boolean = name == "code" || name == "noparse" || name == "pre"

    private fun getTagName(content: String): String {
        if (content.isEmpty()) return ""
        val raw = if (content.startsWith("/")) content.substring(1).trim() else content
        val firstSpaceOrEq = raw.indexOfFirst { it == ' ' || it == '=' || it == '/' }
        return if (firstSpaceOrEq == -1) raw.lowercase() else raw.substring(0, firstSpaceOrEq).lowercase()
    }

    private fun isTagSupported(name: String): Boolean = supportedTags.contains(name)

    /**
     * 核心逻辑
     */
    private fun handleTag(raw: String, tagName: String, fullInput: String, nextPos: Int): Int {
        if (raw.startsWith("/")) {
            handleCloseTag(tagName)
            return 0
        }

        if (tagName == "*" && stack.lastOrNull()?.name == "*") {
            val last = stack.removeLast()
            handler.onCloseTag(last.name, isImplied = true)
        }

        val selfClosing = raw.endsWith("/")
        val (_, allAttrs) = parseTagAttributes(raw.let { if (selfClosing) it.dropLast(1) else it })

        // 1. 分类属性：哪些是独立标签，哪些是当前标签的参数
        val tagSequence = mutableListOf<Pair<String, Map<String, String>>>()
        val currentTagArgs = mutableMapOf<String, String>()

        if (allAttrs.containsKey("value")) {
            currentTagArgs["value"] = requireNotNull(allAttrs["value"])
        }

        allAttrs.forEach { (key, value) ->
            if (key != "value") {
                if (isTagSupported(key)) {
                    tagSequence.add(key to mapOf("value" to value))
                } else {
                    currentTagArgs[key] = value
                }
            }
        }

        // --- 处理 [img]url[/img] 或 [url]link[/url] 前瞻 ---
        var additionalConsumed = 0
        var isImgForward = false // 标记是否为 img 标签的前瞻

        if (!currentTagArgs.containsKey("value") && (tagName == "img" || tagName == "url")) {
            val closeTag = "[/$tagName]"
            val closeTagPos = fullInput.indexOf(closeTag, nextPos, ignoreCase = true)
            if (closeTagPos != -1) {
                val innerText = fullInput.substring(nextPos, closeTagPos)
                if (innerText.isNotEmpty()) {
                    currentTagArgs["value"] = innerText
                    additionalConsumed = innerText.length
                    if (tagName == "img") isImgForward = true
                }
            }
        }

        // 2. 依次触发 OpenTag
        handler.onOpenTagName(tagName)
        handler.onOpenTag(tagName, currentTagArgs, isImplied = false)
        stack.addLast(Tag(tagName))

        tagSequence.forEach { (subTag, subAttrs) ->
            handler.onOpenTagName(subTag)
            handler.onOpenTag(subTag, subAttrs, isImplied = true)
            stack.addLast(Tag(subTag, isSynthesized = true))
        }

        // 3. 处理内容和闭合
        if (additionalConsumed > 0) {
            // 修复逻辑：只有非 img 标签的前瞻才走 onText
            if (!isImgForward) {
                handler.onText(currentTagArgs["value"]!!)
            }

            // 闭合当前前瞻开启的所有标签（主标签 + 拆解标签）
            while (stack.isNotEmpty()) {
                val top = stack.last()
                handler.onCloseTag(top.name, isImplied = top.isSynthesized)
                stack.removeLast()
                if (top.name == tagName) break
            }
            additionalConsumed += "[/$tagName]".length
        } else if (selfClosing) {
            while (stack.isNotEmpty()) {
                val top = stack.last()
                handler.onCloseTag(top.name, isImplied = top.isSynthesized)
                stack.removeLast()
                if (top.name == tagName) break
            }
        }

        return additionalConsumed
    }

    private fun parseTagAttributes(content: String): Pair<String, Map<String, String>> {
        val attrs = mutableMapOf<String, String>()
        val firstSpaceOrEq = content.indexOfFirst { it == ' ' || it == '=' }
        if (firstSpaceOrEq == -1) return content.lowercase() to attrs

        val tagName = content.substring(0, firstSpaceOrEq).lowercase()
        var remaining = content.substring(firstSpaceOrEq)

        if (remaining.startsWith("=")) {
            val firstSpace = remaining.indexOf(' ')
            if (firstSpace == -1) {
                attrs["value"] = remaining.substring(1).trimQuotes()
                return tagName to attrs
            } else {
                attrs["value"] = remaining.substring(1, firstSpace).trimQuotes()
                remaining = remaining.substring(firstSpace)
            }
        }

        val attrRegex = Regex("""(\w+)=("[^"]*"|'[^']*'|[^ ]+)""")
        attrRegex.findAll(remaining).forEach { match ->
            attrs[match.groupValues[1].lowercase()] = match.groupValues[2].trimQuotes()
        }
        return tagName to attrs
    }

    private fun String.trimQuotes(): String =
        if (length >= 2 && ((startsWith("\"") && endsWith("\"")) || (startsWith("'") && endsWith("'")))) substring(1, length - 1) else this

    private fun handleCloseTag(name: String) {
        val index = stack.indexOfLast { it.name == name }
        if (index == -1) return
        while (stack.size - 1 > index) {
            val t = stack.removeLast()
            handler.onCloseTag(t.name, isImplied = true)
        }
        val t = stack.removeLast()
        handler.onCloseTag(t.name, isImplied = false)
    }

    private fun closeRemainingTags() {
        flushText()
        while (stack.isNotEmpty()) {
            val t = stack.removeLast()
            handler.onCloseTag(t.name, isImplied = true)
        }
    }

    private fun flushText() {
        if (textBuffer.isNotEmpty()) {
            handler.onText(textBuffer.toString())
            textBuffer.clear()
        }
    }
}