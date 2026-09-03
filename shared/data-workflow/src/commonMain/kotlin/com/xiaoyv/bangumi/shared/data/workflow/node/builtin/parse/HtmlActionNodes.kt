package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.parse

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.select.Elements
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHtmlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.inPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.nextPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.valueResult
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeCategory
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeDefinition
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeSpec
import com.xiaoyv.bangumi.shared.data.workflow.node.core.int
import com.xiaoyv.bangumi.shared.data.workflow.node.core.string
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionTemplateResolver
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

/**
 * 解析单段 HTML 文本为 [Element]。
 * 若为完整 HTML 文档则返回 Document 根元素；若为片段则返回 body 下的首个子元素或 body 自身。
 */
fun parseHtmlSnippet(html: String): Element {
    val trimmed = html.trim()
    val doc = Ksoup.parse(trimmed)
    if (trimmed.startsWith("<!doctype", ignoreCase = true) || trimmed.startsWith("<html", ignoreCase = true)) {
        return doc
    }
    return doc.body().children().firstOrNull()
        ?: doc.head().children().firstOrNull()
        ?: doc.children().firstOrNull()
        ?: doc
}

/**
 * 解析输入源为 [Elements] 集合。
 * 支持传入 HTML 字符串数组或单个 HTML 字符串。
 */
fun resolveHtmlElements(node: ActionNode, context: ActionExecutionContext): Elements {
    val raw = node.config[ActionHtmlConfigKey.SOURCE]
        ?: node.config[ActionHtmlConfigKey.HTML]
        ?: node.config[ActionHtmlConfigKey.ELEMENT]
        ?: node.config[ActionHtmlConfigKey.ELEMENTS]
        ?: error("${node.type} 缺少 source 或 html 配置")
    return when (val resolved = ActionTemplateResolver.resolveElement(raw, context)) {
        is JsonArray -> {
            val list = resolved.mapNotNull { item ->
                if (item is JsonPrimitive && item.isString) parseHtmlSnippet(item.content) else null
            }
            Elements(list)
        }

        is JsonPrimitive -> {
            if (!resolved.isString) error("${node.type} 节点的输入必须为 HTML 字符串或字符串数组")
            val trimmed = resolved.content.trim()
            val doc = Ksoup.parse(trimmed)
            val bodyChildren = doc.body().children()
            if (bodyChildren.isNotEmpty()) return bodyChildren
            val headChildren = doc.head().children()
            if (headChildren.isNotEmpty()) return headChildren
            return if (doc.children().isNotEmpty()) doc.children() else Elements(listOf(doc))
        }

        else -> error("${node.type} 节点的输入必须为 HTML 字符串或字符串数组，当前类型无效: $resolved")
    }
}

/**
 * 多态操作目标，支持单个 HTML 元素或元素集合。
 */
sealed interface HtmlTarget {
    class Single(val element: Element) : HtmlTarget
    class Multiple(val elements: Elements) : HtmlTarget
}

/**
 * 从节点配置中解析多态目标 [HtmlTarget]。
 * 支持单个 HTML 字符串或 HTML 字符串数组。
 */
fun resolveHtmlTarget(node: ActionNode, context: ActionExecutionContext): HtmlTarget {
    val raw = node.config[ActionHtmlConfigKey.SOURCE]
        ?: node.config[ActionHtmlConfigKey.HTML]
        ?: node.config[ActionHtmlConfigKey.ELEMENT]
        ?: node.config[ActionHtmlConfigKey.ELEMENTS]
        ?: error("${node.type} 缺少 source 或 html 配置")
    return when (val resolved = ActionTemplateResolver.resolveElement(raw, context)) {
        is JsonArray -> {
            val list = resolved.mapNotNull { item ->
                if (item is JsonPrimitive && item.isString) parseHtmlSnippet(item.content) else null
            }
            HtmlTarget.Multiple(Elements(list))
        }

        is JsonPrimitive -> {
            if (!resolved.isString) error("${node.type} 节点的输入必须为 HTML 字符串或字符串数组")
            HtmlTarget.Single(parseHtmlSnippet(resolved.content))
        }

        else -> error("${node.type} 节点的输入必须为 HTML 字符串或字符串数组，当前类型无效: $resolved")
    }
}

/**
 * 基于纯 HTML 源码流转的 Ksoup 节点体系。
 */
