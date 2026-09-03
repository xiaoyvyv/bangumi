package com.xiaoyv.bangumi.shared.data.workflow.node.io

import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionSideEffectResult
import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionWorkflowValidator
import com.xiaoyv.bangumi.shared.data.workflow.engine.runtime.ActionWorkflowEngine
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionEdge
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionPortRef
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionWorkflow
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionEvent
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionSideEffect
import com.xiaoyv.bangumi.shared.data.workflow.model.log.ActionExecutionStatus
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCapability
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionProgressDialogAction
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionProgressDialogConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionProgressDialogMode
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionStorageConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionVideoPreviewConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeRegistry
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionProgressDialogEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionVideoPreviewEffect
import com.xiaoyv.bangumi.shared.data.workflow.node.fixture.ActionNodeTestFixtures
import com.xiaoyv.bangumi.shared.data.workflow.node.fixture.ActionNodeTestFixtures.config
import com.xiaoyv.bangumi.shared.data.workflow.node.fixture.ActionNodeTestFixtures.testHttpRequestExecutor
import com.xiaoyv.bangumi.shared.data.workflow.node.fixture.ActionNodeTestFixtures.testPreferencesStore
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionWorkflowPreferencesStore
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * 输入输出、HTTP 请求、首选项存储与 UI 副作用弹窗节点单元测试。
 */
class ActionIoNodesTest {

    private val definitions = ActionNodeTestFixtures.nodeDefinitions()

    @Test
    fun httpRequestRequiresCookieCapabilityOnlyWhenEnabled() {
        val definition = definitions.getValue(ActionNodeType.HTTP_REQUEST)
        val baseNode = ActionNode(
            id = "request",
            type = ActionNodeType.HTTP_REQUEST,
            config = config(ActionHttpConfigKey.URL to JsonPrimitive("https://bgm.tv")),
        )

        assertEquals(
            setOf(ActionCapability.NETWORK),
            definition.capabilityResolver.requiredCapabilities(baseNode, definition.spec),
        )
        assertEquals(
            setOf(ActionCapability.NETWORK, ActionCapability.NETWORK_LOCAL_COOKIE_ACCESS),
            definition.capabilityResolver.requiredCapabilities(
                baseNode.copy(
                    config = config(
                        ActionHttpConfigKey.URL to JsonPrimitive("https://bgm.tv"),
                        ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE to JsonPrimitive(true),
                    ),
                ),
                definition.spec,
            ),
        )
    }

    @Test
    fun videoPreviewEmitsEffectWithResolvedUrlAndHeaders() = runBlocking {
        val workflow = ActionWorkflow(
            id = "test_video_preview",
            name = "Video Preview Test",
            entryNodeId = "video_preview",
            requiredCapabilities = persistentListOf(ActionCapability.VIDEO_PREVIEW),
            nodes = persistentListOf(
                ActionNode(
                    id = "video_preview",
                    type = ActionNodeType.VIDEO_PREVIEW,
                    config = config(
                        ActionVideoPreviewConfigKey.URL to JsonPrimitive("https://example.com/stream/\${vars.id}.mp4"),
                        ActionVideoPreviewConfigKey.HEADERS to buildJsonObject {
                            put("Referer", JsonPrimitive("https://example.com"))
                            put("User-Agent", JsonPrimitive("BangumiClient/1.0"))
                        },
                    ),
                ),
            ),
        )
        val registry = ActionNodeRegistry(testHttpRequestExecutor, testPreferencesStore)
        val engine = ActionWorkflowEngine(registry, ActionWorkflowValidator(registry), now = { 1000L })

        val sideEffects = mutableListOf<ActionSideEffect>()
        val events = engine.execute(
            workflow = workflow,
            initialContext = ActionExecutionContext(variables = persistentMapOf("id" to JsonPrimitive("12345"))),
            sideEffectHandler = { effect ->
                sideEffects.add(effect)
                ActionSideEffectResult.Success()
            },
        ).toList()

        assertEquals(
            ActionExecutionStatus.SUCCESS,
            events.filterIsInstance<ActionExecutionEvent.Completed>().single().log.status,
        )
        assertEquals(1, sideEffects.size)
        val effect = sideEffects.single() as ActionVideoPreviewEffect
        assertEquals("https://example.com/stream/12345.mp4", effect.url)
        assertEquals("https://example.com", effect.headers["Referer"])
        assertEquals("BangumiClient/1.0", effect.headers["User-Agent"])
    }

