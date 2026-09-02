package com.xiaoyv.bangumi.shared.data.workflow.engine

import com.xiaoyv.bangumi.shared.data.workflow.engine.loop.LoopExecutionController
import com.xiaoyv.bangumi.shared.data.workflow.exception.ActionErrorCode
import com.xiaoyv.bangumi.shared.data.workflow.exception.ActionNodeExecutionException
import com.xiaoyv.bangumi.shared.data.workflow.exception.ActionWorkflowException
import com.xiaoyv.bangumi.shared.data.workflow.exception.ActionWorkflowTraceLogger
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionEdge
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionPortKind
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionWorkflow
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionEvent
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionSideEffect
import com.xiaoyv.bangumi.shared.data.workflow.model.log.ActionExecutionError
import com.xiaoyv.bangumi.shared.data.workflow.model.log.ActionExecutionLog
import com.xiaoyv.bangumi.shared.data.workflow.model.log.ActionExecutionStatus
import com.xiaoyv.bangumi.shared.data.workflow.model.log.ActionExecutionStep
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionErrorKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionLoopConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionLoopContextKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeRegistry
import com.xiaoyv.bangumi.shared.data.workflow.node.core.string
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * 与 UI 无关的高性能有向无环图 (DAG) 工作流执行器。
 *
 * 采用基于就绪队列 (Ready Queue) 与依赖边激活集 (Activated Edges) 的调度机制，完整支持：
 * 1. 多路分叉 (Forking / Fan-Out)：单个出口端口可触发多个下游 (downstream) 分支节点。
 * 2. 多路合流 (Merging / Fan-In / Join)：包含多条活跃前置分支的节点将等待所有活跃上游节点全部完成（pendingPredecessors == 0）后才压入队列，保证恰好执行一次。
 * 3. 条件与循环控制：包含条件路由 (control.if / control.switch) 与循环结构 (loop.repeat / loop.for_each / loop.while) 的流转控制。
 * 4. 全局上下文累加：各节点输出与变量自动合并至 [ActionExecutionContext]，供下游合流节点完整引用。
 *
 * @param registry 节点定义注册中心。
 * @param validator 工作流合法性校验器。
 * @param now 当前时间戳提供者 (毫秒)。
 */
