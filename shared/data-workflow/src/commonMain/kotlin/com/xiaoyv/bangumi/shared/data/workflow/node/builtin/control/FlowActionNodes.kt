package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.control

import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionNodeExecutionResult
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.branchesPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.catchPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.defaultPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.failurePort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.finallyPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.inPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.matchedPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.nextPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.tryPort
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeCategory
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeDefinition
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeSpec
import com.xiaoyv.bangumi.shared.data.workflow.node.core.string
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionTemplateResolver
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionWorkflowLogger
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.time.Duration.Companion.milliseconds

/**
 * 控制工作流入口和终点的内置节点。
 */
internal fun flowActionNodeDefinitions(
    logger: ActionWorkflowLogger = ActionWorkflowLogger.Default,
): List<ActionNodeDefinition> = listOf(
    startDefinition(),
    flowSwitchDefinition(),
    endDefinition(),
    flowDelayDefinition(),
    flowLogDefinition(logger),
    flowAssertDefinition(),
    flowStopDefinition(),
    flowDebugDefinition(logger),
    flowTryDefinition(),
    flowCatchDefinition(),
    flowFinallyDefinition(),
    flowCallDefinition(),
    flowReturnDefinition(),
    flowParallelDefinition(),
    flowJoinDefinition(),
    flowRetryDefinition(),
    flowTimeoutDefinition(),
    flowWaitUntilDefinition(),
    flowRateLimitDefinition(),
)

private fun startDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.FLOW_START,
        category = ActionNodeCategory.FLOW,
        outputPorts = persistentListOf(nextPort),
    ),
    executor = { _, _ -> ActionNodeExecutionResult(outputPortId = "next") },
)

private fun flowSwitchDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.FLOW_SWITCH,
        category = ActionNodeCategory.FLOW,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(
            matchedPort,
            defaultPort,
        ),
        requiredConfigKeys = setOf(ActionFlowConfigKey.VALUE, ActionFlowConfigKey.CASES),
    ),
    executor = { node, context ->
        val value = ActionTemplateResolver.resolveElement(node.config[ActionFlowConfigKey.VALUE], context).jsonPrimitive.content
        val cases = ActionTemplateResolver.resolveElement(node.config[ActionFlowConfigKey.CASES], context).jsonObject
        val matched = cases[value]?.jsonPrimitive?.boolean == true
        ActionNodeExecutionResult(
            outputPortId = if (matched) ActionControlPortId.MATCHED else ActionControlPortId.DEFAULT,
            output = buildJsonObject {
                put(ActionFlowConfigKey.VALUE, JsonPrimitive(value))
                put(ActionFlowConfigKey.MATCHED, JsonPrimitive(matched))
            },
        )
    },
)

private fun endDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.FLOW_END,
        category = ActionNodeCategory.FLOW,
        inputPorts = persistentListOf(inPort),
    ),
    executor = { _, _ -> ActionNodeExecutionResult(outputPortId = "") },
)

private fun flowDelayDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.FLOW_DELAY,
        category = ActionNodeCategory.FLOW,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionFlowConfigKey.DELAY_MILLIS),
    ),
    executor = { node, context ->
        val millis = ActionTemplateResolver.resolveElement(node.config[ActionFlowConfigKey.DELAY_MILLIS], context)
            .jsonPrimitive.content.toLong()
        require(millis >= 0) { "延迟时间不能小于 0" }
        delay(millis.milliseconds)
        ActionNodeExecutionResult(
            outputPortId = ActionControlPortId.NEXT,
            output = buildJsonObject {
                put(ActionFlowConfigKey.DELAY_MILLIS, JsonPrimitive(millis))
            },
        )
    },
)

private fun flowLogDefinition(logger: ActionWorkflowLogger) = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.FLOW_LOG,
        category = ActionNodeCategory.FLOW,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionFlowConfigKey.MESSAGE),
    ),
    executor = { node, context ->
        val message = ActionTemplateResolver.resolveText(node.config.string(ActionFlowConfigKey.MESSAGE), context)
        val level = node.config.string(ActionFlowConfigKey.LEVEL).ifBlank { flowLogDefaultLevel }
        val data = node.config[ActionFlowConfigKey.DATA]?.let { ActionTemplateResolver.resolveElement(it, context) }
        logger.log(level, message, data)
        ActionNodeExecutionResult(
            outputPortId = ActionControlPortId.NEXT,
            output = buildJsonObject {
                put(ActionFlowConfigKey.MESSAGE, JsonPrimitive(message))
                put(ActionFlowConfigKey.LEVEL, JsonPrimitive(level))
                if (data != null) put(ActionFlowConfigKey.DATA, data)
            },
        )
    },
)

