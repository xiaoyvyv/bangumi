package com.xiaoyv.bangumi.shared.data.workflow.node.builtin

import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionNodeExecutionResult
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.control.controlActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.control.flowActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.control.loopActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.data.arrayActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.data.dataActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.data.mathActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.data.objectActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.data.textActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.extension.bilibiliActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.io.fileActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.io.httpActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.io.sideEffectActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.io.storageActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.parse.codecActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.parse.cryptoActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.parse.csvActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.parse.dateActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.parse.htmlActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.parse.jsonActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.parse.urlActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.parse.xmlActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeDefinition
import com.xiaoyv.bangumi.shared.data.workflow.node.core.string
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionTemplateResolver
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionHttpRequestExecutor
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionWorkflowFileStorage
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionWorkflowLogger
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionWorkflowPreferencesStore
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * App 随附的内置节点集合。
 */
fun builtInActionNodeDefinitions(
    httpRequestExecutor: ActionHttpRequestExecutor,
    preferencesStore: ActionWorkflowPreferencesStore,
    logger: ActionWorkflowLogger = ActionWorkflowLogger.Default,
    fileStorage: ActionWorkflowFileStorage = ActionWorkflowFileStorage.Default,
): List<ActionNodeDefinition> = buildList {
    addAll(flowActionNodeDefinitions(logger))
    addAll(dataActionNodeDefinitions)
    addAll(dateActionNodeDefinitions)
    addAll(codecActionNodeDefinitions)
    addAll(cryptoActionNodeDefinitions)
    addAll(htmlActionNodeDefinitions)
    addAll(objectActionNodeDefinitions)
    addAll(jsonActionNodeDefinitions)
    addAll(controlActionNodeDefinitions)
    addAll(loopActionNodeDefinitions)
    addAll(mathActionNodeDefinitions)
    addAll(textActionNodeDefinitions)
    addAll(arrayActionNodeDefinitions)
    addAll(urlActionNodeDefinitions)
    addAll(csvActionNodeDefinitions)
    addAll(xmlActionNodeDefinitions)
    addAll(bilibiliActionNodeDefinitions)
    addAll(sideEffectActionNodeDefinitions)
    addAll(httpActionNodeDefinitions(httpRequestExecutor, fileStorage))
    addAll(storageActionNodeDefinitions(preferencesStore))
    addAll(fileActionNodeDefinitions(fileStorage))
}

internal fun ActionNode.valueResult(
    key: String,
    value: JsonElement,
): ActionNodeExecutionResult {
    require(key.isNotBlank()) { "outputKey 不能为空" }
    return ActionNodeExecutionResult(
        outputPortId = "next",
        output = JsonObject(mapOf(key to value)),
    )
}

internal fun ActionNode.values(context: ActionExecutionContext): JsonArray =
    ActionTemplateResolver.resolveElement(config[ActionArrayConfigKey.VALUES], context) as? JsonArray ?: error("数组节点 values 必须是数组")

internal fun ActionNode.arrayResult(value: JsonArray) =
    valueResult(config.string(ActionArrayConfigKey.OUTPUT_KEY), value)