class ActionWorkflowEngine(
    private val registry: ActionNodeRegistry,
    private val validator: ActionWorkflowValidator,
    private val now: () -> Long,
) {
    /**
     * 执行已校验工作流并持续输出运行事件。
     *
     * 副作用由 [sideEffectHandler] 在宿主层同步执行，其结果决定 success、failure 或取消分支。
     *
     * @param workflow 待运行的工作流。
     * @param initialContext 入口注入的只读上下文。
     * @param sideEffectHandler 副作用执行器，不允许使用默认成功实现绕过平台操作。
     * @param maxStepCount 单次执行允许的最大节点数，用于避免异常流程无限运行。
     * @return 包含节点进度、失败信息和最终日志的事件流。
     */
    fun execute(
        workflow: ActionWorkflow,
        initialContext: ActionExecutionContext,
        sideEffectHandler: ActionSideEffectHandler,
        maxStepCount: Int = DEFAULT_MAX_STEP_COUNT,
    ): Flow<ActionExecutionEvent> = flow {
        val validation = validator.validate(workflow)
        if (!validation.isValid) {
            val firstIssue = validation.issues.firstOrNull()
            val issueNode = firstIssue?.nodeId?.let { id -> workflow.nodes.find { it.id == id } }
            val workflowEx = ActionWorkflowException(
                code = ActionErrorCode.INVALID_WORKFLOW,
                messageText = validation.issues.joinToString("; ") { issue ->
                    val nodePrefix = issue.nodeId?.let { "节点 [$it]: " } ?: ""
                    "$nodePrefix${issue.message}"
                },
                workflowId = workflow.id,
                workflowName = workflow.name,
                nodeId = firstIssue?.nodeId,
                nodeType = issueNode?.type,
                nodeLabel = issueNode?.label,
                details = mapOf(ActionErrorKey.ISSUES to JsonPrimitive(validation.issues.joinToString { it.message })),
                hint = ActionErrorCode.INVALID_WORKFLOW_HINT
            )
            ActionWorkflowTraceLogger.logError(workflowEx)
            emit(ActionExecutionEvent.Failed(workflowEx.toExecutionError()))
            return@flow
        }
        if (!workflow.enabled) {
            val workflowEx = ActionWorkflowException(
                code = ActionErrorCode.WORKFLOW_DISABLED,
                messageText = ActionErrorCode.WORKFLOW_DISABLED_MSG,
                workflowId = workflow.id,
                workflowName = workflow.name,
                hint = ActionErrorCode.WORKFLOW_DISABLED_HINT
            )
            ActionWorkflowTraceLogger.logError(workflowEx)
            emit(ActionExecutionEvent.Failed(workflowEx.toExecutionError()))
            return@flow
        }

        val startedAt = now()
        val steps = mutableListOf<ActionExecutionStep>()
        val nodes = workflow.nodes.associateBy { it.id }
        var context = initialContext
        val loops = LoopExecutionController()
        var completionStatus = ActionExecutionStatus.SUCCESS

        // 待执行节点就绪队列与运行状态追踪
        val readyQueue = ArrayDeque<String>()
        val executedNodes = mutableSetOf<String>()
        val activatedEdges = mutableSetOf<String>() // 已激活的控制边 ID 集合

        readyQueue.add(workflow.entryNodeId)
        var stepCount = 0
        emit(ActionExecutionEvent.Started(workflow.id))

        // 预建边索引以提高查找效率
        val incomingControlEdges = workflow.edges.filter { it.kind == ActionPortKind.CONTROL }.groupBy { it.target.nodeId }
        val outgoingControlEdges = workflow.edges.filter { it.kind == ActionPortKind.CONTROL }.groupBy { it.source.nodeId }

        while (readyQueue.isNotEmpty()) {
            if (++stepCount > maxStepCount) {
                emitFailure(workflow, startedAt, steps, ActionErrorCode.STEP_LIMIT, ActionErrorCode.STEP_LIMIT_MSG, readyQueue.firstOrNull(), this)
                return@flow
            }

            val currentNodeId = readyQueue.removeFirst()
            val node = nodes[currentNodeId] ?: run {
                emitFailure(workflow, startedAt, steps, ActionErrorCode.MISSING_NODE, ActionErrorCode.MISSING_NODE_MSG, currentNodeId, this)
                return@flow
            }
            val definition = registry.find(node.type) ?: run {
                emitFailure(workflow, startedAt, steps, ActionErrorCode.UNKNOWN_NODE, "${ActionErrorCode.UNKNOWN_NODE_MSG}：${node.type}", node.id, this)
                return@flow
            }

            val nodeStartedAt = now()
            emit(ActionExecutionEvent.NodeStarted(node.id))

            val loopTransition = try {
                when (node.type) {
                    ActionNodeType.LOOP_REPEAT, ActionNodeType.LOOP_FOR_EACH, ActionNodeType.LOOP_WHILE -> loops.enter(
                        node,
                        context,
                        findNextNodeId(workflow, node.id, ActionControlPortId.BODY),
                        findNextNodeId(workflow, node.id, ActionControlPortId.COMPLETED),
                    )

                    ActionNodeType.LOOP_NEXT, ActionNodeType.LOOP_CONTINUE -> loops.next(node.config.string(ActionLoopConfigKey.LOOP_ID), context)
                    ActionNodeType.LOOP_BREAK -> loops.breakLoop(node.config.string(ActionLoopConfigKey.LOOP_ID), context)
                    else -> null
                }
            } catch (throwable: Throwable) {
                if (throwable is CancellationException) throw throwable
                val loopNodeId = when (node.type) {
                    ActionNodeType.LOOP_REPEAT, ActionNodeType.LOOP_FOR_EACH, ActionNodeType.LOOP_WHILE -> node.id
                    else -> loops.activeLoopId()
                }
                val failureTarget = loopNodeId?.let { findNextNodeId(workflow, it, ActionControlPortId.FAILURE) }
                if (failureTarget != null) {
                    context = loops.abort(loopNodeId, context)
                    if (failureTarget !in readyQueue) readyQueue.add(failureTarget)
                    continue
                }
                emitFailure(
                    workflow,
                    startedAt,
                    steps,
                    ActionErrorCode.LOOP_EXECUTION_FAILED,
                    throwable.message.orEmpty().ifBlank { ActionErrorCode.LOOP_EXECUTION_FAILED_MSG },
                    node.id,
                    this
                )
                return@flow
            }

            if (loopTransition != null) {
                context = loopTransition.context
                val loopOutput = JsonObject(mapOf(ActionLoopContextKey.LOOP to context.loop))
                steps += ActionExecutionStep(node.id, nodeStartedAt, now(), loopTransition.outputPortId, loopOutput)
                emit(ActionExecutionEvent.NodeCompleted(node.id, loopTransition.outputPortId, loopOutput))
                executedNodes += node.id

                // 当进入新的循环体迭代时，重置循环体内节点的已执行状态，确保循环体内部的合流 (Join) 节点能正确等待当前轮迭代的依赖
                if (loopTransition.outputPortId == ActionControlPortId.BODY) {
                    val loopBodyNodes = findReachableNodesInLoopBody(workflow, node.id)
                    executedNodes.removeAll(loopBodyNodes)
                    val loopBodyEdgeIds = workflow.edges.filter { it.source.nodeId in loopBodyNodes || it.target.nodeId in loopBodyNodes }.map { it.id }.toSet()
                    activatedEdges.removeAll(loopBodyEdgeIds)
                }

                val targetId = loopTransition.nodeId
                if (targetId != null && targetId !in readyQueue) {
                    readyQueue.add(targetId)
                }
                continue
            }

            val result = try {
                definition.executor.execute(node, context)
            } catch (throwable: Throwable) {
                if (throwable is CancellationException) throw throwable
                val workflowEx = throwable as? ActionWorkflowException ?: ActionNodeExecutionException(
                    code = ActionErrorCode.NODE_EXECUTION_FAILED,
                    messageText = throwable.message.orEmpty().ifBlank { "节点 [${node.id}] ${ActionErrorCode.NODE_EXECUTION_FAILED_MSG}" },
                    workflowId = workflow.id,
                    workflowName = workflow.name,
                    nodeId = node.id,
                    nodeType = node.type,
                    nodeLabel = node.label,
                    details = mapOf(ActionErrorKey.CONFIG to node.config),
                    cause = throwable,
                )
                ActionWorkflowTraceLogger.logError(workflowEx)
                val error = workflowEx.toExecutionError()
                val failureOutput = errorOutput(JsonObject(emptyMap()), error)
                steps += ActionExecutionStep(node.id, nodeStartedAt, now(), ActionControlPortId.FAILURE, failureOutput, error)
                context = context.copy(stepOutputs = (context.stepOutputs + (node.id to failureOutput)).toPersistentMap())
                emit(ActionExecutionEvent.Failed(error))
                val nodeFailureTargets = findNextNodeIds(workflow, node.id, ActionControlPortId.FAILURE)
                val loopNodeId = loops.activeLoopId()
                val loopFailureTargets = if (nodeFailureTargets.isEmpty()) {
                    loopNodeId?.let { findNextNodeIds(workflow, it, ActionControlPortId.FAILURE) }.orEmpty()
                } else emptyList()
                val failureTargets = nodeFailureTargets.ifEmpty { loopFailureTargets.ifEmpty { listOfNotNull(workflow.globalErrorNodeId) } }
                if (failureTargets.isEmpty() || failureTargets.contains(node.id)) {
                    emitTerminal(workflow, startedAt, steps, ActionExecutionStatus.FAILED, this)
                    return@flow
                }
                completionStatus = ActionExecutionStatus.FAILED
                if (loopFailureTargets.isNotEmpty() && loopNodeId != null) {
                    context = loops.abort(loopNodeId, context)
                }
                failureTargets.forEach { targetId ->
                    if (targetId !in readyQueue) readyQueue.add(targetId)
                }
                continue
            }

            var output = result.output
            if (result.sideEffect != null) {
                emit(ActionExecutionEvent.SideEffectRequested(node.id, result.sideEffect))
                val effectResult = try {
                    sideEffectHandler.handle(result.sideEffect)
                } catch (throwable: Throwable) {
                    if (throwable is CancellationException) throw throwable
                    ActionSideEffectResult.Failure(throwable.message ?: ActionErrorCode.SIDE_EFFECT_FAILED_MSG)
                }
                when (effectResult) {
                    is ActionSideEffectResult.Success -> {
                        output = JsonObject(output + effectResult.output)
                    }

                    ActionSideEffectResult.Cancelled -> {
                        val error = ActionExecutionError(ActionErrorCode.SIDE_EFFECT_CANCELLED, ActionErrorCode.SIDE_EFFECT_CANCELLED_MSG, node.id)
                        steps += ActionExecutionStep(node.id, nodeStartedAt, now(), error = error)
                        emit(ActionExecutionEvent.Failed(error))
                        emitTerminal(workflow, startedAt, steps, ActionExecutionStatus.CANCELLED, this)
                        return@flow
                    }

                    is ActionSideEffectResult.Failure -> {
                        val workflowEx = ActionWorkflowException(
                            code = ActionErrorCode.SIDE_EFFECT_FAILED,
                            messageText = effectResult.message,
                            workflowId = workflow.id,
                            workflowName = workflow.name,
                            nodeId = node.id,
                            nodeType = node.type,
                            nodeLabel = node.label,
                            details = mapOf(ActionErrorKey.SIDE_EFFECT to JsonPrimitive(result.sideEffect::class.simpleName ?: "unknown")),
                        )
                        ActionWorkflowTraceLogger.logError(workflowEx)
                        val error = workflowEx.toExecutionError()
                        val failureOutput = errorOutput(output, error)
                        steps += ActionExecutionStep(node.id, nodeStartedAt, now(), ActionControlPortId.FAILURE, failureOutput, error)
                        context = context.copy(stepOutputs = (context.stepOutputs + (node.id to failureOutput)).toPersistentMap())
                        emit(ActionExecutionEvent.NodeCompleted(node.id, ActionControlPortId.FAILURE, failureOutput))
                        val nodeFailureTargets = findNextNodeIds(workflow, node.id, ActionControlPortId.FAILURE)
                        val loopNodeId = loops.activeLoopId()
                        val loopFailureTargets = if (nodeFailureTargets.isEmpty()) {
                            loopNodeId?.let { findNextNodeIds(workflow, it, ActionControlPortId.FAILURE) }.orEmpty()
                        } else emptyList()
                        val failureTargets = nodeFailureTargets.ifEmpty { loopFailureTargets.ifEmpty { listOfNotNull(workflow.globalErrorNodeId) } }
                        if (failureTargets.isEmpty() || failureTargets.contains(node.id)) {
                            emit(ActionExecutionEvent.Failed(error))
                            emitTerminal(workflow, startedAt, steps, ActionExecutionStatus.FAILED, this)
                            return@flow
                        }
                        completionStatus = ActionExecutionStatus.FAILED
                        if (loopFailureTargets.isNotEmpty() && loopNodeId != null) {
                            context = loops.abort(loopNodeId, context)
                        }
                        emit(ActionExecutionEvent.Failed(error))
                        failureTargets.forEach { targetId ->
                            if (targetId !in readyQueue) readyQueue.add(targetId)
                        }
                        continue
                    }
                }
            }

            context = context.copy(
                variables = (context.variables + result.variableUpdates).toPersistentMap(),
                stepOutputs = (context.stepOutputs + (node.id to output)).toPersistentMap(),
            )
            steps += ActionExecutionStep(node.id, nodeStartedAt, now(), result.outputPortId, output)
            emit(ActionExecutionEvent.NodeCompleted(node.id, result.outputPortId, output))
            executedNodes += node.id

            // 激活当前节点端口产生的输出边
            val outgoingEdges = outgoingControlEdges[node.id].orEmpty().filter { it.source.portId == result.outputPortId }
            outgoingEdges.forEach { activatedEdges += it.id }

            // 检查并入队就绪的下游节点（完美支持 Fork-Join 多路分叉与汇入合流）
            val targetNodeIds = outgoingEdges.map { it.target.nodeId }.distinct()
            for (targetId in targetNodeIds) {
                if (isNodeReadyToExecute(targetId, activatedEdges, executedNodes, incomingControlEdges)) {
                    if (targetId !in readyQueue) {
                        readyQueue.add(targetId)
                    }
                }
            }
        }

        emit(
            ActionExecutionEvent.Completed(
                ActionExecutionLog(workflow.id, startedAt, now(), completionStatus, steps.toPersistentList()),
            )
        )
    }

    private fun isNodeReadyToExecute(
        nodeId: String,
        activatedEdges: Set<String>,
        executedNodes: Set<String>,
        incomingControlEdges: Map<String, List<ActionEdge>>,
    ): Boolean {
        val incoming = incomingControlEdges[nodeId].orEmpty()
        if (incoming.isEmpty()) return true

        val activeIncoming = incoming.filter { it.id in activatedEdges }
        if (activeIncoming.isEmpty()) return false

        // 检查活跃入边的上游节点是否全都在 executedNodes 中（确保 Join 节点在多路分支均执行完后才运行）
        val pendingPredecessors = activeIncoming
            .map { it.source.nodeId }
            .filter { upstreamId -> upstreamId !in executedNodes }

        return pendingPredecessors.isEmpty()
    }

    private fun findNextNodeIds(workflow: ActionWorkflow, nodeId: String, portId: String): List<String> {
        return workflow.edges.filter {
            it.kind == ActionPortKind.CONTROL && it.source.nodeId == nodeId && it.source.portId == portId
        }.map { it.target.nodeId }.distinct()
    }

    private fun findNextNodeId(workflow: ActionWorkflow, nodeId: String, portId: String): String? {
        return findNextNodeIds(workflow, nodeId, portId).firstOrNull()
    }

    private fun findReachableNodesInLoopBody(workflow: ActionWorkflow, loopNodeId: String): Set<String> {
        val bodyEdge = workflow.edges.firstOrNull {
            it.kind == ActionPortKind.CONTROL && it.source.nodeId == loopNodeId && it.source.portId == ActionControlPortId.BODY
        } ?: return emptySet()

        val reachable = mutableSetOf<String>()
        val queue = ArrayDeque<String>()
        queue.add(bodyEdge.target.nodeId)

        val controlTypes = setOf(ActionNodeType.LOOP_NEXT, ActionNodeType.LOOP_CONTINUE, ActionNodeType.LOOP_BREAK)

        while (queue.isNotEmpty()) {
            val curr = queue.removeFirst()
            if (!reachable.add(curr)) continue

            val node = workflow.nodes.firstOrNull { it.id == curr }
            if (node != null && node.type in controlTypes) {
                continue
            }

            val outgoing = workflow.edges.filter { it.kind == ActionPortKind.CONTROL && it.source.nodeId == curr }
            outgoing.forEach { edge ->
                if (edge.target.nodeId != loopNodeId) {
                    queue.add(edge.target.nodeId)
                }
            }
        }
        return reachable
    }

    private suspend fun emitFailure(
        workflow: ActionWorkflow,
        startedAt: Long,
        steps: List<ActionExecutionStep>,
        code: String,
        message: String,
        nodeId: String?,
        collector: kotlinx.coroutines.flow.FlowCollector<ActionExecutionEvent>,
        cause: Throwable? = null,
    ) {
        val node = nodeId?.let { workflow.nodes.find { n -> n.id == it } }
        val workflowEx = ActionWorkflowException(
            code = code,
            messageText = message,
            workflowId = workflow.id,
            workflowName = workflow.name,
            nodeId = nodeId,
            nodeType = node?.type,
            nodeLabel = node?.label,
            details = node?.config?.let { mapOf(ActionErrorKey.CONFIG to it) } ?: emptyMap(),
            cause = cause,
        )
        ActionWorkflowTraceLogger.logError(workflowEx)
        val error = workflowEx.toExecutionError()
        collector.emit(ActionExecutionEvent.Failed(error))
        emitTerminal(workflow, startedAt, steps, ActionExecutionStatus.FAILED, collector)
    }

    private suspend fun emitTerminal(
        workflow: ActionWorkflow,
        startedAt: Long,
        steps: List<ActionExecutionStep>,
        status: String,
        collector: kotlinx.coroutines.flow.FlowCollector<ActionExecutionEvent>,
    ) {
        collector.emit(
            ActionExecutionEvent.Completed(
                ActionExecutionLog(workflow.id, startedAt, now(), status, steps.toPersistentList()),
            )
        )
    }

    private fun errorOutput(output: JsonObject, error: ActionExecutionError): JsonObject {
        return JsonObject(
            output + mapOf(
                ActionErrorKey.ERROR to JsonObject(
                    mapOf(
                        ActionErrorKey.CODE to JsonPrimitive(error.code),
                        ActionErrorKey.MESSAGE to JsonPrimitive(error.message),
                        ActionErrorKey.NODE_ID to JsonPrimitive(error.nodeId.orEmpty()),
                        ActionErrorKey.DETAILS to error.details,
                    )
                ),
            )
        )
    }

    companion object {
        const val DEFAULT_MAX_STEP_COUNT = 5000
    }
}

/**
 * 工作流副作用的宿主执行契约。
 */
fun interface ActionSideEffectHandler {
    /**
     * 在宿主平台执行节点请求的副作用。
     *
     * @param effect 待执行的导航、外部打开或提示操作。
     * @return 平台执行结果，用于驱动工作流出口。
     */
    suspend fun handle(effect: ActionSideEffect): ActionSideEffectResult
}

/**
 * 宿主执行副作用后返回给工作流引擎的结果。
 */
sealed interface ActionSideEffectResult {
    data class Success(val output: JsonObject = JsonObject(emptyMap())) : ActionSideEffectResult
    data object Cancelled : ActionSideEffectResult
    data class Failure(val message: String) : ActionSideEffectResult
}
