package com.xiaoyv.bangumi.shared.data.workflow.engine

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.workflow.exception.ActionValidationCode
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionEdge
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionPortKind
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionPortRef
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionWorkflow
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionLoopConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeRegistry
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionPortSpec
import com.xiaoyv.bangumi.shared.data.workflow.node.core.string
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * 校验工作流拓扑结构、端口数据类型与配置项。
 */
class ActionWorkflowValidator(private val registry: ActionNodeRegistry) {

    /**
     * 校验传入的工作流对象。
     *
     * @param workflow 待校验的工作流。
     * @return 不可变校验结果；调用方应仅在 [ActionWorkflowValidation.isValid] 为 true 时保存或执行。
     */
    fun validate(workflow: ActionWorkflow): ActionWorkflowValidation {
        val issues = buildList {
            if (workflow.formatVersion > ActionWorkflow.CURRENT_FORMAT_VERSION) {
                add(error(ActionValidationCode.UNSUPPORTED_FORMAT, ActionValidationCode.UNSUPPORTED_FORMAT_MSG))
            }
            val nodes = workflow.nodes.associateBy { it.id }
            if (workflow.nodes.size != nodes.size) add(error(ActionValidationCode.DUPLICATE_NODE_ID, ActionValidationCode.DUPLICATE_NODE_ID_MSG))
            if (workflow.entryNodeId.isBlank() || workflow.entryNodeId !in nodes) {
                add(error(ActionValidationCode.INVALID_ENTRY, ActionValidationCode.INVALID_ENTRY_MSG, nodeId = workflow.entryNodeId.ifBlank { null }))
            }
            if (workflow.globalErrorNodeId != null && workflow.globalErrorNodeId !in nodes) {
                add(error(ActionValidationCode.INVALID_ERROR_NODE, ActionValidationCode.INVALID_ERROR_NODE_MSG, nodeId = workflow.globalErrorNodeId))
            } else if (workflow.globalErrorNodeId != null &&
                findPort(
                    nodes.getValue(workflow.globalErrorNodeId).type,
                    ActionPortRef(workflow.globalErrorNodeId, ActionControlPortId.IN),
                    false,
                ) == null
            ) {
                add(error(ActionValidationCode.INVALID_ERROR_NODE, ActionValidationCode.INVALID_ERROR_NODE_IN_PORT_MSG, nodeId = workflow.globalErrorNodeId))
            }
            workflow.nodes.forEach { node ->
                val definition = registry.find(node.type)
                if (definition == null) {
                    add(error(ActionValidationCode.UNKNOWN_NODE, "${ActionValidationCode.UNKNOWN_NODE_MSG}：${node.type}", nodeId = node.id))
                } else {
                    if (node.nodeVersion > definition.spec.latestVersion) {
                        add(error(ActionValidationCode.UNSUPPORTED_NODE_VERSION, "${ActionValidationCode.UNSUPPORTED_NODE_VERSION_MSG}：${node.type}", nodeId = node.id))
                    }
                    definition.spec.requiredConfigKeys.filterTo(linkedSetOf()) { key ->
                        node.config[key] == null
                    }.forEach { key ->
                        add(error(ActionValidationCode.MISSING_CONFIG, "${ActionValidationCode.MISSING_CONFIG_MSG}：$key", nodeId = node.id))
                    }
                }
            }
            validateEdges(workflow.edges, nodes, this)
            validateLoops(workflow, this)
            validateParallelScopes(workflow, this)
            workflow.edges.filter { it.kind == ActionPortKind.DATA }.forEach { edge ->
                add(error(ActionValidationCode.UNSUPPORTED_DATA_EDGE, ActionValidationCode.UNSUPPORTED_DATA_EDGE_MSG, edgeId = edge.id))
            }
            validateControlCycle(workflow, this)
            val declared = workflow.requiredCapabilities.toSet()
            val actual = registry.requiredCapabilities(workflow.nodes)
            if (!declared.containsAll(actual)) {
                add(error(ActionValidationCode.MISSING_CAPABILITY, ActionValidationCode.MISSING_CAPABILITY_MSG))
            }
        }
        return ActionWorkflowValidation(issues = issues.toPersistentList())
    }

