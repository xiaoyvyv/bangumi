package com.xiaoyv.bangumi.shared.data.workflow.resolver

import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionTemplateResolver
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * [ActionTemplateResolver] 模板解析与行内表达式求值引擎单元测试。
 */
class ActionTemplateResolverTest {

    private val context = ActionExecutionContext(
        input = buildJsonObject {
            put("keyword", JsonPrimitive("芙莉莲"))
            put("score", JsonPrimitive(88.5))
        },
        variables = persistentMapOf(
            "index" to JsonPrimitive(0),
            "count" to JsonPrimitive(5),
            "numStr" to JsonPrimitive("10"),
            "title" to JsonPrimitive(" 葬送的芙莉莲 "),
            "cleanTitle" to JsonPrimitive("葬送的芙莉莲"),
            "emptyTitle" to JsonPrimitive(""),
            "blankTitle" to JsonPrimitive("   "),
            "tags" to buildJsonArray {
                add(JsonPrimitive("奇幻"))
                add(JsonPrimitive("冒险"))
            },
            "deep" to buildJsonObject {
                put("a", buildJsonObject {
                    put("b", buildJsonObject {
                        put("c", buildJsonObject {
                            put("d", JsonPrimitive("深层嵌套目标值"))
                        })
                    })
                })
            }
        ),
        stepOutputs = persistentMapOf(
            "api_request" to buildJsonObject {
                put("body", buildJsonObject {
                    put("data", buildJsonObject {
                        put("user", buildJsonObject {
                            put("profile", buildJsonObject {
                                put("name", JsonPrimitive("BangumiUser"))
                            })
                        })
                    })
                })
            }
        ),
        loop = buildJsonObject {
            put("index", JsonPrimitive(0))
            put("item", buildJsonObject {
                put("name", JsonPrimitive("轻音少女"))
                put("score", JsonPrimitive(91.0))
            })
        }
    )

    /**
     * 测试上下文基础路径解析、任意深度多级点分路径解析 (a.b.c.d) 与数组下标索引读取。
     */
    @Test
    fun testPathResolution() {
        assertEquals("芙莉莲", ActionTemplateResolver.evaluateExpression("input.keyword", context).jsonPrimitive.content)
        assertEquals(" 葬送的芙莉莲 ", ActionTemplateResolver.evaluateExpression("vars.title", context).jsonPrimitive.content)
        assertEquals("轻音少女", ActionTemplateResolver.evaluateExpression("loop.item.name", context).jsonPrimitive.content)
        assertEquals(0, ActionTemplateResolver.evaluateExpression("loop.index", context).jsonPrimitive.intOrNull)

        // 测试点号索引与方括号索引
        assertEquals("奇幻", ActionTemplateResolver.evaluateExpression("vars.tags.0", context).jsonPrimitive.content)
        assertEquals("冒险", ActionTemplateResolver.evaluateExpression("vars.tags[1]", context).jsonPrimitive.content)

        // 测试任意深度的多级嵌套对象路径解析 (a.b.c.d)
        assertEquals("深层嵌套目标值", ActionTemplateResolver.evaluateExpression("vars.deep.a.b.c.d", context).jsonPrimitive.content)
        assertEquals("BangumiUser", ActionTemplateResolver.evaluateExpression("steps.api_request.body.data.user.profile.name", context).jsonPrimitive.content)
    }

    /**
     * 测试行内算术运算（加、减、乘、除、取模、括号优先级与负数一元运算）。
     */
    @Test
    fun testArithmeticExpressions() {
        // 加法与减法
        assertEquals(1, ActionTemplateResolver.evaluateExpression("loop.index + 1", context).jsonPrimitive.intOrNull)
        assertEquals(2, ActionTemplateResolver.evaluateExpression("vars.count - 3", context).jsonPrimitive.intOrNull)

        // 乘法、除法与取模
        assertEquals(10, ActionTemplateResolver.evaluateExpression("vars.count * 2", context).jsonPrimitive.intOrNull)
        assertEquals(2.5, ActionTemplateResolver.evaluateExpression("vars.count / 2", context).jsonPrimitive.doubleOrNull)
        assertEquals(1, ActionTemplateResolver.evaluateExpression("vars.count % 2", context).jsonPrimitive.intOrNull)

        // 括号结合律优先级与一元负数
        assertEquals(12, ActionTemplateResolver.evaluateExpression("(vars.count + 1) * 2", context).jsonPrimitive.intOrNull)
        assertEquals(-5, ActionTemplateResolver.evaluateExpression("-vars.count", context).jsonPrimitive.intOrNull)

        // 字符串加号拼接
        assertEquals("葬送的芙莉莲!", ActionTemplateResolver.evaluateExpression("vars.cleanTitle + '!'", context).jsonPrimitive.content)
    }

