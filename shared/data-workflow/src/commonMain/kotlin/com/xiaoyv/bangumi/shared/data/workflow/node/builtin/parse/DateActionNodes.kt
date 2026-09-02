package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.parse

import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDateConfigKey
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
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

/**
 * 日期与时间处理内置节点。
 */
internal val dateActionNodeDefinitions: List<ActionNodeDefinition> = listOf(
    dateNowDefinition(),
    dateFormatDefinition(),
    dateParseDefinition(),
    dateAddDefinition(),
    dateSubtractDefinition(),
    dateDiffDefinition(),
    dateRelativeTimeDefinition(),
    dateGetComponentDefinition(),
)

private fun dateNowDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.DATE_NOW,
        category = ActionNodeCategory.DATE,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionDateConfigKey.OUTPUT_KEY),
    ),
    executor = { node, _ ->
        node.valueResult(
            node.config.string(ActionDateConfigKey.OUTPUT_KEY),
            JsonPrimitive(kotlin.time.Clock.System.now().toEpochMilliseconds()),
        )
    },
)

private fun dateFormatDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.DATE_FORMAT,
        category = ActionNodeCategory.DATE,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionDateConfigKey.TIMESTAMP, ActionDateConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val timestamp = ActionTemplateResolver.resolveElement(node.config[ActionDateConfigKey.TIMESTAMP], context).jsonPrimitive.content.toLong()
        val isoString = kotlin.time.Instant.fromEpochMilliseconds(timestamp).toString()
        node.valueResult(node.config.string(ActionDateConfigKey.OUTPUT_KEY), JsonPrimitive(isoString))
    },
)

private fun dateParseDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.DATE_PARSE,
        category = ActionNodeCategory.DATE,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionDateConfigKey.TEXT, ActionDateConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionDateConfigKey.TEXT), context)
        val epochMs = kotlin.time.Instant.parse(text).toEpochMilliseconds()
        node.valueResult(node.config.string(ActionDateConfigKey.OUTPUT_KEY), JsonPrimitive(epochMs))
    },
)

private fun dateAddDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.DATE_ADD,
        category = ActionNodeCategory.DATE,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionDateConfigKey.TIMESTAMP, ActionDateConfigKey.COUNT, ActionDateConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val timestamp = ActionTemplateResolver.resolveElement(node.config[ActionDateConfigKey.TIMESTAMP], context).jsonPrimitive.content.toLong()
        val count = ActionTemplateResolver.resolveElement(node.config[ActionDateConfigKey.COUNT], context).jsonPrimitive.content.toLong()
        val unit = node.config[ActionDateConfigKey.UNIT]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) } ?: "milliseconds"
        val offsetMs = resolveUnitMillis(unit, count)
        node.valueResult(node.config.string(ActionDateConfigKey.OUTPUT_KEY), JsonPrimitive(timestamp + offsetMs))
    },
)

private fun dateSubtractDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.DATE_SUBTRACT,
        category = ActionNodeCategory.DATE,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionDateConfigKey.TIMESTAMP, ActionDateConfigKey.COUNT, ActionDateConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val timestamp = ActionTemplateResolver.resolveElement(node.config[ActionDateConfigKey.TIMESTAMP], context).jsonPrimitive.content.toLong()
        val count = ActionTemplateResolver.resolveElement(node.config[ActionDateConfigKey.COUNT], context).jsonPrimitive.content.toLong()
        val unit = node.config[ActionDateConfigKey.UNIT]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) } ?: "milliseconds"
        val offsetMs = resolveUnitMillis(unit, count)
        node.valueResult(node.config.string(ActionDateConfigKey.OUTPUT_KEY), JsonPrimitive(timestamp - offsetMs))
    },
)

