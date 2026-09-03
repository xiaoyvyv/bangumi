package com.xiaoyv.bangumi.shared.data.workflow.node.parse

import com.fleeksoft.ksoup.select.Selector
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHtmlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.fixture.ActionNodeTestFixtures
import com.xiaoyv.bangumi.shared.data.workflow.node.fixture.ActionNodeTestFixtures.config
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * HTML 解析与 DOM 抽取节点全面测试：
 * 包含完整 Ksoup 管道执行、高频增强节点 (remove, parent, children, table_to_json) 以及类型边界校验。
 */
class ActionHtmlNodesTest {

    private val definitions = ActionNodeTestFixtures.nodeDefinitions()

    @Test
    fun testKsoupHtmlPipeline() = runBlocking {
        var context = ActionExecutionContext()

        val sampleDocHtml = """
            <!DOCTYPE html>
            <html>
            <head><title>Bangumi Title</title></head>
            <body>
                <div id="container" class="main-box">
                    <a id="link-1" class="item active" href="https://bgm.tv/1">条目一</a>
                    <a id="link-2" class="item" href="https://bgm.tv/2">条目二</a>
                    <input id="search-input" value="芙莉莲" />
                    <script id="data-script">var config = { id: 100 };</script>
                </div>
            </body>
            </html>
        """.trimIndent()

        // 1. parse 节点解析文档
        val parseNode = ActionNode(
            id = "parse",
            type = ActionNodeType.HTML_PARSE,
            config = config(
                ActionHtmlConfigKey.HTML to JsonPrimitive(sampleDocHtml),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("doc"),
            ),
        )
        val parseResult = definitions.getValue(ActionNodeType.HTML_PARSE).executor.execute(parseNode, context)
        val docElement = parseResult.output.getValue("doc")
        context = context.copy(stepOutputs = (context.stepOutputs + ("parse" to JsonObject(parseResult.output))).toPersistentMap())
        assertTrue(docElement.jsonPrimitive.content.contains("id=\"container\""))

        // 2. select 节点提取全部 .item a 标签
        val selectNode = ActionNode(
            id = "select_items",
            type = ActionNodeType.HTML_SELECT,
            config = config(
                ActionHtmlConfigKey.SOURCE to JsonPrimitive("\${steps.parse.doc}"),
                ActionHtmlConfigKey.SELECTOR to JsonPrimitive("a.item"),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("items"),
            ),
        )
        val selectResult = definitions.getValue(ActionNodeType.HTML_SELECT).executor.execute(selectNode, context)
        context = context.copy(stepOutputs = (context.stepOutputs + ("select_items" to JsonObject(selectResult.output))).toPersistentMap())
        val itemsElements = selectResult.output.getValue("items")
        assertEquals(2, itemsElements.jsonArray.size)

        // 3. first 节点获取第一个元素
        val firstNode = ActionNode(
            id = "first_item",
            type = ActionNodeType.HTML_FIRST,
            config = config(
                ActionHtmlConfigKey.SOURCE to JsonPrimitive("\${steps.select_items.items}"),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("firstEl"),
            ),
        )
        val firstResult = definitions.getValue(ActionNodeType.HTML_FIRST).executor.execute(firstNode, context)
        context = context.copy(stepOutputs = (context.stepOutputs + ("first_item" to JsonObject(firstResult.output))).toPersistentMap())
        val firstElement = firstResult.output.getValue("firstEl").jsonPrimitive.content
        assertTrue(firstElement.contains("id=\"link-1\""))

        // 4. 对 firstElement 提取 tag, text, attr, id
        val tagNode = ActionNode(
            id = "get_tag",
            type = ActionNodeType.HTML_TAG,
            config = config(
                ActionHtmlConfigKey.SOURCE to JsonPrimitive("\${steps.first_item.firstEl}"),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("tag"),
            ),
        )
        val tagResult = definitions.getValue(ActionNodeType.HTML_TAG).executor.execute(tagNode, context)
        assertEquals("a", tagResult.output.getValue("tag").jsonPrimitive.content)

        val attrNode = ActionNode(
            id = "get_attr",
            type = ActionNodeType.HTML_ATTR,
            config = config(
                ActionHtmlConfigKey.SOURCE to JsonPrimitive("\${steps.first_item.firstEl}"),
                ActionHtmlConfigKey.ATTRIBUTE to JsonPrimitive("href"),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("href"),
            ),
        )
        val attrResult = definitions.getValue(ActionNodeType.HTML_ATTR).executor.execute(attrNode, context)
        assertEquals("https://bgm.tv/1", attrResult.output.getValue("href").jsonPrimitive.content)

        val textNode = ActionNode(
            id = "get_text",
            type = ActionNodeType.HTML_TEXT,
            config = config(
                ActionHtmlConfigKey.SOURCE to JsonPrimitive("\${steps.first_item.firstEl}"),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("text"),
            ),
        )
        val textResult = definitions.getValue(ActionNodeType.HTML_TEXT).executor.execute(textNode, context)
        assertEquals("条目一", textResult.output.getValue("text").jsonPrimitive.content)

        // 5. has_class 验证
        val hasClassNode = ActionNode(
            id = "has_class",
            type = ActionNodeType.HTML_HAS_CLASS,
            config = config(
                ActionHtmlConfigKey.SOURCE to JsonPrimitive("\${steps.first_item.firstEl}"),
                ActionHtmlConfigKey.CLASS_NAME to JsonPrimitive("active"),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("isActive"),
            ),
        )
        val hasClassResult = definitions.getValue(ActionNodeType.HTML_HAS_CLASS).executor.execute(hasClassNode, context)
        assertEquals(true, hasClassResult.output.getValue("isActive").jsonPrimitive.booleanOrNull)

        // 6. select_first 提取输入框并获取 value
        val selectInputNode = ActionNode(
            id = "select_input",
            type = ActionNodeType.HTML_SELECT_FIRST,
            config = config(
                ActionHtmlConfigKey.SOURCE to JsonPrimitive("\${steps.parse.doc}"),
                ActionHtmlConfigKey.SELECTOR to JsonPrimitive("#search-input"),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("inputEl"),
            ),
        )
        val selectInputResult = definitions.getValue(ActionNodeType.HTML_SELECT_FIRST).executor.execute(selectInputNode, context)
        context = context.copy(stepOutputs = (context.stepOutputs + ("select_input" to JsonObject(selectInputResult.output))).toPersistentMap())

        val valueNode = ActionNode(
            id = "get_value",
            type = ActionNodeType.HTML_VALUE,
            config = config(
                ActionHtmlConfigKey.SOURCE to JsonPrimitive("\${steps.select_input.inputEl}"),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("val"),
            ),
        )
        val valueResult = definitions.getValue(ActionNodeType.HTML_VALUE).executor.execute(valueNode, context)
        assertEquals("芙莉莲", valueResult.output.getValue("val").jsonPrimitive.content)

        // 7. 提取 script 标签并获取 data
        val selectScriptNode = ActionNode(
            id = "select_script",
            type = ActionNodeType.HTML_SELECT_FIRST,
            config = config(
                ActionHtmlConfigKey.SOURCE to JsonPrimitive("\${steps.parse.doc}"),
                ActionHtmlConfigKey.SELECTOR to JsonPrimitive("#data-script"),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("scriptEl"),
            ),
        )
        val selectScriptResult = definitions.getValue(ActionNodeType.HTML_SELECT_FIRST).executor.execute(selectScriptNode, context)
        context = context.copy(stepOutputs = (context.stepOutputs + ("select_script" to JsonObject(selectScriptResult.output))).toPersistentMap())

        val dataNode = ActionNode(
            id = "get_data",
            type = ActionNodeType.HTML_DATA,
            config = config(
                ActionHtmlConfigKey.SOURCE to JsonPrimitive("\${steps.select_script.scriptEl}"),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("data"),
            ),
        )
        val dataResult = definitions.getValue(ActionNodeType.HTML_DATA).executor.execute(dataNode, context)
        assertTrue(dataResult.output.getValue("data").jsonPrimitive.content.contains("id: 100"))

        // 8. map 批量映射 href 与 text
        val mapHrefNode = ActionNode(
            id = "map_hrefs",
            type = ActionNodeType.HTML_MAP,
            config = config(
                ActionHtmlConfigKey.SOURCE to JsonPrimitive("\${steps.select_items.items}"),
                ActionHtmlConfigKey.OPERATION to JsonPrimitive("attr"),
                ActionHtmlConfigKey.ATTRIBUTE to JsonPrimitive("href"),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("hrefs"),
            ),
        )
        val mapHrefResult = definitions.getValue(ActionNodeType.HTML_MAP).executor.execute(mapHrefNode, context)
        val hrefsArray = mapHrefResult.output.getValue("hrefs").jsonArray.map { it.jsonPrimitive.content }
        assertEquals(listOf("https://bgm.tv/1", "https://bgm.tv/2"), hrefsArray)

        // 9. id, html, outer_html 提取
        val idNode = ActionNode(
            id = "get_id",
            type = ActionNodeType.HTML_ID,
            config = config(
                ActionHtmlConfigKey.SOURCE to JsonPrimitive("\${steps.first_item.firstEl}"),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("elId"),
            ),
        )
        val idResult = definitions.getValue(ActionNodeType.HTML_ID).executor.execute(idNode, context)
        assertEquals("link-1", idResult.output.getValue("elId").jsonPrimitive.content)

        val htmlNode = ActionNode(
            id = "get_inner_html",
            type = ActionNodeType.HTML_HTML,
            config = config(
                ActionHtmlConfigKey.SOURCE to JsonPrimitive("\${steps.first_item.firstEl}"),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("inner"),
            ),
        )
        val htmlResult = definitions.getValue(ActionNodeType.HTML_HTML).executor.execute(htmlNode, context)
        assertEquals("条目一", htmlResult.output.getValue("inner").jsonPrimitive.content)

        val outerHtmlNode = ActionNode(
            id = "get_outer_html",
            type = ActionNodeType.HTML_OUTER_HTML,
            config = config(
                ActionHtmlConfigKey.SOURCE to JsonPrimitive("\${steps.first_item.firstEl}"),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("outer"),
            ),
        )
        val outerHtmlResult = definitions.getValue(ActionNodeType.HTML_OUTER_HTML).executor.execute(outerHtmlNode, context)
        assertTrue(outerHtmlResult.output.getValue("outer").jsonPrimitive.content.contains("<a id=\"link-1\""))

        // 10. last, get, size 集合操作
        val lastNode = ActionNode(
            id = "get_last",
            type = ActionNodeType.HTML_LAST,
            config = config(
                ActionHtmlConfigKey.SOURCE to JsonPrimitive("\${steps.select_items.items}"),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("lastEl"),
            ),
        )
        val lastResult = definitions.getValue(ActionNodeType.HTML_LAST).executor.execute(lastNode, context)
        val lastElement = lastResult.output.getValue("lastEl").jsonPrimitive.content
        assertTrue(lastElement.contains("link-2"))

        val getNode = ActionNode(
            id = "get_by_index",
            type = ActionNodeType.HTML_GET,
            config = config(
                ActionHtmlConfigKey.SOURCE to JsonPrimitive("\${steps.select_items.items}"),
                ActionHtmlConfigKey.INDEX to JsonPrimitive(1),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("getEl"),
            ),
        )
        val getResult = definitions.getValue(ActionNodeType.HTML_GET).executor.execute(getNode, context)
        val getElement = getResult.output.getValue("getEl").jsonPrimitive.content
        assertTrue(getElement.contains("link-2"))

        val sizeNode = ActionNode(
            id = "get_size",
            type = ActionNodeType.HTML_SIZE,
            config = config(
                ActionHtmlConfigKey.SOURCE to JsonPrimitive("\${steps.select_items.items}"),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("count"),
            ),
        )
        val sizeResult = definitions.getValue(ActionNodeType.HTML_SIZE).executor.execute(sizeNode, context)
        assertEquals(2, sizeResult.output.getValue("count").jsonPrimitive.intOrNull)

        // 11. 新增的高频节点：remove, parent, children
        val removeNode = ActionNode(
            id = "remove_script",
            type = ActionNodeType.HTML_REMOVE,
            config = config(
                ActionHtmlConfigKey.SOURCE to JsonPrimitive("\${steps.parse.doc}"),
                ActionHtmlConfigKey.SELECTOR to JsonPrimitive("script"),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("cleanedDoc"),
            ),
        )
        val removeResult = definitions.getValue(ActionNodeType.HTML_REMOVE).executor.execute(removeNode, context)
        val cleanedHtml = removeResult.output.getValue("cleanedDoc").jsonPrimitive.content
        assertFalse(cleanedHtml.contains("<script"))

        val parentNode = ActionNode(
            id = "get_parent",
            type = ActionNodeType.HTML_PARENT,
            config = config(
                ActionHtmlConfigKey.SOURCE to JsonPrimitive("<span id=\"inner\">text</span>"),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("parent"),
            ),
        )
        val parentResult = definitions.getValue(ActionNodeType.HTML_PARENT).executor.execute(parentNode, context)
        assertTrue(parentResult.output.getValue("parent").jsonPrimitive.content.contains("<span id=\"inner\">"))

        val childrenNode = ActionNode(
            id = "get_children",
            type = ActionNodeType.HTML_CHILDREN,
            config = config(
                ActionHtmlConfigKey.SOURCE to JsonPrimitive("<ul id='list'><li>A</li><li>B</li></ul>"),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("children"),
            ),
        )
        val childrenResult = definitions.getValue(ActionNodeType.HTML_CHILDREN).executor.execute(childrenNode, context)
        assertEquals(2, childrenResult.output.getValue("children").jsonArray.size)

        // 12. table_to_json 表格解析
        val tableNode = ActionNode(
            id = "table_to_json",
            type = ActionNodeType.HTML_TABLE_TO_JSON,
            config = config(
                ActionHtmlConfigKey.HTML to JsonPrimitive("<table><tr><th>科目</th><th>得分</th></tr><tr><td>语文</td><td>95</td></tr></table>"),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("rows"),
            ),
        )
        val tableResult = definitions.getValue(ActionNodeType.HTML_TABLE_TO_JSON).executor.execute(tableNode, context)
        val rows = tableResult.output.getValue("rows").jsonArray
        assertEquals(1, rows.size)
        assertEquals("语文", rows[0].jsonObject["科目"]?.jsonPrimitive?.content)
        assertEquals("95", rows[0].jsonObject["得分"]?.jsonPrimitive?.content)
    }

