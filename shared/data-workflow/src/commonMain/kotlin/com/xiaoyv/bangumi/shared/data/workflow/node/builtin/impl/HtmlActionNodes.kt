package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.impl

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHtmlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionHtmlQueryOperation
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.ActionNodeCategory
import com.xiaoyv.bangumi.shared.data.workflow.node.ActionNodeDefinition
import com.xiaoyv.bangumi.shared.data.workflow.node.ActionNodeSpec
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.inPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.nextPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.valueResult
import com.xiaoyv.bangumi.shared.data.workflow.node.support.ActionTemplateResolver
import com.xiaoyv.bangumi.shared.data.workflow.node.support.string
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

/**
 * 使用 Ksoup 解析 HTML 文档和 CSS 选择器的内置节点。
 *
 * 节点始终接收原始 HTML 文本，输出 JSON 基础值或数组；不把 Ksoup 的 Document、Element
 * 等平台对象写入执行上下文，从而保证工作流定义、日志与跨平台执行结果可序列化。
 */
internal val htmlActionNodeDefinitions: List<ActionNodeDefinition> = listOf(
    htmlQueryDefinition(),
    htmlQueryAllDefinition(),
    htmlTitleDefinition(),
    htmlTextDefinition(),
    htmlMetaContentDefinition(),
    htmlLinksDefinition(),
    htmlAttributesDefinition(),
    htmlTableToJsonDefinition(),
)

private fun htmlQueryDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_QUERY,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(
            ActionHtmlConfigKey.HTML,
            ActionHtmlConfigKey.SELECTOR,
            ActionHtmlConfigKey.OPERATION,
            ActionHtmlConfigKey.OUTPUT_KEY,
        ),
    ),
    executor = { node, context ->
        val elements = parseDocument(node.config.string(ActionHtmlConfigKey.HTML), context)
            .select(resolve(node.config.string(ActionHtmlConfigKey.SELECTOR), context))
        node.valueResult(
            node.config.string(ActionHtmlConfigKey.OUTPUT_KEY),
            queryValue(
                elements = elements,
                operation = node.config.string(ActionHtmlConfigKey.OPERATION),
                attribute = node.config.string(ActionHtmlConfigKey.ATTRIBUTE),
            ),
        )
    },
)

private fun htmlQueryAllDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_QUERY_ALL,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(
            ActionHtmlConfigKey.HTML,
            ActionHtmlConfigKey.SELECTOR,
            ActionHtmlConfigKey.OPERATION,
            ActionHtmlConfigKey.OUTPUT_KEY,
        ),
    ),
    executor = { node, context ->
        val operation = node.config.string(ActionHtmlConfigKey.OPERATION)
        require(operation in htmlCollectionValueOperations) {
            "html.query_all 仅支持 ${htmlCollectionValueOperations.joinToString()} 操作"
        }
        val attribute = node.config.string(ActionHtmlConfigKey.ATTRIBUTE)
        val values = parseDocument(node.config.string(ActionHtmlConfigKey.HTML), context)
            .select(resolve(node.config.string(ActionHtmlConfigKey.SELECTOR), context))
            .map { element -> JsonPrimitive(elementValue(element, operation, attribute)) }
        node.valueResult(node.config.string(ActionHtmlConfigKey.OUTPUT_KEY), JsonArray(values))
    },
)

private fun htmlTitleDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_TITLE,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.HTML, ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        node.valueResult(
            node.config.string(ActionHtmlConfigKey.OUTPUT_KEY),
            JsonPrimitive(parseDocument(node.config.string(ActionHtmlConfigKey.HTML), context).title()),
        )
    },
)

private fun htmlTextDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_TEXT,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.HTML, ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        node.valueResult(
            node.config.string(ActionHtmlConfigKey.OUTPUT_KEY),
            JsonPrimitive(parseDocument(node.config.string(ActionHtmlConfigKey.HTML), context).text()),
        )
    },
)

private fun htmlMetaContentDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_META_CONTENT,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.HTML, ActionHtmlConfigKey.NAME, ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val name = resolve(node.config.string(ActionHtmlConfigKey.NAME), context)
        val content = parseDocument(node.config.string(ActionHtmlConfigKey.HTML), context)
            .select("meta")
            .firstOrNull { element ->
                element.attr(htmlMetaNameAttribute).equals(name, ignoreCase = true) ||
                        element.attr(htmlMetaPropertyAttribute).equals(name, ignoreCase = true)
            }
            ?.attr(htmlContentAttribute)
            .orEmpty()
        node.valueResult(node.config.string(ActionHtmlConfigKey.OUTPUT_KEY), JsonPrimitive(content))
    },
)