    @Test
    fun progressDialogSupportsLifecycleAndRoutesToSuccess() = runBlocking {
        val workflow = ActionWorkflow(
            id = "test_progress_dialog_lifecycle",
            name = "Progress Dialog Lifecycle Test",
            entryNodeId = "progress_show",
            requiredCapabilities = persistentListOf(ActionCapability.PROGRESS_DIALOG),
            nodes = persistentListOf(
                ActionNode(
                    id = "progress_show",
                    type = ActionNodeType.UI_PROGRESS_DIALOG,
                    config = config(
                        ActionProgressDialogConfigKey.TITLE to JsonPrimitive("下载测试"),
                        ActionProgressDialogConfigKey.MESSAGE to JsonPrimitive("开始下载"),
                        ActionProgressDialogConfigKey.MODE to JsonPrimitive(ActionProgressDialogMode.DETERMINATE),
                        ActionProgressDialogConfigKey.PROGRESS to JsonPrimitive(0),
                        ActionProgressDialogConfigKey.MAX_PROGRESS to JsonPrimitive(100),
                    ),
                ),
                ActionNode(
                    id = "progress_update",
                    type = ActionNodeType.UI_PROGRESS_UPDATE,
                    config = config(
                        ActionProgressDialogConfigKey.MESSAGE to JsonPrimitive("已完成 60%"),
                        ActionProgressDialogConfigKey.PROGRESS to JsonPrimitive(60),
                    ),
                ),
                ActionNode(
                    id = "progress_dismiss",
                    type = ActionNodeType.UI_PROGRESS_DISMISS,
                    config = config(),
                ),
            ),
            edges = persistentListOf(
                ActionEdge(
                    id = "e1",
                    source = ActionPortRef("progress_show", ActionControlPortId.SUCCESS),
                    target = ActionPortRef("progress_update", ActionControlPortId.IN),
                ),
                ActionEdge(
                    id = "e2",
                    source = ActionPortRef("progress_update", ActionControlPortId.SUCCESS),
                    target = ActionPortRef("progress_dismiss", ActionControlPortId.IN),
                ),
            ),
        )
        val registry = ActionNodeRegistry(testHttpRequestExecutor, testPreferencesStore)
        val engine = ActionWorkflowEngine(registry, ActionWorkflowValidator(registry), now = { 1000L })

        val sideEffects = mutableListOf<ActionSideEffect>()
        val events = engine.execute(
            workflow = workflow,
            initialContext = ActionExecutionContext(),
            sideEffectHandler = { effect ->
                sideEffects.add(effect)
                ActionSideEffectResult.Success()
            },
        ).toList()

        assertEquals(
            ActionExecutionStatus.SUCCESS,
            events.filterIsInstance<ActionExecutionEvent.Completed>().single().log.status,
        )
        assertEquals(3, sideEffects.size)
        val showEffect = sideEffects[0] as ActionProgressDialogEffect
        val updateEffect = sideEffects[1] as ActionProgressDialogEffect
        val dismissEffect = sideEffects[2] as ActionProgressDialogEffect

        assertEquals(ActionProgressDialogAction.SHOW, showEffect.action)
        assertEquals("下载测试", showEffect.title)
        assertEquals(0f, showEffect.progress)

        assertEquals(ActionProgressDialogAction.UPDATE, updateEffect.action)
        assertEquals("已完成 60%", updateEffect.message)
        assertEquals(60f, updateEffect.progress)

        assertEquals(ActionProgressDialogAction.DISMISS, dismissEffect.action)
    }

    @Test
    fun workflowPreferencesKeyUsesReservedPrefix() {
        assertEquals("workflow_subject_633836", ActionWorkflowPreferencesStore.storageKey("subject_633836"))
        assertFailsWith<IllegalArgumentException> {
            ActionWorkflowPreferencesStore.storageKey("workflow_subject_633836")
        }
    }

    @Test
    fun preferencesNodesPersistJsonValue() = runBlocking {
        val context = ActionExecutionContext()
        val key = "node_test_value"
        val value = JsonObject(mapOf("favorite" to JsonPrimitive(true)))
        val setNode = ActionNode(
            id = "set",
            type = ActionNodeType.STORAGE_PREFERENCES_SET,
            config = config(
                ActionStorageConfigKey.KEY to JsonPrimitive(key),
                ActionStorageConfigKey.VALUE to value,
                ActionStorageConfigKey.OUTPUT_KEY to JsonPrimitive("written"),
            ),
        )
        val getNode = ActionNode(
            id = "get",
            type = ActionNodeType.STORAGE_PREFERENCES_GET,
            config = config(
                ActionStorageConfigKey.KEY to JsonPrimitive(key),
                ActionStorageConfigKey.OUTPUT_KEY to JsonPrimitive("read"),
            ),
        )

        val setResult = definitions.getValue(ActionNodeType.STORAGE_PREFERENCES_SET)
            .executor
            .execute(setNode, context)
        val getResult = definitions.getValue(ActionNodeType.STORAGE_PREFERENCES_GET)
            .executor
            .execute(getNode, context)

        assertEquals(value, setResult.output["written"])
        assertEquals(value, getResult.output["read"])
    }
}
