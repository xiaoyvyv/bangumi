package com.xiaoyv.bangumi.shared.data.workflow.resolver

import com.xiaoyv.bangumi.shared.data.workflow.model.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionTemplateResolver
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ActionTemplateResolverTest {

    private val context = ActionExecutionContext(
        input = buildJsonObject {
            put("keyword", JsonPrimitive("芙莉莲"))
            put("score", JsonPrimitive(88.5))
        },
        variables = persistentMapOf(
            "index" to JsonPrimitive(0),
            "count" to JsonPrimitive(5),
            "title" to JsonPrimitive(" 葬送的芙莉莲 "),
            "cleanTitle" to JsonPrimitive("葬送的芙莉莲"),
            "emptyTitle" to JsonPrimitive(""),
            "blankTitle" to JsonPrimitive("   "),
            "tags" to buildJsonArray {
                add(JsonPrimitive("奇幻"))
                add(JsonPrimitive("冒险"))
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

    @Test
    fun testPathResolution() {
        assertEquals("芙莉莲", ActionTemplateResolver.evaluateExpression("input.keyword", context).jsonPrimitive.content)
        assertEquals(" 葬送的芙莉莲 ", ActionTemplateResolver.evaluateExpression("vars.title", context).jsonPrimitive.content)
        assertEquals("轻音少女", ActionTemplateResolver.evaluateExpression("loop.item.name", context).jsonPrimitive.content)
        assertEquals(0, ActionTemplateResolver.evaluateExpression("loop.index", context).jsonPrimitive.intOrNull)
        
        // Dot index and bracket index
        assertEquals("奇幻", ActionTemplateResolver.evaluateExpression("vars.tags.0", context).jsonPrimitive.content)
        assertEquals("冒险", ActionTemplateResolver.evaluateExpression("vars.tags[1]", context).jsonPrimitive.content)
    }

    @Test
    fun testArithmeticExpressions() {
        // Addition & Subtraction
        assertEquals(1, ActionTemplateResolver.evaluateExpression("loop.index + 1", context).jsonPrimitive.intOrNull)
        assertEquals(2, ActionTemplateResolver.evaluateExpression("vars.count - 3", context).jsonPrimitive.intOrNull)
        
        // Multiplication, Division, Modulo
        assertEquals(10, ActionTemplateResolver.evaluateExpression("vars.count * 2", context).jsonPrimitive.intOrNull)
        assertEquals(2.5, ActionTemplateResolver.evaluateExpression("vars.count / 2", context).jsonPrimitive.doubleOrNull)
        assertEquals(1, ActionTemplateResolver.evaluateExpression("vars.count % 2", context).jsonPrimitive.intOrNull)

        // Parentheses Precedence & Unary Negate
        assertEquals(12, ActionTemplateResolver.evaluateExpression("(vars.count + 1) * 2", context).jsonPrimitive.intOrNull)
        assertEquals(-5, ActionTemplateResolver.evaluateExpression("-vars.count", context).jsonPrimitive.intOrNull)
        
        // String Concatenation with +
        assertEquals("葬送的芙莉莲!", ActionTemplateResolver.evaluateExpression("vars.cleanTitle + '!'", context).jsonPrimitive.content)
    }

    @Test
    fun testComparisonExpressions() {
        // Greater than, Greater than or Equal
        assertFalse(ActionTemplateResolver.evaluateExpression("loop.index > 0", context).jsonPrimitive.booleanOrNull == true)
        assertTrue(ActionTemplateResolver.evaluateExpression("loop.index >= 0", context).jsonPrimitive.booleanOrNull == true)
        assertTrue(ActionTemplateResolver.evaluateExpression("vars.count >= 5", context).jsonPrimitive.booleanOrNull == true)
        assertTrue(ActionTemplateResolver.evaluateExpression("input.score > 80", context).jsonPrimitive.booleanOrNull == true)

        // Less than, Less than or Equal
        assertTrue(ActionTemplateResolver.evaluateExpression("loop.index < 5", context).jsonPrimitive.booleanOrNull == true)
        assertTrue(ActionTemplateResolver.evaluateExpression("vars.count <= 5", context).jsonPrimitive.booleanOrNull == true)

        // Equals & Not Equals (Number & String)
        assertTrue(ActionTemplateResolver.evaluateExpression("loop.index == 0", context).jsonPrimitive.booleanOrNull == true)
        assertTrue(ActionTemplateResolver.evaluateExpression("vars.count != 10", context).jsonPrimitive.booleanOrNull == true)
        assertTrue(ActionTemplateResolver.evaluateExpression("vars.cleanTitle == '葬送的芙莉莲'", context).jsonPrimitive.booleanOrNull == true)
        assertTrue(ActionTemplateResolver.evaluateExpression("vars.cleanTitle != '其他'", context).jsonPrimitive.booleanOrNull == true)
    }

    @Test
    fun testLogicalExpressions() {
        // AND, OR, NOT
        assertTrue(ActionTemplateResolver.evaluateExpression("loop.index == 0 && vars.count == 5", context).jsonPrimitive.booleanOrNull == true)
        assertTrue(ActionTemplateResolver.evaluateExpression("loop.index > 0 || vars.count == 5", context).jsonPrimitive.booleanOrNull == true)
        assertTrue(ActionTemplateResolver.evaluateExpression("!vars.emptyTitle", context).jsonPrimitive.booleanOrNull == true)
        assertFalse(ActionTemplateResolver.evaluateExpression("!(loop.index == 0)", context).jsonPrimitive.booleanOrNull == true)
    }

    @Test
    fun testElvisAndTernaryExpressions() {
        // Elvis operator
        assertEquals("默认标题", ActionTemplateResolver.evaluateExpression("vars.emptyTitle ?: '默认标题'", context).jsonPrimitive.content)
        assertEquals("退路", ActionTemplateResolver.evaluateExpression("vars.missingVar ?: '退路'", context).jsonPrimitive.content)
        assertEquals("葬送的芙莉莲", ActionTemplateResolver.evaluateExpression("vars.cleanTitle ?: '默认标题'", context).jsonPrimitive.content)

        // Ternary operator
        assertEquals("足够", ActionTemplateResolver.evaluateExpression("vars.count >= 5 ? '足够' : '不足'", context).jsonPrimitive.content)
        assertEquals("普通", ActionTemplateResolver.evaluateExpression("loop.index > 0 ? '优秀' : '普通'", context).jsonPrimitive.content)
    }

    @Test
    fun testMemberAccess() {
        // String members
        assertEquals(6, ActionTemplateResolver.evaluateExpression("vars.cleanTitle.length", context).jsonPrimitive.intOrNull)
        assertTrue(ActionTemplateResolver.evaluateExpression("vars.emptyTitle.isEmpty", context).jsonPrimitive.booleanOrNull == true)
        assertTrue(ActionTemplateResolver.evaluateExpression("vars.cleanTitle.isNotEmpty", context).jsonPrimitive.booleanOrNull == true)
        assertTrue(ActionTemplateResolver.evaluateExpression("vars.blankTitle.isBlank", context).jsonPrimitive.booleanOrNull == true)
        assertEquals("葬送的芙莉莲", ActionTemplateResolver.evaluateExpression("vars.title.trim", context).jsonPrimitive.content)

        // Array members
        assertEquals(2, ActionTemplateResolver.evaluateExpression("vars.tags.length", context).jsonPrimitive.intOrNull)
        assertEquals("奇幻", ActionTemplateResolver.evaluateExpression("vars.tags.first", context).jsonPrimitive.content)
        assertEquals("冒险", ActionTemplateResolver.evaluateExpression("vars.tags.last", context).jsonPrimitive.content)
        assertFalse(ActionTemplateResolver.evaluateExpression("vars.tags.isEmpty", context).jsonPrimitive.booleanOrNull == true)
    }

    @Test
    fun testResolveElementAndText() {
        // Raw boolean type preserved
        val rawBool = ActionTemplateResolver.resolveElement(JsonPrimitive("\${vars.count >= 5}"), context)
        assertEquals(true, rawBool.jsonPrimitive.booleanOrNull)

        // Raw number type preserved
        val rawNum = ActionTemplateResolver.resolveElement(JsonPrimitive("\${loop.index + 1}"), context)
        assertEquals(1, rawNum.jsonPrimitive.intOrNull)

        // Text string interpolation
        val text = "No.${ActionTemplateResolver.resolveText("\${loop.index + 1}", context)}: ${ActionTemplateResolver.resolveText("\${loop.item.name}", context)}"
        assertEquals("No.1: 轻音少女", text)
    }

    @Test
    fun testMalformedExpressionSafety() {
        // Missing property safely returns JsonNull without crashing
        val missingResult = ActionTemplateResolver.evaluateExpression("vars.non_existent_key", context)
        assertEquals(JsonNull, missingResult)

        // Invalid expression syntax falls back safely without throwing exception
        val malformedResult = ActionTemplateResolver.evaluateExpression("vars.count +++ ", context)
        assertEquals(JsonNull, malformedResult)
    }
}
