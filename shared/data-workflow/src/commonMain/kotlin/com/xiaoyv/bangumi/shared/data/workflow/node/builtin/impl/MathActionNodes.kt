package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.impl

import com.xiaoyv.bangumi.shared.data.workflow.model.ActionMathConfigKey
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
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonPrimitive

import kotlin.math.pow

/**
 * 算术运算内置节点。
 *
 * 所有节点读取模板解析后的 JSON 数字，并将结果同时写入节点输出与 `vars.<outputKey>`。
 */
internal val mathActionNodeDefinitions: List<ActionNodeDefinition> = listOf(
    mathAddDefinition(),
    mathSubtractDefinition(),
    mathMultiplyDefinition(),
    mathDivideDefinition(),
    mathModuloDefinition(),
    mathMinDefinition(),
    mathMaxDefinition(),
    mathPowDefinition(),
    mathSqrtDefinition(),
    mathSumDefinition(),
    mathAvgDefinition(),
    mathLogDefinition(),
    mathExpDefinition(),
    mathNegateDefinition(),
    mathRoundDefinition(),
    mathFloorDefinition(),
    mathCeilDefinition(),
    mathAbsDefinition(),
    mathRandomDefinition(),
    mathClampDefinition(),
)

private fun mathAddDefinition() = binaryMathDefinition(ActionNodeType.MATH_ADD) { left, right -> left + right }
private fun mathSubtractDefinition() = binaryMathDefinition(ActionNodeType.MATH_SUBTRACT) { left, right -> left - right }
private fun mathMultiplyDefinition() = binaryMathDefinition(ActionNodeType.MATH_MULTIPLY) { left, right -> left * right }
private fun mathMinDefinition() = binaryMathDefinition(ActionNodeType.MATH_MIN) { left, right -> kotlin.math.min(left, right) }
private fun mathMaxDefinition() = binaryMathDefinition(ActionNodeType.MATH_MAX) { left, right -> kotlin.math.max(left, right) }
private fun mathDivideDefinition() = binaryMathDefinition(ActionNodeType.MATH_DIVIDE) { left, right ->
    require(right != 0.0) { "除数不能为 0" }
    left / right
}

private fun mathModuloDefinition() = binaryMathDefinition(ActionNodeType.MATH_MODULO) { left, right ->
    require(right != 0.0) { "余数运算的除数不能为 0" }
    left % right
}

private fun mathPowDefinition() = binaryMathDefinition(ActionNodeType.MATH_POW) { left, right -> left.pow(right) }

private fun mathSqrtDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.MATH_SQRT,
        category = ActionNodeCategory.MATH,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionMathConfigKey.VALUE, ActionMathConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val value = ActionTemplateResolver.resolveElement(node.config[ActionMathConfigKey.VALUE], context).asNumber(ActionMathConfigKey.VALUE)
        require(value >= 0) { "负数不能求平方根" }
        node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), JsonPrimitive(kotlin.math.sqrt(value)))
    },
)

private fun mathSumDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.MATH_SUM,
        category = ActionNodeCategory.MATH,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionMathConfigKey.VALUES, ActionMathConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val values = (ActionTemplateResolver.resolveElement(node.config[ActionMathConfigKey.VALUES], context) as? JsonArray).orEmpty()
        val sum = values.sumOf { it.asNumber(ActionMathConfigKey.VALUES) }
        node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), JsonPrimitive(sum))
    },
)

private fun mathAvgDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.MATH_AVG,
        category = ActionNodeCategory.MATH,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionMathConfigKey.VALUES, ActionMathConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val values = (ActionTemplateResolver.resolveElement(node.config[ActionMathConfigKey.VALUES], context) as? JsonArray).orEmpty()
        val avg = if (values.isEmpty()) 0.0 else values.sumOf { it.asNumber(ActionMathConfigKey.VALUES) } / values.size
        node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), JsonPrimitive(avg))
    },
)

private fun mathLogDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.MATH_LOG,
        category = ActionNodeCategory.MATH,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionMathConfigKey.VALUE, ActionMathConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val value = ActionTemplateResolver.resolveElement(node.config[ActionMathConfigKey.VALUE], context).asNumber(ActionMathConfigKey.VALUE)
        require(value > 0) { "对数真数必须大于 0" }
        node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), JsonPrimitive(kotlin.math.ln(value)))
    },
)

private fun mathExpDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.MATH_EXP,
        category = ActionNodeCategory.MATH,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionMathConfigKey.VALUE, ActionMathConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val value = ActionTemplateResolver.resolveElement(node.config[ActionMathConfigKey.VALUE], context).asNumber(ActionMathConfigKey.VALUE)
        node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), JsonPrimitive(kotlin.math.exp(value)))
    },
)

