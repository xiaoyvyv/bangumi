package com.xiaoyv.bangumi.shared.data.workflow.node

import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionSideEffectResult
import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionWorkflowEngine
import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionWorkflowValidator
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionEdge
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionPortRef
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionWorkflow
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionEvent
import com.xiaoyv.bangumi.shared.data.workflow.model.log.ActionExecutionStatus
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionBilibiliConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCapability
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionClipboardConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCodecConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionConfirmConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCryptoConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCsvConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDateConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHashAlgorithm
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHtmlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHtmlQueryOperation
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpResponseKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionImagePreviewConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionInputDialogConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionJsonConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionLoopConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionMathConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNotificationConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionObjectConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionOpenAppConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionOpenUrlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionOpenWebConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectDialogConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionShareConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionStorageConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSyncCookieConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionToastConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionUrlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionXmlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.builtInActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeRegistry
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionHttpRequestExecutor
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionWorkflowPreferencesStore
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * 内置节点的契约测试。
 *
 * 每一个已注册节点均有一份最小且有效的规范配置；新增节点若未补充测试用例，会直接导致覆盖检查失败。
 */
class BuiltInActionNodeTest {

    /**
     * 使用公开 APP 签名向量验证 URL 编码、模板拼接与 MD5 节点可以组合出正确签名。
     *
     * 该用例对应 APP API 的固定参数，不会发起网络请求，也不会涉及任何用户 Cookie 或登录凭据。
     */
    @Test
    fun appSignatureVectorMatchesExpectedQueryAndMd5() = runBlocking {
        val definitions = builtInActionNodeDefinitions(testHttpRequestExecutor, testPreferencesStore)
            .associateBy { it.spec.type }
        val expectedQuery =
            "appkey=1d8b6e7d45233436&id=114514&str=1919810&test=%E3%81%84%E3%81%84%E3%82%88%EF%BC%8C%E3%81%93%E3%81%84%E3%82%88"
        val expectedSign = "01479cf20504d865519ac50f33ba3a7d"
        var context = ActionExecutionContext()

        val encoded = definitions.getValue(ActionNodeType.CODEC_URL_ENCODE).executor.execute(
            ActionNode(
                id = "encode_test_value",
                type = ActionNodeType.CODEC_URL_ENCODE,
                config = config(
                    ActionCodecConfigKey.TEXT to JsonPrimitive("いいよ，こいよ"),
                    ActionCodecConfigKey.OUTPUT_KEY to JsonPrimitive("encodedTest"),
                ),
            ),
            context,
        )
        context = context.copy(variables = encoded.variableUpdates)

        val query = definitions.getValue(ActionNodeType.TEMPLATE).executor.execute(
            ActionNode(
                id = "build_canonical_query",
                type = ActionNodeType.TEMPLATE,
                config = config(
                    ActionDataConfigKey.TEMPLATE to JsonPrimitive(
                        "appkey=1d8b6e7d45233436&id=114514&str=1919810&test=${'$'}{vars.encodedTest}",
                    ),
                    ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive("canonicalQuery"),
                ),
            ),
            context,
        )
        context = context.copy(variables = query.variableUpdates)

        val signature = definitions.getValue(ActionNodeType.CRYPTO_HASH).executor.execute(
            ActionNode(
                id = "calculate_signature",
                type = ActionNodeType.CRYPTO_HASH,
                config = config(
                    ActionCryptoConfigKey.TEXT to JsonPrimitive(
                        "${'$'}{vars.canonicalQuery}560c52ccd288fed045859ed18bffd973",
                    ),
                    ActionCryptoConfigKey.ALGORITHM to JsonPrimitive(ActionHashAlgorithm.MD5),
                    ActionCryptoConfigKey.OUTPUT_KEY to JsonPrimitive("sign"),
                ),
            ),
            context,
        )

        assertEquals(expectedQuery, query.output.getValue("canonicalQuery").jsonPrimitive.content)
        assertEquals(expectedSign, signature.output.getValue("sign").jsonPrimitive.content)
    }

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

    /**
     * 验证测试夹具与节点注册表一一对应，避免新增节点遗漏单元测试。
     */
    @Test
    fun everyBuiltInNodeHasCanonicalTestCase() {
        val registeredTypes = builtInActionNodeDefinitions(
            testHttpRequestExecutor,
            testPreferencesStore,
        ).mapTo(linkedSetOf()) { it.spec.type }
            .filterNotTo(linkedSetOf()) { it.startsWith("file.") }

        assertEquals(registeredTypes, canonicalConfigs.keys)
    }

