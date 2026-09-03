package com.xiaoyv.bangumi.shared.data.workflow.engine

import com.xiaoyv.bangumi.shared.data.workflow.codec.ActionWorkflowCodec
import com.xiaoyv.bangumi.shared.data.workflow.codec.ActionWorkflowImportResult
import com.xiaoyv.bangumi.shared.data.workflow.engine.runtime.ActionWorkflowEngine
import com.xiaoyv.bangumi.shared.data.workflow.exception.ActionErrorCode
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionEdge
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionPortKind
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionPortRef
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionWorkflow
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionEvent
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionSideEffect
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionMathConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeRegistry
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionHttpRequestExecutor
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionWorkflowPreferencesStore
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * [ActionWorkflowEngine] 错误与异常流程调度单元测试。
 */
class ActionWorkflowEngineErrorTest {

    private val stubHttpExecutor = ActionHttpRequestExecutor { buildJsonObject { } }

    private val stubPreferencesStore = object : ActionWorkflowPreferencesStore {
        private val memory = mutableMapOf<String, JsonElement>()
        override suspend fun get(key: String): JsonElement? = memory[key]
        override suspend fun set(key: String, value: JsonElement) {
            memory[key] = value
        }

        override suspend fun delete(key: String) {
            memory.remove(key)
        }

        override suspend fun has(key: String): Boolean = memory.containsKey(key)
        override suspend fun clear() {
            memory.clear()
        }
    }

    private val registry = ActionNodeRegistry(stubHttpExecutor, stubPreferencesStore)
    private val validator = ActionWorkflowValidator(registry)
    private val engine = ActionWorkflowEngine(registry, validator, now = { 1000L })

    private val stubSideEffectHandler = object : ActionSideEffectHandler {
        override suspend fun handle(effect: ActionSideEffect): ActionSideEffectResult {
            return ActionSideEffectResult.Success(buildJsonObject { })
        }
    }

    private fun config(vararg pairs: Pair<String, JsonElement>): JsonObject = buildJsonObject {
        pairs.forEach { (k, v) -> put(k, v) }
    }

    /**
     * 测试已禁用的工作流 (enabled = false) 执行时直接触发 [ActionExecutionEvent.Failed]。
     */
    @Test
    fun testDisabledWorkflowExecutionFails() = runBlocking {
        val workflow = ActionWorkflow(
            id = "wf_disabled",
            name = "已禁用工作流",
            enabled = false,
            entryNodeId = "start",
            nodes = persistentListOf(ActionNode("start", ActionNodeType.FLOW_START)),
            edges = persistentListOf()
        )

        val events = engine.execute(workflow, ActionExecutionContext(), stubSideEffectHandler).toList()

        assertEquals(1, events.size)
        val failedEvent = events.first() as? ActionExecutionEvent.Failed
        assertNotNull(failedEvent)
        assertEquals(ActionErrorCode.WORKFLOW_DISABLED, failedEvent.error.code)
    }

    /**
     * 测试拓扑无效的工作流 (找不到入口节点) 执行时返回 [ActionExecutionEvent.Failed]。
     */
    @Test
    fun testInvalidTopologyExecutionFails() = runBlocking {
        val workflow = ActionWorkflow(
            id = "wf_invalid",
            name = "非法工作流",
            entryNodeId = "non_existent_start_node",
            nodes = persistentListOf(ActionNode("node_1", ActionNodeType.FLOW_START)),
            edges = persistentListOf()
        )

        val events = engine.execute(workflow, ActionExecutionContext(), stubSideEffectHandler).toList()

        assertEquals(1, events.size)
        val failedEvent = events.first() as? ActionExecutionEvent.Failed
        assertNotNull(failedEvent)
        assertEquals(ActionErrorCode.INVALID_WORKFLOW, failedEvent.error.code)
    }

