package com.xiaoyv.bangumi.shared.data.workflow.model.execution

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlin.uuid.Uuid

/**
 * 工作流一次运行期间可供节点读取的结构化上下文。
 */
@Immutable
data class ActionExecutionContext(
    /**
     * 当前执行工作流的稳定标识，由引擎在运行时注入，不暴露给模板表达式。
     */
    val workflowId: String = Uuid.random().toString(),
    val input: JsonObject = JsonObject(emptyMap()),
    val environment: JsonObject = JsonObject(emptyMap()),
    val trigger: JsonObject = JsonObject(emptyMap()),
    val variables: SerializeMap<String, JsonElement> = persistentMapOf(),
    val stepOutputs: SerializeMap<String, JsonObject> = persistentMapOf(),
    val loop: JsonObject = JsonObject(emptyMap()),
)

/**
 * 模板表达式可读取的运行上下文根命名空间。
 */
object ActionTemplateRoot {
    const val INPUT = "input"
    const val ENVIRONMENT = "environment"
    const val TRIGGER = "trigger"
    const val VARIABLES = "vars"
    const val STEP_OUTPUTS = "steps"
    const val LOOP = "loop"
}