private fun dateDiffDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.DATE_DIFF,
        category = ActionNodeCategory.DATE,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionDateConfigKey.TIMESTAMP_LEFT, ActionDateConfigKey.TIMESTAMP_RIGHT, ActionDateConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val left = ActionTemplateResolver.resolveElement(node.config[ActionDateConfigKey.TIMESTAMP_LEFT], context).jsonPrimitive.content.toLong()
        val right = ActionTemplateResolver.resolveElement(node.config[ActionDateConfigKey.TIMESTAMP_RIGHT], context).jsonPrimitive.content.toLong()
        val unit = node.config[ActionDateConfigKey.UNIT]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) } ?: "milliseconds"
        val diffMs = left - right
        val result = convertMillisToUnit(diffMs, unit)
        node.valueResult(node.config.string(ActionDateConfigKey.OUTPUT_KEY), JsonPrimitive(result))
    },
)

private fun dateRelativeTimeDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.DATE_RELATIVE_TIME,
        category = ActionNodeCategory.DATE,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionDateConfigKey.TIMESTAMP, ActionDateConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val timestamp = ActionTemplateResolver.resolveElement(node.config[ActionDateConfigKey.TIMESTAMP], context).jsonPrimitive.content.toLong()
        val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
        val diffSeconds = (now - timestamp) / 1000
        val text = when {
            diffSeconds == 0L -> "刚刚"
            diffSeconds in 1..59 -> "${diffSeconds}秒前"
            diffSeconds in -59..-1 -> "${-diffSeconds}秒后"
            diffSeconds in 60..3599 -> "${diffSeconds / 60}分钟前"
            diffSeconds in -3599..-60 -> "${-diffSeconds / 60}分钟后"
            diffSeconds in 3600..86399 -> "${diffSeconds / 3600}小时前"
            diffSeconds in -86399..-3600 -> "${-diffSeconds / 3600}小时后"
            diffSeconds >= 86400 -> "${diffSeconds / 86400}天前"
            else -> "${-diffSeconds / 86400}天后"
        }
        node.valueResult(node.config.string(ActionDateConfigKey.OUTPUT_KEY), JsonPrimitive(text))
    },
)

private fun dateGetComponentDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.DATE_GET_COMPONENT,
        category = ActionNodeCategory.DATE,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionDateConfigKey.TIMESTAMP, ActionDateConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val timestamp = ActionTemplateResolver.resolveElement(node.config[ActionDateConfigKey.TIMESTAMP], context).jsonPrimitive.content.toLong()
        val isoStr = kotlin.time.Instant.fromEpochMilliseconds(timestamp).toString()
        val regex = Regex("""^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})""")
        val match = regex.find(isoStr)
        val year = match?.groupValues?.get(1)?.toIntOrNull() ?: 1970
        val month = match?.groupValues?.get(2)?.toIntOrNull() ?: 1
        val day = match?.groupValues?.get(3)?.toIntOrNull() ?: 1
        val hour = match?.groupValues?.get(4)?.toIntOrNull() ?: 0
        val minute = match?.groupValues?.get(5)?.toIntOrNull() ?: 0
        val second = match?.groupValues?.get(6)?.toIntOrNull() ?: 0
        val result = kotlinx.serialization.json.buildJsonObject {
            put("year", JsonPrimitive(year))
            put("month", JsonPrimitive(month))
            put("day", JsonPrimitive(day))
            put("hour", JsonPrimitive(hour))
            put("minute", JsonPrimitive(minute))
            put("second", JsonPrimitive(second))
            put("iso", JsonPrimitive(isoStr))
        }
        node.valueResult(node.config.string(ActionDateConfigKey.OUTPUT_KEY), result)
    },
)

private fun resolveUnitMillis(unit: String, count: Long): Long {
    return when (unit.lowercase()) {
        "days", "day", "d" -> count * 86_400_000L
        "hours", "hour", "h" -> count * 3_600_000L
        "minutes", "minute", "m" -> count * 60_000L
        "seconds", "second", "s" -> count * 1_000L
        else -> count
    }
}

private fun convertMillisToUnit(millis: Long, unit: String): Double {
    return when (unit.lowercase()) {
        "days", "day", "d" -> millis.toDouble() / 86_400_000.0
        "hours", "hour", "h" -> millis.toDouble() / 3_600_000.0
        "minutes", "minute", "m" -> millis.toDouble() / 60_000.0
        "seconds", "second", "s" -> millis.toDouble() / 1_000.0
        else -> millis.toDouble()
    }
}
