package com.xiaoyv.bangumi.shared.data.workflow.node.control

import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionSideEffectResult
import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionWorkflowValidator
import com.xiaoyv.bangumi.shared.data.workflow.engine.runtime.ActionWorkflowEngine
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionEdge
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionPortRef
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionWorkflow
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionEvent
import com.xiaoyv.bangumi.shared.data.workflow.model.log.ActionExecutionStatus
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeRegistry
import com.xiaoyv.bangumi.shared.data.workflow.node.fixture.ActionNodeTestFixtures
import com.xiaoyv.bangumi.shared.data.workflow.node.fixture.ActionNodeTestFixtures.config
import com.xiaoyv.bangumi.shared.data.workflow.node.fixture.ActionNodeTestFixtures.testHttpRequestExecutor
import com.xiaoyv.bangumi.shared.data.workflow.node.fixture.ActionNodeTestFixtures.testPreferencesStore
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.TimeSource

/**
 * 流程编排与分支控制节点单元测试：
 * 验证分支 (Fan-Out) 与合流 (Fan-In)、并发分支执行 (Parallel) 以及条件断言 (Assert) 机制。
 */
class ActionControlNodesTest {

    @Test
    fun testBranchingAndJoiningWorkflow() = runBlocking {
        // 构建分支与合流图:
        // entry (node_start) -> node_left AND node_right (Fan-Out)
        // node_left -> node_join
        // node_right -> node_join (Fan-In)
        val workflow = ActionWorkflow(
            id = "test_fork_join",
            name = "Fork Join Test",
            enabled = true,
            entryNodeId = "node_start",
            nodes = persistentListOf(
                ActionNode(
                    id = "node_start",
                    type = ActionNodeType.SET_VARIABLE,
                    config = config(
                        ActionDataConfigKey.KEY to JsonPrimitive("result"),
                        ActionDataConfigKey.VALUE to JsonPrimitive("Start"),
                    ),
                ),
                ActionNode(
                    id = "node_left",
                    type = ActionNodeType.SET_VARIABLE,
                    config = config(
                        ActionDataConfigKey.KEY to JsonPrimitive("result"),
                        ActionDataConfigKey.VALUE to JsonPrimitive("LeftValue"),
                    ),
                ),
                ActionNode(
                    id = "node_right",
                    type = ActionNodeType.SET_VARIABLE,
                    config = config(
                        ActionDataConfigKey.KEY to JsonPrimitive("result"),
                        ActionDataConfigKey.VALUE to JsonPrimitive("RightValue"),
                    ),
                ),
                ActionNode(
                    id = "node_join",
                    type = ActionNodeType.TEMPLATE,
                    config = config(
                        ActionDataConfigKey.TEMPLATE to JsonPrimitive("Combined: \${steps.node_left.result} & \${steps.node_right.result}"),
                        ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive("result"),
                    ),
                ),
            ),
            edges = persistentListOf(
                // Fan-Out from node_start to node_left and node_right
                ActionEdge(id = "e1", source = ActionPortRef("node_start", ActionControlPortId.NEXT), target = ActionPortRef("node_left", ActionControlPortId.IN)),
                ActionEdge(id = "e2", source = ActionPortRef("node_start", ActionControlPortId.NEXT), target = ActionPortRef("node_right", ActionControlPortId.IN)),
                // Fan-In from node_left and node_right to node_join
                ActionEdge(id = "e3", source = ActionPortRef("node_left", ActionControlPortId.NEXT), target = ActionPortRef("node_join", ActionControlPortId.IN)),
                ActionEdge(id = "e4", source = ActionPortRef("node_right", ActionControlPortId.NEXT), target = ActionPortRef("node_join", ActionControlPortId.IN)),
            ),
        )

        val registry = ActionNodeRegistry(testHttpRequestExecutor, testPreferencesStore)
        val validator = ActionWorkflowValidator(registry)
        val engine = ActionWorkflowEngine(registry, validator, now = { 1000L })

        val events = engine.execute(workflow, ActionExecutionContext(), sideEffectHandler = { ActionSideEffectResult.Success() }).toList()

        val completedEvent = events.filterIsInstance<ActionExecutionEvent.Completed>().firstOrNull()
        assertNotNull(completedEvent)
        assertEquals(ActionExecutionStatus.SUCCESS, completedEvent.log.status)

        // 验证 node_join 成功合流并访问了 node_left 和 node_right 的输出
        val joinStep = completedEvent.log.steps.firstOrNull { it.nodeId == "node_join" }
        assertNotNull(joinStep)
        assertEquals("Combined: LeftValue & RightValue", joinStep.output.getValue("result").jsonPrimitive.content)
    }

