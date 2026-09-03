package com.xiaoyv.bangumi.shared.data.workflow.engine.runtime

import com.xiaoyv.bangumi.shared.data.workflow.exception.ActionWorkflowException
import com.xiaoyv.bangumi.shared.data.workflow.exception.ActionWorkflowTraceLogger
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionWorkflow
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionEvent
import com.xiaoyv.bangumi.shared.data.workflow.model.log.ActionExecutionError
import com.xiaoyv.bangumi.shared.data.workflow.model.log.ActionExecutionLog
import com.xiaoyv.bangumi.shared.data.workflow.model.log.ActionExecutionStatus
import com.xiaoyv.bangumi.shared.data.workflow.model.log.ActionExecutionStep
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionErrorKey
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * 统一构造并发射工作流的失败和终态事件。
 *
 * @param now 当前时间戳提供者 (毫秒)。
 */
internal class ActionExecutionEventEmitter(
    private val now: () -> Long,
) {
    /**
     * 发射不可恢复的失败事件和失败终态。
     */
    suspend fun emitFailure(
        workflow: ActionWorkflow,
        startedAt: Long,
        steps: List<ActionExecutionStep>,
        code: String,
        message: String,
        nodeId: String?,
        collector: FlowCollector<ActionExecutionEvent>,
        cause: Throwable? = null,
    ) {
        val node = nodeId?.let { id -> workflow.nodes.find { it.id == id } }
        val workflowException = ActionWorkflowException(
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
        ActionWorkflowTraceLogger.logError(workflowException)
        collector.emit(ActionExecutionEvent.Failed(workflowException.toExecutionError()))
        emitTerminal(workflow, startedAt, steps, ActionExecutionStatus.FAILED, collector)
    }

    /**
     * 发射工作流最终执行日志。
     */
    suspend fun emitTerminal(
        workflow: ActionWorkflow,
        startedAt: Long,
        steps: List<ActionExecutionStep>,
        status: String,
        collector: FlowCollector<ActionExecutionEvent>,
    ) {
        collector.emit(
            ActionExecutionEvent.Completed(
                ActionExecutionLog(workflow.id, startedAt, now(), status, steps.toPersistentList()),
            ),
        )
    }

    /**
     * 将结构化错误写入节点输出，供失败分支继续引用。
     */
    fun errorOutput(output: JsonObject, error: ActionExecutionError): JsonObject = JsonObject(
        output + mapOf(
            ActionErrorKey.ERROR to JsonObject(
                mapOf(
                    ActionErrorKey.CODE to JsonPrimitive(error.code),
                    ActionErrorKey.MESSAGE to JsonPrimitive(error.message),
                    ActionErrorKey.NODE_ID to JsonPrimitive(error.nodeId.orEmpty()),
                    ActionErrorKey.DETAILS to error.details,
                ),
            ),
        ),
    )
}
