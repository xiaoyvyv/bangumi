package com.xiaoyv.bangumi.shared.data.repository

import com.xiaoyv.bangumi.shared.data.workflow.codec.ActionWorkflowImportResult
import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionWorkflowValidation
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionWorkflow
import kotlinx.collections.immutable.ImmutableList

/**
 * 自定义行为工作流的本地存储与导入导出入口。
 */
interface ActionWorkflowRepository {
    /**
     * 返回本地保存的全部工作流。
     */
    fun list(): ImmutableList<ActionWorkflow>

    /**
     * 按工作流 ID 查找工作流。
     *
     * @param id 工作流唯一标识。
     */
    fun find(id: String): ActionWorkflow?

    /**
     * 校验并保存工作流；存在相同 ID 时覆盖旧版本。
     *
     * @param workflow 待保存的工作流。
     * @return 工作流校验结果，存在错误时不会写入本地存储。
     */
    fun save(workflow: ActionWorkflow): ActionWorkflowValidation

    /**
     * 删除指定工作流及其本地保存内容。
     *
     * @param id 工作流唯一标识。
     */
    fun delete(id: String)

    /**
     * 导出指定工作流为 JSON。
     *
     * @param id 工作流唯一标识。
     * @return 可导出的 JSON；工作流不存在时返回 null。
     */
    fun export(id: String): String?

    /**
     * 导入工作流 JSON，并在校验成功后保存。
     *
     * @param raw 工作流 JSON 文本。
     * @param overwrite 是否允许使用相同 ID 覆盖本地工作流。
     */
    fun import(raw: String, overwrite: Boolean = false): ActionWorkflowImportResult
}