    @Test
    fun flowParallelWaitsForTheLongestBranchInsteadOfAddingDurations() = runBlocking {
        val workflow = ActionWorkflow(
            id = "test_flow_parallel",
            name = "Flow Parallel Test",
            entryNodeId = "start",
            nodes = persistentListOf(
                ActionNode(id = "start", type = ActionNodeType.FLOW_START),
                ActionNode(id = "parallel", type = ActionNodeType.FLOW_PARALLEL),
                ActionNode(
                    id = "short_delay",
                    type = ActionNodeType.FLOW_DELAY,
                    config = config(ActionFlowConfigKey.DELAY_MILLIS to JsonPrimitive(100)),
                ),
                ActionNode(
                    id = "long_delay",
                    type = ActionNodeType.FLOW_DELAY,
                    config = config(ActionFlowConfigKey.DELAY_MILLIS to JsonPrimitive(300)),
                ),
                ActionNode(
                    id = "join",
                    type = ActionNodeType.FLOW_JOIN,
                    config = config(
                        ActionFlowConfigKey.VALUES to JsonArray(emptyList()),
                        ActionFlowConfigKey.OUTPUT_KEY to JsonPrimitive("joined"),
                    ),
                ),
                ActionNode(id = "end", type = ActionNodeType.FLOW_END),
            ),
            edges = persistentListOf(
                ActionEdge("start_parallel", source = ActionPortRef("start", ActionControlPortId.NEXT), target = ActionPortRef("parallel", ActionControlPortId.IN)),
                ActionEdge("parallel_short", source = ActionPortRef("parallel", ActionControlPortId.BRANCHES), target = ActionPortRef("short_delay", ActionControlPortId.IN)),
                ActionEdge("parallel_long", source = ActionPortRef("parallel", ActionControlPortId.BRANCHES), target = ActionPortRef("long_delay", ActionControlPortId.IN)),
                ActionEdge("short_join", source = ActionPortRef("short_delay", ActionControlPortId.NEXT), target = ActionPortRef("join", ActionControlPortId.IN)),
                ActionEdge("long_join", source = ActionPortRef("long_delay", ActionControlPortId.NEXT), target = ActionPortRef("join", ActionControlPortId.IN)),
                ActionEdge("join_end", source = ActionPortRef("join", ActionControlPortId.NEXT), target = ActionPortRef("end", ActionControlPortId.IN)),
            ),
        )
        val registry = ActionNodeRegistry(testHttpRequestExecutor, testPreferencesStore)
        val engine = ActionWorkflowEngine(registry, ActionWorkflowValidator(registry), now = { 0L })
        val mark = TimeSource.Monotonic.markNow()
        val events = engine.execute(workflow, ActionExecutionContext(), sideEffectHandler = { ActionSideEffectResult.Success() }).toList()

        assertTrue(mark.elapsedNow().inWholeMilliseconds < 380, "并发段耗时不应累加为 400ms")
        assertTrue(events.filterIsInstance<ActionExecutionEvent.NodeCompleted>().any { it.nodeId == "join" })
        assertEquals(ActionExecutionStatus.SUCCESS, events.filterIsInstance<ActionExecutionEvent.Completed>().single().log.status)
    }

    @Test
    fun testFlowAssertFailsWhenConditionFalseThrowsException() {
        runBlocking {
            val definition = ActionNodeTestFixtures.nodeDefinitions().getValue(ActionNodeType.FLOW_ASSERT)
            val node = ActionNode(
                id = "assert_test",
                type = ActionNodeType.FLOW_ASSERT,
                config = config(
                    ActionFlowConfigKey.CONDITION to JsonPrimitive(false),
                    ActionFlowConfigKey.MESSAGE to JsonPrimitive("Assertion failed"),
                ),
            )
            val context = ActionExecutionContext()
            assertFailsWith<IllegalArgumentException> {
                definition.executor.execute(node, context)
            }
        }
    }
}
