package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.parse

import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionUrlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionUrlParsedKey
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.inPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.nextPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.valueResult
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeCategory
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeDefinition
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeSpec
import com.xiaoyv.bangumi.shared.data.workflow.node.core.string
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionTemplateResolver
import io.ktor.http.URLBuilder
import io.ktor.http.Url
import io.ktor.http.takeFrom
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal val urlActionNodeDefinitions: List<ActionNodeDefinition> = listOf(
    urlParseDefinition(),
    urlBuildDefinition(),
    urlSetQueryParamDefinition(),
    urlGetQueryParamDefinition(),
)

private fun urlParseDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.URL_PARSE,
        category = ActionNodeCategory.URL,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionUrlConfigKey.URL, ActionUrlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val urlStr = ActionTemplateResolver.resolveText(node.config.string(ActionUrlConfigKey.URL), context)
        val url = Url(urlStr)
        val queryParams = JsonObject(url.parameters.entries().associate { (key, values) ->
            key to if (values.size == 1) JsonPrimitive(values.first()) else JsonArray(values.map { JsonPrimitive(it) })
        })
        val parsed = JsonObject(
            mapOf(
                ActionUrlParsedKey.PROTOCOL to JsonPrimitive(url.protocol.name),
                ActionUrlParsedKey.HOST to JsonPrimitive(url.host),
                ActionUrlParsedKey.PORT to JsonPrimitive(url.port),
                ActionUrlParsedKey.PATH to JsonPrimitive(url.encodedPath),
                ActionUrlParsedKey.FULL_URL to JsonPrimitive(url.toString()),
                ActionUrlParsedKey.QUERY_PARAMETERS to queryParams,
            )
        )
        node.valueResult(node.config.string(ActionUrlConfigKey.OUTPUT_KEY), parsed)
    },
)

private fun urlBuildDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.URL_BUILD,
        category = ActionNodeCategory.URL,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionUrlConfigKey.BASE_URL, ActionUrlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val baseUrlStr = ActionTemplateResolver.resolveText(node.config.string(ActionUrlConfigKey.BASE_URL), context)
        val builder = URLBuilder().takeFrom(baseUrlStr)
        node.config[ActionUrlConfigKey.QUERY_PARAMETERS]?.let { queryEl ->
            val resolvedObj = ActionTemplateResolver.resolveElement(queryEl, context).jsonObject
            resolvedObj.forEach { (k, v) ->
                builder.parameters.append(k, v.jsonPrimitive.content)
            }
        }
        node.valueResult(node.config.string(ActionUrlConfigKey.OUTPUT_KEY), JsonPrimitive(builder.buildString()))
    },
)

private fun urlSetQueryParamDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.URL_SET_QUERY_PARAM,
        category = ActionNodeCategory.URL,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionUrlConfigKey.URL, ActionUrlConfigKey.KEY, ActionUrlConfigKey.VALUE, ActionUrlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val urlStr = ActionTemplateResolver.resolveText(node.config.string(ActionUrlConfigKey.URL), context)
        val key = ActionTemplateResolver.resolveText(node.config.string(ActionUrlConfigKey.KEY), context)
        val value = ActionTemplateResolver.resolveText(node.config.string(ActionUrlConfigKey.VALUE), context)
        val builder = URLBuilder().takeFrom(urlStr)
        builder.parameters.remove(key)
        if (value.isNotEmpty()) {
            builder.parameters.append(key, value)
        }
        node.valueResult(node.config.string(ActionUrlConfigKey.OUTPUT_KEY), JsonPrimitive(builder.buildString()))
    },
)

private fun urlGetQueryParamDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.URL_GET_QUERY_PARAM,
        category = ActionNodeCategory.URL,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionUrlConfigKey.URL, ActionUrlConfigKey.KEY, ActionUrlConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val urlStr = ActionTemplateResolver.resolveText(node.config.string(ActionUrlConfigKey.URL), context)
        val key = ActionTemplateResolver.resolveText(node.config.string(ActionUrlConfigKey.KEY), context)
        val url = Url(urlStr)
        val value = url.parameters[key]
        node.valueResult(node.config.string(ActionUrlConfigKey.OUTPUT_KEY), value?.let { JsonPrimitive(it) } ?: JsonNull)
    },
)

