package com.xiaoyv.bangumi.shared.data.workflow.model.definition

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * 工作流中的单个逻辑处理节点。
 */
@Immutable
@Serializable
data class ActionNode(
    val id: String,
    val type: String,
    val nodeVersion: Int = 1,
    val label: String = "",
    val config: JsonObject = JsonObject(emptyMap()),
    val layout: ActionNodeLayout = ActionNodeLayout(),
)

/**
 * 可选的节点 UI 可视化画布坐标。
 */
@Immutable
@Serializable
data class ActionNodeLayout(
    val x: Float = 0f,
    val y: Float = 0f,
)

/**
 * 连接源节点输出端口与目标节点输入端口的控制边。
 */
@Immutable
@Serializable
data class ActionEdge(
    val id: String,
    val kind: String = ActionPortKind.CONTROL,
    val source: ActionPortRef,
    val target: ActionPortRef,
)

/**
 * 节点的端口引用，指定具体的节点 ID 与端口 ID。
 */
@Immutable
@Serializable
data class ActionPortRef(
    val nodeId: String,
    val portId: String,
)

/**
 * 端口连线类型。
 */
object ActionPortKind {
    const val CONTROL = "control"
    const val DATA = "data"
}
