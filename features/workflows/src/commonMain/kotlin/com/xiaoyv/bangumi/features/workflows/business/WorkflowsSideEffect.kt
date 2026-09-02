package com.xiaoyv.bangumi.features.workflows.business

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionSideEffect

/**
 * 工作流页需要由 UI 宿主完成的操作。
 */
@Immutable
sealed interface WorkflowsSideEffect {
    /**
     * 执行工作流节点请求的平台副作用。
     *
     * @param id 副作用交互唯一任务 ID
     * @param effect 节点请求的外部链接、应用跳转或内部网页操作。
     */
    @Immutable
    data class ExecuteAction(val id: String, val effect: ActionSideEffect) : WorkflowsSideEffect
}
