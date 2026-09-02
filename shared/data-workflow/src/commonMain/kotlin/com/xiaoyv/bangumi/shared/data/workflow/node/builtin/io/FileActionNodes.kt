package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.io

import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFileConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.inPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.nextPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.valueResult
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeCategory
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeDefinition
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeSpec
import com.xiaoyv.bangumi.shared.data.workflow.node.core.string
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionTemplateResolver
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionWorkflowFileStorage
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * 工作流文件沙箱节点集合。
 */
internal fun fileActionNodeDefinitions(
    fileStorage: ActionWorkflowFileStorage,
): List<ActionNodeDefinition> = listOf(
    fileReadTextDefinition(fileStorage),
    fileWriteTextDefinition(fileStorage),
    fileDeleteDefinition(fileStorage),
    fileExistsDefinition(fileStorage),
    fileMkdirDefinition(fileStorage),
    fileListDefinition(fileStorage),
    fileCopyDefinition(fileStorage),
    fileMoveDefinition(fileStorage),
)

private fun fileReadTextDefinition(fileStorage: ActionWorkflowFileStorage) = fileDefinition(
    type = ActionNodeType.FILE_READ_TEXT,
    requiredConfigKeys = setOf(ActionFileConfigKey.PATH, ActionFileConfigKey.OUTPUT_KEY),
) { node, context, workflowId ->
    node.valueResult(
        node.config.string(ActionFileConfigKey.OUTPUT_KEY),
        JsonPrimitive(fileStorage.readText(workflowId, text(node, ActionFileConfigKey.PATH, context))),
    )
}

private fun fileWriteTextDefinition(fileStorage: ActionWorkflowFileStorage) = fileDefinition(
    type = ActionNodeType.FILE_WRITE_TEXT,
    requiredConfigKeys = setOf(ActionFileConfigKey.PATH, ActionFileConfigKey.TEXT, ActionFileConfigKey.OUTPUT_KEY),
) { node, context, workflowId ->
    fileStorage.writeText(
        workflowId = workflowId,
        path = text(node, ActionFileConfigKey.PATH, context),
        text = text(node, ActionFileConfigKey.TEXT, context),
        append = node.config[ActionFileConfigKey.APPEND]?.jsonPrimitive?.booleanOrNull ?: false,
    )
    node.valueResult(node.config.string(ActionFileConfigKey.OUTPUT_KEY), JsonPrimitive(true))
}

private fun fileDeleteDefinition(fileStorage: ActionWorkflowFileStorage) = fileDefinition(
    type = ActionNodeType.FILE_DELETE,
    requiredConfigKeys = setOf(ActionFileConfigKey.PATH, ActionFileConfigKey.OUTPUT_KEY),
) { node, context, workflowId ->
    fileStorage.delete(workflowId, text(node, ActionFileConfigKey.PATH, context))
    node.valueResult(node.config.string(ActionFileConfigKey.OUTPUT_KEY), JsonPrimitive(true))
}

private fun fileExistsDefinition(fileStorage: ActionWorkflowFileStorage) = fileDefinition(
    type = ActionNodeType.FILE_EXISTS,
    requiredConfigKeys = setOf(ActionFileConfigKey.PATH, ActionFileConfigKey.OUTPUT_KEY),
) { node, context, workflowId ->
    node.valueResult(
        node.config.string(ActionFileConfigKey.OUTPUT_KEY),
        JsonPrimitive(fileStorage.exists(workflowId, text(node, ActionFileConfigKey.PATH, context))),
    )
}

private fun fileMkdirDefinition(fileStorage: ActionWorkflowFileStorage) = fileDefinition(
    type = ActionNodeType.FILE_MKDIR,
    requiredConfigKeys = setOf(ActionFileConfigKey.PATH, ActionFileConfigKey.OUTPUT_KEY),
) { node, context, workflowId ->
    fileStorage.mkdir(workflowId, text(node, ActionFileConfigKey.PATH, context))
    node.valueResult(node.config.string(ActionFileConfigKey.OUTPUT_KEY), JsonPrimitive(true))
}

private fun fileListDefinition(fileStorage: ActionWorkflowFileStorage) = fileDefinition(
    type = ActionNodeType.FILE_LIST,
    requiredConfigKeys = setOf(ActionFileConfigKey.PATH, ActionFileConfigKey.OUTPUT_KEY),
) { node, context, workflowId ->
    val files = fileStorage.list(workflowId, text(node, ActionFileConfigKey.PATH, context))
    node.valueResult(node.config.string(ActionFileConfigKey.OUTPUT_KEY), JsonArray(files.map(::JsonPrimitive)))
}

private fun fileCopyDefinition(fileStorage: ActionWorkflowFileStorage) = fileDefinition(
    type = ActionNodeType.FILE_COPY,
    requiredConfigKeys = setOf(ActionFileConfigKey.FROM_PATH, ActionFileConfigKey.TO_PATH, ActionFileConfigKey.OUTPUT_KEY),
) { node, context, workflowId ->
    fileStorage.copy(
        workflowId,
        text(node, ActionFileConfigKey.FROM_PATH, context),
        text(node, ActionFileConfigKey.TO_PATH, context),
    )
    node.valueResult(node.config.string(ActionFileConfigKey.OUTPUT_KEY), JsonPrimitive(true))
}

private fun fileMoveDefinition(fileStorage: ActionWorkflowFileStorage) = fileDefinition(
    type = ActionNodeType.FILE_MOVE,
    requiredConfigKeys = setOf(ActionFileConfigKey.FROM_PATH, ActionFileConfigKey.TO_PATH, ActionFileConfigKey.OUTPUT_KEY),
) { node, context, workflowId ->
    fileStorage.move(
        workflowId,
        text(node, ActionFileConfigKey.FROM_PATH, context),
        text(node, ActionFileConfigKey.TO_PATH, context),
    )
    node.valueResult(node.config.string(ActionFileConfigKey.OUTPUT_KEY), JsonPrimitive(true))
}

private fun fileDefinition(
    type: String,
    requiredConfigKeys: Set<String>,
    execute: suspend (com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode, com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext, String) -> com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionNodeExecutionResult,
) = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = type,
        category = ActionNodeCategory.STORAGE,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = requiredConfigKeys,
    ),
    executor = { node, context ->
        execute(node, context, requireNotNull(context.workflowId) { "文件节点必须由工作流引擎执行" })
    },
)

private fun text(
    node: com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode,
    key: String,
    context: com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext,
): String = ActionTemplateResolver.resolveText(node.config.string(key), context)
