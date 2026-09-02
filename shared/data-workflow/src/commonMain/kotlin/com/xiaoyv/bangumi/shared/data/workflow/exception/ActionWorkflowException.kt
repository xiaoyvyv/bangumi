package com.xiaoyv.bangumi.shared.data.workflow.exception

import com.xiaoyv.bangumi.shared.data.workflow.model.log.ActionExecutionError
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionErrorKey
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

/**
 * 工业级工作流统一异常根类。
 *
 * 封装了全局可追溯诊断元数据：
 * - [code]：机器可读的诊断错误码（如 `NODE_EXECUTION_FAILED`, `WORKFLOW_DISABLED`）
 * - [messageText]：清晰的中文错误描述
 * - [workflowId]：产生错误的工作流 ID
 * - [workflowName]：工作流名称
 * - [nodeId]：产生错误的节点 ID
 * - [nodeType]：节点类型（如 `math.divide`）
 * - [nodeLabel]：节点展示名称（如 `计算折扣比例`）
 * - [configKey]：触发错误的配置项键名
 * - [details]：富文本与输入配置上下文快照
 * - [hint]：排查与踩坑建议
 */
open class ActionWorkflowException(
    val code: String,
    val messageText: String,
    val workflowId: String? = null,
    val workflowName: String? = null,
    val nodeId: String? = null,
    val nodeType: String? = null,
    val nodeLabel: String? = null,
    val configKey: String? = null,
    val details: Map<String, JsonElement> = emptyMap(),
    val hint: String? = null,
    cause: Throwable? = null,
) : RuntimeException(buildErrorMessage(code, nodeId, messageText), cause) {

    /**
     * 补充工作流 ID 与名称元数据，确保任何深层抛出的领域异常在引擎层面都能完整溯源。
     */
    fun withWorkflowInfo(workflowId: String, workflowName: String): ActionWorkflowException {
        if (this.workflowId == workflowId && this.workflowName == workflowName) return this
        return ActionWorkflowException(
            code = code,
            messageText = messageText,
            workflowId = this.workflowId ?: workflowId,
            workflowName = this.workflowName ?: workflowName,
            nodeId = nodeId,
            nodeType = nodeType,
            nodeLabel = nodeLabel,
            configKey = configKey,
            details = details,
            hint = hint,
            cause = cause,
        )
    }

    /**
     * 转换为可随工作流事件广播和 JSON 序列化的 [ActionExecutionError]。
     */
    fun toExecutionError(): ActionExecutionError {
        val detailsJson = buildJsonObject {
            details.forEach { (k, v) -> put(k, v) }
            nodeType?.let { put(ActionErrorKey.NODE_TYPE, JsonPrimitive(it)) }
            nodeLabel?.let { put(ActionErrorKey.NODE_LABEL, JsonPrimitive(it)) }
            workflowId?.let { put(ActionErrorKey.WORKFLOW_ID, JsonPrimitive(it)) }
            workflowName?.let { put(ActionErrorKey.WORKFLOW_NAME, JsonPrimitive(it)) }
            configKey?.let { put(ActionErrorKey.CONFIG_KEY, JsonPrimitive(it)) }
            hint?.let { put(ActionErrorKey.HINT, JsonPrimitive(it)) }
            cause?.let {
                put(ActionErrorKey.CAUSE_CLASS, JsonPrimitive(it::class.simpleName.orEmpty()))
                put(ActionErrorKey.CAUSE_MESSAGE, JsonPrimitive(it.message.orEmpty()))
            }
        }
        return ActionExecutionError(
            code = code,
            message = messageText,
            nodeId = nodeId,
            details = detailsJson,
        )
    }

    /**
     * 生成适合控制台打印或开发日志记录的高可读性格式化日志块。
     */
    fun buildFormattedTraceLog(): String {
        return buildString {
            appendLine("================================================================================")
            appendLine("❌ [WORKFLOW ERROR TRACE] 工作流执行异常溯源报告")
            appendLine("--------------------------------------------------------------------------------")
            if (!workflowId.isNullOrEmpty()) {
                appendLine("📍 工作流标识 : [$workflowId] ${workflowName.orEmpty()}")
            }
            if (!nodeId.isNullOrEmpty()) {
                appendLine("📍 节点标识   : [$nodeId]")
            }
            if (!nodeType.isNullOrEmpty()) {
                appendLine("📍 节点类型   : $nodeType (${nodeLabel.orEmpty()})")
            }
            appendLine("📍 错误代码   : $code")
            appendLine("📍 错误原因   : $messageText")
            if (!configKey.isNullOrEmpty()) {
                appendLine("📍 问题配置项 : $configKey")
            }
            if (details.isNotEmpty()) {
                appendLine("📍 上下文快照 : ${JsonObject(details)}")
            }
            cause?.let { c ->
                appendLine("📍 底层异常   : ${c.stackTraceToString()}")
            }
            if (!hint.isNullOrEmpty()) {
                appendLine("💡 排查建议   : $hint")
            }
            appendLine("================================================================================")
        }
    }

    companion object {
        private fun buildErrorMessage(code: String, nodeId: String?, messageText: String): String {
            val nodePrefix = if (nodeId.isNullOrEmpty()) "" else "[$nodeId] "
            return "[$code] $nodePrefix$messageText"
        }
    }
}

/**
 * 节点运行时执行异常。
 */
class ActionNodeExecutionException(
    code: String = ActionErrorCode.NODE_EXECUTION_FAILED,
    messageText: String,
    workflowId: String? = null,
    workflowName: String? = null,
    nodeId: String? = null,
    nodeType: String? = null,
    nodeLabel: String? = null,
    configKey: String? = null,
    details: Map<String, JsonElement> = emptyMap(),
    hint: String? = null,
    cause: Throwable? = null,
) : ActionWorkflowException(code, messageText, workflowId, workflowName, nodeId, nodeType, nodeLabel, configKey, details, hint, cause)
