package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.data

import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionObjectConfigKey
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
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * JSON 对象字段操作节点。
 */
internal val objectActionNodeDefinitions = listOf(
    objectGetDefinition(),
    objectSetDefinition(),
    objectRemoveDefinition(),
    objectOmitDefinition(),
    objectPickDefinition(),
    objectMergeDefinition(),
    objectKeysDefinition(),
    objectValuesDefinition(),
    objectEntriesDefinition(),
    objectFromEntriesDefinition(),
    objectHasKeyDefinition(),
    objectIsEmptyDefinition(),
)

private fun objectGetDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.OBJECT_GET,
        category = ActionNodeCategory.OBJECT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionObjectConfigKey.OBJECT, ActionObjectConfigKey.PATH, ActionObjectConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context ->
        val source = ActionTemplateResolver.resolveElement(node.config[ActionObjectConfigKey.OBJECT], context)
        node.valueResult(node.config.string(ActionObjectConfigKey.OUTPUT_KEY), ActionJsonPath.resolve(source, node.config.string(ActionObjectConfigKey.PATH)))
    },
)

private fun objectSetDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.OBJECT_SET,
        category = ActionNodeCategory.OBJECT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionObjectConfigKey.OBJECT, ActionObjectConfigKey.KEY, ActionObjectConfigKey.VALUE, ActionObjectConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context ->
        val source = ActionTemplateResolver.resolveElement(node.config[ActionObjectConfigKey.OBJECT], context) as? JsonObject ?: error("object.set 节点 object 必须是对象")
        val key = node.config.string(ActionObjectConfigKey.KEY)
        val value = ActionTemplateResolver.resolveElement(node.config[ActionObjectConfigKey.VALUE], context)
        node.valueResult(node.config.string(ActionObjectConfigKey.OUTPUT_KEY), JsonObject(source + (key to value)))
    },
)

private fun objectRemoveDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.OBJECT_REMOVE,
        category = ActionNodeCategory.OBJECT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionObjectConfigKey.OBJECT, ActionObjectConfigKey.KEY, ActionObjectConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context ->
        val source = ActionTemplateResolver.resolveElement(node.config[ActionObjectConfigKey.OBJECT], context) as? JsonObject ?: error("object.remove 节点 object 必须是对象")
        node.valueResult(node.config.string(ActionObjectConfigKey.OUTPUT_KEY), JsonObject(source - node.config.string(ActionObjectConfigKey.KEY)))
    },
)

private fun objectOmitDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.OBJECT_OMIT,
        category = ActionNodeCategory.OBJECT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionObjectConfigKey.OBJECT, ActionObjectConfigKey.KEYS, ActionObjectConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val source = ActionTemplateResolver.resolveElement(node.config[ActionObjectConfigKey.OBJECT], context) as? JsonObject ?: error("object.omit 节点 object 必须是对象")
        val keysArray = ActionTemplateResolver.resolveElement(node.config[ActionObjectConfigKey.KEYS], context) as? JsonArray ?: error("object.omit 节点 keys 必须是数组")
        val omitSet = keysArray.map { (it as? JsonPrimitive)?.content.orEmpty() }.toSet()
        val filtered = source.filterKeys { it !in omitSet }
        node.valueResult(node.config.string(ActionObjectConfigKey.OUTPUT_KEY), JsonObject(filtered))
    },
)

private fun objectMergeDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.OBJECT_MERGE,
        category = ActionNodeCategory.OBJECT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionObjectConfigKey.OBJECTS, ActionObjectConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context ->
        val objects = ActionTemplateResolver.resolveElement(node.config[ActionObjectConfigKey.OBJECTS], context) as? JsonArray ?: error("object.merge 节点 objects 必须是对象数组")
        val merged = objects.fold(emptyMap<String, JsonElement>()) { result, element -> result + ((element as? JsonObject) ?: error("object.merge 节点 objects 必须全部为对象")) }
        node.valueResult(node.config.string(ActionObjectConfigKey.OUTPUT_KEY), JsonObject(merged))
    },
)

private fun objectKeysDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.OBJECT_KEYS,
        category = ActionNodeCategory.OBJECT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionObjectConfigKey.OBJECT, ActionObjectConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val obj = ActionTemplateResolver.resolveElement(node.config[ActionObjectConfigKey.OBJECT], context) as? JsonObject ?: error("object.keys 节点 object 必须是对象")
        val keys = JsonArray(obj.keys.map { JsonPrimitive(it) })
        node.valueResult(node.config.string(ActionObjectConfigKey.OUTPUT_KEY), keys)
    },
)

