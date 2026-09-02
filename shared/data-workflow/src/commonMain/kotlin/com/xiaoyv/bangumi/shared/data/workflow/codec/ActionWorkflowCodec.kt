package com.xiaoyv.bangumi.shared.data.workflow.codec

import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionWorkflowValidation
import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionWorkflowValidator
import com.xiaoyv.bangumi.shared.data.workflow.exception.ActionErrorCode
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionWorkflow
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeRegistry
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * 工作流 JSON 的导入、导出与文档格式迁移。
 *
 * 密钥只可由节点配置引用名称，调用方不应将任何凭据写入本文件。
 */
class ActionWorkflowCodec(
    private val json: Json,
    private val validator: ActionWorkflowValidator,
    private val registry: ActionNodeRegistry,
) {
    /**
     * 校验并导出工作流为可分享的 JSON 文本。
     *
     * @param workflow 待导出的工作流。
     * @return 工作流 JSON。
     * @throws IllegalArgumentException 工作流校验不通过时抛出。
     */
    fun export(workflow: ActionWorkflow): String {
        require(validator.validate(workflow).isValid) { "无法导出不合法的工作流" }
        return json.encodeToString(ActionWorkflow.serializer(), workflow)
    }

    /**
     * 解析、迁移并校验外部 JSON。
     *
     * @param raw 待导入的 JSON 文本。
     * @return 成功、校验失败或解析失败结果。
     */
    fun import(raw: String): ActionWorkflowImportResult {
        val migrated = runCatching { migrate(json.parseToJsonElement(raw).jsonObject) }
            .getOrElse { return ActionWorkflowImportResult.Failure(ActionErrorCode.INVALID_JSON, it.message.orEmpty().ifBlank { ActionErrorCode.INVALID_JSON_MSG }) }
        val workflow = runCatching {
            registry.migrate(json.decodeFromJsonElement(ActionWorkflow.serializer(), migrated))
        }.getOrElse { error ->
            val message = (error as? SerializationException)?.message.orEmpty().ifBlank { ActionErrorCode.INVALID_WORKFLOW_MSG }
            return ActionWorkflowImportResult.Failure(ActionErrorCode.INVALID_WORKFLOW, message)
        }
        val validation = validator.validate(workflow)
        return if (validation.isValid) ActionWorkflowImportResult.Success(workflow) else ActionWorkflowImportResult.Invalid(workflow, validation)
    }

    private fun migrate(document: JsonObject): JsonObject {
        val version = document["formatVersion"]?.jsonPrimitive?.intOrNull ?: 1
        require(version <= ActionWorkflow.CURRENT_FORMAT_VERSION) { "不支持未来版本工作流" }
        return when (version) {
            ActionWorkflow.CURRENT_FORMAT_VERSION -> document
            else -> error("缺少工作流格式迁移：v$version")
        }
    }
}

/**
 * 工作流 JSON 导入结果。
 */
sealed interface ActionWorkflowImportResult {
    data class Success(val workflow: ActionWorkflow) : ActionWorkflowImportResult
    data class Invalid(
        val workflow: ActionWorkflow,
        val validation: ActionWorkflowValidation,
    ) : ActionWorkflowImportResult

    data class Failure(val code: String, val message: String) : ActionWorkflowImportResult
}
