package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.data

import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayFilterOperator
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.arrayResult
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.inPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.nextPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.valueResult
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.values
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeCategory
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeDefinition
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeSpec
import com.xiaoyv.bangumi.shared.data.workflow.node.core.string
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionJsonPath
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionTemplateResolver
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * JSON 数组创建、查询及变换节点。
 */
internal val arrayActionNodeDefinitions = listOf(
    arrayLengthDefinition(),
    arrayCreateDefinition(),
    arrayAppendDefinition(),
    arrayInsertAtDefinition(),
    arrayRemoveAtDefinition(),
    arrayFilterDefinition(),
    arrayMapDefinition(),
    arrayFlatMapDefinition(),
    arrayConcatDefinition(),
    arrayZipDefinition(),
    arrayTakeDefinition(),
    arrayDropDefinition(),
    arrayContainsDefinition(),
    arrayFindDefinition(),
    arrayDistinctDefinition(),
    arraySortDefinition(),
    arrayReverseDefinition(),
    arraySliceDefinition(),
    arrayFlattenDefinition(),
    arrayGroupByDefinition(),
    arrayReduceDefinition(),
    arrayFirstDefinition(),
    arrayLastDefinition(),
    arraySumDefinition(),
    arrayAvgDefinition(),
    arrayMinDefinition(),
    arrayMaxDefinition(),
    arrayChunkDefinition(),
    arrayShuffleDefinition(),
    arraySampleDefinition(),
    arrayIndexOfDefinition(),
    arrayIntersectionDefinition(),
    arrayDifferenceDefinition(),
)

private fun arrayLengthDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_LENGTH,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context -> node.valueResult(node.config.string(ActionArrayConfigKey.OUTPUT_KEY), JsonPrimitive(node.values(context).size)) },
)

private fun arrayCreateDefinition() = arrayTransformDefinition(ActionNodeType.ARRAY_CREATE) { it }

private fun arrayAppendDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_APPEND,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.VALUE, ActionArrayConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context ->
        val values = node.values(context).toMutableList()
        val index = node.config[ActionArrayConfigKey.INDEX]?.let { ActionTemplateResolver.resolveElement(it, context).jsonPrimitive.intOrNull } ?: values.size
        require(index in 0..values.size) { "array.append 节点 index 超出数组插入范围" }
        values.add(index, ActionTemplateResolver.resolveElement(node.config[ActionArrayConfigKey.VALUE], context))
        node.arrayResult(JsonArray(values))
    },
)

private fun arrayRemoveAtDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_REMOVE_AT,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.INDEX, ActionArrayConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context ->
        val values = node.values(context)
        val index =
            ActionTemplateResolver.resolveElement(node.config[ActionArrayConfigKey.INDEX], context).jsonPrimitive.intOrNull ?: error("array.remove_at 节点 index 必须是整数")
        require(index in values.indices) { "array.remove_at 节点 index 超出数组范围" }
        node.arrayResult(JsonArray(values.filterIndexed { current, _ -> current != index }))
    },
)

private fun arrayFilterDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_FILTER,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.OPERATOR, ActionArrayConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context ->
        val path = node.config.string(ActionArrayConfigKey.FIELD_PATH).ifBlank { "$" }
        val expected = ActionTemplateResolver.resolveElement(node.config[ActionArrayConfigKey.EXPECTED], context)
        val operator = node.config.string(ActionArrayConfigKey.OPERATOR)
        val result = node.values(context).filter { element ->
            val value = ActionJsonPath.resolve(element, path)
            when (operator) {
                ActionArrayFilterOperator.EQUALS -> value == expected
                ActionArrayFilterOperator.NOT_EQUALS -> value != expected
                ActionArrayFilterOperator.IS_NULL -> value is JsonNull
                ActionArrayFilterOperator.IS_NOT_NULL -> value !is JsonNull
                ActionArrayFilterOperator.IS_EMPTY -> value is JsonNull || value.toString().trim('"').isEmpty()
                ActionArrayFilterOperator.IS_NOT_EMPTY -> value !is JsonNull && value.toString().trim('"').isNotEmpty()
                else -> error("不支持的数组筛选运算：$operator")
            }
        }
        node.arrayResult(JsonArray(result))
    },
)

