package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.parse

import com.xiaoyv.bangumi.shared.core.utils.defaultJson
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionNodeExecutionResult
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionJsonConfigKey
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
import kotlinx.serialization.json.JsonPrimitive

/**
 * JSON 文本与结构化值转换节点。
 */
internal val jsonActionNodeDefinitions = listOf(
    jsonExtractDefinition(),
    jsonParseDefinition(),
    jsonStringifyDefinition(),
    jsonValidateDefinition(),
    jsonSchemaValidateDefinition(),
)

private fun jsonExtractDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.JSON_EXTRACT,
        category = ActionNodeCategory.JSON,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionJsonConfigKey.SOURCE, ActionJsonConfigKey.PATH, ActionJsonConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val source = ActionTemplateResolver.resolveElement(node.config[ActionJsonConfigKey.SOURCE], context)
        val value = ActionJsonPath.resolve(source, node.config.string(ActionJsonConfigKey.PATH))
        node.valueResult(node.config.string(ActionJsonConfigKey.OUTPUT_KEY), value)
    },
)

private fun jsonParseDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.JSON_PARSE,
        category = ActionNodeCategory.JSON,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionJsonConfigKey.TEXT, ActionJsonConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context ->
        node.valueResult(
            node.config.string(ActionJsonConfigKey.OUTPUT_KEY),
            defaultJson.parseToJsonElement(ActionTemplateResolver.resolveText(node.config.string(ActionJsonConfigKey.TEXT), context))
        )
    },
)

private fun jsonStringifyDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.JSON_STRINGIFY,
        category = ActionNodeCategory.JSON,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionJsonConfigKey.VALUE, ActionJsonConfigKey.OUTPUT_KEY)
    ),
    executor = { node, context ->
        node.valueResult(
            node.config.string(ActionJsonConfigKey.OUTPUT_KEY),
            JsonPrimitive(ActionTemplateResolver.resolveElement(node.config[ActionJsonConfigKey.VALUE], context).toString())
        )
    },
)

private fun jsonValidateDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.JSON_VALIDATE,
        category = ActionNodeCategory.JSON,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort, com.xiaoyv.bangumi.shared.data.workflow.node.builtin.failurePort),
        requiredConfigKeys = setOf(ActionJsonConfigKey.TEXT, ActionJsonConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionJsonConfigKey.TEXT), context)
        val isValid = try {
            defaultJson.parseToJsonElement(text)
            true
        } catch (_: Throwable) {
            false
        }
        val key = node.config.string(ActionJsonConfigKey.OUTPUT_KEY)
        ActionNodeExecutionResult(
            outputPortId = if (isValid) ActionControlPortId.NEXT else ActionControlPortId.FAILURE,
            output = kotlinx.serialization.json.buildJsonObject { put(key, JsonPrimitive(isValid)) },
        )
    },
)

private fun jsonSchemaValidateDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.JSON_SCHEMA_VALIDATE,
        category = ActionNodeCategory.JSON,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort, com.xiaoyv.bangumi.shared.data.workflow.node.builtin.failurePort),
        requiredConfigKeys = setOf(ActionJsonConfigKey.VALUE, ActionJsonConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val element = ActionTemplateResolver.resolveElement(node.config[ActionJsonConfigKey.VALUE], context)
        val schema = node.config[ActionJsonConfigKey.SCHEMA]?.let { ActionTemplateResolver.resolveElement(it, context) }
        val isValid = element !is kotlinx.serialization.json.JsonNull
        val key = node.config.string(ActionJsonConfigKey.OUTPUT_KEY)
        ActionNodeExecutionResult(
            outputPortId = if (isValid) ActionControlPortId.NEXT else ActionControlPortId.FAILURE,
            output = kotlinx.serialization.json.buildJsonObject { put(key, JsonPrimitive(isValid)) },
        )
    },
)
