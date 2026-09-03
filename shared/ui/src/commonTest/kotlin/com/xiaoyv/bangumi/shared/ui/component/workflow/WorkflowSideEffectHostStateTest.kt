package com.xiaoyv.bangumi.shared.ui.component.workflow

import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionSideEffectResult
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionProgressDialogAction
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionProgressDialogMode
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionProgressDialogEffect
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * 工作流副作用宿主的非阻塞进度任务测试。
 */
class WorkflowSideEffectHostStateTest {

    /**
     * dispatch 进度副作用应为非阻塞并立即返回成功，且正确更新进度任务列表。
     */
    @Test
    fun progressDialogDispatchIsNonBlockingAndSupportsLifecycle() = runBlocking {
        val hostState = WorkflowSideEffectHostState()

        // 1. 请求展示进度
        val showResult = hostState.dispatch(
            ActionProgressDialogEffect(
                action = ActionProgressDialogAction.SHOW,
                taskId = "task_download",
                title = "下载视频",
                message = "正在连接服务器...",
                mode = ActionProgressDialogMode.DETERMINATE,
                progress = 0f,
                maxProgress = 100f,
            )
        )
        assertEquals(ActionSideEffectResult.Success(), showResult)
        assertEquals(1, hostState.progressTasks.size)
        val task = hostState.progressTasks.single()
        assertEquals("task_download", task.id)
        assertEquals("下载视频", task.effect.title)
        assertEquals("正在连接服务器...", task.effect.message)
        assertEquals(0f, task.effect.progress)

        // 2. 请求更新进度
        val updateResult = hostState.dispatch(
            ActionProgressDialogEffect(
                action = ActionProgressDialogAction.UPDATE,
                taskId = "task_download",
                message = "已下载 50%",
                progress = 50f,
            )
        )
        assertEquals(ActionSideEffectResult.Success(), updateResult)
        assertEquals(1, hostState.progressTasks.size)
        val updatedTask = hostState.progressTasks.single()
        assertEquals("下载视频", updatedTask.effect.title)
        assertEquals("已下载 50%", updatedTask.effect.message)
        assertEquals(50f, updatedTask.effect.progress)
        assertEquals(100f, updatedTask.effect.maxProgress)

        // 3. 请求关闭进度
        val dismissResult = hostState.dispatch(
            ActionProgressDialogEffect(
                action = ActionProgressDialogAction.DISMISS,
                taskId = "task_download",
            )
        )
        assertEquals(ActionSideEffectResult.Success(), dismissResult)
        assertTrue(hostState.progressTasks.isEmpty())
    }

    /**
     * 多任务并发支持：不同任务独立展示与关闭。
     */
    @Test
    fun concurrentProgressTasksAreManagedIndependently() = runBlocking {
        val hostState = WorkflowSideEffectHostState()

        hostState.dispatch(
            ActionProgressDialogEffect(
                action = ActionProgressDialogAction.SHOW,
                taskId = "task_1",
                title = "任务 1",
            )
        )
        hostState.dispatch(
            ActionProgressDialogEffect(
                action = ActionProgressDialogAction.SHOW,
                taskId = "task_2",
                title = "任务 2",
            )
        )

        assertEquals(listOf("任务 1", "任务 2"), hostState.progressTasks.map { it.effect.title })

        // 关闭任务 1
        hostState.dispatch(
            ActionProgressDialogEffect(
                action = ActionProgressDialogAction.DISMISS,
                taskId = "task_1",
            )
        )
        assertEquals(listOf("任务 2"), hostState.progressTasks.map { it.effect.title })

        // 关闭任务 2
        hostState.dispatch(
            ActionProgressDialogEffect(
                action = ActionProgressDialogAction.DISMISS,
                taskId = "task_2",
            )
        )
        assertTrue(hostState.progressTasks.isEmpty())
    }
}