    @Test
    fun testKsoupTypeValidation() {
        runBlocking {
            val context = ActionExecutionContext()

            // 1. html.parse 传入非字符串必须抛出异常
            val invalidParseNode = ActionNode(
                id = "invalid_parse",
                type = ActionNodeType.HTML_PARSE,
                config = config(
                    ActionHtmlConfigKey.HTML to JsonPrimitive(12345),
                    ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("result"),
                ),
            )
            assertFailsWith<IllegalStateException> {
                definitions.getValue(ActionNodeType.HTML_PARSE).executor.execute(invalidParseNode, context)
            }

            // 2. html.select 传入非字符串/非 Element 必须抛出异常
            val invalidSelectNode = ActionNode(
                id = "invalid_select",
                type = ActionNodeType.HTML_SELECT,
                config = config(
                    ActionHtmlConfigKey.SOURCE to JsonPrimitive(12345),
                    ActionHtmlConfigKey.SELECTOR to JsonPrimitive("a"),
                    ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("result"),
                ),
            )
            assertFailsWith<IllegalStateException> {
                definitions.getValue(ActionNodeType.HTML_SELECT).executor.execute(invalidSelectNode, context)
            }

            // 2.1 html.select 传入纯 HTML 字符串自动解析并成功执行
            val autoParseSelectNode = ActionNode(
                id = "auto_parse_select",
                type = ActionNodeType.HTML_SELECT,
                config = config(
                    ActionHtmlConfigKey.SOURCE to JsonPrimitive("<div><a class='tag'>link</a></div>"),
                    ActionHtmlConfigKey.SELECTOR to JsonPrimitive("a.tag"),
                    ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("result"),
                ),
            )
            val autoParseResult = definitions.getValue(ActionNodeType.HTML_SELECT).executor.execute(autoParseSelectNode, context)
            assertEquals(1, autoParseResult.output.getValue("result").jsonArray.size)

            // 3. html.map 传入非字符串/非数组必须抛出异常
            val invalidMapNode = ActionNode(
                id = "invalid_map",
                type = ActionNodeType.HTML_MAP,
                config = config(
                    ActionHtmlConfigKey.SOURCE to JsonPrimitive(12345),
                    ActionHtmlConfigKey.OPERATION to JsonPrimitive("text"),
                    ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("result"),
                ),
            )
            assertFailsWith<IllegalStateException> {
                definitions.getValue(ActionNodeType.HTML_MAP).executor.execute(invalidMapNode, context)
            }
        }
    }

