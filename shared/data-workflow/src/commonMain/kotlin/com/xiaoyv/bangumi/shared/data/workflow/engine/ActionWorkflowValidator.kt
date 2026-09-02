package com.xiaoyv.bangumi.shared.data.workflow.engine

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionEdge
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionLoopConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionPortKind
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionPortRef
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionWorkflow
import com.xiaoyv.bangumi.shared.data.workflow.node.ActionNodeRegistry
import com.xiaoyv.bangumi.shared.data.workflow.node.ActionPortSpec
import com.xiaoyv.bangumi.shared.data.workflow.node.support.string
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * 工作流图校验器。编辑、导入和执行前均应调用。
 */
class ActionWorkflowValidator(
    private val registry: ActionNodeRegistry,
) {
    /**
     * 检查节点、端口、连线、权限与执行器当前支持范围。
     *
     * @param workflow 待校验的工作流。
     * @return 不可变校验结果；调用方应仅在 [ActionWorkflowValidation.isValid] 为 true 时保存或执行。
     */
    fun validate(workflow: ActionWorkflow): ActionWorkflowValidation {
        val issues = buildList {
            if (workflow.formatVersion > ActionWorkflow.CURRENT_FORMAT_VERSION) {
                add(error("unsupported_format", "工作流格式版本过高"))
            }
            val nodes = workflow.nodes.associateBy { it.id }
            if (workflow.nodes.size != nodes.size) add(error("duplicate_node_id", "存在重复节点 ID"))
            if (workflow.entryNodeId.isBlank() || workflow.entryNodeId !in nodes) {
                add(error("invalid_entry", "入口节点不存在", nodeId = workflow.entryNodeId.ifBlank { null }))
            }
            if (workflow.globalErrorNodeId != null && workflow.globalErrorNodeId !in nodes) {
                add(error("invalid_error_node", "全局错误节点不存在", nodeId = workflow.globalErrorNodeId))
            } else if (workflow.globalErrorNodeId != null &&
                findPort(
                    nodes.getValue(workflow.globalErrorNodeId).type,
                    ActionPortRef(workflow.globalErrorNodeId, ActionControlPortId.IN),
                    false,
                ) == null
            ) {
                add(error("invalid_error_node", "全局错误节点必须具有 in 控制输入端口", nodeId = workflow.globalErrorNodeId))
            }
            workflow.nodes.forEach { node ->
                val definition = registry.find(node.type)
                if (definition == null) {
                    add(error("unknown_node", "不支持节点类型：${node.type}", nodeId = node.id))
                } else {
                    if (node.nodeVersion > definition.spec.latestVersion) {
                        add(error("unsupported_node_version", "节点版本过高：${node.type}", nodeId = node.id))
                    }
                    definition.spec.requiredConfigKeys.filterTo(linkedSetOf()) { key ->
                        node.config[key] == null
                    }.forEach { key ->
                        add(error("missing_config", "缺少节点配置：$key", nodeId = node.id))
                    }
                }
            }
            validateEdges(workflow.edges, nodes, this)
            validateLoops(workflow, this)
            workflow.edges.filter { it.kind == ActionPortKind.DATA }.forEach { edge ->
                add(error("unsupported_data_edge", "当前执行器尚不支持数据连线，请使用变量引用", edgeId = edge.id))
            }
            validateControlCycle(workflow, this)
            val declared = workflow.requiredCapabilities.toSet()
            val actual = registry.requiredCapabilities(workflow.nodes)
            if (!declared.containsAll(actual)) {
                add(error("missing_capability", "工作流未声明所有节点所需权限"))
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
        edgeIds.filterValues { it > 1 }.keys.forEach { id -> issues += error("duplicate_edge_id", "存在重复连线 ID", edgeId = id) }
        edges.forEach { edge ->
            val source = nodes[edge.source.nodeId]
            val target = nodes[edge.target.nodeId]
            if (source == null || target == null) {
                issues += error("missing_edge_node", "连线引用了不存在的节点", edgeId = edge.id)
                return@forEach
            }
            if (edge.source.nodeId == edge.target.nodeId) {
                issues += error("self_edge", "节点不能连接自身", edgeId = edge.id)
            }
            val sourcePort = findPort(source.type, edge.source, true)
            val targetPort = findPort(target.type, edge.target, false)
            if (sourcePort == null || targetPort == null) issues += error("invalid_source_port", "起始端口不存在或不是输出端口", edgeId = edge.id)
            if (targetPort == null) issues += error("invalid_target_port", "目标端口不存在或不是输入端口", edgeId = edge.id)
            if (sourcePort != null && targetPort != null &&
                (sourcePort.kind != edge.kind || targetPort.kind != edge.kind)
            ) {
                issues += error("port_kind_mismatch", "连线类型与端口类型不匹配", edgeId = edge.id)
            }
        }
        edges.groupBy { it.target }.forEach { (target, linkedEdges) ->
            val node = nodes[target.nodeId] ?: return@forEach
            val spec = findPort(node.type, target, false) ?: return@forEach
            if (linkedEdges.size > spec.maxConnections) {
                issues += error("too_many_connections", "输入端口超过最大连接数", nodeId = node.id)
            }
        }
        edges.groupBy { it.source }.forEach { (source, linkedEdges) ->
            val node = nodes[source.nodeId] ?: return@forEach
            val spec = findPort(node.type, source, true) ?: return@forEach
            if (linkedEdges.size > spec.maxConnections) {
                issues += error("too_many_branches", "输出端口超过最大连接数", nodeId = node.id)
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
        val loopTypes = setOf(ActionNodeType.LOOP_REPEAT, ActionNodeType.LOOP_FOR_EACH, ActionNodeType.LOOP_WHILE)
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
            issues += error("control_cycle", "当前版本不支持普通控制连线形成循环")
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
            if (ActionControlPortId.BODY !in ports) issues += error("loop_missing_body", "循环节点必须连接 body 出口", node.id)
            if (ActionControlPortId.COMPLETED !in ports) issues += error("loop_missing_completed", "循环节点必须连接 completed 出口", node.id)
            node.config[ActionLoopConfigKey.MAX_ITERATIONS]?.jsonPrimitive?.intOrNull?.let {
                if (it <= 0) issues += error("invalid_loop_limit", "maxIterations 必须大于 0", node.id)
            }
            if (node.type == ActionNodeType.LOOP_REPEAT) node.config[ActionLoopConfigKey.COUNT]?.jsonPrimitive?.intOrNull?.let {
                if (it < 0) issues += error("invalid_repeat_count", "count 不能小于 0", node.id)
            }
            val bodyId = workflow.edges.firstOrNull {
                it.source.nodeId == node.id && it.source.portId == ActionControlPortId.BODY
            }?.target?.nodeId
            if (bodyId != null && !canReachLoopControl(bodyId, node.id, workflow, nodes, controlTypes)) {
                issues += error("loop_body_not_closed", "循环体必须能到达同一循环的 next、continue 或 break 节点", node.id)
            }
            if (bodyId != null && hasLoopEscape(bodyId, node.id, workflow, nodes, controlTypes)) {
                issues += error("loop_body_escape", "循环体存在未经过循环控制节点就离开的路径", node.id)
            }
        }
        workflow.nodes.filter { it.type in controlTypes }.forEach { node ->
            val loopId = node.config.string(ActionLoopConfigKey.LOOP_ID)
            if (loopId !in nodes || nodes[loopId]?.type !in loopTypes) {
                issues += error("invalid_loop_control_target", "循环控制节点的 loopId 必须指向循环节点", node.id)
            } else {
                val bodyId = workflow.edges.firstOrNull {
                    it.kind == ActionPortKind.CONTROL &&
                            it.source.nodeId == loopId &&
                            it.source.portId == ActionControlPortId.BODY
                }?.target?.nodeId
                if (bodyId == null || !isReachable(bodyId, node.id, workflow)) {
                    issues += error("loop_control_out_of_scope", "循环控制节点必须位于其 loopId 的循环体内", node.id)
                }
            }
        }
    }

    private fun canReachLoopControl(
        startId: String,
        loopId: String,
        workflow: ActionWorkflow,
        nodes: Map<String, ActionNode>,
        controlTypes: Set<String>,
    ): Boolean {
        val adjacency = workflow.edges.filter { it.kind == ActionPortKind.CONTROL }
            .groupBy({ it.source.nodeId }, { it.target.nodeId })
        val loopTypes = setOf(ActionNodeType.LOOP_REPEAT, ActionNodeType.LOOP_FOR_EACH, ActionNodeType.LOOP_WHILE)
        val visited = mutableSetOf<String>()
        val pending = ArrayDeque<String>().apply { add(startId) }
        while (pending.isNotEmpty()) {
            val nodeId = pending.removeFirst()
            if (!visited.add(nodeId)) continue
            val node = nodes[nodeId] ?: continue
            if (node.type in controlTypes && node.config.string(ActionLoopConfigKey.LOOP_ID) == loopId) return true
            adjacency[nodeId].orEmpty().forEach(pending::addLast)
        }
        return false
    }

    private fun hasLoopEscape(
        startId: String,
        loopId: String,
        workflow: ActionWorkflow,
        nodes: Map<String, ActionNode>,
        controlTypes: Set<String>,
    ): Boolean {
        val adjacency = workflow.edges.filter { it.kind == ActionPortKind.CONTROL }
            .groupBy({ it.source.nodeId }, { it.target.nodeId })
        val loopTypes = setOf(ActionNodeType.LOOP_REPEAT, ActionNodeType.LOOP_FOR_EACH, ActionNodeType.LOOP_WHILE)
        val visited = mutableSetOf<String>()
        val pending = ArrayDeque<String>().apply { add(startId) }
        while (pending.isNotEmpty()) {
            val nodeId = pending.removeFirst()
            if (!visited.add(nodeId)) continue
            val node = nodes[nodeId] ?: return true
            if (node.type in controlTypes) {
                if (node.config.string(ActionLoopConfigKey.LOOP_ID) != loopId) return true
                continue
            }
            val targets = if (node.type in loopTypes) {
                workflow.edges.filter {
                    it.kind == ActionPortKind.CONTROL &&
                            it.source.nodeId == nodeId &&
                            it.source.portId == ActionControlPortId.COMPLETED
                }
                    .map { it.target.nodeId }
            } else {
                adjacency[nodeId].orEmpty()
            }
            if (targets.isEmpty()) return true
            targets.forEach(pending::addLast)
        }
        return false
    }

    private fun isReachable(startId: String, targetId: String, workflow: ActionWorkflow): Boolean {
        val adjacency = workflow.edges.filter { it.kind == ActionPortKind.CONTROL }
            .groupBy({ it.source.nodeId }, { it.target.nodeId })
        val visited = mutableSetOf<String>()
        val pending = ArrayDeque<String>().apply { add(startId) }
        while (pending.isNotEmpty()) {
            val nodeId = pending.removeFirst()
            if (!visited.add(nodeId)) continue
            if (nodeId == targetId) return true
            adjacency[nodeId].orEmpty().forEach(pending::addLast)
        }
        return false
    }

    private fun error(
        code: String,
        message: String,
        nodeId: String? = null,
        edgeId: String? = null,
    ) = ActionValidationIssue(code, message, ActionValidationLevel.ERROR, nodeId, edgeId)
}

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
    const val WARNING = "warning"
}
