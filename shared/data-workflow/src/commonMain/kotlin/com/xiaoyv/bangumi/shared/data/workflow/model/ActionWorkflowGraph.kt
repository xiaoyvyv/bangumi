package com.xiaoyv.bangumi.shared.data.workflow.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * 单个节点及其业务配置、画布位置。
 */
@Immutable
@Serializable
data class ActionNode(
    /**
     * 节点在当前工作流内的稳定唯一标识。
     */
    val id: String,
    /**
     * 节点注册中心使用的稳定类型标识。
     */
    val type: String,
    /**
     * 节点配置的结构版本，由对应节点迁移器维护。
     */
    val nodeVersion: Int = 1,
    /**
     * 编辑器与执行日志展示的节点名称；为空时由节点类型提供默认名称。
     */
    val label: String = "",
    /**
     * 节点类型定义的 JSON 配置，不同节点的字段语义各不相同。
     */
    val config: JsonObject = JsonObject(emptyMap()),
    /**
     * 节点在编辑器画布中的位置与展示信息。
     */
    val layout: ActionNodeLayout = ActionNodeLayout(),
)

/**
 * 节点在画布世界坐标系中的位置。
 */
@Immutable
@Serializable
data class ActionNodeLayout(
    /**
     * 节点左上角在画布世界坐标系中的横坐标。
     */
    val x: Float = 0f,
    /**
     * 节点左上角在画布世界坐标系中的纵坐标。
     */
    val y: Float = 0f,
)

/**
 * 两个节点端口的连接。
 */
@Immutable
@Serializable
data class ActionEdge(
    /**
     * 连线在工作流内的稳定唯一标识。
     */
    val id: String,
    /**
     * 连线类别，取值见 [ActionPortKind]。
     */
    val kind: String = ActionPortKind.CONTROL,
    /**
     * 连线起点端口。
     */
    val source: ActionPortRef,
    /**
     * 连线终点端口。
     */
    val target: ActionPortRef,
)

/**
 * 节点的稳定端口引用。
 */
@Immutable
@Serializable
data class ActionPortRef(
    /**
     * 端口所属节点的 ID。
     */
    val nodeId: String,
    /**
     * 节点规格中声明的稳定端口 ID。
     */
    val portId: String,
)

/**
 * 端口连线类型。
 */
object ActionPortKind {
    /**
     * 决定执行顺序与分支走向的控制流连线。
     */
    const val CONTROL = "control"

    /**
     * 传递结构化值的数据流连线；当前格式仅保留模型定义，执行器尚不处理。
     */
    const val DATA = "data"
}
