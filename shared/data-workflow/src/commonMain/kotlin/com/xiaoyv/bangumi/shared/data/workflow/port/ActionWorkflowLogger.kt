package com.xiaoyv.bangumi.shared.data.workflow.port

import kotlinx.serialization.json.JsonElement

/**
 * 工作流调试与日志输出端口。
 *
 * 默认实现 [DefaultActionWorkflowLogger] 将日志输出到控制台，宿主层可实现此接口并接入系统日志。
 */
interface ActionWorkflowLogger {
    /**
     * 打印一条工作流日志。
     *
     * @param level 日志级别，例如 info, debug, warn, error。
     * @param message 日志消息文本。
     * @param data 可选的结构化数据。
     */
    fun log(level: String, message: String, data: JsonElement? = null)

    companion object {
        val Default: ActionWorkflowLogger = DefaultActionWorkflowLogger()
    }
}

/**
 * 默认将工作流日志输出到控制台的实现。
 */
class DefaultActionWorkflowLogger : ActionWorkflowLogger {
    override fun log(level: String, message: String, data: JsonElement?) {
        val detail = if (data != null) " | data=$data" else ""
        println("[ActionWorkflow][$level] $message$detail")
    }
}