private fun arrayMapDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_MAP,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.FIELD_PATH, ActionArrayConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context -> node.arrayResult(JsonArray(node.values(context).map { ActionJsonPath.resolve(it, node.config.string(ActionArrayConfigKey.FIELD_PATH)) })) },
)

private fun arrayFlatMapDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_FLAT_MAP,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.FIELD_PATH, ActionArrayConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val list = node.values(context).flatMap { item ->
            val resolved = ActionJsonPath.resolve(item, node.config.string(ActionArrayConfigKey.FIELD_PATH))
            (resolved as? JsonArray)?.toList() ?: listOf(resolved)
        }
        node.valueResult(node.config.string(ActionArrayConfigKey.OUTPUT_KEY), JsonArray(list))
    },
)

private fun arrayContainsDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_CONTAINS,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.VALUE, ActionArrayConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context ->
        node.valueResult(
            node.config.string(ActionArrayConfigKey.OUTPUT_KEY),
            JsonPrimitive(ActionTemplateResolver.resolveElement(node.config[ActionArrayConfigKey.VALUE], context) in node.values(context))
        )
    },
)

private fun arrayFindDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_FIND,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.FIELD_PATH, ActionArrayConfigKey.EXPECTED, ActionArrayConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context ->
        val expected = ActionTemplateResolver.resolveElement(node.config[ActionArrayConfigKey.EXPECTED], context)
        val result = node.values(context).firstOrNull { ActionJsonPath.resolve(it, node.config.string(ActionArrayConfigKey.FIELD_PATH)) == expected } ?: JsonNull
        node.valueResult(node.config.string(ActionArrayConfigKey.OUTPUT_KEY), result)
    },
)

/**
 * 创建可按对象字段升序或降序排列的数组节点。
 */
private fun arraySortDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_SORT,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val path = node.config.string(ActionArrayConfigKey.FIELD_PATH).ifBlank { "$" }
        val descending = node.config[ActionArrayConfigKey.DESCENDING]
            ?.let { ActionTemplateResolver.resolveElement(it, context).jsonPrimitive.content == "true" }
            ?: false
        val comparator = compareBy<kotlinx.serialization.json.JsonElement> { ActionJsonPath.resolve(it, path).toString() }
        val result = if (descending) node.values(context).sortedWith(comparator.reversed()) else node.values(context).sortedWith(comparator)
        node.arrayResult(JsonArray(result))
    },
)

private fun arraySliceDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_SLICE,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context ->
        val values = node.values(context)
        val start = node.config[ActionArrayConfigKey.START]?.let { ActionTemplateResolver.resolveElement(it, context).jsonPrimitive.intOrNull } ?: 0
        val end = node.config[ActionArrayConfigKey.END]?.let { ActionTemplateResolver.resolveElement(it, context).jsonPrimitive.intOrNull } ?: values.size
        require(start in 0..end && end <= values.size) { "array.slice 节点索引范围无效" }
        node.arrayResult(JsonArray(values.subList(start, end)))
    },
)

private fun arrayTransformDefinition(type: String, transform: (JsonArray) -> JsonArray) = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = type,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context -> node.arrayResult(transform(node.values(context))) },
)

private fun arrayGroupByDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_GROUP_BY,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.FIELD_PATH, ActionArrayConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val values = node.values(context)
        val path = node.config.string(ActionArrayConfigKey.FIELD_PATH)
        val grouped = values.groupBy { element -> ActionJsonPath.resolve(element, path).toString().trim('"') }
        val result = kotlinx.serialization.json.JsonObject(grouped.mapValues { (_, list) -> JsonArray(list) })
        node.valueResult(node.config.string(ActionArrayConfigKey.OUTPUT_KEY), result)
    },
)

private fun arrayReduceDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_REDUCE,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val values = node.values(context)
        val initial = node.config[ActionArrayConfigKey.INITIAL_VALUE]?.let { ActionTemplateResolver.resolveElement(it, context) } ?: JsonNull
        val result = values.fold(initial) { acc, element ->
            if (acc is JsonNull) element else JsonArray(listOf(acc, element))
        }
        node.valueResult(node.config.string(ActionArrayConfigKey.OUTPUT_KEY), result)
    },
)