    private fun validateEdges(
        edges: List<ActionEdge>,
        nodes: Map<String, ActionNode>,
        issues: MutableList<ActionValidationIssue>,
    ) {
        val edgeIds = edges.groupingBy { it.id }.eachCount()
        edgeIds.filterValues { it > 1 }.keys.forEach { id -> issues += error(ActionValidationCode.DUPLICATE_EDGE_ID, ActionValidationCode.DUPLICATE_EDGE_ID_MSG, edgeId = id) }
        edges.forEach { edge ->
            val source = nodes[edge.source.nodeId]
            val target = nodes[edge.target.nodeId]
            if (source == null || target == null) {
                issues += error(ActionValidationCode.MISSING_EDGE_NODE, ActionValidationCode.MISSING_EDGE_NODE_MSG, edgeId = edge.id)
                return@forEach
            }
            if (edge.source.nodeId == edge.target.nodeId) {
                issues += error(ActionValidationCode.SELF_EDGE, ActionValidationCode.SELF_EDGE_MSG, edgeId = edge.id)
            }
            val sourcePort = findPort(source.type, edge.source, true)
            val targetPort = findPort(target.type, edge.target, false)
            if (sourcePort == null || targetPort == null) issues += error(ActionValidationCode.INVALID_SOURCE_PORT, ActionValidationCode.INVALID_SOURCE_PORT_MSG, edgeId = edge.id)
            if (targetPort == null) issues += error(ActionValidationCode.INVALID_TARGET_PORT, ActionValidationCode.INVALID_TARGET_PORT_MSG, edgeId = edge.id)
            if (sourcePort != null && targetPort != null &&
                (sourcePort.kind != edge.kind || targetPort.kind != edge.kind)
            ) {
                issues += error(ActionValidationCode.PORT_KIND_MISMATCH, ActionValidationCode.PORT_KIND_MISMATCH_MSG, edgeId = edge.id)
            }
        }
        edges.groupBy { it.target }.forEach { (target, linkedEdges) ->
            val node = nodes[target.nodeId] ?: return@forEach
            val spec = findPort(node.type, target, false) ?: return@forEach
            if (linkedEdges.size > spec.maxConnections) {
                issues += error(ActionValidationCode.TOO_MANY_CONNECTIONS, ActionValidationCode.TOO_MANY_CONNECTIONS_MSG, nodeId = node.id)
            }
        }
        edges.groupBy { it.source }.forEach { (source, linkedEdges) ->
            val node = nodes[source.nodeId] ?: return@forEach
            val spec = findPort(node.type, source, true) ?: return@forEach
            if (linkedEdges.size > spec.maxConnections) {
                issues += error(ActionValidationCode.TOO_MANY_BRANCHES, ActionValidationCode.TOO_MANY_BRANCHES_MSG, nodeId = node.id)
            }
        }
    }

    private fun findPort(type: String, ref: ActionPortRef, output: Boolean): ActionPortSpec? {
        val spec = registry.find(type)?.spec ?: return null
        val ports = if (output) spec.outputPorts else spec.inputPorts
        return ports.firstOrNull { it.id == ref.portId }
    }

    private fun validateControlCycle(
        workflow: ActionWorkflow,
        issues: MutableList<ActionValidationIssue>,
    ) {
        val adjacency = workflow.edges.filter { it.kind == ActionPortKind.CONTROL }
            .groupBy({ it.source.nodeId }, { it.target.nodeId })
        val visiting = mutableSetOf<String>()
        val visited = mutableSetOf<String>()
        fun visit(nodeId: String): Boolean {
            if (nodeId in visiting) return true
            if (!visited.add(nodeId)) return false
            visiting += nodeId
            val hasCycle = adjacency[nodeId].orEmpty().any(::visit)
            visiting -= nodeId
            return hasCycle
        }
        if (workflow.nodes.any { visit(it.id) }) {
            issues += error(ActionValidationCode.CONTROL_CYCLE, ActionValidationCode.CONTROL_CYCLE_MSG)
        }
    }

