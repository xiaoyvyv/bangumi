package com.xiaoyv.bangumi.features.workflows.business.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dk.kuiver.model.buildKuiver
import com.dk.kuiver.model.edges
import com.dk.kuiver.model.layout.LayoutConfig
import com.dk.kuiver.model.layout.LayoutDirection
import com.dk.kuiver.model.nodes
import com.dk.kuiver.rememberKuiverViewerState
import com.dk.kuiver.renderer.KuiverViewer
import com.dk.kuiver.renderer.KuiverViewerConfig
import com.dk.kuiver.ui.KuiverColors
import com.dk.kuiver.ui.LocalKuiverColors
import com.dk.kuiver.ui.StyledEdgeContent
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionExecutionStep
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionLoopConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionWorkflow
import com.xiaoyv.bangumi.shared.data.workflow.node.ActionNodeRegistry
import kotlinx.serialization.json.JsonPrimitive

/**
 * 基于 io.github.justdeko:kuiver 的工作流节点与连线 UI 绘制容器。
 *
 * 将 [ActionWorkflow] 的节点图拓扑映射为 Kuiver 节点图，自动计算层级布局并绘制节点间指向连线与手势缩放。
 *
 * @param workflow 工作流模型。
 * @param nodeRegistry 节点注册中心。
 * @param steps 运行时步骤记录（可选）。
 * @param onNodeClick 节点卡片点击事件。
 */
@Composable
fun ActionWorkflowNodeList(
    workflow: ActionWorkflow,
    nodeRegistry: ActionNodeRegistry,
    modifier: Modifier = Modifier,
    steps: List<ActionExecutionStep> = emptyList(),
    onNodeClick: ((String) -> Unit)? = null,
) {
    val nodeMap = remember(workflow) { workflow.nodes.associateBy { it.id } }
    val stepMap = remember(steps) { steps.associateBy { it.nodeId } }

    val displayEdges = remember(workflow) {
        buildList {
            workflow.edges.forEach { edge ->
                add(WorkflowDisplayEdge(edge.source.nodeId, edge.target.nodeId, edge.source.portId))
            }
            workflow.nodes
                .filter { it.type == ActionNodeType.LOOP_NEXT || it.type == ActionNodeType.LOOP_CONTINUE }
                .mapNotNull { node ->
                    val loopNodeId = (node.config[ActionLoopConfigKey.LOOP_ID] as? JsonPrimitive)?.content
                    loopNodeId?.takeIf(nodeMap::containsKey)?.let { targetNodeId ->
                        WorkflowDisplayEdge(node.id, targetNodeId, node.type)
                    }
                }
                .forEach(::add)
        }
    }
    val edgeLabels = remember(displayEdges) {
        displayEdges
            .groupBy { it.sourceNodeId to it.targetNodeId }
            .mapValues { (_, edges) -> edges.joinToString(" / ") { it.label } }
    }
    val kuiver = remember(workflow) {
        buildKuiver {
            nodes(workflow.nodes.map { it.id })
            // Kuiver 当前不支持相同起止节点的平行边；同一节点对的端口名称以标签合并展示。
            edges(*displayEdges.map { it.sourceNodeId to it.targetNodeId }.distinct().toTypedArray())
        }
    }

    val layoutConfig = remember {
        LayoutConfig.Hierarchical(
            direction = LayoutDirection.VERTICAL,
        )
    }

    val viewerState = rememberKuiverViewerState(
        initialKuiver = kuiver,
        layoutConfig = layoutConfig,
    )

    CompositionLocalProvider(
        LocalKuiverColors provides KuiverColors(
            edge = androidx.compose.material3.MaterialTheme.colorScheme.primary,
            backEdge = androidx.compose.material3.MaterialTheme.colorScheme.error,
            labelText = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
            labelBackground = androidx.compose.material3.MaterialTheme.colorScheme.surface,
            labelBorder = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        KuiverViewer(
            modifier = modifier.fillMaxWidth().height(360.dp),
            state = viewerState,
            config = KuiverViewerConfig(
                contentPadding = 0.9f,
                nodeDragEnabled = true,
            ),
            nodeContent = { kuiverNode ->
                val node = nodeMap[kuiverNode.id]
                if (node != null) {
                    ActionNodeCard(
                        modifier = Modifier.width(260.dp),
                        node = node,
                        spec = nodeRegistry.find(node.type)?.spec,
                        step = stepMap[node.id],
                        onClick = onNodeClick?.let { { it(node.id) } },
                    )
                }
            },
            edgeContent = { edge, from, to ->
                StyledEdgeContent(
                    edge = edge,
                    from = from,
                    to = to,
                    strokeWidth = 2.dp,
                    label = edgeLabels[edge.fromId to edge.toId],
                )
            },
        )
    }
}

/**
 * 工作流图在 Kuiver 中展示的单条边。
 *
 * 循环控制器在执行期会从 `loop.next` 或 `loop.continue` 回到 loop 节点；此边不属于
 * 可持久化 [ActionWorkflow.edges]，因此仅在展示图中补充，供 Kuiver 分类并渲染为回边。
 */
private data class WorkflowDisplayEdge(
    val sourceNodeId: String,
    val targetNodeId: String,
    val label: String,
)
