package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.data

import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionMathConfigKey
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
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
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
        node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), kotlin.math.sqrt(value).toJsonPrimitive())
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
        node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), sum.toJsonPrimitive())
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
        node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), avg.toJsonPrimitive())
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
        node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), kotlin.math.ln(value).toJsonPrimitive())
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
        node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), kotlin.math.exp(value).toJsonPrimitive())
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
        node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), value.toJsonPrimitive())
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
        node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), rounded.toJsonPrimitive())
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
        node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), kotlin.math.floor(value).toJsonPrimitive())
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
        node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), kotlin.math.ceil(value).toJsonPrimitive())
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
        node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), kotlin.math.abs(value).toJsonPrimitive())
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
        node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), randomVal.toJsonPrimitive())
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
        node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), value.coerceIn(min, max).toJsonPrimitive())
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
            node.valueResult(node.config.string(ActionMathConfigKey.OUTPUT_KEY), operation(left, right).toJsonPrimitive())
        },
    )
}

internal fun JsonElement.asNumber(configKey: String): Double {
    val primitive = this as? JsonPrimitive ?: error("节点配置 $configKey 必须是数字，实际为: $this")
    return primitive.doubleOrNull
        ?: primitive.intOrNull?.toDouble()
        ?: primitive.longOrNull?.toDouble()
        ?: primitive.booleanOrNull?.let { if (it) 1.0 else 0.0 }
        ?: primitive.contentOrNull?.toDoubleOrNull()
        ?: error("节点配置 $configKey 必须是合法数字，实际为: $this")
}

internal fun Double.toJsonPrimitive(): JsonPrimitive {
    return if (this % 1.0 == 0.0 && !this.isInfinite() && !this.isNaN()) {
        JsonPrimitive(this.toLong())
    } else {
        JsonPrimitive(this)
    }
}
