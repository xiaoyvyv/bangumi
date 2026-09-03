package com.xiaoyv.bangumi.shared.ui.component.workflow

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionSideEffectResult
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionSideEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionConfirmEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionInputDialogEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionProgressDialogEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionSelectDialogEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionSyncCookieEffect
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

private var sideEffectIdCounter = 0L

/**
 * 带有独立唯一标识与专属结果回调的 SideEffect 并发包装模型。
 */
@Immutable
data class WorkflowSideEffectData<out T : ActionSideEffect>(
    val id: String,
    val effect: T,
    val onResult: (ActionSideEffectResult) -> Unit,
)

/**
 * 支持并发与队列分发的工作流 SideEffect 宿主控制器。
 *
 * 遵循 Compose [androidx.compose.material3.SnackbarHostState] 原生挂起 API 设计规范。
 * [dispatch] 为挂起函数，直接挂起调用协程并在 SideEffect 处理完成或弹窗响应后恢复协程并返回 [ActionSideEffectResult]。
 */
class WorkflowSideEffectHostState {
    /**
     * 二次确认弹窗队列。
     */
    var confirmQueue by mutableStateOf<List<WorkflowSideEffectData<ActionConfirmEffect>>>(emptyList())
        private set

    /**
     * 文本输入弹窗队列。
     */
    var inputQueue by mutableStateOf<List<WorkflowSideEffectData<ActionInputDialogEffect>>>(emptyList())
        private set

    /**
     * 列表选择弹窗队列（包含单选/多选）。
     */
    var selectQueue by mutableStateOf<List<WorkflowSideEffectData<ActionSelectDialogEffect>>>(emptyList())
        private set

    /**
     * Cookie 同步 BottomSheet 弹窗队列。
     */
    var syncCookieQueue by mutableStateOf<List<WorkflowSideEffectData<ActionSyncCookieEffect>>>(emptyList())
        private set

    /**
     * 当前活动的进度任务。所有任务由一个对话框聚合展示。
     */
    var progressTasks by mutableStateOf<List<WorkflowSideEffectData<ActionProgressDialogEffect>>>(emptyList())
        private set

    /**
     * 即时/单向 SideEffect 队列。
     */
    var oneShotQueue by mutableStateOf<List<WorkflowSideEffectData<ActionSideEffect>>>(emptyList())
        private set

    /**
     * 当前队列顶部的二次确认弹窗。
     */
    val currentConfirmData: WorkflowSideEffectData<ActionConfirmEffect>?
        get() = confirmQueue.firstOrNull()

    /**
     * 当前队列顶部的文本输入弹窗。
     */
    val currentInputData: WorkflowSideEffectData<ActionInputDialogEffect>?
        get() = inputQueue.firstOrNull()

    /**
     * 当前队列顶部的列表选择弹窗。
     */
    val currentSelectData: WorkflowSideEffectData<ActionSelectDialogEffect>?
        get() = selectQueue.firstOrNull()

    /**
     * 当前队列顶部的 Cookie 同步 BottomSheet 弹窗。
     */
    val currentSyncCookieData: WorkflowSideEffectData<ActionSyncCookieEffect>?
        get() = syncCookieQueue.firstOrNull()

    /**
     * 当前队列顶部的单向 SideEffect。
     */
    val currentOneShotData: WorkflowSideEffectData<ActionSideEffect>?
        get() = oneShotQueue.firstOrNull()

