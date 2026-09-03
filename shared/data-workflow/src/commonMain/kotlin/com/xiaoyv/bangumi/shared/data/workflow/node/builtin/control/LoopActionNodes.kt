package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.control

import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionNodeExecutionResult
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionLoopConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.bodyPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.completedPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.failurePort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.inPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.loopControlInPort
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeCategory
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeDefinition
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeSpec
import kotlinx.collections.immutable.persistentListOf

/**
 * 结构化循环节点。
 *
 * 循环体以 `body` 出口进入，并必须通过 [ActionNodeType.LOOP_NEXT]、
 * [ActionNodeType.LOOP_CONTINUE] 或 [ActionNodeType.LOOP_BREAK] 结束本轮；引擎据此驱动下一轮或 completed 出口。
 * [ActionNodeType.LOOP_FOR_EACH] 默认以 items 数组实际长度作为迭代上限；可选的 maxIterations 仅用于额外安全限制。
 */
internal val loopActionNodeDefinitions: List<ActionNodeDefinition> = listOf(
    loopRepeatDefinition(),
    loopForEachDefinition(),
    loopWhileDefinition(),
    loopNextDefinition(),
    loopContinueDefinition(),
    loopBreakDefinition(),
)

private fun loopRepeatDefinition() = loopDefinition(ActionNodeType.LOOP_REPEAT, setOf(ActionLoopConfigKey.COUNT, ActionLoopConfigKey.MAX_ITERATIONS))
private fun loopForEachDefinition() = loopDefinition(ActionNodeType.LOOP_FOR_EACH, setOf(ActionLoopConfigKey.ITEMS))
private fun loopWhileDefinition() = loopDefinition(ActionNodeType.LOOP_WHILE, setOf(ActionLoopConfigKey.CONDITION, ActionLoopConfigKey.MAX_ITERATIONS))
private fun loopNextDefinition() = controlDefinition(ActionNodeType.LOOP_NEXT)
private fun loopContinueDefinition() = controlDefinition(ActionNodeType.LOOP_CONTINUE)
private fun loopBreakDefinition() = controlDefinition(ActionNodeType.LOOP_BREAK)

private fun loopDefinition(type: String, requiredConfigKeys: Set<String>): ActionNodeDefinition = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = type,
        category = ActionNodeCategory.LOOP,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(
            bodyPort,
            completedPort,
            failurePort,
        ),
        requiredConfigKeys = requiredConfigKeys,
    ),
    executor = { _, _ -> ActionNodeExecutionResult(outputPortId = ActionControlPortId.BODY) },
)

private fun controlDefinition(type: String): ActionNodeDefinition = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = type,
        category = ActionNodeCategory.LOOP,
        inputPorts = persistentListOf(loopControlInPort),
        requiredConfigKeys = setOf(ActionLoopConfigKey.LOOP_ID),
    ),
    executor = { _, _ -> ActionNodeExecutionResult(outputPortId = "") },
)
