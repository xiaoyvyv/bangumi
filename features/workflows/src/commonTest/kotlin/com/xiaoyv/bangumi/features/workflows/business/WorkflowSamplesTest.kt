package com.xiaoyv.bangumi.features.workflows.business

import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCapability
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionJsonConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionProgressDialogConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionProgressDialogMode
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

    /**
     * 进度弹窗样例应声明能力，并携带精确模式所需的完整进度配置。
     */
    @Test
    fun progressDialogSampleUsesDeterminateConfiguration() {
        val workflow = WorkflowSamples.all.first { it.id == "workflow_sample_ui_progress_dialog" }
        val progressNode = workflow.nodes.single { it.type == ActionNodeType.UI_PROGRESS_DIALOG }

        assertEquals(true, workflow.requiredCapabilities.contains(ActionCapability.PROGRESS_DIALOG))
        assertEquals(ActionProgressDialogMode.DETERMINATE, progressNode.config[ActionProgressDialogConfigKey.MODE]?.jsonPrimitive?.content)
        assertEquals("35", progressNode.config[ActionProgressDialogConfigKey.PROGRESS]?.jsonPrimitive?.content)
        assertEquals("100", progressNode.config[ActionProgressDialogConfigKey.MAX_PROGRESS]?.jsonPrimitive?.content)
    }

    /**
     * 并发耗时样例必须从 branches 出口启动两条分支，并汇入同一个 flow.join。
     */
    @Test
    fun parallelTimingSampleUsesBranchesAndJoin() {
        val workflow = WorkflowSamples.all.first { it.id == "workflow_sample_flow_parallel_timing" }
        val parallel = workflow.nodes.single { it.type == ActionNodeType.FLOW_PARALLEL }
        val join = workflow.nodes.single { it.type == ActionNodeType.FLOW_JOIN }

        assertEquals(
            setOf("short_delay", "long_delay"),
            workflow.edges.filter { it.source.nodeId == parallel.id && it.source.portId == ActionControlPortId.BRANCHES }
                .mapTo(linkedSetOf()) { it.target.nodeId },
        )
        assertEquals(
            setOf("short_delay", "long_delay"),
            workflow.edges.filter { it.target.nodeId == join.id }
                .mapTo(linkedSetOf()) { it.source.nodeId },
        )
    }

    /**
     * 进度弹窗循环刷新样例应闭环包含初始化、展示、循环刷新与关闭节点。
     */
    @Test
    fun progressDialogLoopSampleConfiguresLoopAndDismiss() {
        val workflow = WorkflowSamples.all.first { it.id == "workflow_sample_ui_progress_dialog_loop" }

        assertEquals(true, workflow.requiredCapabilities.contains(ActionCapability.PROGRESS_DIALOG))
        assertEquals(true, workflow.nodes.any { it.type == ActionNodeType.UI_PROGRESS_DIALOG })
        assertEquals(true, workflow.nodes.any { it.type == ActionNodeType.LOOP_REPEAT })
        assertEquals(true, workflow.nodes.any { it.type == ActionNodeType.UI_PROGRESS_UPDATE })
        assertEquals(true, workflow.nodes.any { it.type == ActionNodeType.UI_PROGRESS_DISMISS })
    }

    /**
     * 进度弹窗并发分支样例应正确连接 flow.parallel 与 flow.join 并派发进度更新。
     */
    @Test
    fun progressDialogParallelSampleConfiguresParallelAndJoin() {
        val workflow = WorkflowSamples.all.first { it.id == "workflow_sample_ui_progress_dialog_parallel" }

        assertEquals(true, workflow.requiredCapabilities.contains(ActionCapability.PROGRESS_DIALOG))
        assertEquals(true, workflow.nodes.any { it.type == ActionNodeType.UI_PROGRESS_DIALOG })
        assertEquals(true, workflow.nodes.any { it.type == ActionNodeType.FLOW_PARALLEL })
        assertEquals(true, workflow.nodes.any { it.type == ActionNodeType.FLOW_JOIN })
        assertEquals(true, workflow.nodes.any { it.type == ActionNodeType.UI_PROGRESS_DISMISS })
    }

    /**
     * 视频预览样例应正确配置 ui.video_preview 节点并声明 VIDEO_PREVIEW 能力。
     */
    @Test
    fun videoPreviewSampleConfiguresCorrectNodeAndCapability() {
        val workflow = WorkflowSamples.all.first { it.id == "workflow_sample_video_preview" }

        assertEquals(true, workflow.requiredCapabilities.contains(ActionCapability.VIDEO_PREVIEW))
        assertEquals(true, workflow.nodes.any { it.type == ActionNodeType.VIDEO_PREVIEW })
    }
}
