package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.data

import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionNodeExecutionResult
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey
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
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * 文本、正则和 URL 编解码节点。
 */
internal val textActionNodeDefinitions = listOf(
    textLengthDefinition(),
    textTrimDefinition(),
    textLowercaseDefinition(),
    textUppercaseDefinition(),
    textCapitalizeDefinition(),
    textRepeatDefinition(),
    textReverseDefinition(),
    textIndexOfDefinition(),
    textTemplateDefinition(),
    textSplitDefinition(),
    textRegexMatchDefinition(),
    textSubstringDefinition(),
    textSubstringBeforeDefinition(),
    textSubstringAfterDefinition(),
    textReplaceDefinition(),
    textReplaceRegexDefinition(),
    textJoinDefinition(),
    textMatchAllDefinition(),
    textPadDefinition(),
    textFormatNumberDefinition(),
    textContainsDefinition(),
    textStartsWithDefinition(),
    textEndsWithDefinition(),
    textSlugifyDefinition(),
    textTruncateDefinition(),
)

private fun textTrimDefinition() = textNode(ActionNodeType.TEXT_TRIM) { it.trim() }
private fun textLowercaseDefinition() = textNode(ActionNodeType.TEXT_LOWERCASE) { it.lowercase() }
private fun textUppercaseDefinition() = textNode(ActionNodeType.TEXT_UPPERCASE) { it.uppercase() }
private fun textReplaceDefinition() = createTextReplaceDefinition(isRegex = false)
private fun textReplaceRegexDefinition() = createTextReplaceDefinition(isRegex = true)

private fun textNode(type: String, transform: (String) -> String) = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = type,
        category = ActionNodeCategory.TEXT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionTextConfigKey.TEXT, ActionTextConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.TEXT), context)
        node.valueResult(node.config.string(ActionTextConfigKey.OUTPUT_KEY), JsonPrimitive(transform(text)))
    },
)

private fun textLengthDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.TEXT_LENGTH,
        category = ActionNodeCategory.TEXT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionTextConfigKey.TEXT, ActionTextConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context ->
        node.valueResult(
            node.config.string(ActionTextConfigKey.OUTPUT_KEY),
            JsonPrimitive(ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.TEXT), context).length)
        )
    },
)

/**
 * 创建字符串分隔节点，支持字面量与正则表达式分隔符。
 */
private fun textSplitDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.TEXT_SPLIT,
        category = ActionNodeCategory.TEXT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionTextConfigKey.TEXT, ActionTextConfigKey.DELIMITER, ActionTextConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.TEXT), context)
        val delimiter = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.DELIMITER), context)
        val limit = node.config[ActionTextConfigKey.LIMIT]?.let { ActionTemplateResolver.resolveElement(it, context).jsonPrimitive.intOrNull } ?: 0
        val regex = node.config[ActionTextConfigKey.IS_REGEX]?.let { ActionTemplateResolver.resolveElement(it, context).jsonPrimitive.booleanOrNull } == true
        val result = if (regex) Regex(delimiter).split(text, limit) else text.split(delimiter, limit = limit)
        node.valueResult(node.config.string(ActionTextConfigKey.OUTPUT_KEY), JsonArray(result.map(::JsonPrimitive)))
    },
)

/**
 * 创建正则匹配节点，输出完整匹配及捕获组数组。
 */
private fun textRegexMatchDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.TEXT_REGEX_MATCH,
        category = ActionNodeCategory.TEXT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionTextConfigKey.TEXT, ActionTextConfigKey.PATTERN, ActionTextConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context ->
        val match = Regex(ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.PATTERN), context)).find(
            ActionTemplateResolver.resolveText(
                node.config.string(ActionTextConfigKey.TEXT), context
            )
        )
        val groups = JsonArray(match?.groupValues.orEmpty().map(::JsonPrimitive))
        val key = node.config.string(ActionTextConfigKey.OUTPUT_KEY)
        ActionNodeExecutionResult(
            outputPortId = ActionControlPortId.NEXT,
            output = kotlinx.serialization.json.buildJsonObject {
                put(ActionTextConfigKey.MATCHED, JsonPrimitive(match != null))
                put(ActionTextConfigKey.GROUP_VALUES, groups)
                if (key.isNotBlank()) {
                    put(key, groups)
                }
            },
        )
    },
)

