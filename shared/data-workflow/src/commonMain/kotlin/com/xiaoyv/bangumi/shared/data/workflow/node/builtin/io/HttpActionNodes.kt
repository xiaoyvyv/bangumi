package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.io

import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionNodeExecutionResult
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCapability
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpBodyType
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpConfigKey
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
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
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
): List<ActionNodeDefinition> = listOf(
    httpRequestDefinition(httpRequestExecutor),
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
        val request = ActionHttpRequestEffect(
            url = ActionTemplateResolver.resolveText(node.config.string(ActionHttpConfigKey.URL), context)
                .also(ActionUrlPolicy::requireHttpUrl),
            method = node.config.string(ActionHttpConfigKey.METHOD).ifBlank { "GET" }.uppercase(),
            query = ActionTemplateResolver.resolveElement(node.config[ActionHttpConfigKey.QUERY], context) as? JsonObject ?: JsonObject(emptyMap()),
            headers = ActionTemplateResolver.resolveElement(node.config[ActionHttpConfigKey.HEADERS], context) as? JsonObject ?: JsonObject(emptyMap()),
            body = ActionTemplateResolver.resolveElement(node.config[ActionHttpConfigKey.BODY], context),
            bodyType = node.config.string(ActionHttpConfigKey.BODY_TYPE).ifBlank { ActionHttpBodyType.JSON },
            contentType = node.config[ActionHttpConfigKey.CONTENT_TYPE]?.jsonPrimitive?.contentOrNull,
            timeoutMillis = node.config[ActionHttpConfigKey.TIMEOUT_MILLIS]?.jsonPrimitive?.longOrNull,
            retryCount = node.config[ActionHttpConfigKey.RETRY_COUNT]?.jsonPrimitive?.intOrNull ?: 0,
            retryDelayMillis = node.config[ActionHttpConfigKey.RETRY_DELAY_MILLIS]?.jsonPrimitive?.longOrNull ?: 0,
            useLocalCookieStorage = node.config[ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE]
                ?.jsonPrimitive
                ?.booleanOrNull
                ?: false,
        )
        ActionNodeExecutionResult(
            outputPortId = ActionControlPortId.SUCCESS,
            output = httpRequestExecutor.execute(request),
        )
    },
    capabilityResolver = ActionNodeCapabilityResolver { node, spec ->
        buildSet {
            addAll(spec.requiredCapabilities)
            if (node.config[ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE]
                    ?.jsonPrimitive
                    ?.booleanOrNull == true
            ) {
                add(ActionCapability.NETWORK_LOCAL_COOKIE_ACCESS)
            }
        }
    },
)