internal val htmlActionNodeDefinitions: List<ActionNodeDefinition> = listOf(
    htmlParseDefinition(),
    htmlSelectDefinition(),
    htmlSelectFirstDefinition(),
    htmlAttrDefinition(),
    htmlTagDefinition(),
    htmlTextDefinition(),
    htmlDataDefinition(),
    htmlValueDefinition(),
    htmlIdDefinition(),
    htmlHtmlDefinition(),
    htmlOuterHtmlDefinition(),
    htmlHasClassDefinition(),
    htmlMapDefinition(),
    htmlFirstDefinition(),
    htmlLastDefinition(),
    htmlGetDefinition(),
    htmlSizeDefinition(),
    htmlRemoveDefinition(),
    htmlParentDefinition(),
    htmlChildrenDefinition(),
    htmlTableToJsonDefinition(),
)

/**
 * 解析并格式化/清洗 HTML 源码。
 */
private fun htmlParseDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_PARSE,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val rawHtml = node.config[ActionHtmlConfigKey.HTML]
            ?: node.config[ActionHtmlConfigKey.SOURCE]
            ?: error("html.parse 缺少 html 或 source 配置")
        val resolved = ActionTemplateResolver.resolveElement(rawHtml, context)
        if (resolved !is JsonPrimitive || !resolved.isString) {
            error("html.parse 节点的输入必须为字符串，当前输入: $resolved")
        }
        val doc = Ksoup.parse(resolved.content)
        node.valueResult(node.config.string(ActionHtmlConfigKey.OUTPUT_KEY), JsonPrimitive(doc.outerHtml()))
    },
)

/**
 * CSS 选择器提取多项元素，输出每个匹配元素的 outerHtml 字符串数组。
 */
private fun htmlSelectDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_SELECT,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.SELECTOR, ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val target = resolveHtmlTarget(node, context)
        val selector = ActionTemplateResolver.resolveText(node.config.string(ActionHtmlConfigKey.SELECTOR), context)
        val elements = when (target) {
            is HtmlTarget.Single -> target.element.select(selector)
            is HtmlTarget.Multiple -> target.elements.select(selector)
        }
        val resultList = elements.map { JsonPrimitive(it.outerHtml()) }
        node.valueResult(node.config.string(ActionHtmlConfigKey.OUTPUT_KEY), JsonArray(resultList))
    },
)

/**
 * CSS 选择器提取首个匹配元素，输出匹配元素的 outerHtml 字符串（若未匹配输出 null）。
 */
private fun htmlSelectFirstDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_SELECT_FIRST,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.SELECTOR, ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val target = resolveHtmlTarget(node, context)
        val selector = ActionTemplateResolver.resolveText(node.config.string(ActionHtmlConfigKey.SELECTOR), context)
        val element = when (target) {
            is HtmlTarget.Single -> target.element.selectFirst(selector)
            is HtmlTarget.Multiple -> target.elements.select(selector).firstOrNull()
        }
        node.valueResult(
            node.config.string(ActionHtmlConfigKey.OUTPUT_KEY),
            element?.let { JsonPrimitive(it.outerHtml()) } ?: JsonNull,
        )
    },
)

/**
 * 提取标签属性值。
 */
private fun htmlAttrDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_ATTR,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.ATTRIBUTE, ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val target = resolveHtmlTarget(node, context)
        val attribute = ActionTemplateResolver.resolveText(node.config.string(ActionHtmlConfigKey.ATTRIBUTE), context)
        require(attribute.isNotBlank()) { "html.attr 的 attribute 不能为空" }
        val attrValue = when (target) {
            is HtmlTarget.Single -> target.element.attr(attribute)
            is HtmlTarget.Multiple -> target.elements.attr(attribute)
        }
        node.valueResult(node.config.string(ActionHtmlConfigKey.OUTPUT_KEY), JsonPrimitive(attrValue))
    },
)

/**
 * 提取标签名。
 */
private fun htmlTagDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_TAG,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val tagName = when (val target = resolveHtmlTarget(node, context)) {
            is HtmlTarget.Single -> target.element.tagName()
            is HtmlTarget.Multiple -> target.elements.firstOrNull()?.tagName().orEmpty()
        }
        node.valueResult(node.config.string(ActionHtmlConfigKey.OUTPUT_KEY), JsonPrimitive(tagName))
    },
)

/**
 * 提取纯文本内容。
 */
private fun htmlTextDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_TEXT,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val textValue = when (val target = resolveHtmlTarget(node, context)) {
            is HtmlTarget.Single -> target.element.text()
            is HtmlTarget.Multiple -> target.elements.text()
        }
        node.valueResult(node.config.string(ActionHtmlConfigKey.OUTPUT_KEY), JsonPrimitive(textValue))
    },
)

/**
 * 提取数据内容（如 script/style 内部数据）。
 */
private fun htmlDataDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_DATA,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val dataValue = when (val target = resolveHtmlTarget(node, context)) {
            is HtmlTarget.Single -> target.element.data()
            is HtmlTarget.Multiple -> target.elements.firstOrNull()?.data().orEmpty()
        }
        node.valueResult(node.config.string(ActionHtmlConfigKey.OUTPUT_KEY), JsonPrimitive(dataValue))
    },
)