private fun arrayFirstDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_FIRST,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val first = node.values(context).firstOrNull() ?: JsonNull
        node.valueResult(node.config.string(ActionArrayConfigKey.OUTPUT_KEY), first)
    },
)

private fun arrayLastDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_LAST,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val last = node.values(context).lastOrNull() ?: JsonNull
        node.valueResult(node.config.string(ActionArrayConfigKey.OUTPUT_KEY), last)
    },
)

private fun arrayConcatDefinition() = arrayTransformDefinition(ActionNodeType.ARRAY_CONCAT) { values ->
    JsonArray(values.flatMap { element -> (element as? JsonArray)?.toList() ?: error("array.concat 节点 values 必须是数组的数组") })
}

private fun arrayDistinctDefinition() = arrayTransformDefinition(ActionNodeType.ARRAY_DISTINCT) { JsonArray(it.distinct()) }

private fun arrayReverseDefinition() = arrayTransformDefinition(ActionNodeType.ARRAY_REVERSE) { JsonArray(it.reversed()) }

private fun arrayFlattenDefinition() = arrayTransformDefinition(ActionNodeType.ARRAY_FLATTEN) { values ->
    JsonArray(values.flatMap { (it as? JsonArray).orEmpty().ifEmpty { listOf(it) } })
}

private fun arraySumDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_SUM,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val numbers = extractNumbers(node.values(context), node.config[ActionArrayConfigKey.FIELD_PATH]?.jsonPrimitive?.contentOrNull)
        val sum = numbers.sum()
        node.valueResult(node.config.string(ActionArrayConfigKey.OUTPUT_KEY), JsonPrimitive(sum))
    },
)

private fun arrayAvgDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_AVG,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val numbers = extractNumbers(node.values(context), node.config[ActionArrayConfigKey.FIELD_PATH]?.jsonPrimitive?.contentOrNull)
        val avg = if (numbers.isEmpty()) 0.0 else numbers.average()
        node.valueResult(node.config.string(ActionArrayConfigKey.OUTPUT_KEY), JsonPrimitive(avg))
    },
)

private fun arrayMinDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_MIN,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val numbers = extractNumbers(node.values(context), node.config[ActionArrayConfigKey.FIELD_PATH]?.jsonPrimitive?.contentOrNull)
        val min = numbers.minOrNull() ?: 0.0
        node.valueResult(node.config.string(ActionArrayConfigKey.OUTPUT_KEY), JsonPrimitive(min))
    },
)

private fun arrayMaxDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_MAX,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val numbers = extractNumbers(node.values(context), node.config[ActionArrayConfigKey.FIELD_PATH]?.jsonPrimitive?.contentOrNull)
        val max = numbers.maxOrNull() ?: 0.0
        node.valueResult(node.config.string(ActionArrayConfigKey.OUTPUT_KEY), JsonPrimitive(max))
    },
)

private fun arrayChunkDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_CHUNK,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.SIZE, ActionArrayConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val size = node.config[ActionArrayConfigKey.SIZE]?.let { ActionTemplateResolver.resolveElement(it, context).jsonPrimitive.content.toInt() } ?: 1
        require(size > 0) { "Chunk size 必须大于 0" }
        val chunks = node.values(context).chunked(size).map { JsonArray(it) }
        node.valueResult(node.config.string(ActionArrayConfigKey.OUTPUT_KEY), JsonArray(chunks))
    },
)

private fun arrayShuffleDefinition() = arrayTransformDefinition(ActionNodeType.ARRAY_SHUFFLE) { JsonArray(it.shuffled()) }

private fun arraySampleDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_SAMPLE,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val count = node.config[ActionArrayConfigKey.COUNT]?.let { ActionTemplateResolver.resolveElement(it, context).jsonPrimitive.content.toInt() } ?: 1
        val sampled = node.values(context).shuffled().take(count.coerceAtLeast(0))
        node.valueResult(node.config.string(ActionArrayConfigKey.OUTPUT_KEY), JsonArray(sampled))
    },
)

