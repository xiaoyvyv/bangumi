package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.io

import com.xiaoyv.bangumi.shared.data.workflow.exception.ActionErrorCode
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionNodeExecutionResult
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCapability
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpBodyType
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpResponseKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.failurePort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.inPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.successPort
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeCapabilityResolver
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeCategory
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeDefinition
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeSpec
import com.xiaoyv.bangumi.shared.data.workflow.node.core.string
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionHttpRequestEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionTemplateResolver
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionUrlPolicy
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionHttpRequestExecutor
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionWorkflowFileStorage
import io.ktor.http.Url
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

/**
 * HTTP 请求内置节点。
 *
 * 请求由节点直接执行并将标准响应写入节点输出；执行器异常由工作流引擎按普通节点失败流程处理。
 */
internal fun httpActionNodeDefinitions(
    httpRequestExecutor: ActionHttpRequestExecutor,
    fileStorage: ActionWorkflowFileStorage,
): List<ActionNodeDefinition> = listOf(
    httpRequestDefinition(httpRequestExecutor),
    httpDownloadDefinition(httpRequestExecutor, fileStorage),
)

private fun httpRequestDefinition(httpRequestExecutor: ActionHttpRequestExecutor) = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTTP_REQUEST,
        category = ActionNodeCategory.HTTP,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(successPort, failurePort),
        requiredConfigKeys = setOf(ActionHttpConfigKey.URL),
        requiredCapabilities = setOf(ActionCapability.NETWORK),
    ),
    executor = { node, context ->
        val request = node.httpRequest(context)
        ActionNodeExecutionResult(
            outputPortId = ActionControlPortId.SUCCESS,
            output = httpRequestExecutor.execute(request),
        )
    },
    capabilityResolver = httpCapabilityResolver,
)

private fun httpDownloadDefinition(
    httpRequestExecutor: ActionHttpRequestExecutor,
    fileStorage: ActionWorkflowFileStorage,
) = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.HTTP_DOWNLOAD,
        category = ActionNodeCategory.HTTP,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(successPort, failurePort),
        requiredConfigKeys = setOf(ActionHttpConfigKey.URL, ActionHttpConfigKey.PATH, ActionHttpConfigKey.OUTPUT_KEY),
        requiredCapabilities = setOf(ActionCapability.NETWORK),
    ),
    executor = { node, context ->
        val workflowId = requireNotNull(context.workflowId) { ActionErrorCode.HTTP_DOWNLOAD_CONTEXT_MISSING_MSG }
        val request = node.httpRequest(context)
        val directory = ActionTemplateResolver.resolveText(node.config.string(ActionHttpConfigKey.PATH), context)
        val configuredFileName = node.config[ActionHttpConfigKey.FILE_NAME]
            ?.jsonPrimitive
            ?.contentOrNull
            ?.let { ActionTemplateResolver.resolveText(it, context) }
            ?.takeIf(String::isNotBlank)
        var fileName = ""
        var filePath = ""
        val response = try {
            httpRequestExecutor.download(
                request = request,
                onResponse = { downloadResponse ->
                    fileName = configuredFileName ?: inferredFileName(
                        contentDisposition = downloadResponse.contentDisposition,
                        url = request.url,
                        contentType = downloadResponse.contentType
                    )
                    filePath = "${directory.trimEnd('/', '\\')}/$fileName"
                    fileStorage.writeBytes(workflowId, filePath, byteArrayOf(), append = false)
                },
                consumeChunk = { chunk ->
                    fileStorage.writeBytes(workflowId, filePath, chunk, append = true)
                },
            )
        } catch (throwable: Throwable) {
            if (filePath.isNotBlank()) runCatching { fileStorage.delete(workflowId, filePath) }
            throw throwable
        }
        val outputKey = node.config.string(ActionHttpConfigKey.OUTPUT_KEY)
        val output = buildJsonObject {
            put(ActionHttpResponseKey.STATUS_CODE, JsonPrimitive(response.statusCode))
            put(ActionHttpResponseKey.IS_SUCCESS, JsonPrimitive(response.statusCode in 200..299))
            put(ActionHttpResponseKey.CONTENT_TYPE, JsonPrimitive(response.contentType))
            put(ActionHttpResponseKey.FILE_NAME, JsonPrimitive(fileName))
            put(ActionHttpResponseKey.FILE_PATH, JsonPrimitive(filePath))
        }
        ActionNodeExecutionResult(
            outputPortId = ActionControlPortId.SUCCESS,
            output = JsonObject(mapOf(outputKey to output)),
        )
    },
    capabilityResolver = httpCapabilityResolver,
)

