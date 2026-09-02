package com.xiaoyv.bangumi.shared.data.workflow.model

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

/**
 * 可导入、导出的自定义行为工作流。
 *
 * 工作流文档是行为系统唯一的持久化事实来源；画布、列表等编辑器仅投影并编辑此结构。
 */
@Immutable
@Serializable
data class ActionWorkflow(
    /**
     * JSON 文档格式版本，用于导入时选择兼容迁移逻辑。
     */
    val formatVersion: Int = CURRENT_FORMAT_VERSION,
    /**
     * 工作流的全局稳定标识，保存后不可随意变更。
     */
    val id: String,
    /**
     * 面向用户展示的工作流名称。
     */
    val name: String,
    /**
     * 面向用户展示的工作流说明，可为空。
     */
    val description: String = "",
    /**
     * 是否允许触发和执行该工作流。
     */
    val enabled: Boolean = true,
    /**
     * 决定工作流在哪个业务入口可被发现和触发的规则。
     */
    val trigger: ActionTrigger = ActionTrigger.Manual,
    /**
     * 工作流声明并经用户授权的能力稳定标识列表。
     */
    val requiredCapabilities: SerializeList<String> = persistentListOf(),
    /**
     * 执行时首先进入的节点 ID。
     */
    val entryNodeId: String = "",
    /**
     * 未被 failure 连线处理的错误所跳转到的兜底节点 ID；为空时直接失败。
     */
    val globalErrorNodeId: String? = null,
    /**
     * 组成画布和运行图的全部节点。
     */
    val nodes: SerializeList<ActionNode> = persistentListOf(),
    /**
     * 节点端口之间的全部有向连线。
     */
    val edges: SerializeList<ActionEdge> = persistentListOf(),
) {
    /**
     * 工作流 JSON 格式相关的稳定版本常量。
     */
    companion object {
        /**
         * 当前代码能够写出和直接读取的最新工作流格式版本。
         */
        const val CURRENT_FORMAT_VERSION = 1
    }
}
