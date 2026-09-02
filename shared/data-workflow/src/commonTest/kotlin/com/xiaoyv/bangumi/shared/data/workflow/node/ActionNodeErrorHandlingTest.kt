package com.xiaoyv.bangumi.shared.data.workflow.node

import com.fleeksoft.ksoup.select.Selector
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHtmlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionJsonConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionMathConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionUrlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.builtInActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionJsonPath
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionUrlPolicy
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionHttpRequestExecutor
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionWorkflowPreferencesStore
import io.ktor.http.URLParserException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonDecodingException
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * 节点层与辅助工具全量边界异常单元测试集合。
 *
 * 覆盖所有内置节点在遇到非法操作数、越界索引、缺失参数或损坏格式时的异常抛出与诊断保护。
 */
class ActionNodeErrorHandlingTest {

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

    private val definitions = builtInActionNodeDefinitions(stubHttpExecutor, stubPreferencesStore).associateBy { it.spec.type }

    private fun config(vararg pairs: Pair<String, JsonElement>): JsonObject = buildJsonObject {
        pairs.forEach { (k, v) -> put(k, v) }
    }

    /**
     * 测试除法节点在除数为 0 时抛出 [IllegalArgumentException]。
     */
    @Test
    fun testMathDivideByZeroThrowsException() {
        val definition = definitions.getValue(ActionNodeType.MATH_DIVIDE)
        val node = ActionNode(
            id = "divide_test",
            type = ActionNodeType.MATH_DIVIDE,
            config = config(
                ActionMathConfigKey.LEFT to JsonPrimitive(100),
                ActionMathConfigKey.RIGHT to JsonPrimitive(0)
            )
        )

        val ex = assertFailsWith<IllegalArgumentException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }

