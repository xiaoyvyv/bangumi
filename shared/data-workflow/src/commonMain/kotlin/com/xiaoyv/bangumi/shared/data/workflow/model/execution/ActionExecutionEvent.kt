package com.xiaoyv.bangumi.shared.data.workflow.model.execution

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.workflow.model.log.ActionExecutionError
import com.xiaoyv.bangumi.shared.data.workflow.model.log.ActionExecutionLog
import kotlinx.serialization.json.JsonObject

/**
 * 执行引擎向 UI 或测试调用方发出的过程事件。
 */
@Immutable
sealed interface ActionExecutionEvent {
    @Immutable
    data class Started(val workflowId: String) : ActionExecutionEvent

    @Immutable
    data class NodeStarted(val nodeId: String) : ActionExecutionEvent

    @Immutable
    data class NodeCompleted(
        val nodeId: String,
        val outputPortId: String,
        val output: JsonObject,
    ) : ActionExecutionEvent

    @Immutable
    data class SideEffectRequested(
        val nodeId: String,
        val effect: ActionSideEffect,
    ) : ActionExecutionEvent

    @Immutable
    data class Completed(val log: ActionExecutionLog) : ActionExecutionEvent

    @Immutable
    data class Failed(val error: ActionExecutionError) : ActionExecutionEvent
}
