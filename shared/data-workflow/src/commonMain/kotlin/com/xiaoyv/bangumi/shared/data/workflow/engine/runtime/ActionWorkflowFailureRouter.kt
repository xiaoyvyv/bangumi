package com.xiaoyv.bangumi.shared.data.workflow.engine.runtime

import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionWorkflow
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId

/**
 * 为节点执行失败选择后续控制流。
 */
internal object ActionWorkflowFailureRouter {
    fun resolve(
        workflow: ActionWorkflow,
        nodeId: String,
        activeLoopId: String?
    ): ActionFailureRoute {
        val nodeTargets = WorkflowRuntimeGraph.findNextNodeIds(workflow, nodeId, ActionControlPortId.FAILURE)
        val loopTargets = if (nodeTargets.isNotEmpty()) emptyList() else {
            activeLoopId?.let { WorkflowRuntimeGraph.findNextNodeIds(workflow, it, ActionControlPortId.FAILURE) }.orEmpty()
        }
        return ActionFailureRoute(
            targetNodeIds = nodeTargets.ifEmpty { loopTargets.ifEmpty { listOfNotNull(workflow.globalErrorNodeId) } },
            loopIdToAbort = activeLoopId?.takeIf { loopTargets.isNotEmpty() },
        )
    }
}

/**
 * 失败路由的解析结果。
 */
internal data class ActionFailureRoute(val targetNodeIds: List<String>, val loopIdToAbort: String?)
