package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.io

import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionNodeExecutionResult
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionSideEffect
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCapability
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionClipboardConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionConfirmConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionImagePreviewConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionInputDialogConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNotificationConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionOpenAppConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionOpenUrlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionOpenWebConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionProgressDialogAction
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionProgressDialogConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionProgressDialogMode
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectDialogConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectOutputMode
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionShareConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSyncCookieConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionToastConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionVideoPreviewConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.failurePort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.inPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.successPort
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeCategory
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeDefinition
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeSpec
import com.xiaoyv.bangumi.shared.data.workflow.node.core.string
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionConfirmEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionImagePreviewEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionInputDialogEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionNotificationEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionOpenExternalAppEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionOpenExternalUrlEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionOpenInternalWebEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionProgressDialogEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionReadClipboardEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionSelectDialogEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionSelectDialogOption
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionShareEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionShowToastEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionSyncCookieEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionVibrateEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionVideoPreviewEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionWriteClipboardEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionTemplateResolver
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionUrlPolicy
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * 请求宿主执行 UI 或网络副作用的内置节点。
 */
internal val sideEffectActionNodeDefinitions: List<ActionNodeDefinition> = listOf(
    openExternalUrlDefinition(),
    openExternalAppDefinition(),
    openInternalWebDefinition(),
    showToastDefinition(),
    writeClipboardDefinition(),
    confirmDefinition(),
    shareDefinition(),
    notificationDefinition(),
    readClipboardDefinition(),
    vibrateDefinition(),
    inputDialogDefinition(),
    progressDialogDefinition(),
    progressUpdateDefinition(),
    progressDismissDefinition(),
    selectDialogDefinition(),
    imagePreviewDefinition(),
    videoPreviewDefinition(),
    syncCookieDefinition(),
)

private fun openExternalUrlDefinition() =
    sideEffectDefinition(ActionNodeType.OPEN_EXTERNAL_URL, ActionOpenUrlConfigKey.URL, setOf(ActionCapability.OPEN_EXTERNAL_URL)) { config, context ->
        ActionOpenExternalUrlEffect(url = ActionTemplateResolver.resolveText(config.string(ActionOpenUrlConfigKey.URL), context).also(ActionUrlPolicy::requireHttpUrl))
    }

private fun openExternalAppDefinition() =
    sideEffectDefinition(ActionNodeType.OPEN_EXTERNAL_APP, ActionOpenAppConfigKey.URI, setOf(ActionCapability.OPEN_EXTERNAL_APP)) { config, context ->
        val uri = ActionTemplateResolver.resolveText(config.string(ActionOpenAppConfigKey.URI), context)
        val fallbackUrl = config[ActionOpenAppConfigKey.FALLBACK_URL]?.jsonPrimitive?.contentOrNull?.let { ActionTemplateResolver.resolveText(it, context) }
        ActionUrlPolicy.requireExternalAppUri(uri)
        fallbackUrl?.let(ActionUrlPolicy::requireHttpUrl)
        ActionOpenExternalAppEffect(uri, fallbackUrl)
    }

private fun openInternalWebDefinition() =
    sideEffectDefinition(ActionNodeType.OPEN_INTERNAL_WEB, ActionOpenWebConfigKey.URL, setOf(ActionCapability.OPEN_INTERNAL_WEB)) { config, context ->
        ActionOpenInternalWebEffect(ActionTemplateResolver.resolveText(config.string(ActionOpenWebConfigKey.URL), context).also(ActionUrlPolicy::requireHttpUrl))
    }

private fun showToastDefinition() = sideEffectDefinition(ActionNodeType.SHOW_TOAST, ActionToastConfigKey.MESSAGE, emptySet()) { config, context ->
    ActionShowToastEffect(ActionTemplateResolver.resolveText(config.string(ActionToastConfigKey.MESSAGE), context))
}

private fun writeClipboardDefinition() =
    sideEffectDefinition(ActionNodeType.WRITE_CLIPBOARD, ActionClipboardConfigKey.TEXT, setOf(ActionCapability.CLIPBOARD_WRITE)) { config, context ->
        ActionWriteClipboardEffect(ActionTemplateResolver.resolveText(config.string(ActionClipboardConfigKey.TEXT), context))
    }