private fun textSubstringDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.TEXT_SUBSTRING,
        category = ActionNodeCategory.TEXT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionTextConfigKey.TEXT, ActionTextConfigKey.START_INDEX, ActionTextConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.TEXT), context)
        val start = ActionTemplateResolver.resolveElement(node.config[ActionTextConfigKey.START_INDEX], context).jsonPrimitive.intOrNull ?: error("startIndex 必须是整数")
        val end = node.config[ActionTextConfigKey.END_INDEX]?.let { ActionTemplateResolver.resolveElement(it, context).jsonPrimitive.intOrNull } ?: text.length
        require(start in 0..end && end <= text.length) { "文本截取索引范围无效" }
        node.valueResult(node.config.string(ActionTextConfigKey.OUTPUT_KEY), JsonPrimitive(text.substring(start, end)))
    },
)

private fun textSubstringBeforeDefinition() =
    delimiterSubstringDefinition(ActionNodeType.TEXT_SUBSTRING_BEFORE) { text, delimiter, missing ->
        text.substringBefore(delimiter, missing)
    }

private fun textSubstringAfterDefinition() =
    delimiterSubstringDefinition(ActionNodeType.TEXT_SUBSTRING_AFTER) { text, delimiter, missing ->
        text.substringAfter(delimiter, missing)
    }

private fun delimiterSubstringDefinition(type: String, operation: (String, String, String) -> String) = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = type,
        category = ActionNodeCategory.TEXT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionTextConfigKey.TEXT, ActionTextConfigKey.DELIMITER, ActionTextConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.TEXT), context)
        val delimiter = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.DELIMITER), context)
        val missing = node.config[ActionTextConfigKey.MISSING_DELIMITER_VALUE]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) } ?: text
        node.valueResult(node.config.string(ActionTextConfigKey.OUTPUT_KEY), JsonPrimitive(operation(text, delimiter, missing)))
    },
)

private fun createTextReplaceDefinition(isRegex: Boolean) = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = if (isRegex) ActionNodeType.TEXT_REPLACE_REGEX else ActionNodeType.TEXT_REPLACE,
        category = ActionNodeCategory.TEXT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionTextConfigKey.TEXT, ActionTextConfigKey.PATTERN, ActionTextConfigKey.REPLACEMENT, ActionTextConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.TEXT), context)
        val pattern = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.PATTERN), context)
        val replacement = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.REPLACEMENT), context)
        val result = if (isRegex) Regex(pattern).replace(text, replacement) else text.replace(pattern, replacement)
        node.valueResult(node.config.string(ActionTextConfigKey.OUTPUT_KEY), JsonPrimitive(result))
    },
)

private fun textJoinDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.TEXT_JOIN,
        category = ActionNodeCategory.TEXT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionTextConfigKey.VALUES, ActionTextConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context ->
        val values = ActionTemplateResolver.resolveElement(node.config[ActionTextConfigKey.VALUES], context) as? JsonArray ?: error("text.join 节点 values 必须是数组")
        node.valueResult(
            node.config.string(ActionTextConfigKey.OUTPUT_KEY),
            JsonPrimitive(values.joinToString(node.config.string(ActionTextConfigKey.SEPARATOR)) { it.toString().trim('"') })
        )
    },
)

private fun textMatchAllDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.TEXT_MATCH_ALL,
        category = ActionNodeCategory.TEXT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionTextConfigKey.TEXT, ActionTextConfigKey.PATTERN, ActionTextConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.TEXT), context)
        val pattern = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.PATTERN), context)
        val matches = Regex(pattern).findAll(text).map { match ->
            JsonArray(match.groupValues.map(::JsonPrimitive))
        }.toList()
        node.valueResult(node.config.string(ActionTextConfigKey.OUTPUT_KEY), JsonArray(matches))
    },
)

