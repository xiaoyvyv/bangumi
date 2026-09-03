package com.xiaoyv.bangumi.shared.data.workflow.node.contract

import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.fixture.ActionNodeTestFixtures
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * 契约测试：确保所有内置 ActionNode 均提供规范测试用例且能在默认输入下成功执行。
 */
class ActionNodeContractTest {

    @Test
    fun everyBuiltInNodeHasCanonicalTestCase() {
        val registeredTypes = ActionNodeTestFixtures.nodeDefinitions().keys
            .filterNotTo(linkedSetOf()) { it.startsWith("file.") || it == ActionNodeType.HTTP_DOWNLOAD }

        registeredTypes.forEach { type ->
            assertTrue(
                ActionNodeTestFixtures.canonicalConfigs.containsKey(type),
                "Missing canonical test configuration for node type: $type",
            )
        }
    }

    @Test
    fun everyBuiltInNodeExecutesCanonicalCase() = runBlocking {
        ActionNodeTestFixtures.nodeDefinitions().values
            .filterNot { it.spec.type.startsWith("file.") || it.spec.type == ActionNodeType.HTTP_DOWNLOAD }
            .forEach { definition ->
                val type = definition.spec.type
                val config = ActionNodeTestFixtures.canonicalConfigs[type]
                assertNotNull(config, "Missing canonical test configuration for $type")

                val node = ActionNode(
                    id = "canonical_$type",
                    type = type,
                    config = config,
                )

                val context = ActionExecutionContext()
                assertTrue(
                    definition.spec.requiredConfigKeys.all(config::containsKey),
                    "节点 $type 的测试配置缺少必填项",
                )
                val result = definition.executor.execute(node, context)
                val declaredOutputPortIds = definition.spec.outputPorts.mapTo(linkedSetOf()) { it.id }
                assertTrue(
                    result.outputPortId.isEmpty() || result.outputPortId in declaredOutputPortIds,
                    "节点 $type 返回了未声明的输出端口 ${result.outputPortId}",
                )
            }
    }
}
