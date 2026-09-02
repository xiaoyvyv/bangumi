package com.xiaoyv.bangumi.shared.data.workflow.exception

import com.xiaoyv.bangumi.shared.libnative.System

/**
 * 工业级工作流统一诊断日志输出接口与日志器。
 */
fun interface ActionWorkflowLogListener {
    fun onLog(level: ActionWorkflowLogLevel, tag: String, message: String)
}

enum class ActionWorkflowLogLevel {
    DEBUG, INFO, WARN, ERROR
}

object ActionWorkflowTraceLogger {
    private val listeners = mutableListOf<ActionWorkflowLogListener>()

    init {
        // 默认控制台输出监听器
        listeners.add { level, tag, message ->
            System.log(tag, "[$level] $message")
        }
    }

    fun addListener(listener: ActionWorkflowLogListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ActionWorkflowLogListener) {
        listeners.remove(listener)
    }

    fun logDebug(tag: String, message: String) {
        listeners.forEach { listener ->
            listener.onLog(ActionWorkflowLogLevel.DEBUG, tag, message)
        }
    }

    fun logInfo(tag: String, message: String) {
        listeners.forEach { listener ->
            listener.onLog(ActionWorkflowLogLevel.INFO, tag, message)
        }
    }

    fun logWarn(tag: String, message: String) {
        listeners.forEach { listener ->
            listener.onLog(ActionWorkflowLogLevel.WARN, tag, message)
        }
    }

    fun logError(exception: ActionWorkflowException) {
        val formattedLog = exception.buildFormattedTraceLog()
        listeners.forEach { listener ->
            listener.onLog(ActionWorkflowLogLevel.ERROR, "ActionWorkflowEngine", formattedLog)
        }
    }

    fun logError(tag: String, message: String) {
        listeners.forEach { listener ->
            listener.onLog(ActionWorkflowLogLevel.ERROR, tag, message)
        }
    }
}