private fun confirmDefinition() =
    sideEffectDefinition(ActionNodeType.UI_CONFIRM, ActionConfirmConfigKey.MESSAGE, setOf(ActionCapability.CONFIRM_DIALOG)) { config, context ->
        ActionConfirmEffect(
            title = config[ActionConfirmConfigKey.TITLE]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) }.orEmpty(),
            message = ActionTemplateResolver.resolveText(config.string(ActionConfirmConfigKey.MESSAGE), context),
            confirmText = config[ActionConfirmConfigKey.CONFIRM_TEXT]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) }.orEmpty(),
            cancelText = config[ActionConfirmConfigKey.CANCEL_TEXT]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) }.orEmpty(),
        )
    }

private fun shareDefinition() =
    sideEffectDefinition(ActionNodeType.SYSTEM_SHARE, ActionShareConfigKey.TEXT, setOf(ActionCapability.SHARE)) { config, context ->
        ActionShareEffect(
            title = config[ActionShareConfigKey.TITLE]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) }.orEmpty(),
            text = ActionTemplateResolver.resolveText(config.string(ActionShareConfigKey.TEXT), context),
            url = config[ActionOpenUrlConfigKey.URL]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) }.orEmpty(),
        )
    }

private fun notificationDefinition() =
    sideEffectDefinition(ActionNodeType.SYSTEM_NOTIFICATION, ActionNotificationConfigKey.CONTENT, setOf(ActionCapability.NOTIFICATION)) { config, context ->
        ActionNotificationEffect(
            title = config[ActionNotificationConfigKey.TITLE]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) }.orEmpty(),
            content = ActionTemplateResolver.resolveText(config.string(ActionNotificationConfigKey.CONTENT), context),
        )
    }

private fun readClipboardDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.READ_CLIPBOARD,
        category = ActionNodeCategory.ACTION,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(successPort, failurePort),
        requiredConfigKeys = setOf(ActionClipboardConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val outputKey = node.config.string(ActionClipboardConfigKey.OUTPUT_KEY)
        val text = context.variables[outputKey]?.let { (it as? JsonPrimitive)?.content }.orEmpty()
        ActionNodeExecutionResult(
            outputPortId = ActionControlPortId.SUCCESS,
            output = JsonObject(mapOf(outputKey to JsonPrimitive(text))),
            sideEffect = ActionReadClipboardEffect(outputKey = outputKey),
        )
    },
)

private fun vibrateDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.SYSTEM_VIBRATE,
        category = ActionNodeCategory.ACTION,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(successPort, failurePort),
        requiredConfigKeys = emptySet(),
    ),
    executor = { _, _ ->
        ActionNodeExecutionResult(
            outputPortId = ActionControlPortId.SUCCESS,
            sideEffect = ActionVibrateEffect(),
        )
    },
)

private fun inputDialogDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.UI_INPUT_DIALOG,
        category = ActionNodeCategory.ACTION,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(successPort, failurePort),
        requiredConfigKeys = setOf(ActionInputDialogConfigKey.OUTPUT_KEY),
        requiredCapabilities = setOf(ActionCapability.INPUT_DIALOG),
    ),
    executor = { node, context ->
        val outputKey = node.config.string(ActionInputDialogConfigKey.OUTPUT_KEY)
        val title = node.config[ActionInputDialogConfigKey.TITLE]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) }.orEmpty()
        val subtitle = node.config[ActionInputDialogConfigKey.SUBTITLE]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) }.orEmpty()
        val defaultValue = node.config[ActionInputDialogConfigKey.DEFAULT_VALUE]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) }.orEmpty()
        ActionNodeExecutionResult(
            outputPortId = ActionControlPortId.SUCCESS,
            sideEffect = ActionInputDialogEffect(
                title = title,
                subtitle = subtitle,
                defaultValue = defaultValue,
                outputKey = outputKey,
            ),
        )
    },
)

/**
 * 解析并构建进度对话框节点的执行结果。
 *
 * @param defaultAction 当未显式指定 action 时使用的默认操作
 * @param node 节点模型
 * @param context 执行上下文
 */