/**
 * 提取输入控件值（value）。
 */
private fun htmlValueDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_VALUE,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val valueStr = when (val target = resolveHtmlTarget(node, context)) {
            is HtmlTarget.Single -> target.element.value()
            is HtmlTarget.Multiple -> target.elements.firstOrNull()?.value().orEmpty()
        }
        node.valueResult(node.config.string(ActionHtmlConfigKey.OUTPUT_KEY), JsonPrimitive(valueStr))
    },
)

/**
 * 提取元素 ID 属性。
 */
private fun htmlIdDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_ID,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val idValue = when (val target = resolveHtmlTarget(node, context)) {
            is HtmlTarget.Single -> target.element.id()
            is HtmlTarget.Multiple -> target.elements.firstOrNull()?.id().orEmpty()
        }
        node.valueResult(node.config.string(ActionHtmlConfigKey.OUTPUT_KEY), JsonPrimitive(idValue))
    },
)

/**
 * 提取内部 HTML (innerHTML)。
 */
private fun htmlHtmlDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_HTML,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val htmlValue = when (val target = resolveHtmlTarget(node, context)) {
            is HtmlTarget.Single -> target.element.html()
            is HtmlTarget.Multiple -> target.elements.firstOrNull()?.html().orEmpty()
        }
        node.valueResult(node.config.string(ActionHtmlConfigKey.OUTPUT_KEY), JsonPrimitive(htmlValue))
    },
)

/**
 * 提取完整外部 HTML (outerHTML)。
 */
private fun htmlOuterHtmlDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_OUTER_HTML,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val outerHtmlValue = when (val target = resolveHtmlTarget(node, context)) {
            is HtmlTarget.Single -> target.element.outerHtml()
            is HtmlTarget.Multiple -> target.elements.outerHtml()
        }
        node.valueResult(node.config.string(ActionHtmlConfigKey.OUTPUT_KEY), JsonPrimitive(outerHtmlValue))
    },
)

/**
 * 检查是否包含指定 CSS 类名。
 */
private fun htmlHasClassDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_HAS_CLASS,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.CLASS_NAME, ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val target = resolveHtmlTarget(node, context)
        val className = ActionTemplateResolver.resolveText(node.config.string(ActionHtmlConfigKey.CLASS_NAME), context)
        require(className.isNotBlank()) { "html.has_class 的 className 不能为空" }
        val hasClass = when (target) {
            is HtmlTarget.Single -> target.element.hasClass(className)
            is HtmlTarget.Multiple -> target.elements.hasClass(className)
        }
        node.valueResult(node.config.string(ActionHtmlConfigKey.OUTPUT_KEY), JsonPrimitive(hasClass))
    },
)

/**
 * 集合映射，遍历 HTML 集合批量提取指定字段（如 text、attr 等）。
 */
private fun htmlMapDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_MAP,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.OPERATION, ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val elements = resolveHtmlElements(node, context)
        val op = ActionTemplateResolver.resolveText(node.config.string(ActionHtmlConfigKey.OPERATION), context).lowercase()
        val attrName = node.config[ActionHtmlConfigKey.ATTRIBUTE]?.let {
            ActionTemplateResolver.resolveText(node.config.string(ActionHtmlConfigKey.ATTRIBUTE), context)
        }

        val mapped = elements.map { el ->
            when (op) {
                "text" -> JsonPrimitive(el.text())
                "attr" -> {
                    require(!attrName.isNullOrBlank()) { "html.map 执行 attr 操作时必须指定 attribute 参数" }
                    JsonPrimitive(el.attr(attrName))
                }

                "tag" -> JsonPrimitive(el.tagName())
                "html", "inner_html" -> JsonPrimitive(el.html())
                "outer_html" -> JsonPrimitive(el.outerHtml())
                "id" -> JsonPrimitive(el.id())
                "data" -> JsonPrimitive(el.data())
                "value", "val" -> JsonPrimitive(el.value())
                else -> error("html.map 不支持的操作: $op")
            }
        }
        node.valueResult(node.config.string(ActionHtmlConfigKey.OUTPUT_KEY), JsonArray(mapped))
    },
)

/**
 * 提取 HTML 集合的首项 outerHtml。
 */
private fun htmlFirstDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_FIRST,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val elements = resolveHtmlElements(node, context)
        val first = elements.firstOrNull()?.let { JsonPrimitive(it.outerHtml()) } ?: JsonNull
        node.valueResult(node.config.string(ActionHtmlConfigKey.OUTPUT_KEY), first)
    },
)