    /**
     * 测试缺少必需配置项时校验器拦截返回 [ActionExecutionEvent.Failed]。
     */
    @Test
    fun testMissingConfigExecutionFails() = runBlocking {
        val workflow = ActionWorkflow(
            id = "wf_missing_config",
            name = "缺少配置工作流",
            entryNodeId = "start",
            nodes = persistentListOf(
                ActionNode("start", ActionNodeType.FLOW_START),
                ActionNode("template_node", ActionNodeType.TEMPLATE, config = config(ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive("res")))
            ),
            edges = persistentListOf(
                ActionEdge("e1", ActionPortKind.CONTROL, ActionPortRef("start", ActionControlPortId.NEXT), ActionPortRef("template_node", ActionControlPortId.IN))
            )
        )

        var traceLogged = false
        val listener = com.xiaoyv.bangumi.shared.data.workflow.exception.ActionWorkflowLogListener { _, _, message ->
            if (message.contains("template")) {
                traceLogged = true
            }
        }
        com.xiaoyv.bangumi.shared.data.workflow.exception.ActionWorkflowTraceLogger.addListener(listener)
        try {
            val events = engine.execute(workflow, ActionExecutionContext(), stubSideEffectHandler).toList()

            val failedEvent = events.filterIsInstance<ActionExecutionEvent.Failed>().firstOrNull()
            assertNotNull(failedEvent)
            assertEquals(ActionErrorCode.INVALID_WORKFLOW, failedEvent.error.code)
            assertTrue(failedEvent.error.message.contains("template"))
            assertTrue(traceLogged)
        } finally {
            com.xiaoyv.bangumi.shared.data.workflow.exception.ActionWorkflowTraceLogger.removeListener(listener)
        }
    }

    /**
     * 测试超出最大运行节点数 [maxStepCount] 时产生 [ActionExecutionEvent.Failed]，错误码为 step_limit。
     */
    @Test
    fun testMaxStepCountExceededFails() = runBlocking {
        val workflow = ActionWorkflow(
            id = "wf_long_chain",
            name = "多节点长链",
            entryNodeId = "start",
            nodes = persistentListOf(
                ActionNode("start", ActionNodeType.FLOW_START),
                ActionNode("s1", ActionNodeType.SET_VARIABLE, config = config(ActionDataConfigKey.KEY to JsonPrimitive("a"), ActionDataConfigKey.VALUE to JsonPrimitive(1))),
                ActionNode("s2", ActionNodeType.SET_VARIABLE, config = config(ActionDataConfigKey.KEY to JsonPrimitive("b"), ActionDataConfigKey.VALUE to JsonPrimitive(2))),
                ActionNode("s3", ActionNodeType.SET_VARIABLE, config = config(ActionDataConfigKey.KEY to JsonPrimitive("c"), ActionDataConfigKey.VALUE to JsonPrimitive(3))),
                ActionNode("s4", ActionNodeType.SET_VARIABLE, config = config(ActionDataConfigKey.KEY to JsonPrimitive("d"), ActionDataConfigKey.VALUE to JsonPrimitive(4)))
            ),
            edges = persistentListOf(
                ActionEdge("e1", ActionPortKind.CONTROL, ActionPortRef("start", ActionControlPortId.NEXT), ActionPortRef("s1", ActionControlPortId.IN)),
                ActionEdge("e2", ActionPortKind.CONTROL, ActionPortRef("s1", ActionControlPortId.NEXT), ActionPortRef("s2", ActionControlPortId.IN)),
                ActionEdge("e3", ActionPortKind.CONTROL, ActionPortRef("s2", ActionControlPortId.NEXT), ActionPortRef("s3", ActionControlPortId.IN)),
                ActionEdge("e4", ActionPortKind.CONTROL, ActionPortRef("s3", ActionControlPortId.NEXT), ActionPortRef("s4", ActionControlPortId.IN))
            )
        )

        val events = engine.execute(workflow, ActionExecutionContext(), stubSideEffectHandler, maxStepCount = 2).toList()

        val failedEvent = events.filterIsInstance<ActionExecutionEvent.Failed>().firstOrNull()
        assertNotNull(failedEvent)
        assertEquals(ActionErrorCode.STEP_LIMIT, failedEvent.error.code)
    }

    /**
     * 测试 [flow.assert] 断言节点计算为 false 时抛出异常并触发失败事件。
     */
    @Test
    fun testAssertNodeFailureTriggersFailedEvent() = runBlocking {
        val workflow = ActionWorkflow(
            id = "wf_assert_failed",
            name = "断言校验失败流",
            entryNodeId = "start",
            nodes = persistentListOf(
                ActionNode("start", ActionNodeType.FLOW_START),
                ActionNode(
                    "assert_node",
                    ActionNodeType.FLOW_ASSERT,
                    config = config(ActionFlowConfigKey.CONDITION to JsonPrimitive("\${1 == 2}"))
                )
            ),
            edges = persistentListOf(
                ActionEdge("e1", ActionPortKind.CONTROL, ActionPortRef("start", ActionControlPortId.NEXT), ActionPortRef("assert_node", ActionControlPortId.IN))
            )
        )

        val events = engine.execute(workflow, ActionExecutionContext(), stubSideEffectHandler).toList()

        val failedEvent = events.filterIsInstance<ActionExecutionEvent.Failed>().firstOrNull()
        assertNotNull(failedEvent)
        assertEquals(ActionErrorCode.NODE_EXECUTION_FAILED, failedEvent.error.code)
        assertTrue(failedEvent.error.message.contains("断言"))
    }