private fun mathNegateDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.MATH_NEGATE,
        category = ActionNodeCategory.MATH,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionMathConfigKey.VALUE, ActionMathConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val value = -ActionTemplateResolver.resolveElement(node.config[ActionMathConfigKey.VALUE], context).asNumber(ActionMathConfigKey.VALUE)
        node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), JsonPrimitive(value))
    },
)

private fun mathRoundDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.MATH_ROUND,
        category = ActionNodeCategory.MATH,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionMathConfigKey.VALUE, ActionMathConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val value = ActionTemplateResolver.resolveElement(node.config[ActionMathConfigKey.VALUE], context).asNumber(ActionMathConfigKey.VALUE)
        val decimals = node.config[ActionMathConfigKey.DECIMALS]?.let { ActionTemplateResolver.resolveElement(it, context).jsonPrimitive.content.toInt() } ?: 0
        val multiplier = 10.0.pow(decimals.toDouble())
        val rounded = kotlin.math.round(value * multiplier) / multiplier
        node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), JsonPrimitive(rounded))
    },
)

private fun mathFloorDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.MATH_FLOOR,
        category = ActionNodeCategory.MATH,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionMathConfigKey.VALUE, ActionMathConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val value = ActionTemplateResolver.resolveElement(node.config[ActionMathConfigKey.VALUE], context).asNumber(ActionMathConfigKey.VALUE)
        node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), JsonPrimitive(kotlin.math.floor(value)))
    },
)

private fun mathCeilDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.MATH_CEIL,
        category = ActionNodeCategory.MATH,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionMathConfigKey.VALUE, ActionMathConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val value = ActionTemplateResolver.resolveElement(node.config[ActionMathConfigKey.VALUE], context).asNumber(ActionMathConfigKey.VALUE)
        node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), JsonPrimitive(kotlin.math.ceil(value)))
    },
)

private fun mathAbsDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.MATH_ABS,
        category = ActionNodeCategory.MATH,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionMathConfigKey.VALUE, ActionMathConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val value = ActionTemplateResolver.resolveElement(node.config[ActionMathConfigKey.VALUE], context).asNumber(ActionMathConfigKey.VALUE)
        node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), JsonPrimitive(kotlin.math.abs(value)))
    },
)

private fun mathRandomDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.MATH_RANDOM,
        category = ActionNodeCategory.MATH,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionMathConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val min = node.config[ActionMathConfigKey.MIN]?.let { ActionTemplateResolver.resolveElement(it, context).asNumber(ActionMathConfigKey.MIN) } ?: 0.0
        val max = node.config[ActionMathConfigKey.MAX]?.let { ActionTemplateResolver.resolveElement(it, context).asNumber(ActionMathConfigKey.MAX) } ?: 1.0
        val randomVal = min + (max - min) * kotlin.random.Random.nextDouble()
        node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), JsonPrimitive(randomVal))
    },
)

private fun mathClampDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.MATH_CLAMP,
        category = ActionNodeCategory.MATH,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionMathConfigKey.VALUE, ActionMathConfigKey.MIN, ActionMathConfigKey.MAX, ActionMathConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val value = ActionTemplateResolver.resolveElement(node.config[ActionMathConfigKey.VALUE], context).asNumber(ActionMathConfigKey.VALUE)
        val min = ActionTemplateResolver.resolveElement(node.config[ActionMathConfigKey.MIN], context).asNumber(ActionMathConfigKey.MIN)
        val max = ActionTemplateResolver.resolveElement(node.config[ActionMathConfigKey.MAX], context).asNumber(ActionMathConfigKey.MAX)
        node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), JsonPrimitive(value.coerceIn(min, max)))
    },
)

private fun binaryMathDefinition(
    type: String,
    operation: (Double, Double) -> Double,
): ActionNodeDefinition {
    return ActionNodeDefinition(
        spec = ActionNodeSpec(
            type = type,
            category = ActionNodeCategory.MATH,
            inputPorts = persistentListOf(inPort),
            outputPorts = persistentListOf(nextPort),
            requiredConfigKeys = setOf(ActionMathConfigKey.LEFT, ActionMathConfigKey.RIGHT, ActionMathConfigKey.OUTPUT_KEY),
        ),
        executor = { node, context ->
            val left = ActionTemplateResolver.resolveElement(node.config[ActionMathConfigKey.LEFT], context).asNumber(ActionMathConfigKey.LEFT)
            val right = ActionTemplateResolver.resolveElement(node.config[ActionMathConfigKey.RIGHT], context).asNumber(ActionMathConfigKey.RIGHT)
            node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), JsonPrimitive(operation(left, right)))
        },
    )
}

internal fun JsonElement.asNumber(configKey: String): Double {
    return jsonPrimitive.doubleOrNull ?: error("节点配置 $configKey 必须是数字")
}

