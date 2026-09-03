package com.xiaoyv.bangumi.shared.data.workflow.engine.runtime

import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionEdge
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionPortKind
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionWorkflow
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType

/**
 * 工作流运行期的控制边拓扑查询。
 */
internal object WorkflowRuntimeGraph {
    fun isNodeReadyToExecute(nodeId: String, activatedEdges: Set<String>, executedNodes: Set<String>, incomingControlEdges: Map<String, List<ActionEdge>>): Boolean {
        val incoming = incomingControlEdges[nodeId].orEmpty()
        if (incoming.isEmpty()) return true
        val activeIncoming = incoming.filter { it.id in activatedEdges }
        return activeIncoming.isNotEmpty() && activeIncoming.map { it.source.nodeId }.none { it !in executedNodes }
    }

    fun findNextNodeIds(workflow: ActionWorkflow, nodeId: String, portId: String): List<String> = workflow.edges.filter {
        it.kind == ActionPortKind.CONTROL && it.source.nodeId == nodeId && it.source.portId == portId
    }.map { it.target.nodeId }.distinct()

    fun findNextNodeId(workflow: ActionWorkflow, nodeId: String, portId: String): String? =
        findNextNodeIds(workflow, nodeId, portId).firstOrNull()

    fun findReachableNodesInLoopBody(workflow: ActionWorkflow, loopNodeId: String): Set<String> {
        val bodyEdge = workflow.edges.firstOrNull {
            it.kind == ActionPortKind.CONTROL && it.source.nodeId == loopNodeId && it.source.portId == ActionControlPortId.BODY
        } ?: return emptySet()
        val reachable = mutableSetOf<String>()
        val queue = ArrayDeque<String>().apply { add(bodyEdge.target.nodeId) }
        val loopControlTypes = setOf(ActionNodeType.LOOP_NEXT, ActionNodeType.LOOP_CONTINUE, ActionNodeType.LOOP_BREAK)
        while (queue.isNotEmpty()) {
            val nodeId = queue.removeFirst()
            if (!reachable.add(nodeId)) continue
            if (workflow.nodes.firstOrNull { it.id == nodeId }?.type in loopControlTypes) continue
            workflow.edges.filter { it.kind == ActionPortKind.CONTROL && it.source.nodeId == nodeId && it.target.nodeId != loopNodeId }.forEach { queue.add(it.target.nodeId) }
        }
        return reachable
    }
}
