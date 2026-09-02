package com.xiaoyv.bangumi.shared.data.repository.impl

import com.xiaoyv.bangumi.shared.core.utils.serialization.ImmutableListSerializer
import com.xiaoyv.bangumi.shared.data.repository.ActionWorkflowRepository
import com.xiaoyv.bangumi.shared.data.repository.DatabaseRepository
import com.xiaoyv.bangumi.shared.data.workflow.codec.ActionWorkflowCodec
import com.xiaoyv.bangumi.shared.data.workflow.codec.ActionWorkflowImportResult
import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionWorkflowValidation
import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionWorkflowValidator
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionWorkflow
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

/**
 * 基于现有可序列化数据库表的工作流仓储实现。
 */
class ActionWorkflowRepositoryImpl(
    private val databaseRepository: DatabaseRepository,
    private val validator: ActionWorkflowValidator,
    private val codec: ActionWorkflowCodec,
) : ActionWorkflowRepository {
    override fun list(): ImmutableList<ActionWorkflow> {
        return databaseRepository.get(STORAGE_KEY, persistentListOf(), WORKFLOW_LIST_SERIALIZER)
    }

    override fun find(id: String): ActionWorkflow? = list().firstOrNull { it.id == id }

    override fun save(workflow: ActionWorkflow): ActionWorkflowValidation {
        val validation = validator.validate(workflow)
        if (!validation.isValid) return validation
        val updated = list().filterNot { it.id == workflow.id }.plus(workflow).toPersistentList()
        databaseRepository.put(STORAGE_KEY, updated, WORKFLOW_LIST_SERIALIZER)
        return validation
    }

    override fun delete(id: String) {
        databaseRepository.put(STORAGE_KEY, list().filterNot { it.id == id }.toPersistentList(), WORKFLOW_LIST_SERIALIZER)
    }

    override fun export(id: String): String? = find(id)?.let(codec::export)

    override fun import(raw: String, overwrite: Boolean): ActionWorkflowImportResult {
        return when (val result = codec.import(raw)) {
            is ActionWorkflowImportResult.Success -> {
                if (!overwrite && find(result.workflow.id) != null) {
                    ActionWorkflowImportResult.Failure("duplicate_workflow_id", "已存在相同 ID 的工作流")
                } else {
                    save(result.workflow)
                    result
                }
            }

            else -> result
        }
    }

    private companion object {
        const val STORAGE_KEY = "action_workflow_documents_v1"
        val WORKFLOW_LIST_SERIALIZER = ImmutableListSerializer(ActionWorkflow.serializer())
    }
}