    /**
     * 逐个执行所有内置节点的规范用例。
     *
     * 该测试覆盖节点的配置读取、模板解析、结构化输出和副作用声明；
     * HTTP 节点仅验证副作用构建，不进行真实网络访问。
     */
    @Test
    fun everyBuiltInNodeExecutesCanonicalCase() = runBlocking {
        builtInActionNodeDefinitions(testHttpRequestExecutor, testPreferencesStore)
            .filterNot { it.spec.type.startsWith("file.") }
            .forEach { definition ->
            val type = definition.spec.type
            val config = canonicalConfigs.getValue(type)
            val node = ActionNode(
                id = type,
                type = type,
                config = config,
            )

            try {
                assertTrue(
                    definition.spec.requiredConfigKeys.all(config::containsKey),
                    "节点 $type 的测试配置缺少必填项",
                )
                val result = definition.executor.execute(node, ActionExecutionContext())
                val declaredOutputPortIds = definition.spec.outputPorts.mapTo(linkedSetOf()) { it.id }
                assertTrue(
                    result.outputPortId.isEmpty() || result.outputPortId in declaredOutputPortIds,
                    "节点 $type 返回了未声明的输出端口 ${result.outputPortId}",
                )
                println(
                    """
                    |[TEST SUCCESS] type=$type
                    |  - Input Config  : $config
                    |  - Output Port   : ${result.outputPortId.ifEmpty { "<terminal>" }}
                    |  - Output Data   : ${result.output}
                    |  - Var Updates   : ${result.variableUpdates}
                    |  - Side Effect   : ${result.sideEffect?.let { it::class.simpleName } ?: "<none>"}
                    """.trimMargin()
                )
            } catch (e: Throwable) {
                println(
                    """
                    |[TEST FAILURE] type=$type
                    |  - Input Config  : $config
                    |  - Error Message : ${e.message}
                    """.trimMargin()
                )
                throw e
            }
        }
    }

