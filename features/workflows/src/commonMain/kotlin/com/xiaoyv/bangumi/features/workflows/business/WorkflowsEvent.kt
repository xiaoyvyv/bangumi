package com.xiaoyv.bangumi.features.workflows.business

import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionSideEffectResult
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [WorkflowsEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class WorkflowsEvent {
    sealed class UI : WorkflowsEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : WorkflowsEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data class OnRunSample(val sampleId: String) : Action()
        data class OnSideEffectResult(val id: String, val result: ActionSideEffectResult) : Action()
    }
}
