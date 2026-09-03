package com.xiaoyv.bangumi.shared.data.workflow.node.fixture

import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionBilibiliConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionClipboardConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCodecConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionConfirmConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCryptoConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCsvConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDateConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHtmlConfigKey
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
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionProgressDialogConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionProgressDialogMode
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectDialogConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionShareConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionStorageConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSyncCookieConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionToastConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionUrlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionVideoPreviewConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionXmlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.builtInActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeDefinition
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionHttpRequestEffect
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionHttpDownloadResponse
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionHttpRequestExecutor
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionWorkflowPreferencesStore
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject

/**
 * 节点单元测试公共桩与基础规范配置。
 */
object ActionNodeTestFixtures {
    const val OUTPUT_KEY = "result"

    const val SAMPLE_HTML =
        "<html><head><title>Title</title><meta name=\"description\" content=\"Description\"></head><body><h1>Value</h1><a href=\"https://example.com\">Link</a></body></html>"

    val sampleElement = JsonPrimitive("<a id=\"link-1\" class=\"active\" href=\"https://bgm.tv\">Bangumi</a>")

    val sampleElements = JsonArray(
        listOf(
            sampleElement,
            JsonPrimitive("<a id=\"link-2\" class=\"active\" href=\"https://bgm.tv/2\">Bangumi 2</a>"),
        )
    )

    val sampleArray = JsonArray(listOf(JsonPrimitive("value"), JsonPrimitive("other")))

    val testHttpRequestExecutor = object : ActionHttpRequestExecutor {
        override suspend fun execute(request: ActionHttpRequestEffect) = buildJsonObject {
            put(ActionHttpResponseKey.STATUS_CODE, JsonPrimitive(200))
            put(ActionHttpResponseKey.IS_SUCCESS, JsonPrimitive(true))
            put(ActionHttpResponseKey.BODY, JsonObject(mapOf("ok" to JsonPrimitive(true))))
        }

        override suspend fun download(
            request: ActionHttpRequestEffect,
            onResponse: suspend (ActionHttpDownloadResponse) -> Unit,
            consumeChunk: suspend (ByteArray) -> Unit,
        ) = ActionHttpDownloadResponse(
            statusCode = 200,
            contentType = "application/octet-stream",
            contentDisposition = "attachment; filename=download.bin",
        )
    }

    val testPreferences = mutableMapOf<String, JsonElement>()

