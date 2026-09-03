package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.control

import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionNodeExecutionResult
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.conditionPorts
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.data.asNumber
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.inPort
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeCategory
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeDefinition
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeSpec
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionTemplateResolver
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * 控制分支的内置节点。
 */
internal val controlActionNodeDefinitions: List<ActionNodeDefinition> = listOf(
    conditionIsEmptyDefinition(),
    conditionIfDefinition(),
    httpStatusDefinition(),
    conditionEqualsDefinition(),
    conditionNotEqualsDefinition(),
    conditionGreaterThanDefinition(),
    conditionGreaterThanOrEqualsDefinition(),
    conditionLessThanDefinition(),
    conditionLessThanOrEqualsDefinition(),
    conditionAndDefinition(),
    conditionOrDefinition(),
    conditionNotDefinition(),
    conditionIsNullDefinition(),
)

private fun conditionIsEmptyDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        ActionNodeType.CONDITION_IS_EMPTY,
        category = ActionNodeCategory.CONTROL,
        inputPorts = persistentListOf(inPort),
        outputPorts = conditionPorts,
        requiredConfigKeys = setOf(ActionControlConfigKey.VALUE)
    ),
    executor = { node, context ->
        val value = ActionTemplateResolver.resolveElement(node.config[ActionControlConfigKey.VALUE], context)
        conditionResult(value is JsonNull || value.toString().trim('"').isEmpty(), value, null)
    },
)

private fun conditionIfDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.CONDITION_IF,
        category = ActionNodeCategory.CONTROL,
        inputPorts = persistentListOf(inPort),
        outputPorts = conditionPorts,
        requiredConfigKeys = setOf(ActionControlConfigKey.CONDITION),
    ),
    executor = { node, context ->
        val condition = ActionTemplateResolver.resolveElement(node.config[ActionControlConfigKey.CONDITION], context)
        val boolValue = condition.asBoolean()
        conditionResult(boolValue, condition, null)
    },
)

private fun httpStatusDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTTP_STATUS,
        category = ActionNodeCategory.CONTROL,
        inputPorts = persistentListOf(inPort),
        outputPorts = conditionPorts,
        requiredConfigKeys = setOf(ActionControlConfigKey.STATUS_CODE),
    ),
    executor = { node, context ->
        val statusCode = ActionTemplateResolver.resolveElement(node.config[ActionControlConfigKey.STATUS_CODE], context)
            .jsonPrimitive.content.toInt()
        val minimum = node.config[ActionControlConfigKey.MIN_STATUS_CODE]
            ?.let { ActionTemplateResolver.resolveElement(it, context).jsonPrimitive.content.toInt() }
            ?: 200
        val maximum = node.config[ActionControlConfigKey.MAX_STATUS_CODE]
            ?.let { ActionTemplateResolver.resolveElement(it, context).jsonPrimitive.content.toInt() }
            ?: 299
        require(minimum <= maximum) { "最小 HTTP 状态码不能大于最大值" }
        conditionResult(statusCode in minimum..maximum, JsonPrimitive(statusCode), JsonPrimitive("$minimum..$maximum"))
    },
)

private fun conditionEqualsDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.CONDITION_EQUALS,
        category = ActionNodeCategory.CONTROL,
        inputPorts = persistentListOf(inPort),
        outputPorts = conditionPorts,
        requiredConfigKeys = setOf(ActionControlConfigKey.LEFT, ActionControlConfigKey.RIGHT),
    ),
    executor = { node, context ->
        val left = ActionTemplateResolver.resolveElement(node.config[ActionControlConfigKey.LEFT], context)
        val right = ActionTemplateResolver.resolveElement(node.config[ActionControlConfigKey.RIGHT], context)
        val matched = left == right
        ActionNodeExecutionResult(
            outputPortId = if (matched) "true" else "false",
            output = JsonObject(mapOf(ActionControlConfigKey.LEFT to left, ActionControlConfigKey.RIGHT to right, ActionControlConfigKey.MATCHED to JsonPrimitive(matched))),
        )
    },
)