private fun flowAssertDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.FLOW_ASSERT,
        category = ActionNodeCategory.FLOW,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort, failurePort),
        requiredConfigKeys = setOf(ActionFlowConfigKey.CONDITION),
    ),
    executor = { node, context ->
        val matched = ActionTemplateResolver.resolveElement(node.config[ActionFlowConfigKey.CONDITION], context)
            .jsonPrimitive.boolean
        require(matched) {
            node.config[ActionFlowConfigKey.MESSAGE]?.jsonPrimitive?.content ?: "工作流断言失败"
        }
        ActionNodeExecutionResult(outputPortId = ActionControlPortId.NEXT)
    },
)

private fun flowStopDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.FLOW_STOP,
        category = ActionNodeCategory.FLOW,
        inputPorts = persistentListOf(inPort),
    ),
    executor = { _, _ -> ActionNodeExecutionResult(outputPortId = "") },
)

private fun flowDebugDefinition(logger: ActionWorkflowLogger) = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.FLOW_DEBUG,
        category = ActionNodeCategory.FLOW,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
    ),
    executor = { node, context ->
        val message = node.config[ActionFlowConfigKey.MESSAGE]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) } ?: "Debug Info"
        val data = node.config[ActionFlowConfigKey.DATA]?.let { ActionTemplateResolver.resolveElement(it, context) }
        logger.log("debug", message, data)
        ActionNodeExecutionResult(
            outputPortId = ActionControlPortId.NEXT,
            output = buildJsonObject {
                put(ActionFlowConfigKey.MESSAGE, JsonPrimitive(message))
                if (data != null) put(ActionFlowConfigKey.DATA, data)
                put(ActionFlowConfigKey.VARIABLES, buildJsonObject { context.variables.forEach { (k, v) -> put(k, v) } })
            },
        )
    },
)

private fun flowTryDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.FLOW_TRY,
        category = ActionNodeCategory.FLOW,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(
            tryPort,
            catchPort,
            finallyPort,
            nextPort,
        ),
    ),
    executor = { _, _ -> ActionNodeExecutionResult(outputPortId = ActionControlPortId.TRY) },
)

private fun flowCatchDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.FLOW_CATCH,
        category = ActionNodeCategory.FLOW,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
    ),
    executor = { _, context ->
        val lastOutput = context.stepOutputs.values.lastOrNull()
        val error = lastOutput?.get(ActionFlowConfigKey.ERROR) ?: buildJsonObject {
            put(ActionFlowConfigKey.MESSAGE, JsonPrimitive("Caught exception"))
        }
        ActionNodeExecutionResult(
            outputPortId = ActionControlPortId.NEXT,
            output = buildJsonObject { put(ActionFlowConfigKey.ERROR, error) },
        )
    },
)

private fun flowFinallyDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.FLOW_FINALLY,
        category = ActionNodeCategory.FLOW,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
    ),
    executor = { _, _ -> ActionNodeExecutionResult(outputPortId = ActionControlPortId.NEXT) },
)

private fun flowCallDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.FLOW_CALL,
        category = ActionNodeCategory.FLOW,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort, failurePort),
        requiredConfigKeys = setOf(ActionFlowConfigKey.WORKFLOW_ID, ActionFlowConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val workflowId = ActionTemplateResolver.resolveText(node.config.string(ActionFlowConfigKey.WORKFLOW_ID), context)
        val inputData = node.config[ActionFlowConfigKey.INPUT]?.let { ActionTemplateResolver.resolveElement(it, context) } ?: context.input
        val key = node.config.string(ActionFlowConfigKey.OUTPUT_KEY)
        ActionNodeExecutionResult(
            outputPortId = ActionControlPortId.NEXT,
            output = buildJsonObject {
                put(ActionFlowConfigKey.WORKFLOW_ID, JsonPrimitive(workflowId))
                put(key, inputData)
            },
        )
    },
)

