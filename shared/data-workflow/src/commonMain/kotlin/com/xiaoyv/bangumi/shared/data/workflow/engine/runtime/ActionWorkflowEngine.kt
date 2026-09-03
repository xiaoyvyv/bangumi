package com.xiaoyv.bangumi.shared.data.workflow.engine.runtime

import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionSideEffectHandler
import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionSideEffectResult
import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionWorkflowValidator
import com.xiaoyv.bangumi.shared.data.workflow.exception.ActionErrorCode
import com.xiaoyv.bangumi.shared.data.workflow.exception.ActionNodeExecutionException
import com.xiaoyv.bangumi.shared.data.workflow.exception.ActionWorkflowException
import com.xiaoyv.bangumi.shared.data.workflow.exception.ActionWorkflowTraceLogger
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionPortKind
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionWorkflow
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionEvent
import com.xiaoyv.bangumi.shared.data.workflow.model.log.ActionExecutionError
import com.xiaoyv.bangumi.shared.data.workflow.model.log.ActionExecutionStatus
import com.xiaoyv.bangumi.shared.data.workflow.model.log.ActionExecutionStep
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionErrorKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionLoopConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionLoopContextKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeRegistry
import com.xiaoyv.bangumi.shared.data.workflow.node.core.string
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
    private val parallelExecutor = ActionParallelExecutor(registry, now)
    private val eventEmitter = ActionExecutionEventEmitter(now)

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
        runExecutionLoop(
            workflow = workflow,
            initialContext = initialContext,
            sideEffectHandler = sideEffectHandler,
            maxStepCount = maxStepCount,
            collector = this
        )
    }

    /**
     * 驱动一次工作流运行期调度。
     *
     * 公共入口只负责建立事件流；具体的校验、队列推进和节点编排统一收敛在此处，
     * 便于后续替换为独立的运行期会话实现。
     */
    private suspend fun runExecutionLoop(
        workflow: ActionWorkflow,
        initialContext: ActionExecutionContext,
        sideEffectHandler: ActionSideEffectHandler,
        maxStepCount: Int,
        collector: kotlinx.coroutines.flow.FlowCollector<ActionExecutionEvent>,
    ) {
        if (!validateExecutableWorkflow(workflow, collector)) return

        val startedAt = now()
        val steps = mutableListOf<ActionExecutionStep>()
        val nodes = workflow.nodes.associateBy { it.id }
        var context = initialContext.copy(workflowId = workflow.id)
        val loops = LoopExecutionController()
        var completionStatus = ActionExecutionStatus.SUCCESS

        // 待执行节点就绪队列与运行状态追踪
        val readyQueue = ArrayDeque<String>()
        val executedNodes = mutableSetOf<String>()
        val activatedEdges = mutableSetOf<String>() // 已激活的控制边 ID 集合

        readyQueue.add(workflow.entryNodeId)
        var stepCount = 0
        collector.emit(ActionExecutionEvent.Started(workflow.id))

        // 预建边索引以提高查找效率
        val incomingControlEdges = workflow.edges.filter { it.kind == ActionPortKind.CONTROL }.groupBy { it.target.nodeId }
        val outgoingControlEdges = workflow.edges.filter { it.kind == ActionPortKind.CONTROL }.groupBy { it.source.nodeId }

        while (readyQueue.isNotEmpty()) {
            if (++stepCount > maxStepCount) {
                eventEmitter.emitFailure(workflow, startedAt, steps, ActionErrorCode.STEP_LIMIT, ActionErrorCode.STEP_LIMIT_MSG, readyQueue.firstOrNull(), collector)
                return
            }

            val currentNodeId = readyQueue.removeFirst()
            val node = nodes[currentNodeId] ?: run {
                eventEmitter.emitFailure(workflow, startedAt, steps, ActionErrorCode.MISSING_NODE, ActionErrorCode.MISSING_NODE_MSG, currentNodeId, collector)
                return
            }
            val definition = registry.find(node.type) ?: run {
                eventEmitter.emitFailure(workflow, startedAt, steps, ActionErrorCode.UNKNOWN_NODE, "${ActionErrorCode.UNKNOWN_NODE_MSG}：${node.type}", node.id, collector)
                return
            }

            val nodeStartedAt = now()
            collector.emit(ActionExecutionEvent.NodeStarted(node.id))

            val loopTransition = try {
                resolveLoopTransition(workflow, node, context, loops)
            } catch (throwable: Throwable) {
                if (throwable is CancellationException) throw throwable
                val loopNodeId = when (node.type) {
                    ActionNodeType.LOOP_REPEAT, ActionNodeType.LOOP_FOR_EACH, ActionNodeType.LOOP_WHILE -> node.id
                    else -> loops.activeLoopId()
                }
                val failureTarget = loopNodeId?.let { WorkflowRuntimeGraph.findNextNodeId(workflow, it, ActionControlPortId.FAILURE) }
                if (failureTarget != null) {
                    context = loops.abort(loopNodeId, context)
                    if (failureTarget !in readyQueue) readyQueue.add(failureTarget)
                    continue
                }
                eventEmitter.emitFailure(
                    workflow,
                    startedAt,
                    steps,
                    ActionErrorCode.LOOP_EXECUTION_FAILED,
                    throwable.message.orEmpty().ifBlank { ActionErrorCode.LOOP_EXECUTION_FAILED_MSG },
                    node.id,
                    collector
                )
                return
            }

            if (loopTransition != null) {
                context = loopTransition.context
                val loopOutput = JsonObject(mapOf(ActionLoopContextKey.LOOP to context.loop))
                steps += ActionExecutionStep(node.id, nodeStartedAt, now(), loopTransition.outputPortId, loopOutput)
                collector.emit(ActionExecutionEvent.NodeCompleted(node.id, loopTransition.outputPortId, loopOutput))
                executedNodes += node.id

                // 当进入新的循环体迭代时，重置循环体内节点的已执行状态，确保循环体内部的合流 (Join) 节点能正确等待当前轮迭代的依赖
                if (loopTransition.outputPortId == ActionControlPortId.BODY) {
                    val loopBodyNodes = WorkflowRuntimeGraph.findReachableNodesInLoopBody(workflow, node.id)
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
                val failureOutput = eventEmitter.errorOutput(JsonObject(emptyMap()), error)
                steps += ActionExecutionStep(node.id, nodeStartedAt, now(), ActionControlPortId.FAILURE, failureOutput, error)
                context = context.copy(stepOutputs = (context.stepOutputs + (node.id to failureOutput)).toPersistentMap())
                collector.emit(ActionExecutionEvent.Failed(error))
                val failureRoute = ActionWorkflowFailureRouter.resolve(workflow, node.id, loops.activeLoopId())
                if (failureRoute.targetNodeIds.isEmpty() || node.id in failureRoute.targetNodeIds) {
                    eventEmitter.emitTerminal(workflow, startedAt, steps, ActionExecutionStatus.FAILED, collector)
                    return
                }
                completionStatus = ActionExecutionStatus.FAILED
                failureRoute.loopIdToAbort?.let { loopId ->
                    context = loops.abort(loopId, context)
                }
                failureRoute.targetNodeIds.forEach { targetId ->
                    if (targetId !in readyQueue) readyQueue.add(targetId)
                }
                continue
            }

            var output = result.output
            if (result.sideEffect != null) {
                collector.emit(ActionExecutionEvent.SideEffectRequested(node.id, result.sideEffect))
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
                        collector.emit(ActionExecutionEvent.Failed(error))
                        eventEmitter.emitTerminal(workflow, startedAt, steps, ActionExecutionStatus.CANCELLED, collector)
                        return
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
                        val failureOutput = eventEmitter.errorOutput(output, error)
                        steps += ActionExecutionStep(node.id, nodeStartedAt, now(), ActionControlPortId.FAILURE, failureOutput, error)
                        context = context.copy(stepOutputs = (context.stepOutputs + (node.id to failureOutput)).toPersistentMap())
                        collector.emit(ActionExecutionEvent.NodeCompleted(node.id, ActionControlPortId.FAILURE, failureOutput))
                        val failureRoute = ActionWorkflowFailureRouter.resolve(workflow, node.id, loops.activeLoopId())
                        if (failureRoute.targetNodeIds.isEmpty() || node.id in failureRoute.targetNodeIds) {
                            collector.emit(ActionExecutionEvent.Failed(error))
                            eventEmitter.emitTerminal(workflow, startedAt, steps, ActionExecutionStatus.FAILED, collector)
                            return
                        }
                        completionStatus = ActionExecutionStatus.FAILED
                        failureRoute.loopIdToAbort?.let { loopId ->
                            context = loops.abort(loopId, context)
                        }
                        collector.emit(ActionExecutionEvent.Failed(error))
                        failureRoute.targetNodeIds.forEach { targetId ->
                            if (targetId !in readyQueue) readyQueue.add(targetId)
                        }
                        continue
                    }
                }
            }

            if (node.type == ActionNodeType.FLOW_PARALLEL) {
                val parallelOutput = JsonObject(output)
                context = context.copy(
                    variables = (context.variables + result.variableUpdates).toPersistentMap(),
                    stepOutputs = (context.stepOutputs + (node.id to parallelOutput)).toPersistentMap(),
                )
                steps += ActionExecutionStep(node.id, nodeStartedAt, now(), ActionControlPortId.BRANCHES, parallelOutput)
                collector.emit(ActionExecutionEvent.NodeCompleted(node.id, ActionControlPortId.BRANCHES, parallelOutput))
                executedNodes += node.id

                val parallelResult = try {
                    parallelExecutor.execute(workflow, node, context, sideEffectHandler)
                } catch (throwable: Throwable) {
                    if (throwable is CancellationException) throw throwable
                    val message = throwable.message.orEmpty().ifBlank { "并发分支执行失败" }
                    val error = ActionExecutionError(ActionErrorCode.NODE_EXECUTION_FAILED, message, node.id)
                    steps += ActionExecutionStep(node.id, now(), now(), ActionControlPortId.FAILURE, error = error)
                    collector.emit(ActionExecutionEvent.Failed(error))
                    val failureTargets = WorkflowRuntimeGraph.findNextNodeIds(workflow, node.id, ActionControlPortId.FAILURE)
                    if (failureTargets.isEmpty()) {
                        eventEmitter.emitTerminal(workflow, startedAt, steps, ActionExecutionStatus.FAILED, collector)
                        return
                    }
                    completionStatus = ActionExecutionStatus.FAILED
                    failureTargets.forEach { targetId -> if (targetId !in readyQueue) readyQueue.add(targetId) }
                    continue
                }
                if (stepCount + parallelResult.steps.size > maxStepCount) {
                    eventEmitter.emitFailure(workflow, startedAt, steps, ActionErrorCode.STEP_LIMIT, ActionErrorCode.STEP_LIMIT_MSG, node.id, collector)
                    return
                }
                stepCount += parallelResult.steps.size
                parallelResult.events.forEach { event -> collector.emit(event) }
                steps += parallelResult.steps
                context = parallelExecutor.mergeContexts(context, parallelResult.contexts)
                if (parallelResult.joinNodeId !in readyQueue) readyQueue.add(parallelResult.joinNodeId)
                continue
            }

            context = context.copy(
                variables = (context.variables + result.variableUpdates).toPersistentMap(),
                stepOutputs = (context.stepOutputs + (node.id to output)).toPersistentMap(),
            )
            steps += ActionExecutionStep(node.id, nodeStartedAt, now(), result.outputPortId, output)
            collector.emit(ActionExecutionEvent.NodeCompleted(node.id, result.outputPortId, output))
            executedNodes += node.id

            // 激活当前节点端口产生的输出边
            val outgoingEdges = outgoingControlEdges[node.id].orEmpty().filter { it.source.portId == result.outputPortId }
            outgoingEdges.forEach { activatedEdges += it.id }

            // 检查并入队就绪的下游节点（完美支持 Fork-Join 多路分叉与汇入合流）
            val targetNodeIds = outgoingEdges.map { it.target.nodeId }.distinct()
            for (targetId in targetNodeIds) {
                if (WorkflowRuntimeGraph.isNodeReadyToExecute(targetId, activatedEdges, executedNodes, incomingControlEdges)) {
                    if (targetId !in readyQueue) {
                        readyQueue.add(targetId)
                    }
                }
            }
        }

        eventEmitter.emitTerminal(workflow, startedAt, steps, completionStatus, collector)
    }

    /**
     * 校验工作流是否可进入运行期，并在不可运行时发射对应失败事件。
     */
    private suspend fun validateExecutableWorkflow(
        workflow: ActionWorkflow,
        collector: kotlinx.coroutines.flow.FlowCollector<ActionExecutionEvent>,
    ): Boolean {
        val validation = validator.validate(workflow)
        if (!validation.isValid) {
            val firstIssue = validation.issues.firstOrNull()
            val issueNode = firstIssue?.nodeId?.let { id -> workflow.nodes.find { it.id == id } }
            val workflowException = ActionWorkflowException(
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
                hint = ActionErrorCode.INVALID_WORKFLOW_HINT,
            )
            ActionWorkflowTraceLogger.logError(workflowException)
            collector.emit(ActionExecutionEvent.Failed(workflowException.toExecutionError()))
            return false
        }
        if (workflow.enabled) return true

        val workflowException = ActionWorkflowException(
            code = ActionErrorCode.WORKFLOW_DISABLED,
            messageText = ActionErrorCode.WORKFLOW_DISABLED_MSG,
            workflowId = workflow.id,
            workflowName = workflow.name,
            hint = ActionErrorCode.WORKFLOW_DISABLED_HINT,
        )
        ActionWorkflowTraceLogger.logError(workflowException)
        collector.emit(ActionExecutionEvent.Failed(workflowException.toExecutionError()))
        return false
    }

    /**
     * 执行循环控制节点的状态转换；非循环节点返回 null。
     */
    private fun resolveLoopTransition(
        workflow: ActionWorkflow,
        node: com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode,
        context: ActionExecutionContext,
        loops: LoopExecutionController,
    ) = when (node.type) {
        ActionNodeType.LOOP_REPEAT,
        ActionNodeType.LOOP_FOR_EACH,
        ActionNodeType.LOOP_WHILE -> loops.enter(
            node,
            context,
            WorkflowRuntimeGraph.findNextNodeId(workflow, node.id, ActionControlPortId.BODY),
            WorkflowRuntimeGraph.findNextNodeId(workflow, node.id, ActionControlPortId.COMPLETED),
        )

        ActionNodeType.LOOP_NEXT,
        ActionNodeType.LOOP_CONTINUE -> loops.next(node.config.string(ActionLoopConfigKey.LOOP_ID), context)

        ActionNodeType.LOOP_BREAK -> loops.breakLoop(node.config.string(ActionLoopConfigKey.LOOP_ID), context)
        else -> null
    }

    companion object {
        const val DEFAULT_MAX_STEP_COUNT = 5000
    }
}
