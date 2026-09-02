package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.io

import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionNodeExecutionResult
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionStorageConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.inPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.nextPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.valueResult
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeCategory
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeDefinition
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeSpec
import com.xiaoyv.bangumi.shared.data.workflow.node.core.string
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionTemplateResolver
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionWorkflowPreferencesStore
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.json.JsonNull

/**
 * 工作流私有 Preferences 持久化节点。
 */
internal fun storageActionNodeDefinitions(
    preferencesStore: ActionWorkflowPreferencesStore,
): List<ActionNodeDefinition> = listOf(
    storagePreferencesGetDefinition(preferencesStore),
    storagePreferencesSetDefinition(preferencesStore),
    storagePreferencesDeleteDefinition(preferencesStore),
    storagePreferencesHasDefinition(preferencesStore),
    storagePreferencesClearDefinition(preferencesStore),
)

private fun storagePreferencesGetDefinition(preferencesStore: ActionWorkflowPreferencesStore) = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.STORAGE_PREFERENCES_GET,
        category = ActionNodeCategory.STORAGE,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionStorageConfigKey.KEY, ActionStorageConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val key = ActionTemplateResolver.resolveText(node.config.string(ActionStorageConfigKey.KEY), context)
        node.valueResult(
            node.config.string(ActionStorageConfigKey.OUTPUT_KEY),
            preferencesStore.get(key) ?: JsonNull,
        )
    },
)

private fun storagePreferencesSetDefinition(preferencesStore: ActionWorkflowPreferencesStore) = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.STORAGE_PREFERENCES_SET,
        category = ActionNodeCategory.STORAGE,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(
            ActionStorageConfigKey.KEY,
            ActionStorageConfigKey.VALUE,
            ActionStorageConfigKey.OUTPUT_KEY,
        ),
    ),
    executor = { node, context ->
        val key = ActionTemplateResolver.resolveText(node.config.string(ActionStorageConfigKey.KEY), context)
        val value = ActionTemplateResolver.resolveElement(node.config[ActionStorageConfigKey.VALUE], context)
        preferencesStore.set(key, value)
        node.valueResult(node.config.string(ActionStorageConfigKey.OUTPUT_KEY), value)
    },
)

private fun storagePreferencesDeleteDefinition(preferencesStore: ActionWorkflowPreferencesStore) = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.STORAGE_PREFERENCES_DELETE,
        category = ActionNodeCategory.STORAGE,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionStorageConfigKey.KEY, ActionStorageConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val key = ActionTemplateResolver.resolveText(node.config.string(ActionStorageConfigKey.KEY), context)
        preferencesStore.delete(key)
        node.valueResult(
            node.config.string(ActionStorageConfigKey.OUTPUT_KEY),
            kotlinx.serialization.json.JsonPrimitive(true),
        )
    },
)

private fun storagePreferencesHasDefinition(preferencesStore: ActionWorkflowPreferencesStore) = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.STORAGE_PREFERENCES_HAS,
        category = ActionNodeCategory.STORAGE,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionStorageConfigKey.KEY, ActionStorageConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val key = ActionTemplateResolver.resolveText(node.config.string(ActionStorageConfigKey.KEY), context)
        node.valueResult(
            node.config.string(ActionStorageConfigKey.OUTPUT_KEY),
            kotlinx.serialization.json.JsonPrimitive(preferencesStore.has(key)),
        )
    },
)

private fun storagePreferencesClearDefinition(preferencesStore: ActionWorkflowPreferencesStore) = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.STORAGE_PREFERENCES_CLEAR,
        category = ActionNodeCategory.STORAGE,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
    ),
    executor = { _, _ ->
        preferencesStore.clear()
        ActionNodeExecutionResult("next")
    },
)
