package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.parse

import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCodecConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.inPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.nextPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.valueResult
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeCategory
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeDefinition
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeSpec
import com.xiaoyv.bangumi.shared.data.workflow.node.core.string
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionTemplateResolver
import io.ktor.http.decodeURLQueryComponent
import io.ktor.http.encodeURLQueryComponent
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.json.JsonPrimitive

/**
 * 文本与二进制编解码内置节点。
 */
internal val codecActionNodeDefinitions: List<ActionNodeDefinition> = listOf(
    codecBase64EncodeDefinition(),
    codecBase64DecodeDefinition(),
    codecBase64UrlEncodeDefinition(),
    codecBase64UrlDecodeDefinition(),
    codecHexEncodeDefinition(),
    codecHexDecodeDefinition(),
    codecUrlEncodeDefinition(),
    codecUrlDecodeDefinition(),
    codecHtmlEscapeDefinition(),
    codecHtmlUnescapeDefinition(),
)

@OptIn(kotlin.io.encoding.ExperimentalEncodingApi::class)
private fun codecBase64EncodeDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.CODEC_BASE64_ENCODE,
        category = ActionNodeCategory.CODEC,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionCodecConfigKey.TEXT, ActionCodecConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionCodecConfigKey.TEXT), context)
        node.valueResult(
            node.config.string(ActionCodecConfigKey.OUTPUT_KEY),
            JsonPrimitive(kotlin.io.encoding.Base64.encode(text.encodeToByteArray())),
        )
    },
)

@OptIn(kotlin.io.encoding.ExperimentalEncodingApi::class)
private fun codecBase64DecodeDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.CODEC_BASE64_DECODE,
        category = ActionNodeCategory.CODEC,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionCodecConfigKey.TEXT, ActionCodecConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionCodecConfigKey.TEXT), context)
        node.valueResult(
            node.config.string(ActionCodecConfigKey.OUTPUT_KEY),
            JsonPrimitive(kotlin.io.encoding.Base64.decode(text).decodeToString()),
        )
    },
)

@OptIn(kotlin.io.encoding.ExperimentalEncodingApi::class)
private fun codecBase64UrlEncodeDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.CODEC_BASE64_URL_ENCODE,
        category = ActionNodeCategory.CODEC,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionCodecConfigKey.TEXT, ActionCodecConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionCodecConfigKey.TEXT), context)
        node.valueResult(
            node.config.string(ActionCodecConfigKey.OUTPUT_KEY),
            JsonPrimitive(kotlin.io.encoding.Base64.UrlSafe.encode(text.encodeToByteArray())),
        )
    },
)

@OptIn(kotlin.io.encoding.ExperimentalEncodingApi::class)
private fun codecBase64UrlDecodeDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.CODEC_BASE64_URL_DECODE,
        category = ActionNodeCategory.CODEC,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionCodecConfigKey.TEXT, ActionCodecConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionCodecConfigKey.TEXT), context)
        node.valueResult(
            node.config.string(ActionCodecConfigKey.OUTPUT_KEY),
            JsonPrimitive(kotlin.io.encoding.Base64.UrlSafe.decode(text).decodeToString()),
        )
    },
)

@OptIn(ExperimentalStdlibApi::class)
private fun codecHexEncodeDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.CODEC_HEX_ENCODE,
        category = ActionNodeCategory.CODEC,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionCodecConfigKey.TEXT, ActionCodecConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionCodecConfigKey.TEXT), context)
        node.valueResult(
            node.config.string(ActionCodecConfigKey.OUTPUT_KEY),
            JsonPrimitive(text.encodeToByteArray().toHexString()),
        )
    },
)

@OptIn(ExperimentalStdlibApi::class)
private fun codecHexDecodeDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.CODEC_HEX_DECODE,
        category = ActionNodeCategory.CODEC,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionCodecConfigKey.TEXT, ActionCodecConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionCodecConfigKey.TEXT), context)
        val decoded = text.hexToByteArray().decodeToString()
        node.valueResult(node.config.string(ActionCodecConfigKey.OUTPUT_KEY), JsonPrimitive(decoded))
    },
)

private fun codecUrlEncodeDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.CODEC_URL_ENCODE,
        category = ActionNodeCategory.CODEC,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionCodecConfigKey.TEXT, ActionCodecConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionCodecConfigKey.TEXT), context)
        node.valueResult(node.config.string(ActionCodecConfigKey.OUTPUT_KEY), JsonPrimitive(text.encodeURLQueryComponent(encodeFull = true)))
    },
)

private fun codecUrlDecodeDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.CODEC_URL_DECODE,
        category = ActionNodeCategory.CODEC,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionCodecConfigKey.TEXT, ActionCodecConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionCodecConfigKey.TEXT), context)
        node.valueResult(node.config.string(ActionCodecConfigKey.OUTPUT_KEY), JsonPrimitive(text.decodeURLQueryComponent()))
    },
)

private fun codecHtmlEscapeDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.CODEC_HTML_ESCAPE,
        category = ActionNodeCategory.CODEC,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionCodecConfigKey.TEXT, ActionCodecConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionCodecConfigKey.TEXT), context)
        val escaped = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;")
        node.valueResult(node.config.string(ActionCodecConfigKey.OUTPUT_KEY), JsonPrimitive(escaped))
    },
)

private fun codecHtmlUnescapeDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.CODEC_HTML_UNESCAPE,
        category = ActionNodeCategory.CODEC,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionCodecConfigKey.TEXT, ActionCodecConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionCodecConfigKey.TEXT), context)
        val unescaped = text.replace("&quot;", "\"").replace("&#39;", "'").replace("&gt;", ">").replace("&lt;", "<").replace("&amp;", "&")
        node.valueResult(node.config.string(ActionCodecConfigKey.OUTPUT_KEY), JsonPrimitive(unescaped))
    },
)