private fun conditionNotEqualsDefinition() = comparisonDefinition(ActionNodeType.CONDITION_NOT_EQUALS) { left, right -> left != right }
private fun conditionGreaterThanDefinition() = numericComparisonDefinition(ActionNodeType.CONDITION_GREATER_THAN) { left, right -> left > right }
private fun conditionGreaterThanOrEqualsDefinition() = numericComparisonDefinition(ActionNodeType.CONDITION_GREATER_THAN_OR_EQUALS) { left, right -> left >= right }
private fun conditionLessThanDefinition() = numericComparisonDefinition(ActionNodeType.CONDITION_LESS_THAN) { left, right -> left < right }
private fun conditionLessThanOrEqualsDefinition() = numericComparisonDefinition(ActionNodeType.CONDITION_LESS_THAN_OR_EQUALS) { left, right -> left <= right }
private fun conditionAndDefinition() = booleanComparisonDefinition(ActionNodeType.CONDITION_AND) { left, right -> left && right }
private fun conditionOrDefinition() = booleanComparisonDefinition(ActionNodeType.CONDITION_OR) { left, right -> left || right }

private fun conditionNotDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.CONDITION_NOT,
        category = ActionNodeCategory.CONTROL,
        inputPorts = persistentListOf(inPort),
        outputPorts = conditionPorts,
        requiredConfigKeys = setOf(ActionControlConfigKey.VALUE),
    ),
    executor = { node, context ->
        val value = ActionTemplateResolver.resolveElement(node.config[ActionControlConfigKey.VALUE], context)
        conditionResult(!value.asBoolean(), value, null)
    },
)

private fun conditionIsNullDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.CONDITION_IS_NULL,
        category = ActionNodeCategory.CONTROL,
        inputPorts = persistentListOf(inPort),
        outputPorts = conditionPorts,
        requiredConfigKeys = setOf(ActionControlConfigKey.VALUE),
    ),
    executor = { node, context ->
        val value = ActionTemplateResolver.resolveElement(node.config[ActionControlConfigKey.VALUE], context)
        conditionResult(value is JsonNull, value, null)
    },
)

private fun comparisonDefinition(type: String, comparison: (JsonElement, JsonElement) -> Boolean) =
    binaryConditionDefinition(type) { left, right ->
        comparison(left, right)
    }

private fun numericComparisonDefinition(type: String, comparison: (Double, Double) -> Boolean) =
    binaryConditionDefinition(type) { left, right ->
        comparison(left.asNumber(ActionControlConfigKey.LEFT), right.asNumber(ActionControlConfigKey.RIGHT))
    }

private fun booleanComparisonDefinition(type: String, comparison: (Boolean, Boolean) -> Boolean) =
    binaryConditionDefinition(type) { left, right ->
        comparison(left.asBoolean(), right.asBoolean())
    }

private fun binaryConditionDefinition(
    type: String,
    comparison: (JsonElement, JsonElement) -> Boolean,
): ActionNodeDefinition = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = type,
        category = ActionNodeCategory.CONTROL,
        inputPorts = persistentListOf(inPort),
        outputPorts = conditionPorts,
        requiredConfigKeys = setOf(ActionControlConfigKey.LEFT, ActionControlConfigKey.RIGHT)
    ),
    executor = { node, context ->
        val left = ActionTemplateResolver.resolveElement(node.config[ActionControlConfigKey.LEFT], context)
        val right = ActionTemplateResolver.resolveElement(node.config[ActionControlConfigKey.RIGHT], context)
        conditionResult(comparison(left, right), left, right)
    },
)

internal fun conditionResult(matched: Boolean, left: JsonElement, right: JsonElement?): ActionNodeExecutionResult {
    return ActionNodeExecutionResult(
        outputPortId = if (matched) ActionControlPortId.TRUE else ActionControlPortId.FALSE,
        output = JsonObject(buildMap {
            put(ActionControlConfigKey.LEFT, left)
            right?.let { put(ActionControlConfigKey.RIGHT, it) }
            put(ActionControlConfigKey.MATCHED, JsonPrimitive(matched))
        })
    )
}

internal fun JsonElement.asBoolean(): Boolean {
    val primitive = this as? JsonPrimitive ?: return false
    return primitive.booleanOrNull
        ?: primitive.contentOrNull?.toBooleanStrictOrNull()
        ?: (primitive.intOrNull?.let { it != 0 })
        ?: (primitive.doubleOrNull?.let { it != 0.0 })
        ?: false
}
