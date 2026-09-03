package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.extension

import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionBilibiliConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.extension.BilibiliUtils.completeSearchTypeParameters
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.extension.BilibiliUtils.mixinKeyEncTab
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.extension.BilibiliUtils.removeWbiInvalidCharacters
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
        paramMap.completeSearchTypeParameters(parsedUrl)
        val wts = (kotlin.time.Clock.System.now().toEpochMilliseconds() / 1000).toString()
        paramMap.remove("w_rid")
        paramMap["wts"] = wts

        val sortedEntries = paramMap.entries.sortedBy { it.key }
        val queryString = sortedEntries.joinToString("&") { (key, value) ->
            "$key=${value.removeWbiInvalidCharacters().encodeURLQueryComponent()}"
        }
        val wRid = com.appmattus.crypto.Algorithm.MD5.hash((queryString + mixinKey).encodeToByteArray()).toHexString()

        val signedQuery = "$queryString&w_rid=$wRid"
        val baseUrl = rawUrl.substringBefore('?')
        val signedUrl = "$baseUrl?$signedQuery"

        node.valueResult(node.config.string(ActionBilibiliConfigKey.OUTPUT_KEY), JsonPrimitive(signedUrl))
    },
)


internal object BilibiliUtils {
    internal val mixinKeyEncTab = intArrayOf(
        46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49,
        33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40,
        61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11,
        36, 20, 34, 44, 52
    )

    internal const val BILIBILI_API_HOST = "api.bilibili.com"
    internal const val BILIBILI_WBI_SEARCH_TYPE_PATH = "/x/web-interface/wbi/search/type"
    internal const val BILIBILI_QUERY_VIEW_ID_LENGTH = 32
    internal const val BILIBILI_QUERY_VIEW_ID_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

    internal val WBI_INVALID_CHARACTERS = Regex("[!'()*]")

    /**
     * WBI 签名会忽略这些字符，发送参数也必须使用同一规范化结果。
     */
    internal fun String.removeWbiInvalidCharacters(): String = replace(WBI_INVALID_CHARACTERS, "")

    /**
     * 补齐 Bilibili Web WBI 搜索接口的浏览器公共参数。
     *
     * 调用方只需提供搜索词等业务参数；分页偏移和请求标识由节点根据当前参数生成。
     * 每个默认项都先保留调用方已提供的值；page_size 由调用方完全控制，不在此补齐。
     */
    internal fun MutableMap<String, String>.completeSearchTypeParameters(url: Url) {
        if (
            url.host != BILIBILI_API_HOST ||
            url.encodedPath != BILIBILI_WBI_SEARCH_TYPE_PATH ||
            get("keyword").isNullOrBlank()
        ) {
            return
        }

        putIfAbsent("order", "totalrank")
        putIfAbsent("search_type", "video")
        putIfAbsent("__refresh__", "true")
        putIfAbsent("_extra", "")
        putIfAbsent("ad_resource", "5654")
        putIfAbsent("category_id", "")
        putIfAbsent("context", "")
        putIfAbsent("from_source", "")
        putIfAbsent("from_spmid", "333.337")
        putIfAbsent("gaia_vtoken", "")
        putIfAbsent("highlight", "1")
        putIfAbsent("platform", "pc")
        putIfAbsent("single_column", "0")
        putIfAbsent("source_tag", "3")
        putIfAbsent("web_location", "1430654")
        putIfAbsent("qv_id", randomQueryViewId())
        val page = get("page")?.toIntOrNull() ?: 1
        if (page > 1) {
            putIfAbsent("dynamic_offset", ((page - 1) * 24).toString())
        }
    }

    internal fun randomQueryViewId(): String = buildString(BILIBILI_QUERY_VIEW_ID_LENGTH) {
        repeat(BILIBILI_QUERY_VIEW_ID_LENGTH) {
            append(BILIBILI_QUERY_VIEW_ID_CHARACTERS.random())
        }
    }
}
