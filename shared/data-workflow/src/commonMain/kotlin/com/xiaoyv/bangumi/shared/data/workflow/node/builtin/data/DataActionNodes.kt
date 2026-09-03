package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.data

import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionNodeExecutionResult
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataMergeStrategy
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.inPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.nextPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.valueResult
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeCategory
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeDefinition
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeSpec
import com.xiaoyv.bangumi.shared.data.workflow.node.core.string
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionJsonPath
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionTemplateResolver
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

/**
 * 处理模板和基础数据合并的内置数据节点。
 */
internal val dataActionNodeDefinitions: List<ActionNodeDefinition> = listOf(
    templateDefinition(),
    setVariableDefinition(),
    coalesceDefinition(),
    concatDefinition(),
    dataMergeDefinition(),
    dataAssignDefinition(),
    dataRemoveDefinition(),
    dataRenameDefinition(),
    dataPickDefinition(),
    dataUuidDefinition(),
    dataToNumberDefinition(),
    dataToStringDefinition(),
    dataToBooleanDefinition(),
    dataTypeOfDefinition(),
)

private fun templateDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.TEMPLATE,
        category = ActionNodeCategory.DATA,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionDataConfigKey.OUTPUT_KEY, ActionDataConfigKey.TEMPLATE),
    ),
    executor = { node, context ->
        val key = node.config.string(ActionDataConfigKey.OUTPUT_KEY)
        val value = ActionTemplateResolver.resolveText(node.config.string(ActionDataConfigKey.TEMPLATE), context)
        ActionNodeExecutionResult("next", JsonObject(mapOf(key to JsonPrimitive(value))))
    },
)

private fun setVariableDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.SET_VARIABLE,
        category = ActionNodeCategory.DATA,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionDataConfigKey.KEY, ActionDataConfigKey.VALUE),
    ),
    executor = { node, context ->
        val key = node.config.string(ActionDataConfigKey.KEY)
        val value = ActionTemplateResolver.resolveElement(node.config[ActionDataConfigKey.VALUE], context)
        ActionNodeExecutionResult("next", JsonObject(mapOf(key to value)), persistentMapOf(key to value))
    },
)

private fun coalesceDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.DATA_COALESCE,
        category = ActionNodeCategory.DATA,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionDataConfigKey.VALUES, ActionDataConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context ->
        val values = ActionTemplateResolver.resolveElement(node.config[ActionDataConfigKey.VALUES], context) as? JsonArray ?: error("coalesce 节点 values 必须是数组")
        node.valueResult(node.config.string(ActionDataConfigKey.OUTPUT_KEY), values.firstOrNull { it !is JsonNull && it.toString().trim('"').isNotEmpty() } ?: JsonNull)
    },
)

private fun concatDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.DATA_CONCAT,
        category = ActionNodeCategory.DATA,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionDataConfigKey.VALUES, ActionDataConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context ->
        val values = ActionTemplateResolver.resolveElement(node.config[ActionDataConfigKey.VALUES], context) as? JsonArray ?: error("concat 节点 values 必须是数组")
        node.valueResult(
            node.config.string(ActionDataConfigKey.OUTPUT_KEY),
            JsonPrimitive(values.joinToString(node.config.string(ActionDataConfigKey.SEPARATOR)) { it.toString().trim('"') })
        )
    },
)

/**
 * 合并对象集合，并按配置决定嵌套对象和数组的冲突处理方式。
 */
private fun dataMergeDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.DATA_MERGE,
        category = ActionNodeCategory.DATA,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(
            ActionDataConfigKey.OBJECTS,
            ActionDataConfigKey.MERGE_STRATEGY,
            ActionDataConfigKey.OUTPUT_KEY,
        ),
    ),
    executor = { node, context ->
        val objects = ActionTemplateResolver.resolveElement(node.config[ActionDataConfigKey.OBJECTS], context) as? JsonArray
            ?: error("data.merge 节点 objects 必须是对象数组")
        val strategy = node.config.string(ActionDataConfigKey.MERGE_STRATEGY)
        require(strategy in dataMergeStrategies) { "不支持的数据合并策略：$strategy" }
        val result = objects.fold(JsonObject(emptyMap())) { merged, element ->
            mergeObjects(merged, element as? JsonObject ?: error("data.merge 节点 objects 必须全部为对象"), strategy)
        }
        node.valueResult(node.config.string(ActionDataConfigKey.OUTPUT_KEY), result)
    },
)