private fun com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode.httpRequest(
    context: com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext,
) = ActionHttpRequestEffect(
    url = ActionTemplateResolver.resolveText(config.string(ActionHttpConfigKey.URL), context)
        .also(ActionUrlPolicy::requireHttpUrl),
    method = config.string(ActionHttpConfigKey.METHOD).ifBlank { "GET" }.uppercase(),
    query = ActionTemplateResolver.resolveElement(config[ActionHttpConfigKey.QUERY], context) as? JsonObject ?: JsonObject(emptyMap()),
    headers = ActionTemplateResolver.resolveElement(config[ActionHttpConfigKey.HEADERS], context) as? JsonObject ?: JsonObject(emptyMap()),
    body = ActionTemplateResolver.resolveElement(config[ActionHttpConfigKey.BODY], context),
    bodyType = config.string(ActionHttpConfigKey.BODY_TYPE).ifBlank { ActionHttpBodyType.JSON },
    contentType = config[ActionHttpConfigKey.CONTENT_TYPE]?.jsonPrimitive?.contentOrNull,
    timeoutMillis = config[ActionHttpConfigKey.TIMEOUT_MILLIS]?.jsonPrimitive?.longOrNull,
    retryCount = config[ActionHttpConfigKey.RETRY_COUNT]?.jsonPrimitive?.intOrNull ?: 0,
    retryDelayMillis = config[ActionHttpConfigKey.RETRY_DELAY_MILLIS]?.jsonPrimitive?.longOrNull ?: 0,
    useLocalCookieStorage = config[ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE]
        ?.jsonPrimitive
        ?.booleanOrNull
        ?: false,
)

private val httpCapabilityResolver = ActionNodeCapabilityResolver { node, spec ->
    buildSet {
        addAll(spec.requiredCapabilities)
        if (node.config[ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE]
                ?.jsonPrimitive
                ?.booleanOrNull == true
        ) {
            add(ActionCapability.NETWORK_LOCAL_COOKIE_ACCESS)
        }
    }
}

private fun inferredFileName(contentDisposition: String?, url: String, contentType: String): String {
    val fromHeader = contentDisposition
        ?.let { Regex("filename\\*?=(?:UTF-8''|\")?([^;\"]+)", RegexOption.IGNORE_CASE).find(it)?.groupValues?.getOrNull(1) }
        ?.trim()
        ?.trim('"')
        ?.takeIf { it.isNotBlank() && !it.contains('/') && !it.contains('\\') }
    if (fromHeader != null) return fromHeader

    val fromUrl = runCatching { Url(url).encodedPath.substringAfterLast('/').substringBefore('?') }.getOrNull()
        ?.takeIf { it.isNotBlank() && it != "/" && !it.contains('\\') }
    if (fromUrl != null) return fromUrl

    return "download_${kotlin.time.Clock.System.now().toEpochMilliseconds()}.${extensionFromContentType(contentType)}"
}

private fun extensionFromContentType(contentType: String): String = when (contentType.substringBefore(';').trim().lowercase()) {
    "application/json" -> "json"
    "application/pdf" -> "pdf"
    "application/zip" -> "zip"
    "image/jpeg" -> "jpg"
    "image/png" -> "png"
    "image/gif" -> "gif"
    "text/html" -> "html"
    "text/plain" -> "txt"
    else -> "bin"
}
