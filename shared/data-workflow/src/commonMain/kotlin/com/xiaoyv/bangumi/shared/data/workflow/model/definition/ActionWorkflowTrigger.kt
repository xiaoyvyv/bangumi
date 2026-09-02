package com.xiaoyv.bangumi.shared.data.workflow.model.definition

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * 触发工作流执行的事件或定时触发器定义。
 */
@Immutable
@Serializable
data class ActionTrigger(
    val id: String,
    val type: String,
    val enabled: Boolean = true,
    val config: JsonObject = JsonObject(emptyMap()),
)

/**
 * 内置触发器类型协议值。
 */
object ActionWorkflowTriggerType {
    const val MANUAL = "manual"
    const val EVENT = "event"
    const val CRON = "cron"
}