/**
 * 根据 path -> value 映射批量写入对象字段，支持 `$.profile.name` 形式的嵌套路径。
 */
private fun dataAssignDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.DATA_ASSIGN,
        category = ActionNodeCategory.DATA,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionDataConfigKey.OBJECT, ActionDataConfigKey.ASSIGNMENTS, ActionDataConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val source = ActionTemplateResolver.resolveElement(node.config[ActionDataConfigKey.OBJECT], context) as? JsonObject
            ?: error("data.assign 节点 object 必须是对象")
        val assignments = ActionTemplateResolver.resolveElement(node.config[ActionDataConfigKey.ASSIGNMENTS], context) as? JsonObject
            ?: error("data.assign 节点 assignments 必须是对象")
        val result = assignments.entries.fold(source) { current, (path, value) -> setObjectPath(current, path, value) }
        node.valueResult(node.config.string(ActionDataConfigKey.OUTPUT_KEY), result)
    },
)

private fun dataRemoveDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.DATA_REMOVE,
        category = ActionNodeCategory.DATA,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionDataConfigKey.OBJECT, ActionDataConfigKey.PATH, ActionDataConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val source = ActionTemplateResolver.resolveElement(node.config[ActionDataConfigKey.OBJECT], context) as? JsonObject
            ?: error("data.remove 节点 object 必须是对象")
        node.valueResult(
            node.config.string(ActionDataConfigKey.OUTPUT_KEY),
            removeObjectPath(source, node.config.string(ActionDataConfigKey.PATH)),
        )
    },
)

private fun dataRenameDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.DATA_RENAME,
        category = ActionNodeCategory.DATA,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(
            ActionDataConfigKey.OBJECT,
            ActionDataConfigKey.FROM_PATH,
            ActionDataConfigKey.TO_PATH,
            ActionDataConfigKey.OUTPUT_KEY,
        ),
    ),
    executor = { node, context ->
        val source = ActionTemplateResolver.resolveElement(node.config[ActionDataConfigKey.OBJECT], context) as? JsonObject
            ?: error("data.rename 节点 object 必须是对象")
        val fromPath = node.config.string(ActionDataConfigKey.FROM_PATH)
        val value = ActionJsonPath.resolve(source, fromPath)
        require(value !is JsonNull) { "data.rename 节点找不到来源路径：$fromPath" }
        val result = setObjectPath(removeObjectPath(source, fromPath), node.config.string(ActionDataConfigKey.TO_PATH), value)
        node.valueResult(node.config.string(ActionDataConfigKey.OUTPUT_KEY), result)
    },
)

private fun dataPickDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.DATA_PICK,
        category = ActionNodeCategory.DATA,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionDataConfigKey.OBJECT, ActionDataConfigKey.PATHS, ActionDataConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val source = ActionTemplateResolver.resolveElement(node.config[ActionDataConfigKey.OBJECT], context) as? JsonObject
            ?: error("data.pick 节点 object 必须是对象")
        val paths = ActionTemplateResolver.resolveElement(node.config[ActionDataConfigKey.PATHS], context) as? JsonArray
            ?: error("data.pick 节点 paths 必须是数组")
        val result = paths.fold(JsonObject(emptyMap())) { current, path ->
            val value = ActionJsonPath.resolve(source, path.jsonPrimitive.content)
            if (value is JsonNull) current else setObjectPath(current, path.jsonPrimitive.content, value)
        }
        node.valueResult(node.config.string(ActionDataConfigKey.OUTPUT_KEY), result)
    },
)

@OptIn(kotlin.uuid.ExperimentalUuidApi::class)
private fun dataUuidDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.DATA_UUID,
        category = ActionNodeCategory.DATA,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionDataConfigKey.OUTPUT_KEY),
    ),
    executor = { node, _ ->
        node.valueResult(
            node.config.string(ActionDataConfigKey.OUTPUT_KEY),
            JsonPrimitive(kotlin.uuid.Uuid.random().toString()),
        )
    },
)

