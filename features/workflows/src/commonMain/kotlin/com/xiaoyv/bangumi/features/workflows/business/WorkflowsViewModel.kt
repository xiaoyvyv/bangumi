package com.xiaoyv.bangumi.features.workflows.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.postEffect
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.data.repository.ActionWorkflowRepository
import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionSideEffectDispatcher
import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionSideEffectResult
import com.xiaoyv.bangumi.shared.data.workflow.engine.runtime.ActionWorkflowEngine
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionEvent
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionSideEffect
import com.xiaoyv.bangumi.shared.data.workflow.model.log.ActionExecutionStatus
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionConfirmEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionImagePreviewEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionInputDialogEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionNotificationEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionOpenExternalAppEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionOpenExternalUrlEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionOpenInternalWebEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionProgressDialogEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionReadClipboardEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionSelectDialogEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionShareEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionShowToastEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionSyncCookieEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionWriteClipboardEffect
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.orbitmvi.orbit.syntax.Syntax

/**
 * [WorkflowsViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class WorkflowsViewModel(
    private val actionWorkflowRepository: ActionWorkflowRepository,
    private val actionWorkflowEngine: ActionWorkflowEngine,
) : BaseViewModel<WorkflowsState, WorkflowsSideEffect, WorkflowsEvent.Action>() {

    private val activeSideEffectDispatcher = ActionSideEffectDispatcher()

    override fun createInitialState() = WorkflowsState()

    override fun onEvent(event: WorkflowsEvent.Action) {
        when (event) {
            is WorkflowsEvent.Action.OnRefresh -> refresh(contentLoading = event.loading)
            is WorkflowsEvent.Action.OnRunSample -> runSample(event.sampleId)
            is WorkflowsEvent.Action.OnSideEffectResult -> {
                activeSideEffectDispatcher.complete(event.id, event.result)
            }
        }
    }

    override suspend fun Syntax<UiState<WorkflowsState>, UiSideEffect<WorkflowsSideEffect>>.refreshSync() {
        reduce { state.copy(data = state.data.copy(workflows = actionWorkflowRepository.list())) }
    }

    /**
     * 使用模拟的详情页条目上下文运行示例，以展示节点执行和副作用分发能力。
     */
    private fun runSample(sampleId: String) = intent {
        val sample = WorkflowSamples.find(sampleId) ?: return@intent
        actionWorkflowEngine.execute(
            workflow = sample,
            initialContext = ActionExecutionContext(
                input = buildJsonObject {
                    put("nameCn", JsonPrimitive("孤独摇滚"))
                    put("id", JsonPrimitive(328609))
                    put("isFavorite", JsonPrimitive(true))
                },
                environment = buildJsonObject { put("locale", JsonPrimitive("zh-CN")) },
                trigger = buildJsonObject { put("source", JsonPrimitive("workflows_screen")) },
            ),
            sideEffectHandler = { effect ->
                val effectId = ActionSideEffectDispatcher.generateSideEffectId()
                when (effect) {
                    is ActionShowToastEffect -> {
                        postToast { effect.message }
                        ActionSideEffectResult.Success()
                    }

                    is ActionConfirmEffect,
                    is ActionReadClipboardEffect,
                    is ActionInputDialogEffect,
                    is ActionProgressDialogEffect,
                    is ActionSelectDialogEffect,
                    is ActionSyncCookieEffect -> {
                        activeSideEffectDispatcher.awaitUiResult(effectId) {
                            postEffect { WorkflowsSideEffect.ExecuteAction(effectId, effect) }
                        }
                    }

                    else -> {
                        postEffect { WorkflowsSideEffect.ExecuteAction(effectId, effect) }
                        ActionSideEffectResult.Success()
                    }
                }
            },
        ).collect { event ->
            val output = event.toOutputLine()
            debugLog {
                tag = "ActionWorkflow"
                output
            }
            when (event) {
                is ActionExecutionEvent.Started -> reduce {
                    state.copy(
                        data = state.data.copy(
                            exampleRun = WorkflowExampleRunState(
                                workflowId = event.workflowId,
                                status = WORKFLOW_RUN_STATUS_RUNNING,
                                outputLines = persistentListOf(output),
                            ),
                        )
                    )
                }

                is ActionExecutionEvent.NodeStarted -> reduce {
                    state.copy(data = state.data.copy(exampleRun = state.data.exampleRun?.appendOutput(output)))
                }

                is ActionExecutionEvent.NodeCompleted -> reduce {
                    val run = state.data.exampleRun ?: return@reduce state
                    state.copy(
                        data = state.data.copy(
                            exampleRun = run.copy(
                                executedNodeIds = (run.executedNodeIds + event.nodeId).toPersistentList(),
                                outputLines = (run.outputLines + output).toPersistentList(),
                            ),
                        )
                    )
                }

                is ActionExecutionEvent.SideEffectRequested -> reduce {
                    val run = state.data.exampleRun ?: return@reduce state
                    state.copy(
                        data = state.data.copy(
                            exampleRun = run.copy(
                                resolvedUrl = (event.effect as? ActionOpenExternalUrlEffect)?.url,
                                outputLines = (run.outputLines + output).toPersistentList(),
                            ),
                        )
                    )
                }

                is ActionExecutionEvent.Failed -> reduce {
                    val run = state.data.exampleRun ?: return@reduce state
                    state.copy(
                        data = state.data.copy(
                            exampleRun = run.copy(
                                status = ActionExecutionStatus.FAILED,
                                errorMessage = event.error.message,
                                outputLines = (run.outputLines + output).toPersistentList(),
                            ),
                        )
                    )
                }

                is ActionExecutionEvent.Completed -> reduce {
                    val run = state.data.exampleRun ?: return@reduce state
                    state.copy(
                        data = state.data.copy(
                            exampleRun = run.copy(
                                status = event.log.status,
                                outputLines = (run.outputLines + output).toPersistentList(),
                            ),
                        )
                    )
                }
            }
        }
    }

    private fun WorkflowExampleRunState.appendOutput(output: String): WorkflowExampleRunState {
        return copy(outputLines = (outputLines + output).toPersistentList())
    }

    private fun ActionExecutionEvent.toOutputLine(): String = when (this) {
        is ActionExecutionEvent.Started -> "🚀 启动工作流 [$workflowId]"
        is ActionExecutionEvent.NodeStarted -> "▶️ 准备执行节点: $nodeId"
        is ActionExecutionEvent.NodeCompleted -> {
            val dataStr = if (output.isEmpty()) "" else " → 输出: $output"
            "✅ 节点 [$nodeId] 执行完成 (出口: $outputPortId)$dataStr"
        }

        is ActionExecutionEvent.SideEffectRequested -> "⚡ 触发系统动作 [$nodeId]: ${formatSideEffect(effect)}"
        is ActionExecutionEvent.Failed -> "❌ 执行发生异常: ${error.message}"
        is ActionExecutionEvent.Completed -> {
            val duration = (log.finishedAt - log.startedAt).coerceAtLeast(0)
            "🏁 工作流运行结束 (状态: ${log.status}, 共 ${log.steps.size} 步, 耗时: ${duration}ms)"
        }
    }

    private fun formatSideEffect(effect: ActionSideEffect): String = when (effect) {
        is ActionShowToastEffect -> "弹窗提示 \"${effect.message}\""
        is ActionWriteClipboardEffect -> "写入剪贴板 \"${effect.text}\""
        is ActionOpenExternalUrlEffect -> "打开外部网页 ${effect.url}"
        is ActionOpenExternalAppEffect -> "唤起应用 ${effect.uri}"
        is ActionOpenInternalWebEffect -> "打开内置网页 ${effect.url}"
        is ActionConfirmEffect -> "二次确认框 \"${effect.message}\""
        is ActionShareEffect -> "系统分享 \"${effect.text}\""
        is ActionNotificationEffect -> "发送通知 \"${effect.content}\""
        is ActionImagePreviewEffect -> "预览图片 (共 ${effect.images.size} 张, 当前: ${effect.index})"
        is ActionSyncCookieEffect -> "同步 Cookie ${effect.url}"
        else -> effect::class.simpleName.orEmpty()
    }

    private companion object {
        const val WORKFLOW_RUN_STATUS_RUNNING = "running"
    }
}