private fun objectValuesDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.OBJECT_VALUES,
        category = ActionNodeCategory.OBJECT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionObjectConfigKey.OBJECT, ActionObjectConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val obj = ActionTemplateResolver.resolveElement(node.config[ActionObjectConfigKey.OBJECT], context) as? JsonObject ?: error("object.values 节点 object 必须是对象")
        val values = JsonArray(obj.values.toList())
        node.valueResult(node.config.string(ActionObjectConfigKey.OUTPUT_KEY), values)
    },
)

private fun objectEntriesDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.OBJECT_ENTRIES,
        category = ActionNodeCategory.OBJECT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionObjectConfigKey.OBJECT, ActionObjectConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val obj = ActionTemplateResolver.resolveElement(node.config[ActionObjectConfigKey.OBJECT], context) as? JsonObject ?: error("object.entries 节点 object 概念必须是对象")
        val entries = JsonArray(obj.entries.map { (k, v) -> JsonArray(listOf(JsonPrimitive(k), v)) })
        node.valueResult(node.config.string(ActionObjectConfigKey.OUTPUT_KEY), entries)
    },
)

private fun objectFromEntriesDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.OBJECT_FROM_ENTRIES,
        category = ActionNodeCategory.OBJECT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionObjectConfigKey.ENTRIES, ActionObjectConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val entries =
            ActionTemplateResolver.resolveElement(node.config[ActionObjectConfigKey.ENTRIES], context) as? JsonArray ?: error("object.from_entries 节点 entries 必须是二元组数组")
        val map = entries.mapNotNull { item ->
            val pair = item as? JsonArray ?: return@mapNotNull null
            if (pair.size < 2) return@mapNotNull null
            val key = (pair[0] as? JsonPrimitive)?.content ?: return@mapNotNull null
            key to pair[1]
        }.toMap()
        node.valueResult(node.config.string(ActionObjectConfigKey.OUTPUT_KEY), JsonObject(map))
    },
)

private fun objectHasKeyDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.OBJECT_HAS_KEY,
        category = ActionNodeCategory.OBJECT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionObjectConfigKey.OBJECT, ActionObjectConfigKey.KEY, ActionObjectConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val obj = ActionTemplateResolver.resolveElement(node.config[ActionObjectConfigKey.OBJECT], context) as? JsonObject ?: error("object.has_key 节点 object 必须是对象")
        val key = ActionTemplateResolver.resolveText(node.config.string(ActionObjectConfigKey.KEY), context)
        node.valueResult(node.config.string(ActionObjectConfigKey.OUTPUT_KEY), JsonPrimitive(obj.containsKey(key)))
    },
)

private fun objectIsEmptyDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.OBJECT_IS_EMPTY,
        category = ActionNodeCategory.OBJECT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionObjectConfigKey.OBJECT, ActionObjectConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val obj = ActionTemplateResolver.resolveElement(node.config[ActionObjectConfigKey.OBJECT], context) as? JsonObject ?: error("object.is_empty 节点 object 必须是对象")
        node.valueResult(node.config.string(ActionObjectConfigKey.OUTPUT_KEY), JsonPrimitive(obj.isEmpty()))
    },
)

private fun objectPickDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.OBJECT_PICK,
        category = ActionNodeCategory.OBJECT,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionObjectConfigKey.OBJECT, ActionObjectConfigKey.KEYS, ActionObjectConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val source = ActionTemplateResolver.resolveElement(node.config[ActionObjectConfigKey.OBJECT], context) as? JsonObject ?: error("object.pick 节点 object 必须是对象")
        val keysArray = ActionTemplateResolver.resolveElement(node.config[ActionObjectConfigKey.KEYS], context) as? JsonArray ?: error("object.pick 节点 keys 必须是数组")
        val pickSet = keysArray.map { (it as? JsonPrimitive)?.content.orEmpty() }.toSet()
        val filtered = source.filterKeys { it in pickSet }
        node.valueResult(node.config.string(ActionObjectConfigKey.OUTPUT_KEY), JsonObject(filtered))
    },
)