private fun buildProgressDialogResult(
    defaultAction: String,
    node: ActionNode,
    context: ActionExecutionContext,
): ActionNodeExecutionResult {
    val action = node.config[ActionProgressDialogConfigKey.ACTION]
        ?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) }
        .orEmpty()
        .ifBlank { defaultAction }
    require(
        action in setOf(
            ActionProgressDialogAction.SHOW,
            ActionProgressDialogAction.UPDATE,
            ActionProgressDialogAction.DISMISS,
        )
    ) {
        "进度操作仅支持 ${ActionProgressDialogAction.SHOW}、${ActionProgressDialogAction.UPDATE} 或 ${ActionProgressDialogAction.DISMISS}"
    }

    val taskId = node.config[ActionProgressDialogConfigKey.TASK_ID]
        ?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) }
        .orEmpty()

    if (action == ActionProgressDialogAction.DISMISS) {
        return ActionNodeExecutionResult(
            outputPortId = ActionControlPortId.SUCCESS,
            sideEffect = ActionProgressDialogEffect(
                action = ActionProgressDialogAction.DISMISS,
                taskId = taskId,
            ),
        )
    }

    val title = node.config[ActionProgressDialogConfigKey.TITLE]
        ?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) }
        .orEmpty()
    val message = node.config[ActionProgressDialogConfigKey.MESSAGE]
        ?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) }
        .orEmpty()
    val mode = node.config[ActionProgressDialogConfigKey.MODE]
        ?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) }
        .orEmpty()
        .ifBlank { ActionProgressDialogMode.INDETERMINATE }
    require(mode in setOf(ActionProgressDialogMode.INDETERMINATE, ActionProgressDialogMode.DETERMINATE)) {
        "进度模式仅支持 ${ActionProgressDialogMode.INDETERMINATE} 或 ${ActionProgressDialogMode.DETERMINATE}"
    }
    val progress = if (action == ActionProgressDialogAction.UPDATE) {
        resolveProgressValueOrNull(node.config[ActionProgressDialogConfigKey.PROGRESS], context)
    } else {
        resolveProgressValue(node.config[ActionProgressDialogConfigKey.PROGRESS], context, 0f)
    }
    val maxProgress = if (action == ActionProgressDialogAction.UPDATE) {
        resolveProgressValueOrNull(node.config[ActionProgressDialogConfigKey.MAX_PROGRESS], context)
    } else {
        resolveProgressValue(node.config[ActionProgressDialogConfigKey.MAX_PROGRESS], context, 1f)
    }
    if (mode == ActionProgressDialogMode.DETERMINATE && progress != null && maxProgress != null) {
        require(maxProgress > 0f && progress in 0f..maxProgress) { "精确进度必须满足 0 ≤ progress ≤ maxProgress，且 maxProgress > 0" }
    }
    return ActionNodeExecutionResult(
        outputPortId = ActionControlPortId.SUCCESS,
        sideEffect = ActionProgressDialogEffect(
            action = action,
            taskId = taskId,
            title = title,
            message = message,
            mode = mode,
            progress = progress,
            maxProgress = maxProgress,
        ),
    )
}

private fun progressDialogDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.UI_PROGRESS_DIALOG,
        category = ActionNodeCategory.ACTION,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(successPort, failurePort),
        requiredCapabilities = setOf(ActionCapability.PROGRESS_DIALOG),
    ),
    executor = { node, context ->
        buildProgressDialogResult(ActionProgressDialogAction.SHOW, node, context)
    },
)

private fun progressUpdateDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.UI_PROGRESS_UPDATE,
        category = ActionNodeCategory.ACTION,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(successPort, failurePort),
        requiredCapabilities = setOf(ActionCapability.PROGRESS_DIALOG),
    ),
    executor = { node, context ->
        buildProgressDialogResult(ActionProgressDialogAction.UPDATE, node, context)
    },
)

private fun progressDismissDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.UI_PROGRESS_DISMISS,
        category = ActionNodeCategory.ACTION,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(successPort, failurePort),
        requiredCapabilities = setOf(ActionCapability.PROGRESS_DIALOG),
    ),
    executor = { node, context ->
        buildProgressDialogResult(ActionProgressDialogAction.DISMISS, node, context)
    },
)

private fun resolveProgressValue(element: JsonElement?, context: ActionExecutionContext, defaultValue: Float): Float =
    resolveProgressValueOrNull(element, context) ?: defaultValue