/**
 * 提取 HTML 集合的末项 outerHtml。
 */
private fun htmlLastDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_LAST,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val elements = resolveHtmlElements(node, context)
        val last = elements.lastOrNull()?.let { JsonPrimitive(it.outerHtml()) } ?: JsonNull
        node.valueResult(node.config.string(ActionHtmlConfigKey.OUTPUT_KEY), last)
    },
)

/**
 * 按 0-based 索引提取 HTML 集合中的元素 outerHtml。
 */
private fun htmlGetDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_GET,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.INDEX, ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val elements = resolveHtmlElements(node, context)
        val index = node.config.int(ActionHtmlConfigKey.INDEX)
        val element = elements.getOrNull(index)?.let { JsonPrimitive(it.outerHtml()) } ?: JsonNull
        node.valueResult(node.config.string(ActionHtmlConfigKey.OUTPUT_KEY), element)
    },
)

/**
 * 统计 HTML 集合中包含的元素数量。
 */
private fun htmlSizeDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_SIZE,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val elements = resolveHtmlElements(node, context)
        node.valueResult(node.config.string(ActionHtmlConfigKey.OUTPUT_KEY), JsonPrimitive(elements.size))
    },
)

/**
 * 剔除匹配 CSS 选择器的标签，返回清洗后的 HTML 源码。
 */
private fun htmlRemoveDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_REMOVE,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.SELECTOR, ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val target = resolveHtmlTarget(node, context)
        val selector = ActionTemplateResolver.resolveText(node.config.string(ActionHtmlConfigKey.SELECTOR), context)
        val resultHtml = when (target) {
            is HtmlTarget.Single -> {
                target.element.select(selector).remove()
                target.element.outerHtml()
            }

            is HtmlTarget.Multiple -> {
                target.elements.select(selector).remove()
                target.elements.outerHtml()
            }
        }
        node.valueResult(node.config.string(ActionHtmlConfigKey.OUTPUT_KEY), JsonPrimitive(resultHtml))
    },
)

/**
 * 提取元素的直接父级 outerHtml。
 */
private fun htmlParentDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_PARENT,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val parent = when (val target = resolveHtmlTarget(node, context)) {
            is HtmlTarget.Single -> target.element.parent()
            is HtmlTarget.Multiple -> target.elements.firstOrNull()?.parent()
        }
        node.valueResult(
            node.config.string(ActionHtmlConfigKey.OUTPUT_KEY),
            parent?.let { JsonPrimitive(it.outerHtml()) } ?: JsonNull,
        )
    },
)

/**
 * 提取元素的直接子元素 outerHtml 列表。
 */
private fun htmlChildrenDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_CHILDREN,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val children = when (val target = resolveHtmlTarget(node, context)) {
            is HtmlTarget.Single -> target.element.children()
            is HtmlTarget.Multiple -> Elements(target.elements.flatMap { it.children() })
        }
        val resultList = children.map { JsonPrimitive(it.outerHtml()) }
        node.valueResult(node.config.string(ActionHtmlConfigKey.OUTPUT_KEY), JsonArray(resultList))
    },
)

/**
 * 将 HTML <table> 表格转换为 JSON 对象数组。
 */
private fun htmlTableToJsonDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_TABLE_TO_JSON,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val rawHtml = node.config[ActionHtmlConfigKey.HTML]
            ?: node.config[ActionHtmlConfigKey.SOURCE]
            ?: error("html.table_to_json 缺少 html 或 source 配置")
        val resolved = ActionTemplateResolver.resolveElement(rawHtml, context)
        val htmlContent = if (resolved is JsonPrimitive && resolved.isString) {
            resolved.content
        } else {
            error("html.table_to_json 节点的输入必须为 HTML 字符串")
        }

        val doc = Ksoup.parse(htmlContent)
        val selector = node.config[ActionHtmlConfigKey.SELECTOR]?.let {
            ActionTemplateResolver.resolveText(node.config.string(ActionHtmlConfigKey.SELECTOR), context)
        } ?: "table"

        val table = doc.selectFirst(selector) ?: error("未找到匹配选择器 '$selector' 的表格")
        val headers = table.select("th").map { it.text().trim() }
        val rows = table.select("tr").filter { it.select("th").isEmpty() }

        val jsonRows = rows.map { row ->
            val cells = row.select("td").map { it.text().trim() }
            buildJsonObject {
                headers.forEachIndexed { index, header ->
                    put(header, JsonPrimitive(cells.getOrElse(index) { "" }))
                }
            }
        }
        node.valueResult(node.config.string(ActionHtmlConfigKey.OUTPUT_KEY), JsonArray(jsonRows))
    },
)