private fun flowReturnDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.FLOW_RETURN,
        category = ActionNodeCategory.FLOW,
        inputPorts = persistentListOf(inPort),
    ),
    executor = { node, context ->
        val outputVal = node.config[ActionFlowConfigKey.OUTPUT]?.let { ActionTemplateResolver.resolveElement(it, context) } ?: buildJsonObject {}
        ActionNodeExecutionResult(
            outputPortId = "",
            output = buildJsonObject { put(ActionFlowConfigKey.RESULT, outputVal) },
        )
    },
)

private fun flowParallelDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.FLOW_PARALLEL,
        category = ActionNodeCategory.FLOW,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(
            branchesPort,
            failurePort,
        ),
    ),
    executor = { _, _ -> ActionNodeExecutionResult(outputPortId = ActionControlPortId.BRANCHES) },
)

private fun flowJoinDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.FLOW_JOIN,
        category = ActionNodeCategory.FLOW,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionFlowConfigKey.VALUES, ActionFlowConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val values = ActionTemplateResolver.resolveElement(node.config[ActionFlowConfigKey.VALUES], context)
        val key = node.config.string(ActionFlowConfigKey.OUTPUT_KEY)
        ActionNodeExecutionResult(
            outputPortId = ActionControlPortId.NEXT,
            output = buildJsonObject { put(key, values) },
        )
    },
)

private fun flowRetryDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.FLOW_RETRY,
        category = ActionNodeCategory.FLOW,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort, failurePort),
        requiredConfigKeys = setOf(ActionFlowConfigKey.RETRY_COUNT),
    ),
    executor = { node, context ->
        val count = ActionTemplateResolver.resolveElement(node.config[ActionFlowConfigKey.RETRY_COUNT], context).jsonPrimitive.content.toInt()
        val delayMs = node.config[ActionFlowConfigKey.RETRY_DELAY_MILLIS]?.let { ActionTemplateResolver.resolveElement(it, context).jsonPrimitive.content.toLong() } ?: 0L
        if (delayMs > 0) delay(delayMs.milliseconds)
        ActionNodeExecutionResult(
            outputPortId = ActionControlPortId.NEXT,
            output = buildJsonObject { put(ActionFlowConfigKey.RETRY_COUNT, JsonPrimitive(count)) },
        )
    },
)

private fun flowTimeoutDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.FLOW_TIMEOUT,
        category = ActionNodeCategory.FLOW,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort, failurePort),
        requiredConfigKeys = setOf(ActionFlowConfigKey.TIMEOUT_MILLIS),
    ),
    executor = { node, context ->
        val timeout = ActionTemplateResolver.resolveElement(node.config[ActionFlowConfigKey.TIMEOUT_MILLIS], context).jsonPrimitive.content.toLong()
        ActionNodeExecutionResult(
            outputPortId = ActionControlPortId.NEXT,
            output = buildJsonObject { put(ActionFlowConfigKey.TIMEOUT_MILLIS, JsonPrimitive(timeout)) },
        )
    },
)

private fun flowWaitUntilDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.FLOW_WAIT_UNTIL,
        category = ActionNodeCategory.FLOW,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort, failurePort),
        requiredConfigKeys = setOf(ActionFlowConfigKey.CONDITION),
    ),
    executor = { node, context ->
        val condition = ActionTemplateResolver.resolveElement(node.config[ActionFlowConfigKey.CONDITION], context).jsonPrimitive.boolean
        ActionNodeExecutionResult(
            outputPortId = if (condition) ActionControlPortId.NEXT else ActionControlPortId.FAILURE,
            output = buildJsonObject { put(ActionFlowConfigKey.CONDITION_MET, JsonPrimitive(condition)) },
        )
    },
)

private fun flowRateLimitDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.FLOW_RATE_LIMIT,
        category = ActionNodeCategory.FLOW,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionFlowConfigKey.DELAY_MILLIS),
    ),
    executor = { node, context ->
        val delayMs = ActionTemplateResolver.resolveElement(node.config[ActionFlowConfigKey.DELAY_MILLIS], context).jsonPrimitive.content.toLong()
        if (delayMs > 0) delay(delayMs.milliseconds)
        ActionNodeExecutionResult(outputPortId = ActionControlPortId.NEXT)
    },
)

private const val flowLogDefaultLevel = "info"
