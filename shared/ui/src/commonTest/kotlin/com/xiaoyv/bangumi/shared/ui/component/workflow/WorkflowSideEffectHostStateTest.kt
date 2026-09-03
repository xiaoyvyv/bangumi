package com.xiaoyv.bangumi.shared.ui.component.workflow

import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionSideEffectResult
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionProgressDialogEffect
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * 工作流副作用宿主的进度任务测试。
 */
class WorkflowSideEffectHostStateTest {

    /**
     * 两个并发进度任务应聚合在同一列表中；停止一个任务不会影响另一个任务的挂起状态。
     */
    @Test
    fun stoppingOneConcurrentProgressTaskOnlyCompletesItsOwnRequest() = runBlocking {
        val hostState = WorkflowSideEffectHostState()
        val first = async(start = CoroutineStart.UNDISPATCHED) {
            hostState.dispatch(ActionProgressDialogEffect(title = "下载视频"))
        }
        val second = async(start = CoroutineStart.UNDISPATCHED) {
            hostState.dispatch(ActionProgressDialogEffect(title = "压缩文件"))
        }

        assertEquals(listOf("下载视频", "压缩文件"), hostState.progressTasks.map { it.effect.title })

        val firstTaskId = hostState.progressTasks.first().id
        hostState.stopProgressTask(firstTaskId) { task ->
            task.onResult(ActionSideEffectResult.Failure("用户停止了进度任务"))
        }

        assertEquals(ActionSideEffectResult.Failure("用户停止了进度任务"), first.await())
        assertEquals(listOf("压缩文件"), hostState.progressTasks.map { it.effect.title })

        hostState.stopProgressTask(hostState.progressTasks.single().id) { task ->
            task.onResult(ActionSideEffectResult.Failure("用户停止了进度任务"))
        }

        assertEquals(ActionSideEffectResult.Failure("用户停止了进度任务"), second.await())
        assertEquals(emptyList(), hostState.progressTasks)
    }
}