    /**
     * 测试 [flow.stop] 打断节点执行，成功停止并生成完整日志。
     */
    @Test
    fun testStopNodeInterruptsExecution() = runBlocking {
        val workflow = ActionWorkflow(
            id = "wf_stop_flow",
            name = "强制终止流",
            entryNodeId = "start",
            nodes = persistentListOf(
                ActionNode("start", ActionNodeType.FLOW_START),
                ActionNode(
                    "stop_node",
                    ActionNodeType.FLOW_STOP,
                    config = config(ActionFlowConfigKey.MESSAGE to JsonPrimitive("测试终止"))
                )
            ),
            edges = persistentListOf(
                ActionEdge("e1", ActionPortKind.CONTROL, ActionPortRef("start", ActionControlPortId.NEXT), ActionPortRef("stop_node", ActionControlPortId.IN))
            )
        )

        val events = engine.execute(workflow, ActionExecutionContext(), stubSideEffectHandler).toList()

        val completedEvent = events.filterIsInstance<ActionExecutionEvent.Completed>().firstOrNull()
        assertNotNull(completedEvent)
        assertTrue(completedEvent.log.steps.isNotEmpty())
    }

    /**
     * 测试数学除法节点在未捕获除零错误时抛出异常，并被引擎成功接收转为 [ActionExecutionEvent.Failed]。
     */
    @Test
    fun testUnhandledNodeExceptionCapturedByEngine() = runBlocking {
        val workflow = ActionWorkflow(
            id = "wf_divide_by_zero",
            name = "除零故障流",
            entryNodeId = "start",
            nodes = persistentListOf(
                ActionNode("start", ActionNodeType.FLOW_START),
                ActionNode(
                    "divide_node",
                    ActionNodeType.MATH_DIVIDE,
                    config = config(
                        ActionMathConfigKey.LEFT to JsonPrimitive(100),
                        ActionMathConfigKey.RIGHT to JsonPrimitive(0),
                        ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive("result")
                    )
                )
            ),
            edges = persistentListOf(
                ActionEdge("e1", ActionPortKind.CONTROL, ActionPortRef("start", ActionControlPortId.NEXT), ActionPortRef("divide_node", ActionControlPortId.IN))
            )
        )

        val events = engine.execute(workflow, ActionExecutionContext(), stubSideEffectHandler).toList()

        val failedEvent = events.filterIsInstance<ActionExecutionEvent.Failed>().firstOrNull()
        assertNotNull(failedEvent)
        assertEquals(ActionErrorCode.NODE_EXECUTION_FAILED, failedEvent.error.code)
        assertTrue(failedEvent.error.message.contains("除数不能为 0"))
    }

    /**
     * 测试 [ActionWorkflowCodec] 导出非法工作流时抛出 [IllegalArgumentException]。
     */
    @Test
    fun testCodecUnvalidatedExportThrowsException() {
        val invalidWorkflow = ActionWorkflow(
            id = "wf_invalid",
            name = "非法工作流",
            entryNodeId = "non_existent",
            nodes = persistentListOf()
        )
        val codec = ActionWorkflowCodec(Json { ignoreUnknownKeys = true }, validator, registry)
        val ex = assertFailsWith<IllegalArgumentException> {
            codec.export(invalidWorkflow)
        }
        assertTrue(ex.message?.contains("无法导出不合法的工作流") == true)
    }

    /**
     * 测试 [ActionWorkflowCodec] 导入未来不支持的高版本工作流 JSON 时返回 [ActionWorkflowImportResult.Failure]。
     */
    @Test
    fun testCodecFutureVersionImportReturnsFailure() {
        val futureJson = """{"formatVersion":999,"id":"wf_future","name":"未来工作流","nodes":[],"edges":[]}"""
        val codec = ActionWorkflowCodec(Json { ignoreUnknownKeys = true }, validator, registry)
        val result = codec.import(futureJson)
        assertTrue(result is ActionWorkflowImportResult.Failure)
    }
}