private fun extractNumbers(values: JsonArray, fieldPath: String?): List<Double> {
    return values.mapNotNull { element ->
        val target = if (fieldPath.isNullOrEmpty()) element else ActionJsonPath.resolve(element, fieldPath)
        target.jsonPrimitive.doubleOrNull
    }
}

private fun arrayIndexOfDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_INDEX_OF,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.VALUE, ActionArrayConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val targetValue = ActionTemplateResolver.resolveElement(node.config[ActionArrayConfigKey.VALUE], context)
        val index = node.values(context).indexOf(targetValue)
        node.valueResult(node.config.string(ActionArrayConfigKey.OUTPUT_KEY), JsonPrimitive(index))
    },
)

private fun arrayIntersectionDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_INTERSECTION,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.OTHER_VALUES, ActionArrayConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val left = node.values(context).toSet()
        val right = (ActionTemplateResolver.resolveElement(node.config[ActionArrayConfigKey.OTHER_VALUES], context) as? JsonArray).orEmpty().toSet()
        val intersection = left.intersect(right).toList()
        node.valueResult(node.config.string(ActionArrayConfigKey.OUTPUT_KEY), JsonArray(intersection))
    },
)

private fun arrayDifferenceDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_DIFFERENCE,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.OTHER_VALUES, ActionArrayConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val left = node.values(context).toList()
        val right = (ActionTemplateResolver.resolveElement(node.config[ActionArrayConfigKey.OTHER_VALUES], context) as? JsonArray).orEmpty().toSet()
        val diff = left.filterNot { it in right }
        node.valueResult(node.config.string(ActionArrayConfigKey.OUTPUT_KEY), JsonArray(diff))
    },
)

private fun arrayInsertAtDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_INSERT_AT,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.INDEX, ActionArrayConfigKey.VALUE, ActionArrayConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val list = node.values(context).toMutableList()
        val index = node.config[ActionArrayConfigKey.INDEX]?.let { ActionTemplateResolver.resolveElement(it, context).jsonPrimitive.content.toIntOrNull() } ?: list.size
        val value = ActionTemplateResolver.resolveElement(node.config[ActionArrayConfigKey.VALUE], context)
        val validIndex = index.coerceIn(0, list.size)
        list.add(validIndex, value)
        node.valueResult(node.config.string(ActionArrayConfigKey.OUTPUT_KEY), JsonArray(list))
    },
)

private fun arrayZipDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_ZIP,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.OTHER_VALUES, ActionArrayConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val left = node.values(context)
        val right = (ActionTemplateResolver.resolveElement(node.config[ActionArrayConfigKey.OTHER_VALUES], context) as? JsonArray).orEmpty()
        val zipped = left.zip(right) { a, b -> JsonArray(listOf(a, b)) }
        node.valueResult(node.config.string(ActionArrayConfigKey.OUTPUT_KEY), JsonArray(zipped))
    },
)

private fun arrayTakeDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_TAKE,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.COUNT, ActionArrayConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val list = node.values(context)
        val count = node.config[ActionArrayConfigKey.COUNT]?.let { ActionTemplateResolver.resolveElement(it, context).jsonPrimitive.content.toIntOrNull() } ?: 0
        node.valueResult(node.config.string(ActionArrayConfigKey.OUTPUT_KEY), JsonArray(list.take(count.coerceAtLeast(0))))
    },
)

private fun arrayDropDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.ARRAY_DROP,
        category = ActionNodeCategory.ARRAY,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionArrayConfigKey.VALUES, ActionArrayConfigKey.COUNT, ActionArrayConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val list = node.values(context)
        val count = node.config[ActionArrayConfigKey.COUNT]?.let { ActionTemplateResolver.resolveElement(it, context).jsonPrimitive.content.toIntOrNull() } ?: 0
        node.valueResult(node.config.string(ActionArrayConfigKey.OUTPUT_KEY), JsonArray(list.drop(count.coerceAtLeast(0))))
    },
)