    /**
     * 分发并处理一个 [ActionSideEffect]。
     *
     * 该方法为挂起函数，直接挂起调用协程，直至 UI 消费完成或弹窗返回后恢复协程并返回 [ActionSideEffectResult]。
     *
     * @param effect 工作流节点发出的 SideEffect
     * @return 节点执行结果 [ActionSideEffectResult]
     */
    suspend fun dispatch(effect: ActionSideEffect): ActionSideEffectResult {
        return suspendCancellableCoroutine { continuation ->
            val entryId = "${effect::class.simpleName}_${++sideEffectIdCounter}"
            val onResult: (ActionSideEffectResult) -> Unit = { result ->
                if (continuation.isActive) {
                    continuation.resume(result)
                }
            }

            when (effect) {
                is ActionConfirmEffect -> {
                    val data = WorkflowSideEffectData(entryId, effect, onResult)
                    confirmQueue = confirmQueue + data
                }

                is ActionInputDialogEffect -> {
                    val data = WorkflowSideEffectData(entryId, effect, onResult)
                    inputQueue = inputQueue + data
                }

                is ActionSelectDialogEffect -> {
                    val data = WorkflowSideEffectData(entryId, effect, onResult)
                    selectQueue = selectQueue + data
                }

                is ActionSyncCookieEffect -> {
                    val data = WorkflowSideEffectData(entryId, effect, onResult)
                    syncCookieQueue = syncCookieQueue + data
                }

                is ActionProgressDialogEffect -> {
                    val data = WorkflowSideEffectData(entryId, effect, onResult)
                    progressTasks = progressTasks + data
                }

                else -> {
                    val data = WorkflowSideEffectData(entryId, effect, onResult)
                    oneShotQueue = oneShotQueue + data
                }
            }

            continuation.invokeOnCancellation {
                confirmQueue = confirmQueue.filterNot { it.id == entryId }
                inputQueue = inputQueue.filterNot { it.id == entryId }
                selectQueue = selectQueue.filterNot { it.id == entryId }
                syncCookieQueue = syncCookieQueue.filterNot { it.id == entryId }
                progressTasks = progressTasks.filterNot { it.id == entryId }
                oneShotQueue = oneShotQueue.filterNot { it.id == entryId }
            }
        }
    }

    /**
     * 弹出二次确认弹窗队列首项，并在出队时触发 [onPopped] 回调。
     *
     * @param onPopped 弹出首项时的回调闭包
     */
    fun popConfirmData(onPopped: (WorkflowSideEffectData<ActionConfirmEffect>) -> Unit) {
        val current = currentConfirmData ?: return
        confirmQueue = confirmQueue.drop(1)
        onPopped(current)
    }

    /**
     * 弹出文本输入弹窗队列首项，并在出队时触发 [onPopped] 回调。
     *
     * @param onPopped 弹出首项时的回调闭包
     */
    fun popInputData(onPopped: (WorkflowSideEffectData<ActionInputDialogEffect>) -> Unit) {
        val current = currentInputData ?: return
        inputQueue = inputQueue.drop(1)
        onPopped(current)
    }

    /**
     * 弹出列表选择弹窗队列首项，并在出队时触发 [onPopped] 回调。
     *
     * @param onPopped 弹出首项时的回调闭包
     */
    fun popSelectData(onPopped: (WorkflowSideEffectData<ActionSelectDialogEffect>) -> Unit) {
        val current = currentSelectData ?: return
        selectQueue = selectQueue.drop(1)
        onPopped(current)
    }

    /**
     * 弹出 Cookie 同步 BottomSheet 弹窗队列首项，并在出队时触发 [onPopped] 回调。
     *
     * @param onPopped 弹出首项时的回调闭包
     */
    fun popSyncCookieData(onPopped: (WorkflowSideEffectData<ActionSyncCookieEffect>) -> Unit) {
        val current = currentSyncCookieData ?: return
        syncCookieQueue = syncCookieQueue.drop(1)
        onPopped(current)
    }

    /**
     * 弹出单向 SideEffect 队列首项，并在出队时触发 [onPopped] 回调。
     *
     * @param onPopped 弹出首项时的回调闭包
     */
    fun popOneShotData(onPopped: (WorkflowSideEffectData<ActionSideEffect>) -> Unit) {
        val current = currentOneShotData ?: return
        oneShotQueue = oneShotQueue.drop(1)
        onPopped(current)
    }

    /**
     * 停止指定进度任务，并将失败结果精确返回至触发该任务的节点。
     */
    fun stopProgressTask(id: String, onStopped: (WorkflowSideEffectData<ActionProgressDialogEffect>) -> Unit) {
        val task = progressTasks.firstOrNull { it.id == id } ?: return
        progressTasks = progressTasks.filterNot { it.id == id }
        onStopped(task)
    }

    /**
     * 清空所有 SideEffect 队列。
     */
    fun clear() {
        confirmQueue = emptyList()
        inputQueue = emptyList()
        selectQueue = emptyList()
        syncCookieQueue = emptyList()
        progressTasks = emptyList()
        oneShotQueue = emptyList()
    }
}

/**
 * 创建并记住 [WorkflowSideEffectHostState]。
 */
@Composable
fun rememberWorkflowSideEffectHostState(): WorkflowSideEffectHostState {
    return remember { WorkflowSideEffectHostState() }
}
