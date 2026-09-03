package com.xiaoyv.bangumi.shared.data.workflow.engine

import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionSideEffect
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonObject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * 方便工作流引擎与宿主 UI 同步等待副作用交互结果的辅助调度锁。
 *
 * 适用于需要等待 UI 用户交互（如二次确认框、弹窗输入、异步剪贴板读取、文件选择等）的场景。
 * 内部基于 [Mutex] 与并发 Key [activeDeferred] 映射结构，统一支持单任务与并发多任务节点的挂起与响应。
 */
class ActionSideEffectDispatcher {
    private val lock = Mutex()
    private val activeDeferred = mutableMapOf<String, CompletableDeferred<ActionSideEffectResult>>()

    /**
     * 引擎侧调用：按副作用 [id] 挂起当前节点，直到宿主 UI 完成交互或取消。
     *
     * @param id 副作用任务唯一 ID（若为空则自动生成）
     * @param onTrigger 触发 UI SideEffect 广播回调
     */
    suspend fun awaitUiResult(
        id: String,
        onTrigger: suspend () -> Unit,
    ): ActionSideEffectResult {
        val deferred = CompletableDeferred<ActionSideEffectResult>()
        lock.withLock {
            activeDeferred[id]?.cancel()
            activeDeferred[id] = deferred
        }
        onTrigger()
        return try {
            deferred.await()
        } finally {
            lock.withLock {
                if (activeDeferred[id] == deferred) {
                    activeDeferred.remove(id)
                }
            }
        }
    }

    /**
     * 宿主 UI 侧调用：按副作用 [id] 完成交互，将 [result] 精确传递回对应的挂起节点。
     * 若 [id] 为空，则默认寻找 [activeDeferred] 中的首个挂起任务。
     */
    fun complete(id: String, result: ActionSideEffectResult) {
        val targetId = id.ifBlank { activeDeferred.keys.firstOrNull().orEmpty() }
        val deferred = activeDeferred.remove(targetId)
        deferred?.complete(result)
    }

    companion object {
        private var autoCounter = 0L

        @OptIn(ExperimentalUuidApi::class)
        fun generateSideEffectId(): String = "se_${++autoCounter}_${Uuid.random().toHexString()}"
    }
}

/**
 * 工作流副作用的宿主执行契约。
 */
fun interface ActionSideEffectHandler {
    suspend fun handle(effect: ActionSideEffect): ActionSideEffectResult
}

/**
 * 宿主执行副作用后返回给工作流引擎的结果。
 */
sealed interface ActionSideEffectResult {
    data class Success(val output: JsonObject = JsonObject(emptyMap())) : ActionSideEffectResult
    data object Cancelled : ActionSideEffectResult
    data class Failure(val message: String) : ActionSideEffectResult
}
