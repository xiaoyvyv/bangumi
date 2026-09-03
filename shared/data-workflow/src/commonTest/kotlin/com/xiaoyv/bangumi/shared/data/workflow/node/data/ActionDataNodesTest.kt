package com.xiaoyv.bangumi.shared.data.workflow.node.data

import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionMathConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionUrlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.node.fixture.ActionNodeTestFixtures
import com.xiaoyv.bangumi.shared.data.workflow.node.fixture.ActionNodeTestFixtures.config
import io.ktor.http.URLParserException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * 基础数据处理、数学运算、对象及数组操作的边界与异常单元测试集合。
 */
class ActionDataNodesTest {

    private val definitions = ActionNodeTestFixtures.nodeDefinitions()

    @Test
    fun testMathDivideByZeroThrowsException() {
        val definition = definitions.getValue(ActionNodeType.MATH_DIVIDE)
        val node = ActionNode(
            id = "divide_test",
            type = ActionNodeType.MATH_DIVIDE,
            config = config(
                ActionMathConfigKey.LEFT to JsonPrimitive(100),
                ActionMathConfigKey.RIGHT to JsonPrimitive(0),
            ),
        )

        val ex = assertFailsWith<IllegalArgumentException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }
        assertTrue(ex.message?.contains("除数不能为 0") == true)
    }

    @Test
    fun testMathModuloByZeroThrowsException() {
        val definition = definitions.getValue(ActionNodeType.MATH_MODULO)
        val node = ActionNode(
            id = "modulo_test",
            type = ActionNodeType.MATH_MODULO,
            config = config(
                ActionMathConfigKey.LEFT to JsonPrimitive(10),
                ActionMathConfigKey.RIGHT to JsonPrimitive(0),
            ),
        )

        val ex = assertFailsWith<IllegalArgumentException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }
        assertTrue(ex.message?.contains("余数运算的除数不能为 0") == true)
    }

    @Test
    fun testMathSqrtNegativeOperandThrowsException() {
        val definition = definitions.getValue(ActionNodeType.MATH_SQRT)
        val node = ActionNode(
            id = "sqrt_test",
            type = ActionNodeType.MATH_SQRT,
            config = config(ActionMathConfigKey.VALUE to JsonPrimitive(-9.0)),
        )

        val ex = assertFailsWith<IllegalArgumentException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }
        assertTrue(ex.message?.contains("负数不能求平方根") == true)
    }

    @Test
    fun testMathLogNonPositiveOperandThrowsException() {
        val definition = definitions.getValue(ActionNodeType.MATH_LOG)
        val node = ActionNode(
            id = "log_test",
            type = ActionNodeType.MATH_LOG,
            config = config(ActionMathConfigKey.VALUE to JsonPrimitive(-5.0)),
        )

        val ex = assertFailsWith<IllegalArgumentException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }
        assertTrue(ex.message?.contains("对数真数必须大于 0") == true)
    }

    @Test
    fun testInvalidMergeStrategyThrowsException() {
        val definition = definitions.getValue(ActionNodeType.DATA_MERGE)
        val node = ActionNode(
            id = "data_merge_invalid",
            type = ActionNodeType.DATA_MERGE,
            config = config(
                ActionDataConfigKey.OBJECTS to buildJsonArray { add(buildJsonObject { }) },
                ActionDataConfigKey.MERGE_STRATEGY to JsonPrimitive("invalid_strategy_type"),
                ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive("target"),
            ),
        )

        val ex = assertFailsWith<IllegalArgumentException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }
        assertTrue(ex.message?.contains("不支持的数据合并策略") == true)
    }

    @Test
    fun testDataRenameMissingPathThrowsException() {
        val definition = definitions.getValue(ActionNodeType.DATA_RENAME)
        val node = ActionNode(
            id = "data_rename_bad",
            type = ActionNodeType.DATA_RENAME,
            config = config(
                ActionDataConfigKey.OBJECT to buildJsonObject { },
                ActionDataConfigKey.FROM_PATH to JsonPrimitive("$.non_existent_key_path"),
                ActionDataConfigKey.TO_PATH to JsonPrimitive("$.new_key"),
                ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive("res"),
            ),
        )

        val ex = assertFailsWith<IllegalArgumentException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }
        assertTrue(ex.message?.contains("data.rename 节点找不到来源路径") == true)
    }

    @Test
    fun testTextSubstringInvalidRangeThrowsException() {
        val definition = definitions.getValue(ActionNodeType.TEXT_SUBSTRING)
        val node = ActionNode(
            id = "text_sub_bad",
            type = ActionNodeType.TEXT_SUBSTRING,
            config = config(
                ActionTextConfigKey.TEXT to JsonPrimitive("hello"),
                ActionTextConfigKey.START_INDEX to JsonPrimitive(5),
                ActionTextConfigKey.END_INDEX to JsonPrimitive(2),
                ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive("res"),
            ),
        )

        val ex = assertFailsWith<IllegalArgumentException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }
        assertTrue(ex.message?.contains("文本截取索引范围无效") == true)
    }

    @Test
    fun testArraySliceInvalidRangeThrowsException() {
        val definition = definitions.getValue(ActionNodeType.ARRAY_SLICE)
        val node = ActionNode(
            id = "array_slice_bad",
            type = ActionNodeType.ARRAY_SLICE,
            config = config(
                ActionArrayConfigKey.VALUES to buildJsonArray { add(JsonPrimitive("a")) },
                ActionArrayConfigKey.START to JsonPrimitive(0),
                ActionArrayConfigKey.END to JsonPrimitive(10),
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive("res"),
            ),
        )

        val ex = assertFailsWith<IllegalArgumentException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }
        assertTrue(ex.message?.contains("array.slice 节点索引范围无效") == true)
    }

    @Test
    fun testArrayChunkInvalidSizeThrowsException() {
        val definition = definitions.getValue(ActionNodeType.ARRAY_CHUNK)
        val node = ActionNode(
            id = "array_chunk_bad",
            type = ActionNodeType.ARRAY_CHUNK,
            config = config(
                ActionArrayConfigKey.VALUES to buildJsonArray { add(JsonPrimitive("a")) },
                ActionArrayConfigKey.SIZE to JsonPrimitive(0),
                ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive("res"),
            ),
        )

        val ex = assertFailsWith<IllegalArgumentException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }
        assertTrue(ex.message?.contains("Chunk size 必须大于 0") == true)
    }

    @Test
    fun testArrayRemoveOutOfBoundsIndexThrowsException() {
        val definition = definitions.getValue(ActionNodeType.ARRAY_REMOVE_AT)
        val node = ActionNode(
            id = "array_remove_bad_index",
            type = ActionNodeType.ARRAY_REMOVE_AT,
            config = config(
                ActionArrayConfigKey.VALUES to buildJsonArray { add(JsonPrimitive("a")) },
                ActionArrayConfigKey.INDEX to JsonPrimitive(99),
            ),
        )

        val ex = assertFailsWith<IllegalArgumentException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }
        assertTrue(ex.message?.contains("超出数组范围") == true)
    }

    @Test
    fun testUrlParseMalformedUrlThrowsException() {
        val definition = definitions.getValue(ActionNodeType.URL_PARSE)
        val node = ActionNode(
            id = "url_parse_bad",
            type = ActionNodeType.URL_PARSE,
            config = config(ActionUrlConfigKey.URL to JsonPrimitive("ht tps://invalid url string")),
        )

        assertFailsWith<URLParserException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }
    }

    @Test
    fun testActionJsonPathInvalidPrefixThrowsException() {
        val ex = assertFailsWith<IllegalArgumentException> {
            com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionJsonPath.resolve(buildJsonObject { }, "invalid_path_without_dollar")
        }
        assertTrue(ex.message?.contains("JSON 路径必须以 $ 或 $. 开头") == true)
    }

    @Test
    fun testActionUrlPolicyNonHttpUrlThrowsException() {
        val ex = assertFailsWith<IllegalArgumentException> {
            com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionUrlPolicy.requireHttpUrl("ftp://example.com/file")
        }
        assertTrue(ex.message?.contains("http") == true)
    }
}
