package com.xiaoyv.bangumi.features.workflows.business

import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionJsonConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * 工作流内置样例的回归测试。
 */
class WorkflowSamplesTest {
    /**
     * Bilibili WBI 工作流初始化时，HTTP Header 必须在样例列表构建前已具备有效 UA。
     */
    @Test
    fun bilibiliWbiSampleUsesConfiguredUserAgent() {
        val workflow = WorkflowSamples.all.first { it.id == "workflow_sample_search_bilibili_bangumi_and_open" }
        val headerNodes = workflow.nodes.filter { node ->
            node.id in setOf(
                "init_bilibili_cookie",
                "request_bilibili_spi",
                "request_wbi_keys",
                "search_bilibili",
            )
        }

        assertEquals(4, headerNodes.size)
        headerNodes.forEach { node ->
            assertEquals(
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:154.0) Gecko/20100101 Firefox/154.0",
                node.config
                    .getValue(ActionHttpConfigKey.HEADERS)
                    .jsonObject
                    .getValue("User-Agent")
                    .jsonPrimitive
                    .content,
            )
        }
    }

    /**
     * Bilibili WBI 工作流必须从 SPI 响应提取 buvid4，并在第一个后续 API 请求中写入 CookieStorage。
     */
    @Test
    fun bilibiliWbiSamplePropagatesSpiBuvid4ToSubsequentRequests() {
        val workflow = WorkflowSamples.all.first { it.id == "workflow_sample_search_bilibili_bangumi_and_open" }
        val spiRequest = workflow.nodes.first { it.id == "request_bilibili_spi" }
        val extractBuvid4 = workflow.nodes.first { it.id == "extract_bilibili_buvid4" }
        val wbiKeyRequest = workflow.nodes.first { it.id == "request_wbi_keys" }
        val searchRequest = workflow.nodes.first { it.id == "search_bilibili" }

        assertEquals(ActionNodeType.HTTP_REQUEST, spiRequest.type)
        assertEquals("https://api.bilibili.com/x/frontend/finger/spi", spiRequest.config[ActionHttpConfigKey.URL]?.jsonPrimitive?.content)
        assertEquals(ActionNodeType.JSON_EXTRACT, extractBuvid4.type)
        assertEquals("$.data.b_4", extractBuvid4.config[ActionJsonConfigKey.PATH]?.jsonPrimitive?.content)
        assertEquals("buvid4", extractBuvid4.config[ActionJsonConfigKey.OUTPUT_KEY]?.jsonPrimitive?.content)
        assertEquals(
            "buvid4=${'$'}{vars.buvid4}",
            wbiKeyRequest.config
                .getValue(ActionHttpConfigKey.HEADERS)
                .jsonObject
                .getValue("Cookie")
                .jsonPrimitive
                .content,
        )
        assertEquals(null, searchRequest.config.getValue(ActionHttpConfigKey.HEADERS).jsonObject["Cookie"])
        assertEquals(
            "request_bilibili_spi",
            workflow.edges.first { it.source.nodeId == "init_bilibili_cookie" }.target.nodeId,
        )
        assertEquals(
            "extract_bilibili_buvid4",
            workflow.edges.first { it.source.nodeId == "request_bilibili_spi" }.target.nodeId,
        )
    }
}