private fun textPadDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.TEXT_PAD,
        category = ActionNodeCategory.TEXT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionTextConfigKey.TEXT, ActionTextConfigKey.PAD_LENGTH, ActionTextConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.TEXT), context)
        val length =
            ActionTemplateResolver.resolveElement(node.config[ActionTextConfigKey.PAD_LENGTH], context).jsonPrimitive.intOrNull ?: 0
        val char = node.config[ActionTextConfigKey.PAD_CHARACTER]?.let {
            ActionTemplateResolver.resolveText(
                it.jsonPrimitive.content,
                context
            )
        }?.firstOrNull() ?: ' '
        val padEnd = node.config[ActionTextConfigKey.PAD_END]?.let {
            ActionTemplateResolver.resolveElement(
                it,
                context
            ).jsonPrimitive.booleanOrNull
        } == true
        val result = if (padEnd) text.padEnd(length, char) else text.padStart(length, char)
        node.valueResult(node.config.string(ActionTextConfigKey.OUTPUT_KEY), JsonPrimitive(result))
    },
)

private fun textFormatNumberDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.TEXT_FORMAT_NUMBER,
        category = ActionNodeCategory.TEXT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionTextConfigKey.TEXT, ActionTextConfigKey.FRACTION_DIGITS, ActionTextConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.TEXT), context)
        val digits =
            ActionTemplateResolver.resolveElement(node.config[ActionTextConfigKey.FRACTION_DIGITS], context).jsonPrimitive.intOrNull
                ?: 0
        val num = text.toDoubleOrNull() ?: 0.0
        val formatted = if (digits <= 0) num.toLong().toString() else {
            val parts = num.toString().split('.')
            val whole = parts.first()
            val frac = (parts.getOrNull(1) ?: "").padEnd(digits, '0').take(digits)
            "$whole.$frac"
        }
        node.valueResult(node.config.string(ActionTextConfigKey.OUTPUT_KEY), JsonPrimitive(formatted))
    },
)

private fun textContainsDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.TEXT_CONTAINS,
        category = ActionNodeCategory.TEXT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionTextConfigKey.TEXT, ActionTextConfigKey.PATTERN, ActionTextConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.TEXT), context)
        val pattern = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.PATTERN), context)
        val ignoreCase = node.config[ActionTextConfigKey.IGNORE_CASE]?.let { ActionTemplateResolver.resolveElement(it, context).jsonPrimitive.booleanOrNull } ?: false
        val contains = text.contains(pattern, ignoreCase = ignoreCase)
        node.valueResult(node.config.string(ActionTextConfigKey.OUTPUT_KEY), JsonPrimitive(contains))
    },
)

private fun textStartsWithDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.TEXT_STARTS_WITH,
        category = ActionNodeCategory.TEXT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionTextConfigKey.TEXT, ActionTextConfigKey.PATTERN, ActionTextConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.TEXT), context)
        val pattern = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.PATTERN), context)
        val ignoreCase = node.config[ActionTextConfigKey.IGNORE_CASE]?.let { ActionTemplateResolver.resolveElement(it, context).jsonPrimitive.booleanOrNull } ?: false
        val startsWith = text.startsWith(pattern, ignoreCase = ignoreCase)
        node.valueResult(node.config.string(ActionTextConfigKey.OUTPUT_KEY), JsonPrimitive(startsWith))
    },
)

private fun textEndsWithDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.TEXT_ENDS_WITH,
        category = ActionNodeCategory.TEXT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionTextConfigKey.TEXT, ActionTextConfigKey.PATTERN, ActionTextConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.TEXT), context)
        val pattern = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.PATTERN), context)
        val ignoreCase = node.config[ActionTextConfigKey.IGNORE_CASE]?.let { ActionTemplateResolver.resolveElement(it, context).jsonPrimitive.booleanOrNull } ?: false
        val endsWith = text.endsWith(pattern, ignoreCase = ignoreCase)
        node.valueResult(node.config.string(ActionTextConfigKey.OUTPUT_KEY), JsonPrimitive(endsWith))
    },
)

