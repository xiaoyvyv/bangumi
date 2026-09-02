package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.parse

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.parser.Parser
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionXmlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.inPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.nextPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.valueResult
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeCategory
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeDefinition
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeSpec
import com.xiaoyv.bangumi.shared.data.workflow.node.core.string
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionTemplateResolver
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * XML / RSS 转换内置节点。
 *
 * 使用 Ksoup.parse(..., Parser.xmlParser()) 解析 XML / RSS 文档，彻底替代正则匹配。
 */
internal val xmlActionNodeDefinitions: List<ActionNodeDefinition> = listOf(
    xmlParseDefinition(),
    xmlStringifyDefinition(),
)

private fun xmlParseDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.XML_PARSE,
        category = ActionNodeCategory.XML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionXmlConfigKey.TEXT, ActionXmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val xmlText = ActionTemplateResolver.resolveText(node.config.string(ActionXmlConfigKey.TEXT), context)
        val parsedJson = parseXmlToJson(xmlText)
        node.valueResult(node.config.string(ActionXmlConfigKey.OUTPUT_KEY), parsedJson)
    },
)

private fun xmlStringifyDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.XML_STRINGIFY,
        category = ActionNodeCategory.XML,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionXmlConfigKey.DATA, ActionXmlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val dataObj = ActionTemplateResolver.resolveElement(node.config[ActionXmlConfigKey.DATA], context)
        val xmlString = jsonToXml(dataObj)
        node.valueResult(node.config.string(ActionXmlConfigKey.OUTPUT_KEY), JsonPrimitive(xmlString))
    },
)

private fun parseXmlToJson(xml: String): JsonElement {
    if (xml.isBlank()) return JsonObject(emptyMap())
    val doc = Ksoup.parse(html = xml, parser = Parser.xmlParser())
    val rootElement = doc.children().firstOrNull()
        ?: return JsonObject(mapOf("root" to JsonPrimitive(xml.trim())))
    return JsonObject(mapOf(rootElement.tagName() to elementToJson(rootElement)))
}

private fun elementToJson(element: Element): JsonElement {
    val children = element.children()
    if (children.isEmpty()) {
        return JsonPrimitive(element.text().trim())
    }

    val resultMap = mutableMapOf<String, MutableList<JsonElement>>()
    for (child in children) {
        val childTag = child.tagName()
        val childElement = elementToJson(child)
        resultMap.getOrPut(childTag) { mutableListOf() }.add(childElement)
    }

    val finalMap = resultMap.mapValues { (_, list) ->
        if (list.size == 1) list.first() else JsonArray(list)
    }
    return JsonObject(finalMap)
}

private fun jsonToXml(element: JsonElement, tagName: String = "root"): String {
    return when (element) {
        is JsonObject -> {
            val inner = element.entries.joinToString("") { (k, v) -> jsonToXml(v, k) }
            "<$tagName>$inner</$tagName>"
        }

        is JsonArray -> {
            element.joinToString("") { jsonToXml(it, tagName) }
        }

        is JsonPrimitive -> {
            "<$tagName>${element.content}</$tagName>"
        }
    }
}
