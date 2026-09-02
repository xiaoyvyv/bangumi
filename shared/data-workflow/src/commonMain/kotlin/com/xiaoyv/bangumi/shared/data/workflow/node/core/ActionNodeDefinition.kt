package com.xiaoyv.bangumi.shared.data.workflow.node.core

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionPortKind
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionNodeExecutionResult
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionPortConnectionLimit.UNLIMITED
import kotlinx.collections.immutable.persistentListOf

/**
 * 节点的运行时定义，由规格、执行器与可选迁移器组成。
 */
@Immutable
data class ActionNodeDefinition(
    val spec: ActionNodeSpec,
    val executor: ActionNodeExecutor,
    val migrator: ActionNodeMigrator = ActionNodeMigrator.None,
    val capabilityResolver: ActionNodeCapabilityResolver = ActionNodeCapabilityResolver.FromSpec,
)

/**
 * 根据节点实际配置计算执行所需能力。
 *
 * 某些能力仅在特定控制项启用时需要，例如 HTTP 节点选择携带本地 Cookie；此接口避免把
 * 条件权限硬编码在校验器或引擎中。
 */
fun interface ActionNodeCapabilityResolver {
    /**
     * 返回该节点在当前配置下实际需要的全部能力。
     *
     * @param node 工作流中的节点实例。
     * @param spec 节点静态规格。
     */
    fun requiredCapabilities(node: ActionNode, spec: ActionNodeSpec): Set<String>

    companion object {
        /**
         * 使用 [ActionNodeSpec.requiredCapabilities] 作为节点能力集合的默认实现。
         */
        val FromSpec = ActionNodeCapabilityResolver { _, spec -> spec.requiredCapabilities }
    }
}

/**
 * 供编辑器、校验器和执行器共用的节点端口与配置契约。
 */
@Immutable
data class ActionNodeSpec(
    val type: String,
    val latestVersion: Int = 1,
    val category: String,
    val inputPorts: SerializeList<ActionPortSpec> = persistentListOf(),
    val outputPorts: SerializeList<ActionPortSpec> = persistentListOf(),
    val requiredConfigKeys: Set<String> = emptySet(),
    val requiredCapabilities: Set<String> = emptySet(),
)

/**
 * 节点单个输入或输出端口的连接约束。
 */
@Immutable
data class ActionPortSpec(
    val id: String,
    val kind: String = ActionPortKind.CONTROL,
    val direction: String,
    val maxConnections: Int = UNLIMITED,
)

/**
 * 端口连接数量限制。
 *
 * 仅具有明确合流语义的控制节点应使用 [UNLIMITED]；普通数据与控制输入仍应保持单入边，
 * 以免工作流在尚未支持并发调度时出现不确定的执行顺序。
 */
object ActionPortConnectionLimit {
    const val UNLIMITED = Int.MAX_VALUE
}

/**
 * 节点端口方向常量。
 */
object ActionPortDirection {
    const val INPUT = "input"
    const val OUTPUT = "output"
}

/**
 * 节点运行时执行器。
 */
fun interface ActionNodeExecutor {
    /**
     * 执行节点业务逻辑；副作用应通过结果交由宿主执行。
     *
     * @param node 当前节点配置。
     * @param context 当前不可变执行上下文。
     * @return 节点出口、结构化输出及可选副作用。
     */
    suspend fun execute(node: ActionNode, context: ActionExecutionContext): ActionNodeExecutionResult
}

/**
 * 将历史节点配置迁移到节点规格的最新版本。
 */
fun interface ActionNodeMigrator {
    /**
     * 将单次历史节点配置升级到更高版本。
     *
     * @param node 待迁移节点。
     * @return 必须具有更高 [ActionNode.nodeVersion] 的同类型节点。
     */
    fun migrate(node: ActionNode): ActionNode

    companion object {
        val None = ActionNodeMigrator { node ->
            require(node.nodeVersion == 1) { "节点 ${node.type} 缺少从 v${node.nodeVersion} 的迁移" }
            node
        }
    }
}