private fun textSlugifyDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.TEXT_SLUGIFY,
        category = ActionNodeCategory.TEXT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionTextConfigKey.TEXT, ActionTextConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.TEXT), context)
        val slug = text.trim()
            .lowercase()
            .replace(Regex("[^a-z0-9\\s-]"), "")
            .replace(Regex("[\\s-]+"), "-")
            .trim('-')
        node.valueResult(node.config.string(ActionTextConfigKey.OUTPUT_KEY), JsonPrimitive(slug))
    },
)

private fun textTruncateDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.TEXT_TRUNCATE,
        category = ActionNodeCategory.TEXT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionTextConfigKey.TEXT, ActionTextConfigKey.LIMIT, ActionTextConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.TEXT), context)
        val limit = ActionTemplateResolver.resolveElement(node.config[ActionTextConfigKey.LIMIT], context).jsonPrimitive.intOrNull ?: text.length
        val ellipsis = node.config[ActionTextConfigKey.ELLIPSIS]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) } ?: "..."
        val truncated = if (text.length > limit) text.take(limit) + ellipsis else text
        node.valueResult(node.config.string(ActionTextConfigKey.OUTPUT_KEY), JsonPrimitive(truncated))
    },
)

private fun textCapitalizeDefinition() = textNode(ActionNodeType.TEXT_CAPITALIZE) { it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() } }

private fun textRepeatDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.TEXT_REPEAT,
        category = ActionNodeCategory.TEXT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionTextConfigKey.TEXT, ActionTextConfigKey.COUNT, ActionTextConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.TEXT), context)
        val count = node.config[ActionTextConfigKey.COUNT]?.let { ActionTemplateResolver.resolveElement(it, context).jsonPrimitive.intOrNull } ?: 1
        val result = text.repeat(count.coerceAtLeast(0))
        node.valueResult(node.config.string(ActionTextConfigKey.OUTPUT_KEY), JsonPrimitive(result))
    },
)

private fun textReverseDefinition() = textNode(ActionNodeType.TEXT_REVERSE) { it.reversed() }

private fun textIndexOfDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.TEXT_INDEX_OF,
        category = ActionNodeCategory.TEXT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionTextConfigKey.TEXT, ActionTextConfigKey.PATTERN, ActionTextConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.TEXT), context)
        val pattern = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.PATTERN), context)
        val ignoreCase = node.config[ActionTextConfigKey.IGNORE_CASE]?.let { ActionTemplateResolver.resolveElement(it, context).jsonPrimitive.content.toBoolean() } ?: false
        val index = text.indexOf(pattern, ignoreCase = ignoreCase)
        node.valueResult(node.config.string(ActionTextConfigKey.OUTPUT_KEY), JsonPrimitive(index))
    },
)

private fun textTemplateDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.TEXT_TEMPLATE,
        category = ActionNodeCategory.TEXT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionTextConfigKey.TEMPLATE, ActionTextConfigKey.OBJECT, ActionTextConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val template = ActionTemplateResolver.resolveText(node.config.string(ActionTextConfigKey.TEMPLATE), context)
        val dataObj = ActionTemplateResolver.resolveElement(node.config[ActionTextConfigKey.OBJECT], context) as? JsonObject ?: error("text.template 节点 object 必须是对象")
        var rendered = template
        dataObj.forEach { (key, element) ->
            val strValue = (element as? JsonPrimitive)?.content ?: element.toString()
            rendered = rendered.replace("\${$key}", strValue).replace("{$key}", strValue)
        }
        node.valueResult(node.config.string(ActionTextConfigKey.OUTPUT_KEY), JsonPrimitive(rendered))
    },
)
