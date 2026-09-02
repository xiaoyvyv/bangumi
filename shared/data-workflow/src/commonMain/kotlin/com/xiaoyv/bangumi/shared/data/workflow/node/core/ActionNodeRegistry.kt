package com.xiaoyv.bangumi.shared.data.workflow.node.core

import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionWorkflow
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.builtInActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionHttpRequestExecutor
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionWorkflowFileStorage
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionWorkflowLogger
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionWorkflowPreferencesStore
import kotlinx.collections.immutable.toPersistentList

/**
 * 节点注册中心。扩展节点只需注册规格与执行器，无需修改引擎。
 */
class ActionNodeRegistry(
    httpRequestExecutor: ActionHttpRequestExecutor,
    preferencesStore: ActionWorkflowPreferencesStore,
    logger: ActionWorkflowLogger = ActionWorkflowLogger.Default,
    fileStorage: ActionWorkflowFileStorage = ActionWorkflowFileStorage.Default,
    nodeDefinitions: List<ActionNodeDefinition> = builtInActionNodeDefinitions(httpRequestExecutor, preferencesStore, logger, fileStorage),
) {
    private val definitions = linkedMapOf<String, ActionNodeDefinition>()

    init {
        registerAll(nodeDefinitions)
    }

    /**
     * 注册业务模块提供的节点。节点类型是全局稳定标识，重复注册视为配置错误。
     *
     * @param definition 待注册的节点定义。
     */
    fun register(definition: ActionNodeDefinition) {
        check(definition.spec.type !in definitions) {
            "重复注册行为节点：${definition.spec.type}"
        }
        definitions[definition.spec.type] = definition
    }

    /**
     * 批量注册业务模块提供的节点。
     *
     * @param nodeDefinitions 节点定义集合。
     */
    fun registerAll(nodeDefinitions: Iterable<ActionNodeDefinition>) {
        nodeDefinitions.forEach(::register)
    }

    /**
     * 查找指定类型的节点定义。
     *
     * @param type 节点稳定类型标识。
     * @return 已注册的节点定义；未注册时为 null。
     */
    fun find(type: String): ActionNodeDefinition? = definitions[type]

    /**
     * 返回当前全部已注册节点定义。
     */
    fun all(): Collection<ActionNodeDefinition> = definitions.values

    /**
     * 计算工作流实际使用节点所需的权限并集。
     *
     * @param nodes 工作流节点列表。
     */
    fun requiredCapabilities(nodes: List<ActionNode>): Set<String> {
        return nodes.flatMapTo(linkedSetOf()) { node ->
            find(node.type)?.let { definition ->
                definition.capabilityResolver.requiredCapabilities(node, definition.spec)
            }.orEmpty()
        }
    }

    /**
     * 在校验和执行前将所有已知节点升级到其最新配置版本。
     *
     * @param workflow 待迁移的工作流。
     * @return 节点版本已升级的工作流副本。
     */
    fun migrate(workflow: ActionWorkflow): ActionWorkflow {
        return workflow.copy(nodes = workflow.nodes.map(::migrate).toPersistentList())
    }

    private fun migrate(node: ActionNode): ActionNode {
        val definition = find(node.type) ?: return node
        require(node.nodeVersion <= definition.spec.latestVersion) { "节点 ${node.type} 版本过高" }
        var current = node
        while (current.nodeVersion < definition.spec.latestVersion) {
            val migrated = definition.migrator.migrate(current)
            require(migrated.type == current.type && migrated.nodeVersion > current.nodeVersion) {
                "节点 ${node.type} 的迁移器未提升版本"
            }
            current = migrated
        }
        return current
    }
}