    /**
     * 测试跨类型混合运算与隐式转换规则（如 String + Number 字符串拼接、数字字符串隐式转换求值、跨类型相等判定与真值转换）。
     */
    @Test
    fun testCrossTypeOperations() {
        // 1. 加法运算：字符串与数字/布尔拼接
        assertEquals("数量: 5", ActionTemplateResolver.evaluateExpression("'数量: ' + vars.count", context).jsonPrimitive.content)
        assertEquals("葬送的芙莉莲 - 5", ActionTemplateResolver.evaluateExpression("vars.cleanTitle + ' - ' + vars.count", context).jsonPrimitive.content)
        assertEquals("状态: true", ActionTemplateResolver.evaluateExpression("'状态: ' + true", context).jsonPrimitive.content)

        // 2. 算术运算：数字字符串隐式转换为数字进行计算
        assertEquals(20, ActionTemplateResolver.evaluateExpression("vars.numStr * 2", context).jsonPrimitive.intOrNull)
        assertEquals(8, ActionTemplateResolver.evaluateExpression("vars.numStr - 2", context).jsonPrimitive.intOrNull)

        // 3. 比较运算：数值与数字字符串的相等判定与大小比较
        assertTrue(ActionTemplateResolver.evaluateExpression("vars.count == '5'", context).jsonPrimitive.booleanOrNull == true)
        assertTrue(ActionTemplateResolver.evaluateExpression("vars.numStr > vars.count", context).jsonPrimitive.booleanOrNull == true)

        // 4. 逻辑运算与真值转换：非空文本/非0数字判定为 true，0/空串判定为 false
        assertTrue(ActionTemplateResolver.evaluateExpression("vars.cleanTitle && vars.count", context).jsonPrimitive.booleanOrNull == true)
        assertFalse(ActionTemplateResolver.evaluateExpression("vars.emptyTitle && vars.count", context).jsonPrimitive.booleanOrNull == true)
        assertTrue(ActionTemplateResolver.evaluateExpression("!vars.emptyTitle", context).jsonPrimitive.booleanOrNull == true)
    }

    /**
     * 测试关系与条件比较运算符（大于、大于等于、小于、小于等于、等于、不等于）。
     */
    @Test
    fun testComparisonExpressions() {
        // 大于、大于等于比较
        assertFalse(ActionTemplateResolver.evaluateExpression("loop.index > 0", context).jsonPrimitive.booleanOrNull == true)
        assertTrue(ActionTemplateResolver.evaluateExpression("loop.index >= 0", context).jsonPrimitive.booleanOrNull == true)
        assertTrue(ActionTemplateResolver.evaluateExpression("vars.count >= 5", context).jsonPrimitive.booleanOrNull == true)
        assertTrue(ActionTemplateResolver.evaluateExpression("input.score > 80", context).jsonPrimitive.booleanOrNull == true)

        // 小于、小于等于比较
        assertTrue(ActionTemplateResolver.evaluateExpression("loop.index < 5", context).jsonPrimitive.booleanOrNull == true)
        assertTrue(ActionTemplateResolver.evaluateExpression("vars.count <= 5", context).jsonPrimitive.booleanOrNull == true)

        // 数值与字符串的相等、不相等比较
        assertTrue(ActionTemplateResolver.evaluateExpression("loop.index == 0", context).jsonPrimitive.booleanOrNull == true)
        assertTrue(ActionTemplateResolver.evaluateExpression("vars.count != 10", context).jsonPrimitive.booleanOrNull == true)
        assertTrue(ActionTemplateResolver.evaluateExpression("vars.cleanTitle == '葬送的芙莉莲'", context).jsonPrimitive.booleanOrNull == true)
        assertTrue(ActionTemplateResolver.evaluateExpression("vars.cleanTitle != '其他'", context).jsonPrimitive.booleanOrNull == true)
    }

    /**
     * 测试逻辑运算符（与 &&、或 ||、非 ! 与嵌套组合）。
     */
    @Test
    fun testLogicalExpressions() {
        // 逻辑与、逻辑或与逻辑非
        assertTrue(ActionTemplateResolver.evaluateExpression("loop.index == 0 && vars.count == 5", context).jsonPrimitive.booleanOrNull == true)
        assertTrue(ActionTemplateResolver.evaluateExpression("loop.index > 0 || vars.count == 5", context).jsonPrimitive.booleanOrNull == true)
        assertTrue(ActionTemplateResolver.evaluateExpression("!vars.emptyTitle", context).jsonPrimitive.booleanOrNull == true)
        assertFalse(ActionTemplateResolver.evaluateExpression("!(loop.index == 0)", context).jsonPrimitive.booleanOrNull == true)
    }