    /**
     * 启用本地 Cookie 控制项时，HTTP 节点必须额外声明 Cookie 访问能力，避免导入工作流静默
     * 使用已登录会话。
     */
    @Test
    fun httpRequestRequiresCookieCapabilityOnlyWhenEnabled() {
        val definition = builtInActionNodeDefinitions(testHttpRequestExecutor, testPreferencesStore)
            .first { it.spec.type == ActionNodeType.HTTP_REQUEST }
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

    /**
     * 验证工作流存储键始终与应用其他 Preferences 键隔离。
     */
    @Test
    fun workflowPreferencesKeyUsesReservedPrefix() {
        assertEquals("workflow_subject_633836", ActionWorkflowPreferencesStore.storageKey("subject_633836"))
        assertFailsWith<IllegalArgumentException> {
            ActionWorkflowPreferencesStore.storageKey("workflow_subject_633836")
        }
    }

    /**
     * 验证存储节点可在同一工作流中写入并读取任意 JSON 值。
     */
    @Test
    fun preferencesNodesPersistJsonValue() = runBlocking {
        val definitions = builtInActionNodeDefinitions(testHttpRequestExecutor, testPreferencesStore)
            .associateBy { it.spec.type }
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

    private companion object {
        private val array = JsonArray(listOf(JsonPrimitive("value"), JsonPrimitive("other")))
        private val testHttpRequestExecutor = ActionHttpRequestExecutor {
            buildJsonObject {
                put(ActionHttpResponseKey.STATUS_CODE, JsonPrimitive(200))
                put(ActionHttpResponseKey.IS_SUCCESS, JsonPrimitive(true))
                put(ActionHttpResponseKey.BODY, JsonObject(mapOf("ok" to JsonPrimitive(true))))
            }
        }
        private val testPreferences = mutableMapOf<String, kotlinx.serialization.json.JsonElement>()
        private val testPreferencesStore = object : ActionWorkflowPreferencesStore {
            override suspend fun get(key: String): kotlinx.serialization.json.JsonElement? = testPreferences[key]

            override suspend fun set(key: String, value: kotlinx.serialization.json.JsonElement) {
                testPreferences[key] = value
            }

            override suspend fun delete(key: String) {
                testPreferences.remove(key)
            }

            override suspend fun has(key: String): Boolean = key in testPreferences

            override suspend fun clear() {
                testPreferences.clear()
            }
        }

        private val canonicalConfigs: Map<String, JsonObject> = mapOf(
            ActionNodeType.FLOW_START to config(),
            ActionNodeType.FLOW_END to config(),
            ActionNodeType.FLOW_STOP to config(),
            ActionNodeType.FLOW_DELAY to config(
                ActionFlowConfigKey.DELAY_MILLIS to JsonPrimitive(0),
            ),
            ActionNodeType.FLOW_ASSERT to config(
                ActionFlowConfigKey.CONDITION to JsonPrimitive(true),
            ),
            ActionNodeType.FLOW_SWITCH to config(
                ActionFlowConfigKey.VALUE to JsonPrimitive("published"),
                ActionFlowConfigKey.CASES to JsonObject(mapOf("published" to JsonPrimitive(true))),
            ),
            ActionNodeType.FLOW_LOG to config(
                ActionFlowConfigKey.MESSAGE to JsonPrimitive("message"),
            ),
            ActionNodeType.CONDITION_IF to config(
                ActionControlConfigKey.CONDITION to JsonPrimitive(true),
            ),
            ActionNodeType.CONDITION_EQUALS to binaryConfig(),
            ActionNodeType.CONDITION_NOT_EQUALS to binaryConfig(),
            ActionNodeType.CONDITION_GREATER_THAN to binaryConfig(),
            ActionNodeType.CONDITION_GREATER_THAN_OR_EQUALS to binaryConfig(),
            ActionNodeType.CONDITION_LESS_THAN to binaryConfig(),
            ActionNodeType.CONDITION_LESS_THAN_OR_EQUALS to binaryConfig(),
            ActionNodeType.CONDITION_AND to config(
                ActionControlConfigKey.LEFT to JsonPrimitive(true),
                ActionControlConfigKey.RIGHT to JsonPrimitive(false),
            ),
            ActionNodeType.CONDITION_OR to config(
                ActionControlConfigKey.LEFT to JsonPrimitive(true),
                ActionControlConfigKey.RIGHT to JsonPrimitive(false),
            ),
            ActionNodeType.CONDITION_NOT to config(
                ActionControlConfigKey.VALUE to JsonPrimitive(false),
            ),
            ActionNodeType.CONDITION_IS_NULL to config(
                ActionControlConfigKey.VALUE to JsonPrimitive("value"),
            ),
            ActionNodeType.CONDITION_IS_EMPTY to config(
                ActionControlConfigKey.VALUE to JsonPrimitive("value"),
            ),
            ActionNodeType.HTTP_STATUS to config(
                ActionControlConfigKey.STATUS_CODE to JsonPrimitive(200),
            ),
            ActionNodeType.LOOP_REPEAT to config(
                ActionLoopConfigKey.COUNT to JsonPrimitive(1),
                ActionLoopConfigKey.MAX_ITERATIONS to JsonPrimitive(1),
            ),
            ActionNodeType.LOOP_FOR_EACH to config(
                ActionLoopConfigKey.ITEMS to JsonArray(listOf(JsonPrimitive("item"))),
            ),
            ActionNodeType.LOOP_WHILE to config(
                ActionLoopConfigKey.CONDITION to JsonPrimitive(false),
                ActionLoopConfigKey.MAX_ITERATIONS to JsonPrimitive(1),
            ),
            ActionNodeType.LOOP_NEXT to config(
                ActionLoopConfigKey.LOOP_ID to JsonPrimitive("loop"),
            ),
            ActionNodeType.LOOP_BREAK to config(
                ActionLoopConfigKey.LOOP_ID to JsonPrimitive("loop"),
            ),
            ActionNodeType.LOOP_CONTINUE to config(
                ActionLoopConfigKey.LOOP_ID to JsonPrimitive("loop"),
            ),
            ActionNodeType.TEMPLATE to config(
                ActionDataConfigKey.TEMPLATE to JsonPrimitive("value"),
                ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.SET_VARIABLE to config(
                ActionDataConfigKey.KEY to JsonPrimitive(outputKey),
                ActionDataConfigKey.VALUE to JsonPrimitive("value"),
            ),
            ActionNodeType.DATA_COALESCE to arrayConfig(),
            ActionNodeType.DATA_CONCAT to arrayConfig(),
            ActionNodeType.DATA_MERGE to config(
                ActionDataConfigKey.OBJECTS to JsonArray(listOf(JsonObject(mapOf("value" to JsonPrimitive(true))))),
                ActionDataConfigKey.MERGE_STRATEGY to JsonPrimitive("shallow"),
                ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.DATA_ASSIGN to config(
                ActionDataConfigKey.OBJECT to JsonObject(emptyMap()),
                ActionDataConfigKey.ASSIGNMENTS to JsonObject(mapOf("$.value" to JsonPrimitive(true))),
                ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.DATA_REMOVE to config(
                ActionDataConfigKey.OBJECT to JsonObject(mapOf("value" to JsonPrimitive(true))),
                ActionDataConfigKey.PATH to JsonPrimitive("$.value"),
                ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.DATA_RENAME to config(
                ActionDataConfigKey.OBJECT to JsonObject(mapOf("value" to JsonPrimitive(true))),
                ActionDataConfigKey.FROM_PATH to JsonPrimitive("$.value"),
                ActionDataConfigKey.TO_PATH to JsonPrimitive("$.renamed"),
                ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.DATA_PICK to config(
                ActionDataConfigKey.OBJECT to JsonObject(mapOf("value" to JsonPrimitive(true))),
                ActionDataConfigKey.PATHS to JsonArray(listOf(JsonPrimitive("$.value"))),
                ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.DATA_UUID to config(
                ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.DATA_TO_NUMBER to config(
                ActionDataConfigKey.VALUE to JsonPrimitive("123"),
                ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.DATA_TO_STRING to config(
                ActionDataConfigKey.VALUE to JsonPrimitive(123),
                ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.DATA_TO_BOOLEAN to config(
                ActionDataConfigKey.VALUE to JsonPrimitive("true"),
                ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.DATA_TYPE_OF to config(
                ActionDataConfigKey.VALUE to JsonPrimitive("test"),
                ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.XML_PARSE to config(
                ActionXmlConfigKey.TEXT to JsonPrimitive("<rss><title>Bangumi</title></rss>"),
                ActionXmlConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.XML_STRINGIFY to config(
                ActionXmlConfigKey.DATA to JsonObject(mapOf("title" to JsonPrimitive("Bangumi"))),
                ActionXmlConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.HTML_QUERY to htmlQueryConfig(),
            ActionNodeType.HTML_QUERY_ALL to htmlQueryConfig(),
            ActionNodeType.HTML_TITLE to htmlConfig(),
            ActionNodeType.HTML_TEXT to htmlConfig(),
            ActionNodeType.HTML_META_CONTENT to config(
                ActionHtmlConfigKey.HTML to JsonPrimitive(html),
                ActionHtmlConfigKey.NAME to JsonPrimitive("description"),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.HTML_LINKS to htmlConfig(),
            ActionNodeType.OBJECT_GET to config(
                ActionObjectConfigKey.OBJECT to JsonObject(mapOf("value" to JsonPrimitive("value"))),
                ActionObjectConfigKey.PATH to JsonPrimitive("$.value"),
                ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.OBJECT_SET to config(
                ActionObjectConfigKey.OBJECT to JsonObject(emptyMap()),
                ActionObjectConfigKey.KEY to JsonPrimitive("value"),
                ActionObjectConfigKey.VALUE to JsonPrimitive("value"),
                ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.OBJECT_REMOVE to config(
                ActionObjectConfigKey.OBJECT to JsonObject(mapOf("value" to JsonPrimitive("value"))),
                ActionObjectConfigKey.KEY to JsonPrimitive("value"),
                ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.OBJECT_OMIT to config(
                ActionObjectConfigKey.OBJECT to JsonObject(mapOf("a" to JsonPrimitive(1), "b" to JsonPrimitive(2))),
                ActionObjectConfigKey.KEYS to JsonArray(listOf(JsonPrimitive("b"))),
                ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.OBJECT_PICK to config(
                ActionObjectConfigKey.OBJECT to JsonObject(mapOf("a" to JsonPrimitive(1), "b" to JsonPrimitive(2))),
                ActionObjectConfigKey.KEYS to JsonArray(listOf(JsonPrimitive("a"))),
                ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.OBJECT_MERGE to config(
                ActionObjectConfigKey.OBJECTS to JsonArray(listOf(JsonObject(mapOf("value" to JsonPrimitive("value"))))),
                ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.OBJECT_KEYS to config(
                ActionObjectConfigKey.OBJECT to JsonObject(mapOf("value" to JsonPrimitive("value"))),
                ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.OBJECT_VALUES to config(
                ActionObjectConfigKey.OBJECT to JsonObject(mapOf("value" to JsonPrimitive("value"))),
                ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.OBJECT_ENTRIES to config(
                ActionObjectConfigKey.OBJECT to JsonObject(mapOf("value" to JsonPrimitive("value"))),
                ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.OBJECT_FROM_ENTRIES to config(
                ActionObjectConfigKey.ENTRIES to JsonArray(listOf(JsonArray(listOf(JsonPrimitive("a"), JsonPrimitive(1))))),
                ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.OBJECT_HAS_KEY to config(
                ActionObjectConfigKey.OBJECT to JsonObject(mapOf("value" to JsonPrimitive("value"))),
                ActionObjectConfigKey.KEY to JsonPrimitive("value"),
                ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.OBJECT_IS_EMPTY to config(
                ActionObjectConfigKey.OBJECT to JsonObject(emptyMap()),
                ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.JSON_EXTRACT to config(
                ActionJsonConfigKey.SOURCE to JsonObject(mapOf("value" to JsonPrimitive("value"))),
                ActionJsonConfigKey.PATH to JsonPrimitive("$.value"),
                ActionJsonConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.JSON_PARSE to config(
                ActionJsonConfigKey.TEXT to JsonPrimitive("{\"value\":true}"),
                ActionJsonConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.JSON_STRINGIFY to config(
                ActionJsonConfigKey.VALUE to JsonPrimitive("value"),
                ActionJsonConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.TEXT_LENGTH to textConfig(),
            ActionNodeType.TEXT_TRIM to textConfig(),
            ActionNodeType.TEXT_LOWERCASE to textConfig(),
            ActionNodeType.TEXT_UPPERCASE to textConfig(),
            ActionNodeType.TEXT_CAPITALIZE to textConfig(),
            ActionNodeType.TEXT_REPEAT to config(
                ActionTextConfigKey.TEXT to JsonPrimitive("hi"),
                ActionTextConfigKey.COUNT to JsonPrimitive(2),
                ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.TEXT_REVERSE to textConfig(),
            ActionNodeType.TEXT_INDEX_OF to config(
                ActionTextConfigKey.TEXT to JsonPrimitive("hello"),
                ActionTextConfigKey.PATTERN to JsonPrimitive("ll"),
                ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.TEXT_TEMPLATE to config(
                ActionTextConfigKey.TEMPLATE to JsonPrimitive("Hello \${name}"),
                ActionTextConfigKey.OBJECT to JsonObject(mapOf("name" to JsonPrimitive("World"))),
                ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.TEXT_SPLIT to config(
                ActionTextConfigKey.TEXT to JsonPrimitive("a,b"),
                ActionTextConfigKey.DELIMITER to JsonPrimitive(","),
                ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.TEXT_REGEX_MATCH to config(
                ActionTextConfigKey.TEXT to JsonPrimitive("tag-1"),
                ActionTextConfigKey.PATTERN to JsonPrimitive("tag-(\\d+)"),
                ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.TEXT_SUBSTRING to config(
                ActionTextConfigKey.TEXT to JsonPrimitive("value"),
                ActionTextConfigKey.START_INDEX to JsonPrimitive(1),
                ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.TEXT_SUBSTRING_BEFORE to delimiterTextConfig(),
            ActionNodeType.TEXT_SUBSTRING_AFTER to delimiterTextConfig(),
            ActionNodeType.TEXT_REPLACE to replaceTextConfig(),
            ActionNodeType.TEXT_REPLACE_REGEX to replaceTextConfig(),
            ActionNodeType.TEXT_JOIN to arrayConfig(),
            ActionNodeType.ARRAY_LENGTH to arrayConfig(),
            ActionNodeType.ARRAY_CREATE to arrayConfig(),
            ActionNodeType.ARRAY_APPEND to config(
                ActionArrayConfigKey.VALUES to array,
                ActionArrayConfigKey.VALUE to JsonPrimitive("value"),
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_INSERT_AT to config(
                ActionArrayConfigKey.VALUES to array,
                ActionArrayConfigKey.INDEX to JsonPrimitive(0),
                ActionArrayConfigKey.VALUE to JsonPrimitive("value"),
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_REMOVE_AT to config(
                ActionArrayConfigKey.VALUES to array,
                ActionArrayConfigKey.INDEX to JsonPrimitive(0),
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_FILTER to config(
                ActionArrayConfigKey.VALUES to array,
                ActionArrayConfigKey.OPERATOR to JsonPrimitive("equals"),
                ActionArrayConfigKey.EXPECTED to JsonPrimitive("value"),
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_MAP to config(
                ActionArrayConfigKey.VALUES to array,
                ActionArrayConfigKey.FIELD_PATH to JsonPrimitive("$"),
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_FLAT_MAP to config(
                ActionArrayConfigKey.VALUES to array,
                ActionArrayConfigKey.FIELD_PATH to JsonPrimitive("$"),
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_CONCAT to config(
                ActionArrayConfigKey.VALUES to JsonArray(listOf(array, array)),
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_ZIP to config(
                ActionArrayConfigKey.VALUES to array,
                ActionArrayConfigKey.OTHER_VALUES to array,
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_TAKE to config(
                ActionArrayConfigKey.VALUES to array,
                ActionArrayConfigKey.COUNT to JsonPrimitive(1),
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_DROP to config(
                ActionArrayConfigKey.VALUES to array,
                ActionArrayConfigKey.COUNT to JsonPrimitive(1),
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_CONTAINS to config(
                ActionArrayConfigKey.VALUES to array,
                ActionArrayConfigKey.VALUE to JsonPrimitive("value"),
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_FIND to config(
                ActionArrayConfigKey.VALUES to array,
                ActionArrayConfigKey.FIELD_PATH to JsonPrimitive("$"),
                ActionArrayConfigKey.EXPECTED to JsonPrimitive("value"),
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_DISTINCT to arrayConfig(),
            ActionNodeType.ARRAY_SORT to arrayConfig(),
            ActionNodeType.ARRAY_REVERSE to arrayConfig(),
            ActionNodeType.ARRAY_SLICE to arrayConfig(),
            ActionNodeType.ARRAY_FLATTEN to config(
                ActionArrayConfigKey.VALUES to JsonArray(listOf(array)),
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.MATH_ADD to mathConfig(),
            ActionNodeType.MATH_SUBTRACT to mathConfig(),
            ActionNodeType.MATH_MULTIPLY to mathConfig(),
            ActionNodeType.MATH_DIVIDE to mathConfig(),
            ActionNodeType.MATH_MODULO to mathConfig(),
            ActionNodeType.MATH_MIN to mathConfig(),
            ActionNodeType.MATH_MAX to mathConfig(),
            ActionNodeType.MATH_POW to mathConfig(),
            ActionNodeType.MATH_SQRT to config(
                ActionMathConfigKey.VALUE to JsonPrimitive(4),
                ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.MATH_SUM to config(
                ActionMathConfigKey.VALUES to JsonArray(listOf(JsonPrimitive(1), JsonPrimitive(2), JsonPrimitive(3))),
                ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.MATH_AVG to config(
                ActionMathConfigKey.VALUES to JsonArray(listOf(JsonPrimitive(1), JsonPrimitive(2), JsonPrimitive(3))),
                ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.MATH_LOG to config(
                ActionMathConfigKey.VALUE to JsonPrimitive(10),
                ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.MATH_EXP to config(
                ActionMathConfigKey.VALUE to JsonPrimitive(1),
                ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.MATH_NEGATE to config(
                ActionMathConfigKey.VALUE to JsonPrimitive(1),
                ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.OPEN_EXTERNAL_URL to config(
                ActionOpenUrlConfigKey.URL to JsonPrimitive("https://bgm.tv"),
            ),
            ActionNodeType.OPEN_EXTERNAL_APP to config(
                ActionOpenAppConfigKey.URI to JsonPrimitive("market://details?id=example"),
            ),
            ActionNodeType.OPEN_INTERNAL_WEB to config(
                ActionOpenWebConfigKey.URL to JsonPrimitive("https://bgm.tv"),
            ),
            ActionNodeType.SHOW_TOAST to config(
                ActionToastConfigKey.MESSAGE to JsonPrimitive("message"),
            ),
            ActionNodeType.WRITE_CLIPBOARD to config(
                ActionClipboardConfigKey.TEXT to JsonPrimitive("text"),
            ),
            ActionNodeType.IMAGE_PREVIEW to config(
                ActionImagePreviewConfigKey.INDEX to JsonPrimitive(0),
                ActionImagePreviewConfigKey.IMAGES to JsonArray(listOf(JsonPrimitive("https://lain.bgm.tv/pic/cover/l/00/00/1.jpg"))),
            ),
            ActionNodeType.SYNC_COOKIE to config(
                ActionSyncCookieConfigKey.URL to JsonPrimitive("https://bgm.tv"),
                ActionSyncCookieConfigKey.TITLE to JsonPrimitive("同步 Cookie"),
            ),
            ActionNodeType.FLOW_DEBUG to config(
                ActionFlowConfigKey.MESSAGE to JsonPrimitive("debug"),
            ),
            ActionNodeType.FLOW_TRY to config(),
            ActionNodeType.FLOW_CATCH to config(),
            ActionNodeType.FLOW_FINALLY to config(),
            ActionNodeType.FLOW_CALL to config(
                ActionFlowConfigKey.WORKFLOW_ID to JsonPrimitive("sub_flow"),
                ActionFlowConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.FLOW_RETURN to config(
                ActionFlowConfigKey.OUTPUT to JsonPrimitive("result"),
            ),
            ActionNodeType.FLOW_PARALLEL to config(),
            ActionNodeType.FLOW_JOIN to config(
                ActionFlowConfigKey.VALUES to array,
                ActionFlowConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.FLOW_RETRY to config(
                ActionFlowConfigKey.RETRY_COUNT to JsonPrimitive(3),
            ),
            ActionNodeType.FLOW_TIMEOUT to config(
                ActionFlowConfigKey.TIMEOUT_MILLIS to JsonPrimitive(1000),
            ),
            ActionNodeType.FLOW_WAIT_UNTIL to config(
                ActionFlowConfigKey.CONDITION to JsonPrimitive(true),
            ),
            ActionNodeType.FLOW_RATE_LIMIT to config(
                ActionFlowConfigKey.DELAY_MILLIS to JsonPrimitive(100),
            ),
            ActionNodeType.DATA_UUID to config(
                ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.DATE_NOW to config(
                ActionDateConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.DATE_GET_COMPONENT to config(
                ActionDateConfigKey.TIMESTAMP to JsonPrimitive(1700000000000L),
                ActionDateConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.DATE_FORMAT to config(
                ActionDateConfigKey.TIMESTAMP to JsonPrimitive(1700000000000L),
                ActionDateConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.DATE_PARSE to config(
                ActionDateConfigKey.TEXT to JsonPrimitive("2026-09-01T04:15:01Z"),
                ActionDateConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.CODEC_BASE64_ENCODE to config(
                ActionCodecConfigKey.TEXT to JsonPrimitive("hello"),
                ActionCodecConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.CODEC_BASE64_DECODE to config(
                ActionCodecConfigKey.TEXT to JsonPrimitive("aGVsbG8="),
                ActionCodecConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.CODEC_BASE64_URL_ENCODE to config(
                ActionCodecConfigKey.TEXT to JsonPrimitive("hello?world"),
                ActionCodecConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.CODEC_BASE64_URL_DECODE to config(
                ActionCodecConfigKey.TEXT to JsonPrimitive("aGVsbG8_d29ybGQ="),
                ActionCodecConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.CODEC_HEX_ENCODE to config(
                ActionCodecConfigKey.TEXT to JsonPrimitive("hello"),
                ActionCodecConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.CODEC_HEX_DECODE to config(
                ActionCodecConfigKey.TEXT to JsonPrimitive("68656c6c6f"),
                ActionCodecConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.CODEC_URL_ENCODE to config(
                ActionCodecConfigKey.TEXT to JsonPrimitive("hello world"),
                ActionCodecConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.CODEC_URL_DECODE to config(
                ActionCodecConfigKey.TEXT to JsonPrimitive("hello+world"),
                ActionCodecConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.CODEC_HTML_ESCAPE to config(
                ActionCodecConfigKey.TEXT to JsonPrimitive("<div>hello & world</div>"),
                ActionCodecConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.CODEC_HTML_UNESCAPE to config(
                ActionCodecConfigKey.TEXT to JsonPrimitive("&lt;div&gt;hello &amp; world&lt;/div&gt;"),
                ActionCodecConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.CRYPTO_HASH to config(
                ActionCryptoConfigKey.TEXT to JsonPrimitive("hello"),
                ActionCryptoConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.CRYPTO_HMAC to config(
                ActionCryptoConfigKey.TEXT to JsonPrimitive("hello"),
                ActionCryptoConfigKey.SECRET to JsonPrimitive("secret"),
                ActionCryptoConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.CRYPTO_ENCRYPT to config(
                ActionCryptoConfigKey.TEXT to JsonPrimitive("hello"),
                ActionCryptoConfigKey.SECRET_KEY to JsonPrimitive("secret"),
                ActionCryptoConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.CRYPTO_DECRYPT to config(
                ActionCryptoConfigKey.TEXT to JsonPrimitive("68656c6c6f"),
                ActionCryptoConfigKey.SECRET_KEY to JsonPrimitive("secret"),
                ActionCryptoConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.CRYPTO_RANDOM_BYTES to config(
                ActionCryptoConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.CRYPTO_UUID to config(
                ActionCryptoConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_SUM to config(
                ActionArrayConfigKey.VALUES to array,
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_AVG to config(
                ActionArrayConfigKey.VALUES to array,
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_MIN to config(
                ActionArrayConfigKey.VALUES to array,
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_MAX to config(
                ActionArrayConfigKey.VALUES to array,
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_CHUNK to config(
                ActionArrayConfigKey.VALUES to array,
                ActionArrayConfigKey.SIZE to JsonPrimitive(2),
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_SHUFFLE to config(
                ActionArrayConfigKey.VALUES to array,
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_SAMPLE to config(
                ActionArrayConfigKey.VALUES to array,
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.DATE_ADD to config(
                ActionDateConfigKey.TIMESTAMP to JsonPrimitive(1700000000000L),
                ActionDateConfigKey.COUNT to JsonPrimitive(1),
                ActionDateConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.DATE_SUBTRACT to config(
                ActionDateConfigKey.TIMESTAMP to JsonPrimitive(1700000000000L),
                ActionDateConfigKey.COUNT to JsonPrimitive(1),
                ActionDateConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.DATE_DIFF to config(
                ActionDateConfigKey.TIMESTAMP_LEFT to JsonPrimitive(1700000000000L),
                ActionDateConfigKey.TIMESTAMP_RIGHT to JsonPrimitive(1600000000000L),
                ActionDateConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.DATE_RELATIVE_TIME to config(
                ActionDateConfigKey.TIMESTAMP to JsonPrimitive(1700000000000L),
                ActionDateConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.MATH_ROUND to config(
                ActionMathConfigKey.VALUE to JsonPrimitive(1.5),
                ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.MATH_FLOOR to config(
                ActionMathConfigKey.VALUE to JsonPrimitive(1.5),
                ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.MATH_CEIL to config(
                ActionMathConfigKey.VALUE to JsonPrimitive(1.5),
                ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.MATH_ABS to config(
                ActionMathConfigKey.VALUE to JsonPrimitive(-1.5),
                ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.MATH_RANDOM to config(
                ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.MATH_CLAMP to config(
                ActionMathConfigKey.VALUE to JsonPrimitive(5),
                ActionMathConfigKey.MIN to JsonPrimitive(1),
                ActionMathConfigKey.MAX to JsonPrimitive(10),
                ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.TEXT_CONTAINS to config(
                ActionTextConfigKey.TEXT to JsonPrimitive("hello world"),
                ActionTextConfigKey.PATTERN to JsonPrimitive("world"),
                ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.TEXT_STARTS_WITH to config(
                ActionTextConfigKey.TEXT to JsonPrimitive("hello world"),
                ActionTextConfigKey.PATTERN to JsonPrimitive("hello"),
                ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.TEXT_ENDS_WITH to config(
                ActionTextConfigKey.TEXT to JsonPrimitive("hello world"),
                ActionTextConfigKey.PATTERN to JsonPrimitive("world"),
                ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.TEXT_SLUGIFY to config(
                ActionTextConfigKey.TEXT to JsonPrimitive("Hello World!"),
                ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.TEXT_TRUNCATE to config(
                ActionTextConfigKey.TEXT to JsonPrimitive("Hello World!"),
                ActionTextConfigKey.LIMIT to JsonPrimitive(5),
                ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.URL_PARSE to config(
                ActionUrlConfigKey.URL to JsonPrimitive("https://bangumi.tv/subject/123?page=1"),
                ActionUrlConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.URL_BUILD to config(
                ActionUrlConfigKey.BASE_URL to JsonPrimitive("https://bangumi.tv/subject/123"),
                ActionUrlConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.URL_SET_QUERY_PARAM to config(
                ActionUrlConfigKey.URL to JsonPrimitive("https://bangumi.tv/subject/123"),
                ActionUrlConfigKey.KEY to JsonPrimitive("page"),
                ActionUrlConfigKey.VALUE to JsonPrimitive("2"),
                ActionUrlConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.URL_GET_QUERY_PARAM to config(
                ActionUrlConfigKey.URL to JsonPrimitive("https://bangumi.tv/subject/123?page=2"),
                ActionUrlConfigKey.KEY to JsonPrimitive("page"),
                ActionUrlConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.BILIBILI_SIGN_URL to config(
                ActionBilibiliConfigKey.URL to JsonPrimitive("https://api.bilibili.com/x/space/wbi/acc/info?mid=123456"),
                ActionBilibiliConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.CSV_PARSE to config(
                ActionCsvConfigKey.TEXT to JsonPrimitive("a,b\n1,2"),
                ActionCsvConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.CSV_STRINGIFY to config(
                ActionCsvConfigKey.ITEMS to JsonArray(listOf(JsonObject(mapOf("a" to JsonPrimitive("1"))))),
                ActionCsvConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.OBJECT_KEYS to config(
                ActionObjectConfigKey.OBJECT to JsonObject(mapOf("a" to JsonPrimitive("1"))),
                ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.OBJECT_VALUES to config(
                ActionObjectConfigKey.OBJECT to JsonObject(mapOf("a" to JsonPrimitive("1"))),
                ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.OBJECT_ENTRIES to config(
                ActionObjectConfigKey.OBJECT to JsonObject(mapOf("a" to JsonPrimitive("1"))),
                ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.OBJECT_HAS_KEY to config(
                ActionObjectConfigKey.OBJECT to JsonObject(mapOf("a" to JsonPrimitive("1"))),
                ActionObjectConfigKey.KEY to JsonPrimitive("a"),
                ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_INDEX_OF to config(
                ActionArrayConfigKey.VALUES to array,
                ActionArrayConfigKey.VALUE to JsonPrimitive("value"),
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_INTERSECTION to config(
                ActionArrayConfigKey.VALUES to array,
                ActionArrayConfigKey.OTHER_VALUES to array,
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_DIFFERENCE to config(
                ActionArrayConfigKey.VALUES to array,
                ActionArrayConfigKey.OTHER_VALUES to array,
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.JSON_VALIDATE to config(
                ActionJsonConfigKey.TEXT to JsonPrimitive("{\"a\":1}"),
                ActionJsonConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.JSON_SCHEMA_VALIDATE to config(
                ActionJsonConfigKey.VALUE to JsonObject(mapOf("a" to JsonPrimitive(1))),
                ActionJsonConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.TEXT_MATCH_ALL to config(
                ActionTextConfigKey.TEXT to JsonPrimitive("tag1 tag2"),
                ActionTextConfigKey.PATTERN to JsonPrimitive("tag(\\d+)"),
                ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.TEXT_PAD to config(
                ActionTextConfigKey.TEXT to JsonPrimitive("1"),
                ActionTextConfigKey.PAD_LENGTH to JsonPrimitive(3),
                ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.TEXT_FORMAT_NUMBER to config(
                ActionTextConfigKey.TEXT to JsonPrimitive("12.3456"),
                ActionTextConfigKey.FRACTION_DIGITS to JsonPrimitive(2),
                ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_GROUP_BY to config(
                ActionArrayConfigKey.VALUES to JsonArray(listOf(JsonObject(mapOf("cat" to JsonPrimitive("a"))))),
                ActionArrayConfigKey.FIELD_PATH to JsonPrimitive("$.cat"),
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_REDUCE to config(
                ActionArrayConfigKey.VALUES to array,
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.ARRAY_FIRST to arrayConfig(),
            ActionNodeType.ARRAY_LAST to arrayConfig(),
            ActionNodeType.HTML_ATTRIBUTES to config(
                ActionHtmlConfigKey.HTML to JsonPrimitive(html),
                ActionHtmlConfigKey.SELECTOR to JsonPrimitive("a"),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.HTML_TABLE_TO_JSON to config(
                ActionHtmlConfigKey.HTML to JsonPrimitive("<table><tr><th>A</th></tr><tr><td>1</td></tr></table>"),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.UI_CONFIRM to config(
                ActionConfirmConfigKey.MESSAGE to JsonPrimitive("confirm?"),
            ),
            ActionNodeType.SYSTEM_SHARE to config(
                ActionShareConfigKey.TEXT to JsonPrimitive("share content"),
            ),
            ActionNodeType.SYSTEM_NOTIFICATION to config(
                ActionNotificationConfigKey.CONTENT to JsonPrimitive("notification body"),
            ),
            ActionNodeType.READ_CLIPBOARD to config(
                ActionClipboardConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.SYSTEM_VIBRATE to config(),
            ActionNodeType.UI_INPUT_DIALOG to config(
                ActionInputDialogConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.UI_SELECT_DIALOG to config(
                ActionSelectDialogConfigKey.OPTIONS to buildJsonArray {
                    add(buildJsonObject {
                        put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("Option 1"))
                        put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("opt1"))
                    })
                },
                ActionSelectDialogConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.HTTP_REQUEST to config(
                ActionHttpConfigKey.URL to JsonPrimitive("https://bgm.tv"),
            ),
            ActionNodeType.STORAGE_PREFERENCES_GET to config(
                ActionStorageConfigKey.KEY to JsonPrimitive("subject_633836"),
                ActionStorageConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.STORAGE_PREFERENCES_SET to config(
                ActionStorageConfigKey.KEY to JsonPrimitive("subject_633836"),
                ActionStorageConfigKey.VALUE to JsonPrimitive(true),
                ActionStorageConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.STORAGE_PREFERENCES_DELETE to config(
                ActionStorageConfigKey.KEY to JsonPrimitive("subject_633836"),
                ActionStorageConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.STORAGE_PREFERENCES_HAS to config(
                ActionStorageConfigKey.KEY to JsonPrimitive("subject_633836"),
                ActionStorageConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
            ),
            ActionNodeType.STORAGE_PREFERENCES_CLEAR to config(),
        )

        private fun config(vararg values: Pair<String, kotlinx.serialization.json.JsonElement>): JsonObject = buildJsonObject {
            values.forEach { (key, value) -> put(key, value) }
        }

        private fun binaryConfig(): JsonObject = config(
            ActionControlConfigKey.LEFT to JsonPrimitive(2),
            ActionControlConfigKey.RIGHT to JsonPrimitive(1),
        )

        private fun mathConfig(): JsonObject = config(
            ActionMathConfigKey.LEFT to JsonPrimitive(2),
            ActionMathConfigKey.RIGHT to JsonPrimitive(1),
            ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
        )

        private fun htmlConfig(): JsonObject = config(
            ActionHtmlConfigKey.HTML to JsonPrimitive(html),
            ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
        )

        private fun htmlQueryConfig(): JsonObject = config(
            ActionHtmlConfigKey.HTML to JsonPrimitive(html),
            ActionHtmlConfigKey.SELECTOR to JsonPrimitive("h1"),
            ActionHtmlConfigKey.OPERATION to JsonPrimitive(ActionHtmlQueryOperation.TEXT),
            ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
        )

        private fun textConfig(): JsonObject = config(
            ActionTextConfigKey.TEXT to JsonPrimitive("Value"),
            ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
        )

        private fun delimiterTextConfig(): JsonObject = config(
            ActionTextConfigKey.TEXT to JsonPrimitive("left:right"),
            ActionTextConfigKey.DELIMITER to JsonPrimitive(":"),
            ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
        )

        private fun replaceTextConfig(): JsonObject = config(
            ActionTextConfigKey.TEXT to JsonPrimitive("value"),
            ActionTextConfigKey.PATTERN to JsonPrimitive("value"),
            ActionTextConfigKey.REPLACEMENT to JsonPrimitive("result"),
            ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
        )

        private fun arrayConfig(): JsonObject = config(
            ActionArrayConfigKey.VALUES to array,
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(outputKey),
        )

        private const val outputKey = "result"
        private const val html =
            "<html><head><title>Title</title><meta name=\"description\" content=\"Description\"></head><body><h1>Value</h1><a href=\"https://example.com\">Link</a></body></html>"
    }
}