    private fun validateLoops(workflow: ActionWorkflow, issues: MutableList<ActionValidationIssue>) {
        val loopTypes = setOf(ActionNodeType.LOOP_REPEAT, ActionNodeType.LOOP_FOR_EACH, ActionNodeType.LOOP_WHILE)
        val nodes = workflow.nodes.associateBy { it.id }
        val controlTypes = setOf(ActionNodeType.LOOP_NEXT, ActionNodeType.LOOP_CONTINUE, ActionNodeType.LOOP_BREAK)
        workflow.nodes.filter { it.type in loopTypes }.forEach { node ->
            val ports = workflow.edges.filter { it.kind == ActionPortKind.CONTROL && it.source.nodeId == node.id }
                .map { it.source.portId }
                .toSet()
            if (ActionControlPortId.BODY !in ports) issues += error(ActionValidationCode.LOOP_MISSING_BODY, ActionValidationCode.LOOP_MISSING_BODY_MSG, node.id)
            if (ActionControlPortId.COMPLETED !in ports) issues += error(ActionValidationCode.LOOP_MISSING_COMPLETED, ActionValidationCode.LOOP_MISSING_COMPLETED_MSG, node.id)
            node.config[ActionLoopConfigKey.MAX_ITERATIONS]?.jsonPrimitive?.intOrNull?.let {
                if (it <= 0) issues += error(ActionValidationCode.INVALID_LOOP_LIMIT, ActionValidationCode.INVALID_LOOP_LIMIT_MSG, node.id)
            }
            if (node.type == ActionNodeType.LOOP_REPEAT) node.config[ActionLoopConfigKey.COUNT]?.jsonPrimitive?.intOrNull?.let {
                if (it < 0) issues += error(ActionValidationCode.INVALID_REPEAT_COUNT, ActionValidationCode.INVALID_REPEAT_COUNT_MSG, node.id)
            }
            val bodyId = workflow.edges.firstOrNull {
                it.source.nodeId == node.id && it.source.portId == ActionControlPortId.BODY
            }?.target?.nodeId
            if (bodyId != null && !canReachLoopControl(bodyId, node.id, workflow, nodes, controlTypes)) {
                issues += error(ActionValidationCode.LOOP_BODY_NOT_CLOSED, ActionValidationCode.LOOP_BODY_NOT_CLOSED_MSG, node.id)
            }
            if (bodyId != null && hasLoopEscape(bodyId, node.id, workflow, nodes, controlTypes)) {
                issues += error(ActionValidationCode.LOOP_BODY_ESCAPE, ActionValidationCode.LOOP_BODY_ESCAPE_MSG, node.id)
            }
        }
        workflow.nodes.filter { it.type in controlTypes }.forEach { node ->
            val loopId = node.config.string(ActionLoopConfigKey.LOOP_ID)
            if (loopId !in nodes || nodes[loopId]?.type !in loopTypes) {
                issues += error(ActionValidationCode.INVALID_LOOP_CONTROL_TARGET, ActionValidationCode.INVALID_LOOP_CONTROL_TARGET_MSG, node.id)
            } else {
                val bodyId = workflow.edges.firstOrNull {
                    it.kind == ActionPortKind.CONTROL &&
                            it.source.nodeId == loopId &&
                            it.source.portId == ActionControlPortId.BODY
                }?.target?.nodeId
                if (bodyId == null || !isReachable(bodyId, node.id, workflow)) {
                    issues += error(ActionValidationCode.LOOP_CONTROL_OUT_OF_SCOPE, ActionValidationCode.LOOP_CONTROL_OUT_OF_SCOPE_MSG, node.id)
                }
            }
        }
    }

    /**
     * 校验 flow.parallel 与 flow.join 组成的并发域。
     */
    private fun validateParallelScopes(workflow: ActionWorkflow, issues: MutableList<ActionValidationIssue>) {
        val nodes = workflow.nodes.associateBy { it.id }
        val pairedJoins = mutableSetOf<String>()
        workflow.nodes.filter { it.type == ActionNodeType.FLOW_PARALLEL }.forEach { parallel ->
            val roots = workflow.edges.filter {
                it.kind == ActionPortKind.CONTROL && it.source.nodeId == parallel.id && it.source.portId == ActionControlPortId.BRANCHES
            }.map { it.target.nodeId }.distinct()
            if (roots.isEmpty()) {
                issues += error(ActionValidationCode.PARALLEL_MISSING_BRANCH, ActionValidationCode.PARALLEL_MISSING_BRANCH_MSG, parallel.id)
                return@forEach
            }
            val joins = roots.map { reachableJoinNodes(workflow, it, nodes) }
            if (joins.any { it.isEmpty() }) {
                issues += error(ActionValidationCode.PARALLEL_JOIN_MISSING, ActionValidationCode.PARALLEL_JOIN_MISSING_MSG, parallel.id)
                return@forEach
            }
            val commonJoins = joins.reduce { left, right -> left intersect right }
            when {
                commonJoins.isEmpty() -> issues += error(ActionValidationCode.PARALLEL_JOIN_MISSING, ActionValidationCode.PARALLEL_JOIN_MISSING_MSG, parallel.id)
                commonJoins.size > 1 -> issues += error(ActionValidationCode.PARALLEL_JOIN_AMBIGUOUS, ActionValidationCode.PARALLEL_JOIN_AMBIGUOUS_MSG, parallel.id)
                else -> pairedJoins += commonJoins.single()
            }
            if (roots.any { hasNestedParallel(workflow, it, nodes) }) {
                issues += error(ActionValidationCode.PARALLEL_NESTED, ActionValidationCode.PARALLEL_NESTED_MSG, parallel.id)
            }
        }
        workflow.nodes.filter { it.type == ActionNodeType.FLOW_JOIN && it.id !in pairedJoins }.forEach { join ->
            issues += error(ActionValidationCode.JOIN_NOT_PAIRED, ActionValidationCode.JOIN_NOT_PAIRED_MSG, join.id)
        }
    }

