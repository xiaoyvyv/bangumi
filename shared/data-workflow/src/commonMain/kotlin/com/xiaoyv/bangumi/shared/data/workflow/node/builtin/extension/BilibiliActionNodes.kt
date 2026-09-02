package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.extension

import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionBilibiliConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.inPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.nextPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.valueResult
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeCategory
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeDefinition
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeSpec
import com.xiaoyv.bangumi.shared.data.workflow.node.core.string
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionTemplateResolver
import io.ktor.http.Url
import io.ktor.http.encodeURLQueryComponent
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

/**
 * 哔哩哔哩专用内置节点。
 */
internal val bilibiliActionNodeDefinitions: List<ActionNodeDefinition> = listOf(
    bilibiliSignUrlDefinition(),
)

private val mixinKeyEncTab = intArrayOf(
    46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49,
    33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40,
    61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11,
    36, 20, 34, 44, 52
)

@OptIn(ExperimentalStdlibApi::class)
private fun bilibiliSignUrlDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.BILIBILI_SIGN_URL,
        category = ActionNodeCategory.BILIBILI,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionBilibiliConfigKey.URL, ActionBilibiliConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val rawUrl = ActionTemplateResolver.resolveText(node.config.string(ActionBilibiliConfigKey.URL), context)
        val imgKey = node.config[ActionBilibiliConfigKey.IMG_KEY]
            ?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) }
            .orEmpty()
            .ifBlank { "7cd084941338484aae1ad9425b84077c" }
        val subKey = node.config[ActionBilibiliConfigKey.SUB_KEY]
            ?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) }
            .orEmpty()
            .ifBlank { "4932caff0ff746eab6f01bf08b70ac45" }

        val parsedUrl = Url(rawUrl)
        val mixinKey = (imgKey + subKey).let { s ->
            buildString {
                repeat(32) {
                    append(s[mixinKeyEncTab[it]])
                }
            }
        }

        val paramMap = mutableMapOf<String, String>()
        parsedUrl.parameters.forEach { key, values ->
            if (values.isNotEmpty()) {
                paramMap[key] = values.last()
            }
        }
        val wts = (kotlin.time.Clock.System.now().toEpochMilliseconds() / 1000).toString()
        paramMap["wts"] = wts

        val sortedEntries = paramMap.entries.sortedBy { it.key }
        val queryString = sortedEntries.joinToString("&") { (k, v) -> "$k=${v.encodeURLQueryComponent()}" }
        debugLog { "queryString=$queryString" }
        val wRid = com.appmattus.crypto.Algorithm.MD5.hash((queryString + mixinKey).encodeToByteArray()).toHexString()

        val signedQuery = "$queryString&w_rid=$wRid"
        val baseUrl = rawUrl.substringBefore('?')
        val signedUrl = "$baseUrl?$signedQuery"

        node.valueResult(node.config.string(ActionBilibiliConfigKey.OUTPUT_KEY), JsonPrimitive(signedUrl))
    },
)