        assertTrue(ex.message?.contains("除数不能为 0") == true)
    }

    /**
     * 测试取模运算节点在除数为 0 时抛出 [IllegalArgumentException]。
     */
    @Test
    fun testMathModuloByZeroThrowsException() {
        val definition = definitions.getValue(ActionNodeType.MATH_MODULO)
        val node = ActionNode(
            id = "modulo_test",
            type = ActionNodeType.MATH_MODULO,
            config = config(
                ActionMathConfigKey.LEFT to JsonPrimitive(10),
                ActionMathConfigKey.RIGHT to JsonPrimitive(0)
            )
        )

        val ex = assertFailsWith<IllegalArgumentException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }

        assertTrue(ex.message?.contains("余数运算的除数不能为 0") == true)
    }

    /**
     * 测试平方根计算非负数校验失败时抛出 [IllegalArgumentException]。
     */
    @Test
    fun testMathSqrtNegativeOperandThrowsException() {
        val definition = definitions.getValue(ActionNodeType.MATH_SQRT)
        val node = ActionNode(
            id = "sqrt_test",
            type = ActionNodeType.MATH_SQRT,
            config = config(ActionMathConfigKey.VALUE to JsonPrimitive(-9.0))
        )

        val ex = assertFailsWith<IllegalArgumentException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }

        assertTrue(ex.message?.contains("负数不能求平方根") == true)
    }

    /**
     * 测试对数计算真数非正数校验失败时抛出 [IllegalArgumentException]。
     */
    @Test
    fun testMathLogNonPositiveOperandThrowsException() {
        val definition = definitions.getValue(ActionNodeType.MATH_LOG)
        val node = ActionNode(
            id = "log_test",
            type = ActionNodeType.MATH_LOG,
            config = config(ActionMathConfigKey.VALUE to JsonPrimitive(-5.0))
        )

        val ex = assertFailsWith<IllegalArgumentException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }

        assertTrue(ex.message?.contains("对数真数必须大于 0") == true)
    }

    /**
     * 测试数据合并节点在传入不支持的合并策略时抛出 [IllegalArgumentException]。
     */
    @Test
    fun testInvalidMergeStrategyThrowsException() {
        val definition = definitions.getValue(ActionNodeType.DATA_MERGE)
        val node = ActionNode(
            id = "data_merge_invalid",
            type = ActionNodeType.DATA_MERGE,
            config = config(
                ActionDataConfigKey.OBJECTS to buildJsonArray { add(buildJsonObject { }) },
                ActionDataConfigKey.MERGE_STRATEGY to JsonPrimitive("invalid_strategy_type"),
                ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive("target")
            )
        )

        val ex = assertFailsWith<IllegalArgumentException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }

        assertTrue(ex.message?.contains("不支持的数据合并策略") == true)
    }

    /**
     * 测试字段重命名节点在找不到来源路径时抛出 [IllegalArgumentException]。
     */
    @Test
    fun testDataRenameMissingPathThrowsException() {
        val definition = definitions.getValue(ActionNodeType.DATA_RENAME)
        val node = ActionNode(
            id = "data_rename_bad",
            type = ActionNodeType.DATA_RENAME,
            config = config(
                Pair(ActionDataConfigKey.OBJECT, buildJsonObject { }),
                Pair(ActionDataConfigKey.FROM_PATH, JsonPrimitive("$.non_existent_key_path")),
                Pair(ActionDataConfigKey.TO_PATH, JsonPrimitive("$.new_key")),
                Pair(ActionDataConfigKey.OUTPUT_KEY, JsonPrimitive("res"))
            )
        )

        val ex = assertFailsWith<IllegalArgumentException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }

        assertTrue(ex.message?.contains("data.rename 节点找不到来源路径") == true)
    }

    /**
     * 测试文本截取节点在 start > end 或超出长度范围时抛出 [IllegalArgumentException]。
     */
    @Test
    fun testTextSubstringInvalidRangeThrowsException() {
        val definition = definitions.getValue(ActionNodeType.TEXT_SUBSTRING)
        val node = ActionNode(
            id = "text_sub_bad",
            type = ActionNodeType.TEXT_SUBSTRING,
            config = config(
                Pair(ActionTextConfigKey.TEXT, JsonPrimitive("hello")),
                Pair(ActionTextConfigKey.START_INDEX, JsonPrimitive(5)),
                Pair(ActionTextConfigKey.END_INDEX, JsonPrimitive(2)),
                Pair(ActionTextConfigKey.OUTPUT_KEY, JsonPrimitive("res"))
            )
        )

        val ex = assertFailsWith<IllegalArgumentException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }

        assertTrue(ex.message?.contains("文本截取索引范围无效") == true)
    }

    /**
     * 测试数组切片节点在 range 超出范围时抛出 [IllegalArgumentException]。
     */
    @Test
    fun testArraySliceInvalidRangeThrowsException() {
        val definition = definitions.getValue(ActionNodeType.ARRAY_SLICE)
        val node = ActionNode(
            id = "array_slice_bad",
            type = ActionNodeType.ARRAY_SLICE,
            config = config(
                Pair(ActionArrayConfigKey.VALUES, buildJsonArray { add(JsonPrimitive("a")) }),
                Pair(ActionArrayConfigKey.START, JsonPrimitive(0)),
                Pair(ActionArrayConfigKey.END, JsonPrimitive(10)),
                Pair(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("res"))
            )
        )

        val ex = assertFailsWith<IllegalArgumentException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }

        assertTrue(ex.message?.contains("array.slice 节点索引范围无效") == true)
    }

    /**
     * 测试数组分块节点在 size <= 0 时抛出 [IllegalArgumentException]。
     */
    @Test
    fun testArrayChunkInvalidSizeThrowsException() {
        val definition = definitions.getValue(ActionNodeType.ARRAY_CHUNK)
        val node = ActionNode(
            id = "array_chunk_bad",
            type = ActionNodeType.ARRAY_CHUNK,
            config = config(
                Pair(ActionArrayConfigKey.VALUES, buildJsonArray { add(JsonPrimitive("a")) }),
                Pair(ActionArrayConfigKey.SIZE, JsonPrimitive(0)),
                Pair(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("res"))
            )
        )

        val ex = assertFailsWith<IllegalArgumentException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }

        assertTrue(ex.message?.contains("Chunk size 必须大于 0") == true)
    }

    /**
     * 测试延迟节点在 millis < 0 时抛出 [IllegalArgumentException]。
     */
    @Test
    fun testFlowDelayNegativeMillisThrowsException() {
        val definition = definitions.getValue(ActionNodeType.FLOW_DELAY)
        val node = ActionNode(
            id = "delay_bad",
            type = ActionNodeType.FLOW_DELAY,
            config = config(Pair(ActionFlowConfigKey.DELAY_MILLIS, JsonPrimitive(-100)))
        )

        val ex = assertFailsWith<IllegalArgumentException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }

        assertTrue(ex.message?.contains("延迟时间不能小于 0") == true)
    }

    /**
     * 测试 HTTP 状态码分支节点在 min > max 时抛出 [IllegalArgumentException]。
     */
    @Test
    fun testHttpStatusSwitchInvalidRangeThrowsException() {
        val definition = definitions.getValue(ActionNodeType.HTTP_STATUS)
        val node = ActionNode(
            id = "http_status_bad",
            type = ActionNodeType.HTTP_STATUS,
            config = config(
                Pair(ActionControlConfigKey.STATUS_CODE, JsonPrimitive(500)),
                Pair(ActionControlConfigKey.MIN_STATUS_CODE, JsonPrimitive(500)),
                Pair(ActionControlConfigKey.MAX_STATUS_CODE, JsonPrimitive(200))
            )
        )

        val ex = assertFailsWith<IllegalArgumentException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }

        assertTrue(ex.message?.contains("最小 HTTP 状态码不能大于最大值") == true)
    }

    /**
     * 测试 JSON 解析节点处理非法 JSON 文本格式时抛出 [JsonDecodingException]。
     */
    @Test
    fun testJsonParseMalformedStringThrowsException() {
        val definition = definitions.getValue(ActionNodeType.JSON_PARSE)
        val node = ActionNode(
            id = "json_parse_bad",
            type = ActionNodeType.JSON_PARSE,
            config = config(Pair(ActionJsonConfigKey.TEXT, JsonPrimitive("{invalid_json_text")))
        )

        assertFailsWith<JsonDecodingException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }
    }

    /**
     * 测试数组删除下标越界时抛出 [IllegalArgumentException]。
     */
    @Test
    fun testArrayRemoveOutOfBoundsIndexThrowsException() {
        val definition = definitions.getValue(ActionNodeType.ARRAY_REMOVE_AT)
        val node = ActionNode(
            id = "array_remove_bad_index",
            type = ActionNodeType.ARRAY_REMOVE_AT,
            config = config(
                Pair(ActionArrayConfigKey.VALUES, buildJsonArray { add(JsonPrimitive("a")) }),
                Pair(ActionArrayConfigKey.INDEX, JsonPrimitive(99))
            )
        )

        val ex = assertFailsWith<IllegalArgumentException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }

        assertTrue(ex.message?.contains("超出数组范围") == true)
    }

    /**
     * 测试 URL 解析节点传入非法 URL 字符串时抛出 [URLParserException]。
     */
    @Test
    fun testUrlParseMalformedUrlThrowsException() {
        val definition = definitions.getValue(ActionNodeType.URL_PARSE)
        val node = ActionNode(
            id = "url_parse_bad",
            type = ActionNodeType.URL_PARSE,
            config = config(Pair(ActionUrlConfigKey.URL, JsonPrimitive("ht tps://invalid url string")))
        )

        assertFailsWith<URLParserException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }
    }

    /**
     * 测试 HTML 查询节点传入非法选择器时抛出 [Selector.SelectorParseException]。
     */
    @Test
    fun testHtmlQueryMalformedSelectorThrowsException() {
        val definition = definitions.getValue(ActionNodeType.HTML_QUERY)
        val node = ActionNode(
            id = "html_query_bad",
            type = ActionNodeType.HTML_QUERY,
            config = config(
                Pair(ActionHtmlConfigKey.HTML, JsonPrimitive("<div>hello</div>")),
                Pair(ActionHtmlConfigKey.SELECTOR, JsonPrimitive(":::invalid_css_selector:::")),
                Pair(ActionHtmlConfigKey.OPERATION, JsonPrimitive("text")),
                Pair(ActionHtmlConfigKey.OUTPUT_KEY, JsonPrimitive("result")),
            )
        )

        assertFailsWith<Selector.SelectorParseException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }
    }

    /**
     * 测试 [ActionJsonPath] 传入非法 Path 前缀时抛出 [IllegalArgumentException]。
     */
    @Test
    fun testActionJsonPathInvalidPrefixThrowsException() {
        val ex = assertFailsWith<IllegalArgumentException> {
            ActionJsonPath.resolve(buildJsonObject { }, "invalid_path_without_dollar")
        }
        assertTrue(ex.message?.contains("JSON 路径必须以 $ 或 $. 开头") == true)
    }

    /**
     * 测试 [ActionUrlPolicy] 校验非 HTTP URL 时抛出 [IllegalArgumentException]。
     */
    @Test
    fun testActionUrlPolicyNonHttpUrlThrowsException() {
        val ex = assertFailsWith<IllegalArgumentException> {
            ActionUrlPolicy.requireHttpUrl("ftp://example.com/file")
        }
        assertTrue(ex.message?.contains("http") == true)
    }
}
