package com.xiaoyv.bangumi.shared.data.workflow.model.definition

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

/**
 * 代表一条完整、可序列化、可在客户端引擎中运行的声明式工作流。
 */
@Immutable
@Serializable
data class ActionWorkflow(
    val formatVersion: Int = CURRENT_FORMAT_VERSION,
    val id: String,
    val name: String,
    val description: String = "",
    val enabled: Boolean = true,
    val entryNodeId: String = "",
    val globalErrorNodeId: String? = null,
    val requiredCapabilities: SerializeList<String> = persistentListOf(),
    val nodes: SerializeList<ActionNode> = persistentListOf(),
    val edges: SerializeList<ActionEdge> = persistentListOf(),
) {
    companion object {
        const val CURRENT_FORMAT_VERSION = 1
    }
}