    @Test
    fun testHtmlSelectorInvalidThrowsException() {
        val definition = definitions.getValue(ActionNodeType.HTML_SELECT)
        val node = ActionNode(
            id = "html_select_invalid_selector",
            type = ActionNodeType.HTML_SELECT,
            config = config(
                ActionHtmlConfigKey.SOURCE to JsonPrimitive("<div>hello</div>"),
                ActionHtmlConfigKey.SELECTOR to JsonPrimitive("div[invalid==="),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("result"),
            ),
        )
        assertFailsWith<Selector.SelectorParseException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }
    }

    @Test
    fun testHtmlMapUnsupportedOperationThrowsException() {
        val definition = definitions.getValue(ActionNodeType.HTML_MAP)
        val sampleElements = JsonArray(listOf(JsonPrimitive("<div>a</div>")))
        val node = ActionNode(
            id = "html_map_unsupported_op",
            type = ActionNodeType.HTML_MAP,
            config = config(
                ActionHtmlConfigKey.SOURCE to sampleElements,
                ActionHtmlConfigKey.OPERATION to JsonPrimitive("unsupported_op"),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("result"),
            ),
        )
        assertFailsWith<IllegalStateException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }
    }

    @Test
    fun testHtmlFirstNonElementsThrowsException() {
        val definition = definitions.getValue(ActionNodeType.HTML_FIRST)
        val node = ActionNode(
            id = "html_first_non_elements",
            type = ActionNodeType.HTML_FIRST,
            config = config(
                ActionHtmlConfigKey.SOURCE to JsonPrimitive(12345),
                ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive("result"),
            ),
        )
        assertFailsWith<IllegalStateException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }
    }
}