private fun resolveProgressValueOrNull(element: JsonElement?, context: ActionExecutionContext): Float? =
    element?.let { ActionTemplateResolver.resolveElement(it, context) }
        ?.jsonPrimitive
        ?.contentOrNull
        ?.toFloatOrNull()

private fun selectDialogDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.UI_SELECT_DIALOG,
        category = ActionNodeCategory.ACTION,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(successPort, failurePort),
        requiredConfigKeys = setOf(ActionSelectDialogConfigKey.OUTPUT_KEY, ActionSelectDialogConfigKey.OPTIONS),
        requiredCapabilities = setOf(ActionCapability.SELECT_DIALOG),
    ),
    executor = { node, context ->
        val outputKey = node.config.string(ActionSelectDialogConfigKey.OUTPUT_KEY)
        val title = node.config[ActionSelectDialogConfigKey.TITLE]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) }.orEmpty()
        val subtitle = node.config[ActionSelectDialogConfigKey.SUBTITLE]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) }.orEmpty()
        val confirmText = node.config[ActionSelectDialogConfigKey.CONFIRM_TEXT]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) }.orEmpty()
        val cancelText = node.config[ActionSelectDialogConfigKey.CANCEL_TEXT]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) }.orEmpty()
        val isMultiSelect = node.config[ActionSelectDialogConfigKey.IS_MULTI_SELECT]?.jsonPrimitive?.booleanOrNull ?: false
        val outputMode = node.config[ActionSelectDialogConfigKey.OUTPUT_MODE]?.jsonPrimitive?.contentOrNull
            ?: ActionSelectOutputMode.VALUE

        val optionsElement = ActionTemplateResolver.resolveElement(node.config[ActionSelectDialogConfigKey.OPTIONS], context)
        val options = parseSelectOptions(optionsElement, context)

        val defaultValuesElement = ActionTemplateResolver.resolveElement(node.config[ActionSelectDialogConfigKey.DEFAULT_VALUES], context)
        val defaultValues = parseDefaultValues(defaultValuesElement)

        val defaultIndicesElement = ActionTemplateResolver.resolveElement(node.config[ActionSelectDialogConfigKey.DEFAULT_INDICES], context)
        val defaultIndices = parseDefaultIndices(defaultIndicesElement)

        ActionNodeExecutionResult(
            outputPortId = ActionControlPortId.SUCCESS,
            sideEffect = ActionSelectDialogEffect(
                title = title,
                subtitle = subtitle,
                options = options,
                defaultValues = defaultValues,
                defaultIndices = defaultIndices,
                isMultiSelect = isMultiSelect,
                outputMode = outputMode,
                outputKey = outputKey,
                confirmText = confirmText,
                cancelText = cancelText,
            ),
        )
    },
)

private fun parseSelectOptions(
    element: JsonElement?,
    context: ActionExecutionContext,
): List<ActionSelectDialogOption> {
    if (element !is JsonArray) return emptyList()
    return element.mapNotNull { item ->
        when (item) {
            is JsonObject -> {
                val rawTitle = item[ActionSelectDialogConfigKey.TITLE]?.jsonPrimitive?.contentOrNull.orEmpty()
                val rawValue = item[ActionSelectDialogConfigKey.VALUE]?.jsonPrimitive?.contentOrNull.orEmpty()
                val rawImage = item[ActionSelectDialogConfigKey.IMAGE]?.jsonPrimitive?.contentOrNull.orEmpty()
                val title = ActionTemplateResolver.resolveText(rawTitle, context)
                val value = ActionTemplateResolver.resolveText(rawValue, context)
                val image = ActionTemplateResolver.resolveText(rawImage, context)
                ActionSelectDialogOption(title = title, value = value, image = image)
            }

            is JsonPrimitive -> {
                val text = ActionTemplateResolver.resolveText(item.content, context)
                ActionSelectDialogOption(title = text, value = text)
            }

            else -> null
        }
    }
}

private fun parseDefaultValues(element: JsonElement?): List<String> {
    return when (element) {
        is JsonArray -> element.mapNotNull { (it as? JsonPrimitive)?.content }
        is JsonPrimitive -> listOf(element.content)
        else -> emptyList()
    }
}

private fun parseDefaultIndices(element: JsonElement?): List<Int> {
    return when (element) {
        is JsonArray -> element.mapNotNull { (it as? JsonPrimitive)?.intOrNull ?: (it as? JsonPrimitive)?.content?.toIntOrNull() }
        is JsonPrimitive -> listOfNotNull(element.intOrNull ?: element.content.toIntOrNull())
        else -> emptyList()
    }
}

