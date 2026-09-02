package com.xiaoyv.bangumi.shared.data.workflow.model.log

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * 可持久化的单次运行记录。
 */
@Immutable
@Serializable
data class ActionExecutionLog(
    val workflowId: String,
    val startedAt: Long,
    val finishedAt: Long,
    val status: String,
    val steps: SerializeList<ActionExecutionStep> = persistentListOf(),
)

/**
 * 单个节点在一次工作流运行中的执行记录。
 */
@Immutable
@Serializable
data class ActionExecutionStep(
    val nodeId: String,
    val startedAt: Long,
    val finishedAt: Long,
    val outputPortId: String? = null,
    val output: JsonObject = JsonObject(emptyMap()),
    val error: ActionExecutionError? = null,
)

/**
 * 工作流执行结果的稳定状态值。
 */
object ActionExecutionStatus {
    const val SUCCESS = "success"
    const val FAILED = "failed"
    const val CANCELLED = "cancelled"
}
