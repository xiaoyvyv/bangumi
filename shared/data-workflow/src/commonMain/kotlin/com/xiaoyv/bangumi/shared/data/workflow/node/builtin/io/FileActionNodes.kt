package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.io

import com.xiaoyv.bangumi.shared.data.workflow.exception.ActionErrorCode
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFileConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.failurePort
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
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

/**
 * 工作流文件沙箱节点集合。
 */
internal fun fileActionNodeDefinitions(
    fileStorage: ActionWorkflowFileStorage,
): List<ActionNodeDefinition> = listOf(
    fileReadTextDefinition(fileStorage),
    fileWriteTextDefinition(fileStorage),
    fileCreateDefinition(fileStorage),
    fileGetWorkingDirectoryDefinition(fileStorage),
    fileDeleteDefinition(fileStorage),
    fileExistsDefinition(fileStorage),
    fileMkdirDefinition(fileStorage),
    fileListDefinition(fileStorage),
    fileCopyDefinition(fileStorage),
    fileMoveDefinition(fileStorage),
    fileCompressZipDefinition(fileStorage),
    fileExtractZipDefinition(fileStorage),
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

private fun fileCreateDefinition(fileStorage: ActionWorkflowFileStorage) = fileDefinition(
    type = ActionNodeType.FILE_CREATE,
    requiredConfigKeys = setOf(ActionFileConfigKey.PATH, ActionFileConfigKey.OUTPUT_KEY),
) { node, context, workflowId ->
    fileStorage.createFile(workflowId, text(node, ActionFileConfigKey.PATH, context))
    node.valueResult(node.config.string(ActionFileConfigKey.OUTPUT_KEY), JsonPrimitive(true))
}

private fun fileGetWorkingDirectoryDefinition(fileStorage: ActionWorkflowFileStorage) = fileDefinition(
    type = ActionNodeType.FILE_GET_WORKING_DIRECTORY,
    requiredConfigKeys = setOf(ActionFileConfigKey.OUTPUT_KEY),
) { node, _, workflowId ->
    node.valueResult(
        node.config.string(ActionFileConfigKey.OUTPUT_KEY),
        JsonPrimitive(fileStorage.workingDirectory(workflowId)),
    )
}

private fun fileDeleteDefinition(fileStorage: ActionWorkflowFileStorage) = fileDefinition(
    type = ActionNodeType.FILE_DELETE,
    requiredConfigKeys = setOf(ActionFileConfigKey.PATH, ActionFileConfigKey.OUTPUT_KEY),
) { node, context, workflowId ->
    fileStorage.delete(
        workflowId = workflowId,
        path = text(node, ActionFileConfigKey.PATH, context)
    )
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
    fileStorage.mkdir(
        workflowId = workflowId,
        path = text(node, ActionFileConfigKey.PATH, context)
    )
    node.valueResult(node.config.string(ActionFileConfigKey.OUTPUT_KEY), JsonPrimitive(true))
}

private fun fileListDefinition(fileStorage: ActionWorkflowFileStorage) = fileDefinition(
    type = ActionNodeType.FILE_LIST,
    requiredConfigKeys = setOf(ActionFileConfigKey.PATH, ActionFileConfigKey.OUTPUT_KEY),
) { node, context, workflowId ->
    val files = fileStorage.list(
        workflowId = workflowId,
        path = text(node, ActionFileConfigKey.PATH, context)
    )
    node.valueResult(node.config.string(ActionFileConfigKey.OUTPUT_KEY), JsonArray(files.map(::JsonPrimitive)))
}

private fun fileCopyDefinition(fileStorage: ActionWorkflowFileStorage) = fileDefinition(
    type = ActionNodeType.FILE_COPY,
    requiredConfigKeys = setOf(ActionFileConfigKey.FROM_PATH, ActionFileConfigKey.TO_PATH, ActionFileConfigKey.OUTPUT_KEY),
) { node, context, workflowId ->
    fileStorage.copy(
        workflowId = workflowId,
        fromPath = text(node, ActionFileConfigKey.FROM_PATH, context),
        toPath = text(node, ActionFileConfigKey.TO_PATH, context),
    )
    node.valueResult(node.config.string(ActionFileConfigKey.OUTPUT_KEY), JsonPrimitive(true))
}

private fun fileMoveDefinition(fileStorage: ActionWorkflowFileStorage) = fileDefinition(
    type = ActionNodeType.FILE_MOVE,
    requiredConfigKeys = setOf(ActionFileConfigKey.FROM_PATH, ActionFileConfigKey.TO_PATH, ActionFileConfigKey.OUTPUT_KEY),
) { node, context, workflowId ->
    fileStorage.move(
        workflowId = workflowId,
        fromPath = text(node, ActionFileConfigKey.FROM_PATH, context),
        toPath = text(node, ActionFileConfigKey.TO_PATH, context),
    )
    node.valueResult(node.config.string(ActionFileConfigKey.OUTPUT_KEY), JsonPrimitive(true))
}

private fun fileCompressZipDefinition(fileStorage: ActionWorkflowFileStorage) = fileDefinition(
    type = ActionNodeType.FILE_COMPRESS_ZIP,
    requiredConfigKeys = setOf(ActionFileConfigKey.PATHS, ActionFileConfigKey.TO_PATH, ActionFileConfigKey.OUTPUT_KEY),
) { node, context, workflowId ->
    fileStorage.compressZip(
        workflowId = workflowId,
        paths = paths(node, context),
        toPath = text(node, ActionFileConfigKey.TO_PATH, context),
    )
    node.valueResult(node.config.string(ActionFileConfigKey.OUTPUT_KEY), JsonPrimitive(true))
}

private fun fileExtractZipDefinition(fileStorage: ActionWorkflowFileStorage) = fileDefinition(
    type = ActionNodeType.FILE_EXTRACT_ZIP,
    requiredConfigKeys = setOf(ActionFileConfigKey.FROM_PATH, ActionFileConfigKey.TO_PATH, ActionFileConfigKey.OUTPUT_KEY),
) { node, context, workflowId ->
    fileStorage.extractZip(
        workflowId = workflowId,
        fromPath = text(node, ActionFileConfigKey.FROM_PATH, context),
        toPath = text(node, ActionFileConfigKey.TO_PATH, context),
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
        outputPorts = persistentListOf(nextPort, failurePort),
        requiredConfigKeys = requiredConfigKeys,
    ),
    executor = { node, context ->
        execute(node, context, requireNotNull(context.workflowId) { ActionErrorCode.FILE_CONTEXT_MISSING_MSG })
    },
)

private fun text(
    node: com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode,
    key: String,
    context: com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext,
): String = ActionTemplateResolver.resolveText(node.config.string(key), context)

private fun paths(
    node: com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode,
    context: com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext,
): List<String> = node.config.getValue(ActionFileConfigKey.PATHS).jsonArray.map { element ->
    ActionTemplateResolver.resolveText(element.jsonPrimitive.content, context)
}