private fun imagePreviewDefinition() =
    sideEffectDefinition(
        ActionNodeType.IMAGE_PREVIEW,
        ActionImagePreviewConfigKey.IMAGES,
        setOf(ActionCapability.IMAGE_PREVIEW),
    ) { config, context ->
        val resolvedIndex = ActionTemplateResolver.resolveElement(config[ActionImagePreviewConfigKey.INDEX], context)
        val index = (resolvedIndex as? JsonPrimitive)?.intOrNull
            ?: (resolvedIndex as? JsonPrimitive)?.content?.toIntOrNull()
            ?: 0
        val images = when (val resolvedImages = ActionTemplateResolver.resolveElement(config[ActionImagePreviewConfigKey.IMAGES], context)) {
            is JsonArray -> resolvedImages.map { (it as? JsonPrimitive)?.content ?: it.toString() }
            is JsonPrimitive -> listOf(resolvedImages.content)
            else -> emptyList()
        }
        ActionImagePreviewEffect(index = index, images = images)
    }

private fun videoPreviewDefinition() =
    sideEffectDefinition(
        type = ActionNodeType.VIDEO_PREVIEW,
        requiredConfigKey = ActionVideoPreviewConfigKey.URL,
        capabilities = setOf(ActionCapability.VIDEO_PREVIEW),
    ) { config, context ->
        val url = ActionTemplateResolver.resolveText(config.string(ActionVideoPreviewConfigKey.URL), context)
        require(url.isNotBlank()) { "视频 URL 不能为空" }
        val headersElement = config[ActionVideoPreviewConfigKey.HEADERS]?.let { ActionTemplateResolver.resolveElement(it, context) } as? JsonObject
        val headers = headersElement?.entries?.associate { (k, v) ->
            val resolvedKey = ActionTemplateResolver.resolveText(k, context)
            val resolvedVal = ActionTemplateResolver.resolveText(v.jsonPrimitive.contentOrNull.orEmpty(), context)
            resolvedKey to resolvedVal
        }.orEmpty()
        ActionVideoPreviewEffect(url = url, headers = headers)
    }

private fun syncCookieDefinition() =
    sideEffectDefinition(
        type = ActionNodeType.SYNC_COOKIE,
        requiredConfigKey = ActionSyncCookieConfigKey.URL,
        capabilities = setOf(ActionCapability.NETWORK_COOKIE_SYNC),
    ) { config, context ->
        val url = ActionTemplateResolver.resolveText(config.string(ActionSyncCookieConfigKey.URL), context).also(ActionUrlPolicy::requireHttpUrl)
        val title = config[ActionSyncCookieConfigKey.TITLE]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) }.orEmpty()
        val userAgent = config[ActionSyncCookieConfigKey.USER_AGENT]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) }.orEmpty()

        val headersElement = ActionTemplateResolver.resolveElement(config[ActionSyncCookieConfigKey.HEADERS], context) as? JsonObject
        val headers = headersElement?.entries?.associate { (k, v) ->
            val resolvedKey = ActionTemplateResolver.resolveText(k, context)
            val resolvedVal = ActionTemplateResolver.resolveText(v.jsonPrimitive.contentOrNull.orEmpty(), context)
            resolvedKey to resolvedVal
        }.orEmpty()

        ActionSyncCookieEffect(
            url = url,
            title = title,
            headers = headers,
            userAgent = userAgent,
        )
    }

private fun sideEffectDefinition(
    type: String,
    requiredConfigKey: String,
    capabilities: Set<String>,
    create: (JsonObject, ActionExecutionContext) -> ActionSideEffect,
): ActionNodeDefinition {
    return ActionNodeDefinition(
        spec = ActionNodeSpec(
            type = type,
            category = ActionNodeCategory.ACTION,
            inputPorts = persistentListOf(inPort),
            outputPorts = persistentListOf(successPort, failurePort),
            requiredConfigKeys = setOf(requiredConfigKey),
            requiredCapabilities = capabilities,
        ),
        executor = { node, context ->
            ActionNodeExecutionResult(outputPortId = ActionControlPortId.SUCCESS, sideEffect = create(node.config, context))
        }
    )
}