    val testPreferencesStore = object : ActionWorkflowPreferencesStore {
        override suspend fun get(key: String): JsonElement? = testPreferences[key]

        override suspend fun set(key: String, value: JsonElement) {
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

    fun nodeDefinitions(): Map<String, ActionNodeDefinition> =
        builtInActionNodeDefinitions(testHttpRequestExecutor, testPreferencesStore).associateBy { it.spec.type }

    fun config(vararg values: Pair<String, JsonElement>): JsonObject = buildJsonObject {
        values.forEach { (key, value) -> put(key, value) }
    }

    fun binaryConfig(): JsonObject = config(
        ActionControlConfigKey.LEFT to JsonPrimitive(2),
        ActionControlConfigKey.RIGHT to JsonPrimitive(1),
    )

    fun mathConfig(): JsonObject = config(
        ActionMathConfigKey.LEFT to JsonPrimitive(2),
        ActionMathConfigKey.RIGHT to JsonPrimitive(1),
        ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
    )

    fun htmlConfig(): JsonObject = config(
        ActionHtmlConfigKey.HTML to JsonPrimitive(SAMPLE_HTML),
        ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
    )

    fun textConfig(): JsonObject = config(
        ActionTextConfigKey.TEXT to JsonPrimitive("Value"),
        ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
    )

    fun delimiterTextConfig(): JsonObject = config(
        ActionTextConfigKey.TEXT to JsonPrimitive("left:right"),
        ActionTextConfigKey.DELIMITER to JsonPrimitive(":"),
        ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
    )

    fun replaceTextConfig(): JsonObject = config(
        ActionTextConfigKey.TEXT to JsonPrimitive("value"),
        ActionTextConfigKey.PATTERN to JsonPrimitive("value"),
        ActionTextConfigKey.REPLACEMENT to JsonPrimitive("result"),
        ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
    )

    fun arrayConfig(): JsonObject = config(
        ActionArrayConfigKey.VALUES to sampleArray,
        ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
    )

    val canonicalConfigs: Map<String, JsonObject> = mapOf(
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
            ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.SET_VARIABLE to config(
            ActionDataConfigKey.KEY to JsonPrimitive(OUTPUT_KEY),
            ActionDataConfigKey.VALUE to JsonPrimitive("value"),
        ),
        ActionNodeType.DATA_COALESCE to arrayConfig(),
        ActionNodeType.DATA_CONCAT to arrayConfig(),
        ActionNodeType.DATA_MERGE to config(
            ActionDataConfigKey.OBJECTS to JsonArray(listOf(JsonObject(mapOf("value" to JsonPrimitive(true))))),
            ActionDataConfigKey.MERGE_STRATEGY to JsonPrimitive("shallow"),
            ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.DATA_ASSIGN to config(
            ActionDataConfigKey.OBJECT to JsonObject(emptyMap()),
            ActionDataConfigKey.ASSIGNMENTS to JsonObject(mapOf("$.value" to JsonPrimitive(true))),
            ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.DATA_REMOVE to config(
            ActionDataConfigKey.OBJECT to JsonObject(mapOf("value" to JsonPrimitive(true))),
            ActionDataConfigKey.PATH to JsonPrimitive("$.value"),
            ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.DATA_RENAME to config(
            ActionDataConfigKey.OBJECT to JsonObject(mapOf("value" to JsonPrimitive(true))),
            ActionDataConfigKey.FROM_PATH to JsonPrimitive("$.value"),
            ActionDataConfigKey.TO_PATH to JsonPrimitive("$.renamed"),
            ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.DATA_PICK to config(
            ActionDataConfigKey.OBJECT to JsonObject(mapOf("value" to JsonPrimitive(true))),
            ActionDataConfigKey.PATHS to JsonArray(listOf(JsonPrimitive("$.value"))),
            ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.DATA_UUID to config(
            ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.DATA_TO_NUMBER to config(
            ActionDataConfigKey.VALUE to JsonPrimitive("123"),
            ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.DATA_TO_STRING to config(
            ActionDataConfigKey.VALUE to JsonPrimitive(123),
            ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.DATA_TO_BOOLEAN to config(
            ActionDataConfigKey.VALUE to JsonPrimitive("true"),
            ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.DATA_TYPE_OF to config(
            ActionDataConfigKey.VALUE to JsonPrimitive("test"),
            ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.XML_PARSE to config(
            ActionXmlConfigKey.TEXT to JsonPrimitive("<rss><title>Bangumi</title></rss>"),
            ActionXmlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.XML_STRINGIFY to config(
            ActionXmlConfigKey.DATA to JsonObject(mapOf("title" to JsonPrimitive("Bangumi"))),
            ActionXmlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.HTML_PARSE to htmlConfig(),
        ActionNodeType.HTML_SELECT to config(
            ActionHtmlConfigKey.SOURCE to sampleElement,
            ActionHtmlConfigKey.SELECTOR to JsonPrimitive("a"),
            ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.HTML_SELECT_FIRST to config(
            ActionHtmlConfigKey.SOURCE to sampleElement,
            ActionHtmlConfigKey.SELECTOR to JsonPrimitive("a"),
            ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.HTML_ATTR to config(
            ActionHtmlConfigKey.SOURCE to sampleElement,
            ActionHtmlConfigKey.ATTRIBUTE to JsonPrimitive("href"),
            ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.HTML_TAG to config(
            ActionHtmlConfigKey.SOURCE to sampleElement,
            ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.HTML_TEXT to config(
            ActionHtmlConfigKey.SOURCE to sampleElement,
            ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.HTML_DATA to config(
            ActionHtmlConfigKey.SOURCE to sampleElement,
            ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.HTML_VALUE to config(
            ActionHtmlConfigKey.SOURCE to sampleElement,
            ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.HTML_ID to config(
            ActionHtmlConfigKey.SOURCE to sampleElement,
            ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.HTML_HTML to config(
            ActionHtmlConfigKey.SOURCE to sampleElement,
            ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.HTML_OUTER_HTML to config(
            ActionHtmlConfigKey.SOURCE to sampleElement,
            ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.HTML_MAP to config(
            ActionHtmlConfigKey.SOURCE to sampleElements,
            ActionHtmlConfigKey.OPERATION to JsonPrimitive("attr"),
            ActionHtmlConfigKey.ATTRIBUTE to JsonPrimitive("href"),
            ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.HTML_FIRST to config(
            ActionHtmlConfigKey.SOURCE to sampleElements,
            ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.HTML_LAST to config(
            ActionHtmlConfigKey.SOURCE to sampleElements,
            ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.HTML_GET to config(
            ActionHtmlConfigKey.SOURCE to sampleElements,
            ActionHtmlConfigKey.INDEX to JsonPrimitive(0),
            ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.HTML_SIZE to config(
            ActionHtmlConfigKey.SOURCE to sampleElements,
            ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.HTML_HAS_CLASS to config(
            ActionHtmlConfigKey.SOURCE to sampleElement,
            ActionHtmlConfigKey.CLASS_NAME to JsonPrimitive("active"),
            ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.HTML_REMOVE to config(
            ActionHtmlConfigKey.SOURCE to sampleElement,
            ActionHtmlConfigKey.SELECTOR to JsonPrimitive("span"),
            ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.HTML_PARENT to config(
            ActionHtmlConfigKey.SOURCE to sampleElement,
            ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.HTML_CHILDREN to config(
            ActionHtmlConfigKey.SOURCE to sampleElement,
            ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.OBJECT_GET to config(
            ActionObjectConfigKey.OBJECT to JsonObject(mapOf("value" to JsonPrimitive("value"))),
            ActionObjectConfigKey.PATH to JsonPrimitive("$.value"),
            ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.OBJECT_SET to config(
            ActionObjectConfigKey.OBJECT to JsonObject(emptyMap()),
            ActionObjectConfigKey.KEY to JsonPrimitive("value"),
            ActionObjectConfigKey.VALUE to JsonPrimitive("value"),
            ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.OBJECT_REMOVE to config(
            ActionObjectConfigKey.OBJECT to JsonObject(mapOf("value" to JsonPrimitive("value"))),
            ActionObjectConfigKey.KEY to JsonPrimitive("value"),
            ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.OBJECT_OMIT to config(
            ActionObjectConfigKey.OBJECT to JsonObject(mapOf("a" to JsonPrimitive(1), "b" to JsonPrimitive(2))),
            ActionObjectConfigKey.KEYS to JsonArray(listOf(JsonPrimitive("b"))),
            ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.OBJECT_PICK to config(
            ActionObjectConfigKey.OBJECT to JsonObject(mapOf("a" to JsonPrimitive(1), "b" to JsonPrimitive(2))),
            ActionObjectConfigKey.KEYS to JsonArray(listOf(JsonPrimitive("a"))),
            ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.OBJECT_MERGE to config(
            ActionObjectConfigKey.OBJECTS to JsonArray(listOf(JsonObject(mapOf("value" to JsonPrimitive("value"))))),
            ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.OBJECT_KEYS to config(
            ActionObjectConfigKey.OBJECT to JsonObject(mapOf("a" to JsonPrimitive("1"))),
            ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.OBJECT_VALUES to config(
            ActionObjectConfigKey.OBJECT to JsonObject(mapOf("a" to JsonPrimitive("1"))),
            ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.OBJECT_ENTRIES to config(
            ActionObjectConfigKey.OBJECT to JsonObject(mapOf("a" to JsonPrimitive("1"))),
            ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.OBJECT_FROM_ENTRIES to config(
            ActionObjectConfigKey.ENTRIES to JsonArray(listOf(JsonArray(listOf(JsonPrimitive("a"), JsonPrimitive(1))))),
            ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.OBJECT_HAS_KEY to config(
            ActionObjectConfigKey.OBJECT to JsonObject(mapOf("a" to JsonPrimitive("1"))),
            ActionObjectConfigKey.KEY to JsonPrimitive("a"),
            ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.OBJECT_IS_EMPTY to config(
            ActionObjectConfigKey.OBJECT to JsonObject(emptyMap()),
            ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.JSON_EXTRACT to config(
            ActionJsonConfigKey.SOURCE to JsonObject(mapOf("value" to JsonPrimitive("value"))),
            ActionJsonConfigKey.PATH to JsonPrimitive("$.value"),
            ActionJsonConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.JSON_PARSE to config(
            ActionJsonConfigKey.TEXT to JsonPrimitive("{\"value\":true}"),
            ActionJsonConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.JSON_STRINGIFY to config(
            ActionJsonConfigKey.VALUE to JsonPrimitive("value"),
            ActionJsonConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.TEXT_LENGTH to textConfig(),
        ActionNodeType.TEXT_TRIM to textConfig(),
        ActionNodeType.TEXT_LOWERCASE to textConfig(),
        ActionNodeType.TEXT_UPPERCASE to textConfig(),
        ActionNodeType.TEXT_CAPITALIZE to textConfig(),
        ActionNodeType.TEXT_REPEAT to config(
            ActionTextConfigKey.TEXT to JsonPrimitive("hi"),
            ActionTextConfigKey.COUNT to JsonPrimitive(2),
            ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.TEXT_REVERSE to textConfig(),
        ActionNodeType.TEXT_INDEX_OF to config(
            ActionTextConfigKey.TEXT to JsonPrimitive("hello"),
            ActionTextConfigKey.PATTERN to JsonPrimitive("ll"),
            ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.TEXT_TEMPLATE to config(
            ActionTextConfigKey.TEMPLATE to JsonPrimitive("Hello \${name}"),
            ActionTextConfigKey.OBJECT to JsonObject(mapOf("name" to JsonPrimitive("World"))),
            ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.TEXT_SPLIT to config(
            ActionTextConfigKey.TEXT to JsonPrimitive("a,b"),
            ActionTextConfigKey.DELIMITER to JsonPrimitive(","),
            ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.TEXT_REGEX_MATCH to config(
            ActionTextConfigKey.TEXT to JsonPrimitive("tag-1"),
            ActionTextConfigKey.PATTERN to JsonPrimitive("tag-(\\d+)"),
            ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.TEXT_SUBSTRING to config(
            ActionTextConfigKey.TEXT to JsonPrimitive("value"),
            ActionTextConfigKey.START_INDEX to JsonPrimitive(1),
            ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.TEXT_SUBSTRING_BEFORE to delimiterTextConfig(),
        ActionNodeType.TEXT_SUBSTRING_AFTER to delimiterTextConfig(),
        ActionNodeType.TEXT_REPLACE to replaceTextConfig(),
        ActionNodeType.TEXT_REPLACE_REGEX to replaceTextConfig(),
        ActionNodeType.TEXT_JOIN to arrayConfig(),
        ActionNodeType.ARRAY_LENGTH to arrayConfig(),
        ActionNodeType.ARRAY_CREATE to arrayConfig(),
        ActionNodeType.ARRAY_APPEND to config(
            ActionArrayConfigKey.VALUES to sampleArray,
            ActionArrayConfigKey.VALUE to JsonPrimitive("value"),
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_INSERT_AT to config(
            ActionArrayConfigKey.VALUES to sampleArray,
            ActionArrayConfigKey.INDEX to JsonPrimitive(0),
            ActionArrayConfigKey.VALUE to JsonPrimitive("value"),
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_REMOVE_AT to config(
            ActionArrayConfigKey.VALUES to sampleArray,
            ActionArrayConfigKey.INDEX to JsonPrimitive(0),
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_FILTER to config(
            ActionArrayConfigKey.VALUES to sampleArray,
            ActionArrayConfigKey.OPERATOR to JsonPrimitive("equals"),
            ActionArrayConfigKey.EXPECTED to JsonPrimitive("value"),
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_MAP to config(
            ActionArrayConfigKey.VALUES to sampleArray,
            ActionArrayConfigKey.FIELD_PATH to JsonPrimitive("$"),
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_FLAT_MAP to config(
            ActionArrayConfigKey.VALUES to sampleArray,
            ActionArrayConfigKey.FIELD_PATH to JsonPrimitive("$"),
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_CONCAT to config(
            ActionArrayConfigKey.VALUES to JsonArray(listOf(sampleArray, sampleArray)),
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_ZIP to config(
            ActionArrayConfigKey.VALUES to sampleArray,
            ActionArrayConfigKey.OTHER_VALUES to sampleArray,
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_TAKE to config(
            ActionArrayConfigKey.VALUES to sampleArray,
            ActionArrayConfigKey.COUNT to JsonPrimitive(1),
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_DROP to config(
            ActionArrayConfigKey.VALUES to sampleArray,
            ActionArrayConfigKey.COUNT to JsonPrimitive(1),
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_CONTAINS to config(
            ActionArrayConfigKey.VALUES to sampleArray,
            ActionArrayConfigKey.VALUE to JsonPrimitive("value"),
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_FIND to config(
            ActionArrayConfigKey.VALUES to sampleArray,
            ActionArrayConfigKey.FIELD_PATH to JsonPrimitive("$"),
            ActionArrayConfigKey.EXPECTED to JsonPrimitive("value"),
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_DISTINCT to arrayConfig(),
        ActionNodeType.ARRAY_SORT to arrayConfig(),
        ActionNodeType.ARRAY_REVERSE to arrayConfig(),
        ActionNodeType.ARRAY_SLICE to arrayConfig(),
        ActionNodeType.ARRAY_FLATTEN to config(
            ActionArrayConfigKey.VALUES to JsonArray(listOf(sampleArray)),
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
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
            ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.MATH_SUM to config(
            ActionMathConfigKey.VALUES to JsonArray(listOf(JsonPrimitive(1), JsonPrimitive(2), JsonPrimitive(3))),
            ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.MATH_AVG to config(
            ActionMathConfigKey.VALUES to JsonArray(listOf(JsonPrimitive(1), JsonPrimitive(2), JsonPrimitive(3))),
            ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.MATH_LOG to config(
            ActionMathConfigKey.VALUE to JsonPrimitive(10),
            ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.MATH_EXP to config(
            ActionMathConfigKey.VALUE to JsonPrimitive(1),
            ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.MATH_NEGATE to config(
            ActionMathConfigKey.VALUE to JsonPrimitive(1),
            ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
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
        ActionNodeType.VIDEO_PREVIEW to config(
            ActionVideoPreviewConfigKey.URL to JsonPrimitive("https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/uni-app-video-courses.mp4"),
            ActionVideoPreviewConfigKey.HEADERS to buildJsonObject {
                put("User-Agent", JsonPrimitive("Mozilla/5.0"))
            },
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
            ActionFlowConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.FLOW_RETURN to config(
            ActionFlowConfigKey.OUTPUT to JsonPrimitive("result"),
        ),
        ActionNodeType.FLOW_PARALLEL to config(),
        ActionNodeType.FLOW_JOIN to config(
            ActionFlowConfigKey.VALUES to sampleArray,
            ActionFlowConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
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
        ActionNodeType.DATE_NOW to config(
            ActionDateConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.DATE_GET_COMPONENT to config(
            ActionDateConfigKey.TIMESTAMP to JsonPrimitive(1700000000000L),
            ActionDateConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.DATE_FORMAT to config(
            ActionDateConfigKey.TIMESTAMP to JsonPrimitive(1700000000000L),
            ActionDateConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.DATE_PARSE to config(
            ActionDateConfigKey.TEXT to JsonPrimitive("2026-09-01T04:15:01Z"),
            ActionDateConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.CODEC_BASE64_ENCODE to config(
            ActionCodecConfigKey.TEXT to JsonPrimitive("hello"),
            ActionCodecConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.CODEC_BASE64_DECODE to config(
            ActionCodecConfigKey.TEXT to JsonPrimitive("aGVsbG8="),
            ActionCodecConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.CODEC_BASE64_URL_ENCODE to config(
            ActionCodecConfigKey.TEXT to JsonPrimitive("hello?world"),
            ActionCodecConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.CODEC_BASE64_URL_DECODE to config(
            ActionCodecConfigKey.TEXT to JsonPrimitive("aGVsbG8_d29ybGQ="),
            ActionCodecConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.CODEC_HEX_ENCODE to config(
            ActionCodecConfigKey.TEXT to JsonPrimitive("hello"),
            ActionCodecConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.CODEC_HEX_DECODE to config(
            ActionCodecConfigKey.TEXT to JsonPrimitive("68656c6c6f"),
            ActionCodecConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.CODEC_URL_ENCODE to config(
            ActionCodecConfigKey.TEXT to JsonPrimitive("hello world"),
            ActionCodecConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.CODEC_URL_DECODE to config(
            ActionCodecConfigKey.TEXT to JsonPrimitive("hello+world"),
            ActionCodecConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.CODEC_HTML_ESCAPE to config(
            ActionCodecConfigKey.TEXT to JsonPrimitive("<div>hello & world</div>"),
            ActionCodecConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.CODEC_HTML_UNESCAPE to config(
            ActionCodecConfigKey.TEXT to JsonPrimitive("&lt;div&gt;hello &amp; world&lt;/div&gt;"),
            ActionCodecConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.CRYPTO_HASH to config(
            ActionCryptoConfigKey.TEXT to JsonPrimitive("hello"),
            ActionCryptoConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.CRYPTO_HMAC to config(
            ActionCryptoConfigKey.TEXT to JsonPrimitive("hello"),
            ActionCryptoConfigKey.SECRET to JsonPrimitive("secret"),
            ActionCryptoConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.CRYPTO_ENCRYPT to config(
            ActionCryptoConfigKey.TEXT to JsonPrimitive("hello"),
            ActionCryptoConfigKey.SECRET_KEY to JsonPrimitive("secret"),
            ActionCryptoConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.CRYPTO_DECRYPT to config(
            ActionCryptoConfigKey.TEXT to JsonPrimitive("68656c6c6f"),
            ActionCryptoConfigKey.SECRET_KEY to JsonPrimitive("secret"),
            ActionCryptoConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.CRYPTO_RANDOM_BYTES to config(
            ActionCryptoConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.CRYPTO_UUID to config(
            ActionCryptoConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_SUM to config(
            ActionArrayConfigKey.VALUES to sampleArray,
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_AVG to config(
            ActionArrayConfigKey.VALUES to sampleArray,
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_MIN to config(
            ActionArrayConfigKey.VALUES to sampleArray,
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_MAX to config(
            ActionArrayConfigKey.VALUES to sampleArray,
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_CHUNK to config(
            ActionArrayConfigKey.VALUES to sampleArray,
            ActionArrayConfigKey.SIZE to JsonPrimitive(2),
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_SHUFFLE to config(
            ActionArrayConfigKey.VALUES to sampleArray,
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_SAMPLE to config(
            ActionArrayConfigKey.VALUES to sampleArray,
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.DATE_ADD to config(
            ActionDateConfigKey.TIMESTAMP to JsonPrimitive(1700000000000L),
            ActionDateConfigKey.COUNT to JsonPrimitive(1),
            ActionDateConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.DATE_SUBTRACT to config(
            ActionDateConfigKey.TIMESTAMP to JsonPrimitive(1700000000000L),
            ActionDateConfigKey.COUNT to JsonPrimitive(1),
            ActionDateConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.DATE_DIFF to config(
            ActionDateConfigKey.TIMESTAMP_LEFT to JsonPrimitive(1700000000000L),
            ActionDateConfigKey.TIMESTAMP_RIGHT to JsonPrimitive(1600000000000L),
            ActionDateConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.DATE_RELATIVE_TIME to config(
            ActionDateConfigKey.TIMESTAMP to JsonPrimitive(1700000000000L),
            ActionDateConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.MATH_ROUND to config(
            ActionMathConfigKey.VALUE to JsonPrimitive(1.5),
            ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.MATH_FLOOR to config(
            ActionMathConfigKey.VALUE to JsonPrimitive(1.5),
            ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.MATH_CEIL to config(
            ActionMathConfigKey.VALUE to JsonPrimitive(1.5),
            ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.MATH_ABS to config(
            ActionMathConfigKey.VALUE to JsonPrimitive(-1.5),
            ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.MATH_RANDOM to config(
            ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.MATH_CLAMP to config(
            ActionMathConfigKey.VALUE to JsonPrimitive(5),
            ActionMathConfigKey.MIN to JsonPrimitive(1),
            ActionMathConfigKey.MAX to JsonPrimitive(10),
            ActionMathConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.TEXT_CONTAINS to config(
            ActionTextConfigKey.TEXT to JsonPrimitive("hello world"),
            ActionTextConfigKey.PATTERN to JsonPrimitive("world"),
            ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.TEXT_STARTS_WITH to config(
            ActionTextConfigKey.TEXT to JsonPrimitive("hello world"),
            ActionTextConfigKey.PATTERN to JsonPrimitive("hello"),
            ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.TEXT_ENDS_WITH to config(
            ActionTextConfigKey.TEXT to JsonPrimitive("hello world"),
            ActionTextConfigKey.PATTERN to JsonPrimitive("world"),
            ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.TEXT_SLUGIFY to config(
            ActionTextConfigKey.TEXT to JsonPrimitive("Hello World!"),
            ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.TEXT_TRUNCATE to config(
            ActionTextConfigKey.TEXT to JsonPrimitive("Hello World!"),
            ActionTextConfigKey.LIMIT to JsonPrimitive(5),
            ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.URL_PARSE to config(
            ActionUrlConfigKey.URL to JsonPrimitive("https://bangumi.tv/subject/123?page=1"),
            ActionUrlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.URL_BUILD to config(
            ActionUrlConfigKey.BASE_URL to JsonPrimitive("https://bangumi.tv/subject/123"),
            ActionUrlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.URL_SET_QUERY_PARAM to config(
            ActionUrlConfigKey.URL to JsonPrimitive("https://bangumi.tv/subject/123"),
            ActionUrlConfigKey.KEY to JsonPrimitive("page"),
            ActionUrlConfigKey.VALUE to JsonPrimitive("2"),
            ActionUrlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.URL_GET_QUERY_PARAM to config(
            ActionUrlConfigKey.URL to JsonPrimitive("https://bangumi.tv/subject/123?page=2"),
            ActionUrlConfigKey.KEY to JsonPrimitive("page"),
            ActionUrlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.BILIBILI_SIGN_URL to config(
            ActionBilibiliConfigKey.URL to JsonPrimitive("https://api.bilibili.com/x/space/wbi/acc/info?mid=123456"),
            ActionBilibiliConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.CSV_PARSE to config(
            ActionCsvConfigKey.TEXT to JsonPrimitive("a,b\n1,2"),
            ActionCsvConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.CSV_STRINGIFY to config(
            ActionCsvConfigKey.ITEMS to JsonArray(listOf(JsonObject(mapOf("a" to JsonPrimitive("1"))))),
            ActionCsvConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.OBJECT_KEYS to config(
            ActionObjectConfigKey.OBJECT to JsonObject(mapOf("a" to JsonPrimitive("1"))),
            ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.OBJECT_VALUES to config(
            ActionObjectConfigKey.OBJECT to JsonObject(mapOf("a" to JsonPrimitive("1"))),
            ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.OBJECT_ENTRIES to config(
            ActionObjectConfigKey.OBJECT to JsonObject(mapOf("a" to JsonPrimitive("1"))),
            ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.OBJECT_HAS_KEY to config(
            ActionObjectConfigKey.OBJECT to JsonObject(mapOf("a" to JsonPrimitive("1"))),
            ActionObjectConfigKey.KEY to JsonPrimitive("a"),
            ActionObjectConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_INDEX_OF to config(
            ActionArrayConfigKey.VALUES to sampleArray,
            ActionArrayConfigKey.VALUE to JsonPrimitive("value"),
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_INTERSECTION to config(
            ActionArrayConfigKey.VALUES to sampleArray,
            ActionArrayConfigKey.OTHER_VALUES to sampleArray,
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_DIFFERENCE to config(
            ActionArrayConfigKey.VALUES to sampleArray,
            ActionArrayConfigKey.OTHER_VALUES to sampleArray,
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.JSON_VALIDATE to config(
            ActionJsonConfigKey.TEXT to JsonPrimitive("{\"a\":1}"),
            ActionJsonConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.JSON_SCHEMA_VALIDATE to config(
            ActionJsonConfigKey.VALUE to JsonObject(mapOf("a" to JsonPrimitive(1))),
            ActionJsonConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.TEXT_MATCH_ALL to config(
            ActionTextConfigKey.TEXT to JsonPrimitive("tag1 tag2"),
            ActionTextConfigKey.PATTERN to JsonPrimitive("tag(\\d+)"),
            ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.TEXT_PAD to config(
            ActionTextConfigKey.TEXT to JsonPrimitive("1"),
            ActionTextConfigKey.PAD_LENGTH to JsonPrimitive(3),
            ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.TEXT_FORMAT_NUMBER to config(
            ActionTextConfigKey.TEXT to JsonPrimitive("12.3456"),
            ActionTextConfigKey.FRACTION_DIGITS to JsonPrimitive(2),
            ActionTextConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_GROUP_BY to config(
            ActionArrayConfigKey.VALUES to JsonArray(listOf(JsonObject(mapOf("cat" to JsonPrimitive("a"))))),
            ActionArrayConfigKey.FIELD_PATH to JsonPrimitive("$.cat"),
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_REDUCE to config(
            ActionArrayConfigKey.VALUES to sampleArray,
            ActionArrayConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.ARRAY_FIRST to arrayConfig(),
        ActionNodeType.ARRAY_LAST to arrayConfig(),
        ActionNodeType.HTML_TABLE_TO_JSON to config(
            ActionHtmlConfigKey.HTML to JsonPrimitive("<table><tr><th>A</th></tr><tr><td>1</td></tr></table>"),
            ActionHtmlConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
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
            ActionClipboardConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.SYSTEM_VIBRATE to config(),
        ActionNodeType.UI_INPUT_DIALOG to config(
            ActionInputDialogConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.UI_PROGRESS_DIALOG to config(
            ActionProgressDialogConfigKey.MODE to JsonPrimitive(ActionProgressDialogMode.DETERMINATE),
            ActionProgressDialogConfigKey.PROGRESS to JsonPrimitive(1),
            ActionProgressDialogConfigKey.MAX_PROGRESS to JsonPrimitive(2),
        ),
        ActionNodeType.UI_PROGRESS_UPDATE to config(
            ActionProgressDialogConfigKey.MESSAGE to JsonPrimitive("已完成 50%"),
            ActionProgressDialogConfigKey.PROGRESS to JsonPrimitive(50),
        ),
        ActionNodeType.UI_PROGRESS_DISMISS to config(),
        ActionNodeType.UI_SELECT_DIALOG to config(
            ActionSelectDialogConfigKey.OPTIONS to buildJsonArray {
                add(buildJsonObject {
                    put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("Option 1"))
                    put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("opt1"))
                })
            },
            ActionSelectDialogConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.HTTP_REQUEST to config(
            ActionHttpConfigKey.URL to JsonPrimitive("https://bgm.tv"),
        ),
        ActionNodeType.STORAGE_PREFERENCES_GET to config(
            ActionStorageConfigKey.KEY to JsonPrimitive("subject_633836"),
            ActionStorageConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.STORAGE_PREFERENCES_SET to config(
            ActionStorageConfigKey.KEY to JsonPrimitive("subject_633836"),
            ActionStorageConfigKey.VALUE to JsonPrimitive(true),
            ActionStorageConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.STORAGE_PREFERENCES_DELETE to config(
            ActionStorageConfigKey.KEY to JsonPrimitive("subject_633836"),
            ActionStorageConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.STORAGE_PREFERENCES_HAS to config(
            ActionStorageConfigKey.KEY to JsonPrimitive("subject_633836"),
            ActionStorageConfigKey.OUTPUT_KEY to JsonPrimitive(OUTPUT_KEY),
        ),
        ActionNodeType.STORAGE_PREFERENCES_CLEAR to config(),
    )
}