    private fun reachableJoinNodes(workflow: ActionWorkflow, startId: String, nodes: Map<String, ActionNode>): Set<String> {
        val visited = mutableSetOf<String>()
        val queue = ArrayDeque<String>().apply { add(startId) }
        val joins = mutableSetOf<String>()
        while (queue.isNotEmpty()) {
            val nodeId = queue.removeFirst()
            if (!visited.add(nodeId)) continue
            val node = nodes[nodeId] ?: continue
            if (node.type == ActionNodeType.FLOW_JOIN) {
                joins += nodeId
            } else {
                workflow.edges.filter { it.kind == ActionPortKind.CONTROL && it.source.nodeId == nodeId }
                    .forEach { edge -> queue.add(edge.target.nodeId) }
            }
        }
        return joins
    }

    private fun hasNestedParallel(workflow: ActionWorkflow, startId: String, nodes: Map<String, ActionNode>): Boolean {
        val visited = mutableSetOf<String>()
        val queue = ArrayDeque<String>().apply { add(startId) }
        while (queue.isNotEmpty()) {
            val nodeId = queue.removeFirst()
            if (!visited.add(nodeId)) continue
            val node = nodes[nodeId] ?: continue
            if (node.type == ActionNodeType.FLOW_JOIN) continue
            if (node.type == ActionNodeType.FLOW_PARALLEL) return true
            workflow.edges.filter { it.kind == ActionPortKind.CONTROL && it.source.nodeId == nodeId }
                .forEach { edge -> queue.add(edge.target.nodeId) }
        }
        return false
    }

    private fun canReachLoopControl(
        startId: String,
        loopId: String,
        workflow: ActionWorkflow,
        nodes: Map<String, ActionNode>,
        controlTypes: Set<String>,
    ): Boolean {
        val visiting = mutableSetOf<String>()
        fun visit(currentId: String): Boolean {
            if (!visiting.add(currentId)) return false
            val node = nodes[currentId] ?: return false
            if (node.type in controlTypes) {
                val targetLoopId = node.config.string(ActionLoopConfigKey.LOOP_ID)
                if (targetLoopId == loopId) return true
            }
            val nextTargets = workflow.edges.filter {
                it.kind == ActionPortKind.CONTROL && it.source.nodeId == currentId
            }.map { it.target.nodeId }
            return nextTargets.any(::visit)
        }
        return visit(startId)
    }

    private fun hasLoopEscape(
        startId: String,
        loopId: String,
        workflow: ActionWorkflow,
        nodes: Map<String, ActionNode>,
        controlTypes: Set<String>,
    ): Boolean {
        val visiting = mutableSetOf<String>()
        fun visit(currentId: String): Boolean {
            if (!visiting.add(currentId)) return false
            val node = nodes[currentId] ?: return true
            if (node.type in controlTypes && node.config.string(ActionLoopConfigKey.LOOP_ID) == loopId) {
                return false
            }
            val outgoingEdges = workflow.edges.filter {
                it.kind == ActionPortKind.CONTROL && it.source.nodeId == currentId
            }
            if (outgoingEdges.isEmpty()) return true
            return outgoingEdges.map { it.target.nodeId }.any(::visit)
        }
        return visit(startId)
    }

    private fun isReachable(startId: String, targetId: String, workflow: ActionWorkflow): Boolean {
        val visited = mutableSetOf<String>()
        val queue = ArrayDeque<String>()
        queue.add(startId)
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (current == targetId) return true
            if (visited.add(current)) {
                workflow.edges.filter {
                    it.kind == ActionPortKind.CONTROL && it.source.nodeId == current
                }.forEach { queue.add(it.target.nodeId) }
            }
        }
        return false
    }

    private fun error(
        code: String,
        message: String,
        nodeId: String? = null,
        edgeId: String? = null,
    ) = ActionValidationIssue(
        code = code,
        message = message,
        level = ActionValidationLevel.ERROR,
        nodeId = nodeId,
        edgeId = edgeId,
    )
}

/**
 * 工作流静态图与配置校验结果。
 */
@Immutable
data class ActionWorkflowValidation(
    val issues: SerializeList<ActionValidationIssue> = persistentListOf(),
) {
    val isValid: Boolean get() = issues.none { it.level == ActionValidationLevel.ERROR }
}

@Immutable
data class ActionValidationIssue(
    val code: String,
    val message: String,
    val level: String,
    val nodeId: String? = null,
    val edgeId: String? = null,
)

object ActionValidationLevel {
    const val ERROR = "error"
}