    /**
     * 测试 Elvis 空值兜底运算符 (?:) 与三元表达式 (? :)。
     */
    @Test
    fun testElvisAndTernaryExpressions() {
        // Elvis 兜底运算
        assertEquals("默认标题", ActionTemplateResolver.evaluateExpression("vars.emptyTitle ?: '默认标题'", context).jsonPrimitive.content)
        assertEquals("退路", ActionTemplateResolver.evaluateExpression("vars.missingVar ?: '退路'", context).jsonPrimitive.content)
        assertEquals("葬送的芙莉莲", ActionTemplateResolver.evaluateExpression("vars.cleanTitle ?: '默认标题'", context).jsonPrimitive.content)

        // 三元条件表达
        assertEquals("足够", ActionTemplateResolver.evaluateExpression("vars.count >= 5 ? '足够' : '不足'", context).jsonPrimitive.content)
        assertEquals("普通", ActionTemplateResolver.evaluateExpression("loop.index > 0 ? '优秀' : '普通'", context).jsonPrimitive.content)
    }

    /**
     * 测试文本与数组的成员属性和内置方法读取（length、isEmpty、trim、first、last 等）。
     */
    @Test
    fun testMemberAccess() {
        // 字符串成员方法与属性
        assertEquals(6, ActionTemplateResolver.evaluateExpression("vars.cleanTitle.length", context).jsonPrimitive.intOrNull)
        assertTrue(ActionTemplateResolver.evaluateExpression("vars.emptyTitle.isEmpty", context).jsonPrimitive.booleanOrNull == true)
        assertTrue(ActionTemplateResolver.evaluateExpression("vars.cleanTitle.isNotEmpty", context).jsonPrimitive.booleanOrNull == true)
        assertTrue(ActionTemplateResolver.evaluateExpression("vars.blankTitle.isBlank", context).jsonPrimitive.booleanOrNull == true)
        assertEquals("葬送的芙莉莲", ActionTemplateResolver.evaluateExpression("vars.title.trim", context).jsonPrimitive.content)

        // 数组成员属性与首尾元素
        assertEquals(2, ActionTemplateResolver.evaluateExpression("vars.tags.length", context).jsonPrimitive.intOrNull)
        assertEquals("奇幻", ActionTemplateResolver.evaluateExpression("vars.tags.first", context).jsonPrimitive.content)
        assertEquals("冒险", ActionTemplateResolver.evaluateExpression("vars.tags.last", context).jsonPrimitive.content)
        assertFalse(ActionTemplateResolver.evaluateExpression("vars.tags.isEmpty", context).jsonPrimitive.booleanOrNull == true)
    }

    /**
     * 测试单一占位符解析保留 JSON 原始类型以及字符串多占位符文本插值。
     */
    @Test
    fun testResolveElementAndText() {
        // 单一占位符保留原生布尔类型
        val rawBool = ActionTemplateResolver.resolveElement(JsonPrimitive("\${vars.count >= 5}"), context)
        assertEquals(true, rawBool.jsonPrimitive.booleanOrNull)

        // 单一占位符保留原生数字类型
        val rawNum = ActionTemplateResolver.resolveElement(JsonPrimitive("\${loop.index + 1}"), context)
        assertEquals(1, rawNum.jsonPrimitive.intOrNull)

        // 字符串多占位符插值
        val text = "No.${ActionTemplateResolver.resolveText("\${loop.index + 1}", context)}: ${ActionTemplateResolver.resolveText("\${loop.item.name}", context)}"
        assertEquals("No.1: 轻音少女", text)
    }

    /**
     * 测试异常表达式与缺失字段的安全容错，确保不会引发运行时异常。
     */
    @Test
    fun testMalformedExpressionSafety() {
        // 不存在的变量属性安全解析为 JsonNull
        val missingResult = ActionTemplateResolver.evaluateExpression("vars.non_existent_key", context)
        assertEquals(JsonNull, missingResult)

        // 语法错误的表达式安全降级兜底为 JsonNull，不抛出异常
        val malformedResult = ActionTemplateResolver.evaluateExpression("vars.count +++ ", context)
        assertEquals(JsonNull, malformedResult)
    }
}