private fun htmlLinksDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_LINKS,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.HTML, ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val links = parseDocument(node.config.string(ActionHtmlConfigKey.HTML), context)
            .select(htmlLinkSelector)
            .map { element ->
                JsonObject(
                    mapOf(
                        htmlLinkHrefKey to JsonPrimitive(element.attr(htmlHrefAttribute)),
                        htmlLinkTextKey to JsonPrimitive(element.text()),
                        htmlLinkHtmlKey to JsonPrimitive(element.outerHtml()),
                    ),
                )
            }
        node.valueResult(node.config.string(ActionHtmlConfigKey.OUTPUT_KEY), JsonArray(links))
    },
)

private fun htmlAttributesDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_ATTRIBUTES,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.HTML, ActionHtmlConfigKey.SELECTOR, ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val element = parseDocument(node.config.string(ActionHtmlConfigKey.HTML), context)
            .select(resolve(node.config.string(ActionHtmlConfigKey.SELECTOR), context))
            .firstOrNull()
        val attrs = element?.attributes()?.associate { attr -> attr.key to JsonPrimitive(attr.value) }.orEmpty()
        node.valueResult(node.config.string(ActionHtmlConfigKey.OUTPUT_KEY), JsonObject(attrs))
    },
)

private fun htmlTableToJsonDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTML_TABLE_TO_JSON,
        category = ActionNodeCategory.HTML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionHtmlConfigKey.HTML, ActionHtmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val selector = node.config[ActionHtmlConfigKey.SELECTOR]?.let { resolve(it.jsonPrimitive.content, context) } ?: "table"
        val table = parseDocument(node.config.string(ActionHtmlConfigKey.HTML), context).select(selector).firstOrNull()
        val headers = table?.select("tr")?.firstOrNull()?.select("th, td")?.map { it.text() }.orEmpty()
        val rows = table?.select("tr")?.drop(1)?.map { tr ->
            val cells = tr.select("td, th").map { it.text() }
            val map = headers.zip(cells).associate { (h, c) -> h to JsonPrimitive(c) }
            JsonObject(map)
        }.orEmpty()
        node.valueResult(node.config.string(ActionHtmlConfigKey.OUTPUT_KEY), JsonArray(rows))
    },
)

private fun parseDocument(
    html: String,
    context: ActionExecutionContext,
) = Ksoup.parse(resolve(html, context))

private fun resolve(
    value: String,
    context: ActionExecutionContext,
): String = ActionTemplateResolver.resolveText(value, context)

private fun queryValue(
    elements: Iterable<Element>,
    operation: String,
    attribute: String,
): JsonElement = when (operation) {
    ActionHtmlQueryOperation.TEXT -> JsonPrimitive(elements.firstOrNull()?.text().orEmpty())
    ActionHtmlQueryOperation.HTML -> JsonPrimitive(elements.firstOrNull()?.html().orEmpty())
    ActionHtmlQueryOperation.OUTER_HTML -> JsonPrimitive(elements.firstOrNull()?.outerHtml().orEmpty())
    ActionHtmlQueryOperation.ATTRIBUTE -> JsonPrimitive(elements.firstOrNull()?.attr(attribute).orEmpty())
    ActionHtmlQueryOperation.COUNT -> JsonPrimitive(elements.count())
    ActionHtmlQueryOperation.EXISTS -> JsonPrimitive(elements.any())
    ActionHtmlQueryOperation.ALL_TEXT -> JsonPrimitive(elements.joinToString(htmlJoinSeparator) { it.text() })
    ActionHtmlQueryOperation.ALL_HTML -> JsonPrimitive(elements.joinToString(htmlJoinSeparator) { it.html() })
    ActionHtmlQueryOperation.ALL_OUTER_HTML -> JsonPrimitive(elements.joinToString(htmlJoinSeparator) { it.outerHtml() })
    else -> error("不支持的 HTML 查询操作：$operation")
}

private fun elementValue(
    element: Element,
    operation: String,
    attribute: String,
): String = when (operation) {
    ActionHtmlQueryOperation.TEXT -> element.text()
    ActionHtmlQueryOperation.HTML -> element.html()
    ActionHtmlQueryOperation.OUTER_HTML -> element.outerHtml()
    ActionHtmlQueryOperation.ATTRIBUTE -> element.attr(attribute)
    else -> error("不支持的 HTML 单元素查询操作：$operation")
}

private val htmlCollectionValueOperations = setOf(
    ActionHtmlQueryOperation.TEXT,
    ActionHtmlQueryOperation.HTML,
    ActionHtmlQueryOperation.OUTER_HTML,
    ActionHtmlQueryOperation.ATTRIBUTE,
)

private const val htmlJoinSeparator = "\n"
private const val htmlMetaNameAttribute = "name"
private const val htmlMetaPropertyAttribute = "property"
private const val htmlContentAttribute = "content"
private const val htmlLinkSelector = "a[href]"
private const val htmlHrefAttribute = "href"
private const val htmlLinkHrefKey = "href"
private const val htmlLinkTextKey = "text"
private const val htmlLinkHtmlKey = "html"
