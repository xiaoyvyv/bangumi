package com.xiaoyv.bangumi.shared.data.workflow.engine.runtime

import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionSideEffectHandler
import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionSideEffectResult
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionPortKind
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionWorkflow
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionEvent
import com.xiaoyv.bangumi.shared.data.workflow.model.log.ActionExecutionStep
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeRegistry
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.JsonObject

/**
 * flow.parallel 并发域执行器。
 */
internal class ActionParallelExecutor(
    private val registry: ActionNodeRegistry,
    private val now: () -> Long,
) {
    suspend fun execute(
        workflow: ActionWorkflow,
        parallelNode: ActionNode,
        context: ActionExecutionContext,
        sideEffectHandler: ActionSideEffectHandler,
    ): ParallelExecutionResult {
        val roots = nextNodeIds(workflow, parallelNode.id, ActionControlPortId.BRANCHES)
        require(roots.isNotEmpty()) { "flow.parallel 至少需要一条 branches 出口" }
        val joins = roots.map { reachableJoinNodes(workflow, it) }.reduce { left, right -> left intersect right }
        require(joins.size == 1) { "flow.parallel 的所有分支必须汇入同一个 flow.join" }
        val joinNodeId = joins.single()
        val results = coroutineScope {
            roots.map { root -> async { executeBranch(workflow, root, joinNodeId, context, sideEffectHandler) } }.awaitAll()
        }
        return ParallelExecutionResult(joinNodeId, results.map { it.context }, results.flatMap { it.steps }, results.flatMap { it.events })
    }

    fun mergeContexts(baseline: ActionExecutionContext, branches: List<ActionExecutionContext>): ActionExecutionContext {
        val variables = baseline.variables.toMutableMap()
        val outputs = baseline.stepOutputs.toMutableMap()
        branches.forEach { branch ->
            branch.variables.forEach { (key, value) ->
                val previous = variables[key]
                require(previous == null || previous == value || baseline.variables[key] == previous) { "flow.parallel 分支同时写入变量 [$key]，请使用不同变量名后在 flow.join 合并" }
                variables[key] = value
            }
            branch.stepOutputs.forEach { (key, value) ->
                val previous = outputs[key]
                require(previous == null || previous == value || baseline.stepOutputs[key] == previous) { "flow.parallel 分支重复执行节点 [$key]" }
                outputs[key] = value
            }
        }
        return baseline.copy(variables = variables.toPersistentMap(), stepOutputs = outputs.toPersistentMap())
    }

    private suspend fun executeBranch(
        workflow: ActionWorkflow,
        startId: String,
        joinId: String,
        initial: ActionExecutionContext,
        handler: ActionSideEffectHandler
    ): ParallelBranchResult {
        var nodeId = startId
        var context = initial
        val steps = mutableListOf<ActionExecutionStep>()
        val events = mutableListOf<ActionExecutionEvent>()
        val visited = mutableSetOf<String>()
        while (nodeId != joinId) {
            check(visited.add(nodeId)) { "flow.parallel 分支包含循环：$nodeId" }
            val node = workflow.nodes.firstOrNull { it.id == nodeId } ?: error("flow.parallel 分支引用了不存在的节点：$nodeId")
            check(node.type != ActionNodeType.FLOW_PARALLEL) { "flow.parallel 不支持嵌套并发域" }
            val definition = registry.find(node.type) ?: error("未知节点类型：${node.type}")
            val startedAt = now()
            events += ActionExecutionEvent.NodeStarted(node.id)
            val result = definition.executor.execute(node, context)
            var output = result.output
            if (result.sideEffect != null) {
                events += ActionExecutionEvent.SideEffectRequested(node.id, result.sideEffect)
                when (val sideEffect = handler.handle(result.sideEffect)) {
                    is ActionSideEffectResult.Success -> output = JsonObject(output + sideEffect.output)
                    ActionSideEffectResult.Cancelled -> error("并发分支节点 [${node.id}] 已取消")
                    is ActionSideEffectResult.Failure -> error(sideEffect.message)
                }
            }
            context = context.copy(
                variables = (context.variables + result.variableUpdates).toPersistentMap(),
                stepOutputs = (context.stepOutputs + (node.id to output)).toPersistentMap()
            )
            steps += ActionExecutionStep(node.id, startedAt, now(), result.outputPortId, output)
            events += ActionExecutionEvent.NodeCompleted(node.id, result.outputPortId, output)
            val next = nextNodeIds(workflow, node.id, result.outputPortId)
            require(next.size == 1) { "flow.parallel 分支节点 [${node.id}] 必须只有一条通向 flow.join 的后继边" }
            nodeId = next.single()
        }
        return ParallelBranchResult(context, steps, events)
    }

    private fun reachableJoinNodes(workflow: ActionWorkflow, startId: String): Set<String> {
        val visited = mutableSetOf<String>()
        val queue = ArrayDeque<String>().apply { add(startId) }
        val joins = mutableSetOf<String>()
        while (queue.isNotEmpty()) {
            val nodeId = queue.removeFirst()
            if (!visited.add(nodeId)) continue
            val node = workflow.nodes.firstOrNull { it.id == nodeId } ?: continue
            if (node.type == ActionNodeType.FLOW_JOIN) joins += nodeId else workflow.edges.filter { it.kind == ActionPortKind.CONTROL && it.source.nodeId == nodeId }
                .forEach { queue.add(it.target.nodeId) }
        }
        return joins
    }

    private fun nextNodeIds(workflow: ActionWorkflow, nodeId: String, portId: String) =
        workflow.edges.filter { it.kind == ActionPortKind.CONTROL && it.source.nodeId == nodeId && it.source.portId == portId }.map { it.target.nodeId }.distinct()
}

internal data class ParallelExecutionResult(
    val joinNodeId: String,
    val contexts: List<ActionExecutionContext>,
    val steps: List<ActionExecutionStep>,
    val events: List<ActionExecutionEvent>
)

private data class ParallelBranchResult(val context: ActionExecutionContext, val steps: List<ActionExecutionStep>, val events: List<ActionExecutionEvent>)