private fun dataToNumberDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.DATA_TO_NUMBER,
        category = ActionNodeCategory.DATA,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionDataConfigKey.VALUE, ActionDataConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val num = when (val element = ActionTemplateResolver.resolveElement(node.config[ActionDataConfigKey.VALUE], context)) {
            is JsonPrimitive -> element.content.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
        node.valueResult(node.config.string(ActionDataConfigKey.OUTPUT_KEY), JsonPrimitive(num))
    },
)

private fun dataToStringDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.DATA_TO_STRING,
        category = ActionNodeCategory.DATA,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionDataConfigKey.VALUE, ActionDataConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val str = when (val element = ActionTemplateResolver.resolveElement(node.config[ActionDataConfigKey.VALUE], context)) {
            is JsonPrimitive -> element.content
            else -> element.toString()
        }
        node.valueResult(node.config.string(ActionDataConfigKey.OUTPUT_KEY), JsonPrimitive(str))
    },
)

private fun dataToBooleanDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.DATA_TO_BOOLEAN,
        category = ActionNodeCategory.DATA,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionDataConfigKey.VALUE, ActionDataConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val bool = when (val element = ActionTemplateResolver.resolveElement(node.config[ActionDataConfigKey.VALUE], context)) {
            is JsonPrimitive -> element.content.lowercase().toBooleanStrictOrNull() ?: (element.content != "0" && element.content.isNotEmpty())
            is JsonArray -> element.isNotEmpty()
            is JsonObject -> element.isNotEmpty()
        }
        node.valueResult(node.config.string(ActionDataConfigKey.OUTPUT_KEY), JsonPrimitive(bool))
    },
)

private fun dataTypeOfDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.DATA_TYPE_OF,
        category = ActionNodeCategory.DATA,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionDataConfigKey.VALUE, ActionDataConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val typeName = when (val element = ActionTemplateResolver.resolveElement(node.config[ActionDataConfigKey.VALUE], context)) {
            is JsonNull -> "null"
            is JsonArray -> "array"
            is JsonObject -> "object"
            is JsonPrimitive -> {
                when {
                    element.isString -> "string"
                    element.content.toBooleanStrictOrNull() != null -> "boolean"
                    element.content.toDoubleOrNull() != null -> "number"
                    else -> "string"
                }
            }
        }
        node.valueResult(node.config.string(ActionDataConfigKey.OUTPUT_KEY), JsonPrimitive(typeName))
    },
)

private val dataMergeStrategies = setOf(
    ActionDataMergeStrategy.SHALLOW,
    ActionDataMergeStrategy.DEEP,
    ActionDataMergeStrategy.DEEP_APPEND_ARRAYS,
)

private fun mergeObjects(left: JsonObject, right: JsonObject, strategy: String): JsonObject = JsonObject(left + right.mapValues { (key, rightValue) ->
    val leftValue = left[key]
    when {
        strategy == ActionDataMergeStrategy.SHALLOW -> rightValue
        leftValue is JsonObject && rightValue is JsonObject -> mergeObjects(leftValue, rightValue, strategy)
        strategy == ActionDataMergeStrategy.DEEP_APPEND_ARRAYS && leftValue is JsonArray && rightValue is JsonArray -> JsonArray(leftValue + rightValue)
        else -> rightValue
    }
})

private fun setObjectPath(source: JsonObject, path: String, value: JsonElement): JsonObject {
    val parts = objectPathParts(path)
    fun set(current: JsonObject, index: Int): JsonObject {
        val key = parts[index]
        if (index == parts.lastIndex) return JsonObject(current + (key to value))
        val child = current[key] as? JsonObject ?: JsonObject(emptyMap())
        return JsonObject(current + (key to set(child, index + 1)))
    }
    return set(source, 0)
}

private fun removeObjectPath(source: JsonObject, path: String): JsonObject {
    val parts = objectPathParts(path)
    fun remove(current: JsonObject, index: Int): JsonObject {
        val key = parts[index]
        if (index == parts.lastIndex) return JsonObject(current - key)
        val child = current[key] as? JsonObject ?: return current
        return JsonObject(current + (key to remove(child, index + 1)))
    }
    return remove(source, 0)
}

private fun objectPathParts(path: String): List<String> {
    val normalized = path.removePrefix("$").removePrefix(".")
    require(normalized.isNotBlank() && normalized.split('.').all(String::isNotBlank)) { "对象路径无效：$path" }
    return normalized.split('.')
}
