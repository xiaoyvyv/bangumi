package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.parse

import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCsvConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
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
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * CSV 文本解析与反序列化内置节点。
 */
internal val csvActionNodeDefinitions: List<ActionNodeDefinition> = listOf(
    csvParseDefinition(),
    csvStringifyDefinition(),
)

private fun csvParseDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.CSV_PARSE,
        category = ActionNodeCategory.CSV,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionCsvConfigKey.TEXT, ActionCsvConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionCsvConfigKey.TEXT), context)
        val delimiter = node.config[ActionCsvConfigKey.DELIMITER]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) } ?: ","
        val hasHeader = node.config[ActionCsvConfigKey.HEADER_ROW]?.let { ActionTemplateResolver.resolveElement(it, context).jsonPrimitive.booleanOrNull } ?: true

        val lines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val parsedResult = if (lines.isEmpty()) {
            JsonArray(emptyList())
        } else if (hasHeader && lines.size > 1) {
            val headers = parseCsvLine(lines.first(), delimiter)
            val rows = lines.drop(1).map { line ->
                val cols = parseCsvLine(line, delimiter)
                JsonObject(headers.indices.associate { i ->
                    headers[i] to JsonPrimitive(cols.getOrElse(i) { "" })
                })
            }
            JsonArray(rows)
        } else {
            val rows = lines.map { line ->
                JsonArray(parseCsvLine(line, delimiter).map { JsonPrimitive(it) })
            }
            JsonArray(rows)
        }
        node.valueResult(node.config.string(ActionCsvConfigKey.OUTPUT_KEY), parsedResult)
    },
)

private fun csvStringifyDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.CSV_STRINGIFY,
        category = ActionNodeCategory.CSV,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionCsvConfigKey.ITEMS, ActionCsvConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val itemsArray = ActionTemplateResolver.resolveElement(node.config[ActionCsvConfigKey.ITEMS], context).jsonArray
        val delimiter = node.config[ActionCsvConfigKey.DELIMITER]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) } ?: ","
        val csvText = if (itemsArray.isEmpty()) "" else {
            val firstObj = itemsArray.first().jsonObject
            val headers = firstObj.keys.toList()
            val headerLine = headers.joinToString(delimiter) { escapeCsvCell(it, delimiter) }
            val dataLines = itemsArray.map { item ->
                val obj = item.jsonObject
                headers.joinToString(delimiter) { h -> escapeCsvCell(obj[h]?.jsonPrimitive?.content ?: "", delimiter) }
            }
            (listOf(headerLine) + dataLines).joinToString("\n")
        }
        node.valueResult(node.config.string(ActionCsvConfigKey.OUTPUT_KEY), JsonPrimitive(csvText))
    },
)

private fun parseCsvLine(line: String, delimiter: String): List<String> {
    return line.split(delimiter).map { cell ->
        cell.trim().removeSurrounding("\"").replace("\"\"", "\"")
    }
}

private fun escapeCsvCell(cell: String, delimiter: String): String {
    return if (cell.contains(delimiter) || cell.contains("\"") || cell.contains("\n")) {
        "\"" + cell.replace("\"", "\"\"") + "\""
    } else {
        cell
    }
}
