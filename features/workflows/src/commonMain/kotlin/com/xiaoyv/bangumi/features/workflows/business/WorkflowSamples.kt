package com.xiaoyv.bangumi.features.workflows.business

import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionEdge
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionPortRef
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionWorkflow
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayFilterOperator
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
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFileConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHtmlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHtmlQueryOperation
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpConfigKey
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
import com.xiaoyv.bangumi.shared.libnative.System
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject

/**
 * 内置节点的可运行回归样例。
 *
 * 每个已注册节点至少由一个样例覆盖；页面会将其直接渲染为运行入口，便于验证导入、校验、执行和副作用分发。
 */
object WorkflowSamples {
    private const val BILIBILI_WEB_USER_AGENT =
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:154.0) Gecko/20100101 Firefox/154.0"
    private val htmlSampleDocument = """
        <html>
          <head>
            <title>孤独摇滚！</title>
            <meta name="description" content="乐队少女的青春故事">
          </head>
          <body>
            <h1 class="nameSingle">孤独摇滚！</h1>
            <ul class="tags"><li>音乐</li><li>校园</li></ul>
            <a href="https://bgm.tv/subject/328609">条目页</a>
          </body>
        </html>
    """.trimIndent()

    private val sideEffectTypes = setOf(
        ActionNodeType.OPEN_EXTERNAL_URL,
        ActionNodeType.OPEN_EXTERNAL_APP,
        ActionNodeType.OPEN_INTERNAL_WEB,
        ActionNodeType.SHOW_TOAST,
        ActionNodeType.WRITE_CLIPBOARD,
        ActionNodeType.READ_CLIPBOARD,
        ActionNodeType.UI_CONFIRM,
        ActionNodeType.UI_INPUT_DIALOG,
        ActionNodeType.UI_SELECT_DIALOG,
        ActionNodeType.SYSTEM_SHARE,
        ActionNodeType.SYSTEM_NOTIFICATION,
        ActionNodeType.SYSTEM_VIBRATE,
        ActionNodeType.HTTP_REQUEST,
        ActionNodeType.IMAGE_PREVIEW,
        ActionNodeType.SYNC_COOKIE,
    )

    /**
     * 所有当前内置节点的测试工作流。
     */
    val all: List<ActionWorkflow> = buildList {
        // 错误与异常故障测试样例
        add(errorDivideByZeroSample())
        add(errorSqrtNegativeSample())
        add(errorAssertFailedSample())
        add(errorJsonParseMalformedSample())
        add(errorArrayOutOfBoundsSample())
        add(errorUrlParseMalformedSample())
        add(errorHtmlSelectorInvalidSample())
        add(errorMissingConfigSample())

        // 复合遍历业务实操样例
        add(subjectTagsToToast())
        add(searchBilibiliBangumiWithWebWbi())
        add(searchMangaDexAndPreviewImages())
        add(searchHanimeVideo())
        add(complexWorkflowSample())

        // Flow 节点
        add(linear("flow_start_end", "流程开始与结束", ActionNodeType.FLOW_END))
        add(linear("flow_delay", "延迟等待", ActionNodeType.FLOW_DELAY, config(ActionFlowConfigKey.DELAY_MILLIS to 3000L)))
        add(linear("flow_log", "打印日志", ActionNodeType.FLOW_LOG, config(ActionFlowConfigKey.MESSAGE to "测试日志信息")))
        add(linear("flow_debug", "调试断点", ActionNodeType.FLOW_DEBUG, config(ActionFlowConfigKey.MESSAGE to "到达调试节点")))
        add(linear("flow_assert", "流程断言", ActionNodeType.FLOW_ASSERT, config(ActionFlowConfigKey.CONDITION to true)))
        add(terminal("flow_stop", "提前结束流程", ActionNodeType.FLOW_STOP))
        add(switchSample())
        add(forkAndJoinSample())

        // Control 节点
        add(condition("control_if", "条件分支", ActionNodeType.CONDITION_IF, config(ActionControlConfigKey.CONDITION to true)))
        add(condition("control_equals", "相等判断", ActionNodeType.CONDITION_EQUALS, config(ActionControlConfigKey.LEFT to 7, ActionControlConfigKey.RIGHT to 7)))
        add(condition("control_not_equals", "不相等判断", ActionNodeType.CONDITION_NOT_EQUALS, config(ActionControlConfigKey.LEFT to "A", ActionControlConfigKey.RIGHT to "B")))
        add(condition("control_greater_than", "大于判断", ActionNodeType.CONDITION_GREATER_THAN, config(ActionControlConfigKey.LEFT to 8, ActionControlConfigKey.RIGHT to 7)))
        add(
            condition(
                "control_greater_than_or_equals",
                "大于等于判断",
                ActionNodeType.CONDITION_GREATER_THAN_OR_EQUALS,
                config(ActionControlConfigKey.LEFT to 7, ActionControlConfigKey.RIGHT to 7)
            )
        )
        add(condition("control_less_than", "小于判断", ActionNodeType.CONDITION_LESS_THAN, config(ActionControlConfigKey.LEFT to 6, ActionControlConfigKey.RIGHT to 7)))
        add(
            condition(
                "control_less_than_or_equals",
                "小于等于判断",
                ActionNodeType.CONDITION_LESS_THAN_OR_EQUALS,
                config(ActionControlConfigKey.LEFT to 7, ActionControlConfigKey.RIGHT to 7)
            )
        )
        add(condition("control_and", "逻辑与判断", ActionNodeType.CONDITION_AND, config(ActionControlConfigKey.LEFT to true, ActionControlConfigKey.RIGHT to true)))
        add(condition("control_or", "逻辑或判断", ActionNodeType.CONDITION_OR, config(ActionControlConfigKey.LEFT to false, ActionControlConfigKey.RIGHT to true)))
        add(condition("control_not", "逻辑非判断", ActionNodeType.CONDITION_NOT, config(ActionControlConfigKey.VALUE to false)))
        add(condition("control_is_null", "空值判断", ActionNodeType.CONDITION_IS_NULL, buildJsonObject { put(ActionControlConfigKey.VALUE, JsonNull) }))
        add(condition("control_is_empty", "空判定", ActionNodeType.CONDITION_IS_EMPTY, config(ActionControlConfigKey.VALUE to "")))

        // Data 节点
        add(linear("data_set_var", "写入临时变量", ActionNodeType.SET_VARIABLE, config(ActionDataConfigKey.KEY to "enabled", ActionDataConfigKey.VALUE to true)))
        add(linear("data_remove_var", "删除变量", ActionNodeType.DATA_REMOVE, config(ActionDataConfigKey.KEY to "enabled")))
        add(
            linear(
                "data_template",
                "模板插值",
                ActionNodeType.TEMPLATE,
                config(ActionDataConfigKey.TEMPLATE to "你好，${'$'}{input.nameCn}", ActionDataConfigKey.OUTPUT_KEY to "greeting")
            )
        )
        add(linear("data_to_number", "转为数字", ActionNodeType.DATA_TO_NUMBER, config(ActionDataConfigKey.VALUE to "123.45", ActionDataConfigKey.OUTPUT_KEY to "num")))
        add(linear("data_to_string", "转为文本", ActionNodeType.DATA_TO_STRING, config(ActionDataConfigKey.VALUE to 633836, ActionDataConfigKey.OUTPUT_KEY to "str")))
        add(linear("data_to_boolean", "转为布尔", ActionNodeType.DATA_TO_BOOLEAN, config(ActionDataConfigKey.VALUE to "true", ActionDataConfigKey.OUTPUT_KEY to "bool")))
        add(linear("data_type_of", "检测类型", ActionNodeType.DATA_TYPE_OF, config(ActionDataConfigKey.VALUE to "Hello", ActionDataConfigKey.OUTPUT_KEY to "type")))
        add(linear("data_uuid", "生成 UUID", ActionNodeType.DATA_UUID, config(ActionDataConfigKey.OUTPUT_KEY to "uuid")))

        // Object 节点
        add(
            linear(
                "object_get",
                "读取对象字段",
                ActionNodeType.OBJECT_GET,
                config(ActionObjectConfigKey.OBJECT to "${'$'}{input}", ActionObjectConfigKey.PATH to "$.nameCn", ActionObjectConfigKey.OUTPUT_KEY to "name")
            )
        )
        add(linear("object_set", "写入对象字段", ActionNodeType.OBJECT_SET, buildJsonObject {
            put(ActionObjectConfigKey.OBJECT, JsonObject(emptyMap()))
            put(ActionObjectConfigKey.KEY, JsonPrimitive("enabled"))
            put(ActionObjectConfigKey.VALUE, JsonPrimitive(true))
            put(ActionObjectConfigKey.OUTPUT_KEY, JsonPrimitive("obj"))
        }))
        add(linear("object_remove", "移除对象字段", ActionNodeType.OBJECT_REMOVE, buildJsonObject {
            put(ActionObjectConfigKey.OBJECT, JsonObject(mapOf("internal" to JsonPrimitive(true))))
            put(ActionObjectConfigKey.KEY, JsonPrimitive("internal"))
            put(ActionObjectConfigKey.OUTPUT_KEY, JsonPrimitive("obj"))
        }))
        add(linear("object_omit", "剔除属性键", ActionNodeType.OBJECT_OMIT, buildJsonObject {
            put(ActionObjectConfigKey.OBJECT, JsonObject(mapOf("a" to JsonPrimitive(1), "b" to JsonPrimitive(2))))
            put(ActionObjectConfigKey.KEYS, JsonArray(listOf(JsonPrimitive("a"))))
            put(ActionObjectConfigKey.OUTPUT_KEY, JsonPrimitive("obj"))
        }))
        add(linear("object_pick", "挑选保留键", ActionNodeType.OBJECT_PICK, buildJsonObject {
            put(ActionObjectConfigKey.OBJECT, JsonObject(mapOf("a" to JsonPrimitive(1), "b" to JsonPrimitive(2))))
            put(ActionObjectConfigKey.KEYS, JsonArray(listOf(JsonPrimitive("b"))))
            put(ActionObjectConfigKey.OUTPUT_KEY, JsonPrimitive("obj"))
        }))
        add(linear("object_merge", "合并对象", ActionNodeType.OBJECT_MERGE, buildJsonObject {
            put(ActionObjectConfigKey.OBJECTS, JsonArray(listOf(JsonObject(mapOf("a" to JsonPrimitive(1))), JsonObject(mapOf("b" to JsonPrimitive(2))))))
            put(ActionObjectConfigKey.OUTPUT_KEY, JsonPrimitive("obj"))
        }))
        add(linear("object_keys", "获取属性 Key 数组", ActionNodeType.OBJECT_KEYS, buildJsonObject {
            put(ActionObjectConfigKey.OBJECT, JsonObject(mapOf("name" to JsonPrimitive("BGM"))))
            put(ActionObjectConfigKey.OUTPUT_KEY, JsonPrimitive("keys"))
        }))
        add(linear("object_values", "获取属性值数组", ActionNodeType.OBJECT_VALUES, buildJsonObject {
            put(ActionObjectConfigKey.OBJECT, JsonObject(mapOf("name" to JsonPrimitive("BGM"))))
            put(ActionObjectConfigKey.OUTPUT_KEY, JsonPrimitive("values"))
        }))
        add(linear("object_entries", "转为键值元组", ActionNodeType.OBJECT_ENTRIES, buildJsonObject {
            put(ActionObjectConfigKey.OBJECT, JsonObject(mapOf("name" to JsonPrimitive("BGM"))))
            put(ActionObjectConfigKey.OUTPUT_KEY, JsonPrimitive("entries"))
        }))
        add(linear("object_from_entries", "元组还原为对象", ActionNodeType.OBJECT_FROM_ENTRIES, buildJsonObject {
            put(ActionObjectConfigKey.ENTRIES, JsonArray(listOf(JsonArray(listOf(JsonPrimitive("name"), JsonPrimitive("BGM"))))))
            put(ActionObjectConfigKey.OUTPUT_KEY, JsonPrimitive("obj"))
        }))
        add(linear("object_has_key", "包含 Key 校验", ActionNodeType.OBJECT_HAS_KEY, buildJsonObject {
            put(ActionObjectConfigKey.OBJECT, JsonObject(mapOf("name" to JsonPrimitive("BGM"))))
            put(ActionObjectConfigKey.KEY, JsonPrimitive("name"))
            put(ActionObjectConfigKey.OUTPUT_KEY, JsonPrimitive("hasKey"))
        }))
        add(linear("object_is_empty", "空对象校验", ActionNodeType.OBJECT_IS_EMPTY, buildJsonObject {
            put(ActionObjectConfigKey.OBJECT, JsonObject(emptyMap()))
            put(ActionObjectConfigKey.OUTPUT_KEY, JsonPrimitive("isEmpty"))
        }))

        // Array 节点
        add(linear("array_length", "数组长度", ActionNodeType.ARRAY_LENGTH, buildJsonObject {
            put(ActionArrayConfigKey.VALUES, JsonArray(listOf(JsonPrimitive("动画"), JsonPrimitive("音乐"))))
            put(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("length"))
        }))
        add(linear("array_create", "创建数组", ActionNodeType.ARRAY_CREATE, buildJsonObject {
            put(ActionArrayConfigKey.VALUES, JsonArray(listOf(JsonPrimitive("动画"), JsonPrimitive("音乐"))))
            put(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("tags"))
        }))
        add(linear("array_append", "追加数组元素", ActionNodeType.ARRAY_APPEND, buildJsonObject {
            put(ActionArrayConfigKey.VALUES, JsonArray(listOf(JsonPrimitive("动画"))))
            put(ActionArrayConfigKey.VALUE, JsonPrimitive("音乐"))
            put(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("tags"))
        }))
        add(linear("array_insert_at", "插入数组元素", ActionNodeType.ARRAY_INSERT_AT, buildJsonObject {
            put(ActionArrayConfigKey.VALUES, JsonArray(listOf(JsonPrimitive("动画"), JsonPrimitive("校园"))))
            put(ActionArrayConfigKey.INDEX, JsonPrimitive(1))
            put(ActionArrayConfigKey.VALUE, JsonPrimitive("音乐"))
            put(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("tags"))
        }))
        add(linear("array_remove_at", "删除数组元素", ActionNodeType.ARRAY_REMOVE_AT, buildJsonObject {
            put(ActionArrayConfigKey.VALUES, JsonArray(listOf(JsonPrimitive("动画"), JsonPrimitive("音乐"))))
            put(ActionArrayConfigKey.INDEX, JsonPrimitive(0))
            put(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("tags"))
        }))
        add(linear("array_filter", "筛选数组", ActionNodeType.ARRAY_FILTER, buildJsonObject {
            put(ActionArrayConfigKey.VALUES, JsonArray(listOf(JsonObject(mapOf("name" to JsonPrimitive("动画"), "enabled" to JsonPrimitive(true))))))
            put(ActionArrayConfigKey.FIELD_PATH, JsonPrimitive("$.enabled"))
            put(ActionArrayConfigKey.OPERATOR, JsonPrimitive(ActionArrayFilterOperator.EQUALS))
            put(ActionArrayConfigKey.EXPECTED, JsonPrimitive(true))
            put(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("filtered"))
        }))
        add(linear("array_map", "数组字段映射", ActionNodeType.ARRAY_MAP, buildJsonObject {
            put(ActionArrayConfigKey.VALUES, JsonArray(listOf(JsonObject(mapOf("title" to JsonPrimitive("孤独摇滚！"))))))
            put(ActionArrayConfigKey.FIELD_PATH, JsonPrimitive("$.title"))
            put(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("titles"))
        }))
        add(linear("array_flat_map", "数组扁平映射", ActionNodeType.ARRAY_FLAT_MAP, buildJsonObject {
            put(ActionArrayConfigKey.VALUES, JsonArray(listOf(JsonObject(mapOf("tags" to JsonArray(listOf(JsonPrimitive("音乐"))))))))
            put(ActionArrayConfigKey.FIELD_PATH, JsonPrimitive("$.tags"))
            put(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("allTags"))
        }))
        add(linear("array_concat", "拼接数组", ActionNodeType.ARRAY_CONCAT, buildJsonObject {
            put(ActionArrayConfigKey.VALUES, JsonArray(listOf(JsonArray(listOf(JsonPrimitive("A"))), JsonArray(listOf(JsonPrimitive("B"))))))
            put(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("values"))
        }))
        add(linear("array_zip", "数组打包", ActionNodeType.ARRAY_ZIP, buildJsonObject {
            put(ActionArrayConfigKey.VALUES, JsonArray(listOf(JsonPrimitive("A"), JsonPrimitive("B"))))
            put(ActionArrayConfigKey.OTHER_VALUES, JsonArray(listOf(JsonPrimitive(1), JsonPrimitive(2))))
            put(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("zipped"))
        }))
        add(linear("array_take", "提取前 N 项", ActionNodeType.ARRAY_TAKE, buildJsonObject {
            put(ActionArrayConfigKey.VALUES, JsonArray(listOf(JsonPrimitive("A"), JsonPrimitive("B"), JsonPrimitive("C"))))
            put(ActionArrayConfigKey.COUNT, JsonPrimitive(2))
            put(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("took"))
        }))
        add(linear("array_drop", "跳过前 N 项", ActionNodeType.ARRAY_DROP, buildJsonObject {
            put(ActionArrayConfigKey.VALUES, JsonArray(listOf(JsonPrimitive("A"), JsonPrimitive("B"), JsonPrimitive("C"))))
            put(ActionArrayConfigKey.COUNT, JsonPrimitive(1))
            put(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("dropped"))
        }))
        add(linear("array_contains", "数组包含判断", ActionNodeType.ARRAY_CONTAINS, buildJsonObject {
            put(ActionArrayConfigKey.VALUES, JsonArray(listOf(JsonPrimitive("动画"))))
            put(ActionArrayConfigKey.VALUE, JsonPrimitive("动画"))
            put(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("contains"))
        }))
        add(linear("array_find", "查找数组对象", ActionNodeType.ARRAY_FIND, buildJsonObject {
            put(ActionArrayConfigKey.VALUES, JsonArray(listOf(JsonObject(mapOf("id" to JsonPrimitive(1))))))
            put(ActionArrayConfigKey.FIELD_PATH, JsonPrimitive("$.id"))
            put(ActionArrayConfigKey.EXPECTED, JsonPrimitive(1))
            put(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("item"))
        }))
        add(linear("array_distinct", "数组去重", ActionNodeType.ARRAY_DISTINCT, buildJsonObject {
            put(ActionArrayConfigKey.VALUES, JsonArray(listOf(JsonPrimitive("A"), JsonPrimitive("A"), JsonPrimitive("B"))))
            put(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("distinct"))
        }))
        add(linear("array_sort", "数组排序", ActionNodeType.ARRAY_SORT, buildJsonObject {
            put(ActionArrayConfigKey.VALUES, JsonArray(listOf(JsonPrimitive(3), JsonPrimitive(1), JsonPrimitive(2))))
            put(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("sorted"))
        }))
        add(linear("array_reverse", "数组反转", ActionNodeType.ARRAY_REVERSE, buildJsonObject {
            put(ActionArrayConfigKey.VALUES, JsonArray(listOf(JsonPrimitive("A"), JsonPrimitive("B"))))
            put(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("reversed"))
        }))
        add(linear("array_slice", "截取数组", ActionNodeType.ARRAY_SLICE, buildJsonObject {
            put(ActionArrayConfigKey.VALUES, JsonArray(listOf(JsonPrimitive("A"), JsonPrimitive("B"), JsonPrimitive("C"))))
            put(ActionArrayConfigKey.START, JsonPrimitive(1))
            put(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("sliced"))
        }))
        add(linear("array_flatten", "展平数组", ActionNodeType.ARRAY_FLATTEN, buildJsonObject {
            put(ActionArrayConfigKey.VALUES, JsonArray(listOf(JsonArray(listOf(JsonPrimitive("A"))), JsonPrimitive("B"))))
            put(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("flattened"))
        }))
        add(linear("array_sum", "数组求和", ActionNodeType.ARRAY_SUM, buildJsonObject {
            put(ActionArrayConfigKey.VALUES, JsonArray(listOf(JsonPrimitive(10), JsonPrimitive(20))))
            put(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("sum"))
        }))
        add(linear("array_avg", "数组求平均值", ActionNodeType.ARRAY_AVG, buildJsonObject {
            put(ActionArrayConfigKey.VALUES, JsonArray(listOf(JsonPrimitive(10), JsonPrimitive(20))))
            put(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("avg"))
        }))

        // Text 节点
        add(linear("text_length", "字符串长度", ActionNodeType.TEXT_LENGTH, config(ActionTextConfigKey.TEXT to "Bangumi", ActionTextConfigKey.OUTPUT_KEY to "length")))
        add(linear("text_trim", "去除首尾空白", ActionNodeType.TEXT_TRIM, config(ActionTextConfigKey.TEXT to " Bangumi ", ActionTextConfigKey.OUTPUT_KEY to "text")))
        add(linear("text_lowercase", "文本小写", ActionNodeType.TEXT_LOWERCASE, config(ActionTextConfigKey.TEXT to "BGM", ActionTextConfigKey.OUTPUT_KEY to "text")))
        add(linear("text_uppercase", "文本大写", ActionNodeType.TEXT_UPPERCASE, config(ActionTextConfigKey.TEXT to "bgm", ActionTextConfigKey.OUTPUT_KEY to "text")))
        add(linear("text_capitalize", "首字母大写", ActionNodeType.TEXT_CAPITALIZE, config(ActionTextConfigKey.TEXT to "bangumi", ActionTextConfigKey.OUTPUT_KEY to "text")))
        add(
            linear(
                "text_repeat",
                "重复文本",
                ActionNodeType.TEXT_REPEAT,
                config(ActionTextConfigKey.TEXT to "A", ActionTextConfigKey.COUNT to 3, ActionTextConfigKey.OUTPUT_KEY to "text")
            )
        )
        add(linear("text_reverse", "文本反转", ActionNodeType.TEXT_REVERSE, config(ActionTextConfigKey.TEXT to "Bangumi", ActionTextConfigKey.OUTPUT_KEY to "text")))
        add(
            linear(
                "text_index_of",
                "文本查找下标",
                ActionNodeType.TEXT_INDEX_OF,
                config(ActionTextConfigKey.TEXT to "Hello World", ActionTextConfigKey.PATTERN to "World", ActionTextConfigKey.OUTPUT_KEY to "idx")
            )
        )
        add(
            linear(
                "text_split",
                "文本分隔",
                ActionNodeType.TEXT_SPLIT,
                config(
                    ActionTextConfigKey.TEXT to "动画,音乐,校园",
                    ActionTextConfigKey.DELIMITER to ",",
                    ActionTextConfigKey.IS_REGEX to false,
                    ActionTextConfigKey.OUTPUT_KEY to "tags"
                )
            )
        )
        add(
            linear(
                "text_regex_match",
                "正则匹配捕获组",
                ActionNodeType.TEXT_REGEX_MATCH,
                config(ActionTextConfigKey.TEXT to "subject-633836", ActionTextConfigKey.PATTERN to "(subject)-(\\d+)", ActionTextConfigKey.OUTPUT_KEY to "matchGroups")
            )
        )
        add(
            linear(
                "text_substring",
                "按索引截取文本",
                ActionNodeType.TEXT_SUBSTRING,
                config(
                    ActionTextConfigKey.TEXT to "Bangumi 工作流",
                    ActionTextConfigKey.START_INDEX to 8,
                    ActionTextConfigKey.END_INDEX to 11,
                    ActionTextConfigKey.OUTPUT_KEY to "part"
                )
            )
        )
        add(
            linear(
                "text_substring_before",
                "截取分隔符前文本",
                ActionNodeType.TEXT_SUBSTRING_BEFORE,
                config(ActionTextConfigKey.TEXT to "https://bgm.tv/subject/633836", ActionTextConfigKey.DELIMITER to "/subject", ActionTextConfigKey.OUTPUT_KEY to "baseUrl")
            )
        )
        add(
            linear(
                "text_substring_after",
                "截取分隔符后文本",
                ActionNodeType.TEXT_SUBSTRING_AFTER,
                config(ActionTextConfigKey.TEXT to "https://bgm.tv/subject/633836", ActionTextConfigKey.DELIMITER to "/subject/", ActionTextConfigKey.OUTPUT_KEY to "subjectId")
            )
        )
        add(
            linear(
                "text_replace",
                "静态文本替换",
                ActionNodeType.TEXT_REPLACE,
                config(
                    ActionTextConfigKey.TEXT to "Bangumi 工作流",
                    ActionTextConfigKey.PATTERN to "工作流",
                    ActionTextConfigKey.REPLACEMENT to "节点",
                    ActionTextConfigKey.OUTPUT_KEY to "text"
                )
            )
        )
        add(
            linear(
                "text_replace_regex",
                "正则文本替换",
                ActionNodeType.TEXT_REPLACE_REGEX,
                config(
                    ActionTextConfigKey.TEXT to "tag-633836",
                    ActionTextConfigKey.PATTERN to "\\d+",
                    ActionTextConfigKey.REPLACEMENT to "ID",
                    ActionTextConfigKey.OUTPUT_KEY to "text"
                )
            )
        )
        add(linear("text_join", "拼接文本数组", ActionNodeType.TEXT_JOIN, buildJsonObject {
            put(ActionTextConfigKey.VALUES, JsonArray(listOf(JsonPrimitive("动画"), JsonPrimitive("音乐"))))
            put(ActionTextConfigKey.SEPARATOR, JsonPrimitive(" · "))
            put(ActionTextConfigKey.OUTPUT_KEY, JsonPrimitive("text"))
        }))

        // Math 节点
        addAll(mathSamples())

        // Date 节点
        add(linear("date_now", "当前时间戳", ActionNodeType.DATE_NOW, config(ActionDateConfigKey.OUTPUT_KEY to "now")))
        add(linear("date_format", "格式化时间", ActionNodeType.DATE_FORMAT, config(ActionDateConfigKey.TIMESTAMP to 1700000000000L, ActionDateConfigKey.OUTPUT_KEY to "dateStr")))
        add(linear("date_parse", "解析日期文本", ActionNodeType.DATE_PARSE, config(ActionDateConfigKey.TEXT to "2026-09-01T04:15:00Z", ActionDateConfigKey.OUTPUT_KEY to "ts")))
        add(
            linear(
                "date_get_component",
                "提取时间分量",
                ActionNodeType.DATE_GET_COMPONENT,
                config(ActionDateConfigKey.TIMESTAMP to 1700000000000L, ActionDateConfigKey.OUTPUT_KEY to "components")
            )
        )
        add(
            linear(
                "date_relative_time",
                "相对时间显示",
                ActionNodeType.DATE_RELATIVE_TIME,
                config(ActionDateConfigKey.TIMESTAMP to 1700000000000L, ActionDateConfigKey.OUTPUT_KEY to "relTime")
            )
        )

        // URL 节点
        add(
            linear(
                "url_parse",
                "解析 URL",
                ActionNodeType.URL_PARSE,
                config(ActionUrlConfigKey.URL to "https://bgm.tv/subject/123?page=1", ActionUrlConfigKey.OUTPUT_KEY to "parsedUrl")
            )
        )
        add(
            linear(
                "url_build",
                "构建 URL",
                ActionNodeType.URL_BUILD,
                config(ActionUrlConfigKey.BASE_URL to "https://bgm.tv/subject/123", ActionUrlConfigKey.OUTPUT_KEY to "builtUrl")
            )
        )
        add(
            linear(
                "url_set_query_param",
                "修改 URL 参数",
                ActionNodeType.URL_SET_QUERY_PARAM,
                config(
                    ActionUrlConfigKey.URL to "https://bgm.tv/subject/123",
                    ActionUrlConfigKey.KEY to "page",
                    ActionUrlConfigKey.VALUE to "2",
                    ActionUrlConfigKey.OUTPUT_KEY to "updatedUrl"
                )
            )
        )
        add(
            linear(
                "url_get_query_param",
                "读取 URL 参数",
                ActionNodeType.URL_GET_QUERY_PARAM,
                config(ActionUrlConfigKey.URL to "https://bgm.tv/subject/123?page=2", ActionUrlConfigKey.KEY to "page", ActionUrlConfigKey.OUTPUT_KEY to "paramVal")
            )
        )

        // Codec & Crypto 节点
        add(
            linear(
                "codec_base64_encode",
                "Base64 编码",
                ActionNodeType.CODEC_BASE64_ENCODE,
                config(ActionCodecConfigKey.TEXT to "Hello", ActionCodecConfigKey.OUTPUT_KEY to "b64")
            )
        )
        add(
            linear(
                "codec_base64_decode",
                "Base64 解码",
                ActionNodeType.CODEC_BASE64_DECODE,
                config(ActionCodecConfigKey.TEXT to "SGVsbG8=", ActionCodecConfigKey.OUTPUT_KEY to "raw")
            )
        )
        add(linear("codec_url_encode", "URL 编码", ActionNodeType.CODEC_URL_ENCODE, config(ActionCodecConfigKey.TEXT to "孤独摇滚！", ActionCodecConfigKey.OUTPUT_KEY to "encoded")))
        add(
            linear(
                "codec_url_decode",
                "URL 解码",
                ActionNodeType.CODEC_URL_DECODE,
                config(ActionCodecConfigKey.TEXT to "%E5%AD%A4%E7%8B%AC", ActionCodecConfigKey.OUTPUT_KEY to "decoded")
            )
        )
        add(
            linear(
                "codec_html_escape",
                "HTML 转义",
                ActionNodeType.CODEC_HTML_ESCAPE,
                config(ActionCodecConfigKey.TEXT to "<div>hello</div>", ActionCodecConfigKey.OUTPUT_KEY to "escaped")
            )
        )
        add(
            linear(
                "codec_html_unescape",
                "HTML 反转义",
                ActionNodeType.CODEC_HTML_UNESCAPE,
                config(ActionCodecConfigKey.TEXT to "&lt;div&gt;hello&lt;/div&gt;", ActionCodecConfigKey.OUTPUT_KEY to "unescaped")
            )
        )
        add(linear("crypto_hash", "哈希计算 (SHA-256)", ActionNodeType.CRYPTO_HASH, config(ActionCryptoConfigKey.TEXT to "Hello", ActionCryptoConfigKey.OUTPUT_KEY to "hash")))
        add(
            linear(
                "crypto_hmac",
                "HMAC 签名",
                ActionNodeType.CRYPTO_HMAC,
                config(ActionCryptoConfigKey.TEXT to "Hello", ActionCryptoConfigKey.SECRET to "secretKey", ActionCryptoConfigKey.OUTPUT_KEY to "hmac")
            )
        )
        add(linear("crypto_random_bytes", "随机字节", ActionNodeType.CRYPTO_RANDOM_BYTES, config(ActionCryptoConfigKey.OUTPUT_KEY to "bytes")))
        add(linear("crypto_uuid", "Crypto UUID", ActionNodeType.CRYPTO_UUID, config(ActionCryptoConfigKey.OUTPUT_KEY to "uuid")))

        // HTML 节点
        add(
            linear(
                "html_query",
                "HTML CSS 单元素查询",
                ActionNodeType.HTML_QUERY,
                config(
                    ActionHtmlConfigKey.HTML to htmlSampleDocument,
                    ActionHtmlConfigKey.SELECTOR to "h1.nameSingle",
                    ActionHtmlConfigKey.OPERATION to ActionHtmlQueryOperation.TEXT,
                    ActionHtmlConfigKey.OUTPUT_KEY to "name"
                )
            )
        )
        add(
            linear(
                "html_query_all",
                "HTML CSS 多元素查询",
                ActionNodeType.HTML_QUERY_ALL,
                config(
                    ActionHtmlConfigKey.HTML to htmlSampleDocument,
                    ActionHtmlConfigKey.SELECTOR to "ul.tags > li",
                    ActionHtmlConfigKey.OPERATION to ActionHtmlQueryOperation.TEXT,
                    ActionHtmlConfigKey.OUTPUT_KEY to "tags"
                )
            )
        )
        add(linear("html_title", "HTML 文档标题", ActionNodeType.HTML_TITLE, config(ActionHtmlConfigKey.HTML to htmlSampleDocument, ActionHtmlConfigKey.OUTPUT_KEY to "title")))
        add(linear("html_text", "HTML 纯文本", ActionNodeType.HTML_TEXT, config(ActionHtmlConfigKey.HTML to htmlSampleDocument, ActionHtmlConfigKey.OUTPUT_KEY to "text")))
        add(
            linear(
                "html_meta_content",
                "HTML Meta 内容",
                ActionNodeType.HTML_META_CONTENT,
                config(ActionHtmlConfigKey.HTML to htmlSampleDocument, ActionHtmlConfigKey.NAME to "description", ActionHtmlConfigKey.OUTPUT_KEY to "description")
            )
        )
        add(linear("html_links", "HTML 链接列表", ActionNodeType.HTML_LINKS, config(ActionHtmlConfigKey.HTML to htmlSampleDocument, ActionHtmlConfigKey.OUTPUT_KEY to "links")))

        // JSON / XML / CSV 节点
        add(linear("json_extract", "JSON 路径提取", ActionNodeType.JSON_EXTRACT, buildJsonObject {
            put(ActionJsonConfigKey.SOURCE, JsonObject(mapOf("subject" to JsonObject(mapOf("name" to JsonPrimitive("孤独摇滚"))))))
            put(ActionJsonConfigKey.PATH, JsonPrimitive("$.subject.name"))
            put(ActionJsonConfigKey.OUTPUT_KEY, JsonPrimitive("name"))
        }))
        add(
            linear(
                "json_parse",
                "解析 JSON 文本",
                ActionNodeType.JSON_PARSE,
                config(ActionJsonConfigKey.TEXT to "{\"name\":\"Bangumi\"}", ActionJsonConfigKey.OUTPUT_KEY to "json")
            )
        )
        add(linear("json_stringify", "序列化 JSON", ActionNodeType.JSON_STRINGIFY, buildJsonObject {
            put(ActionJsonConfigKey.VALUE, JsonObject(mapOf("name" to JsonPrimitive("Bangumi"))))
            put(ActionJsonConfigKey.OUTPUT_KEY, JsonPrimitive("text"))
        }))
        add(
            linear(
                "xml_parse",
                "解析 XML",
                ActionNodeType.XML_PARSE,
                config(ActionXmlConfigKey.TEXT to "<rss><title>Bangumi</title></rss>", ActionXmlConfigKey.OUTPUT_KEY to "xmlObj")
            )
        )
        add(linear("xml_stringify", "生成 XML", ActionNodeType.XML_STRINGIFY, buildJsonObject {
            put(ActionXmlConfigKey.DATA, JsonObject(mapOf("title" to JsonPrimitive("Bangumi"))))
            put(ActionXmlConfigKey.OUTPUT_KEY, JsonPrimitive("xmlStr"))
        }))
        add(linear("csv_parse", "解析 CSV", ActionNodeType.CSV_PARSE, config(ActionCsvConfigKey.TEXT to "a,b\n1,2", ActionCsvConfigKey.OUTPUT_KEY to "csvObj")))
        add(linear("csv_stringify", "导出 CSV", ActionNodeType.CSV_STRINGIFY, buildJsonObject {
            put(ActionCsvConfigKey.ITEMS, JsonArray(listOf(JsonObject(mapOf("a" to JsonPrimitive("1"), "b" to JsonPrimitive("2"))))))
            put(ActionCsvConfigKey.OUTPUT_KEY, JsonPrimitive("csvStr"))
        }))

        // Storage 节点
        add(fileReadWriteSample())
        add(
            linear(
                "storage_preferences_set",
                "写入工作流私有存储",
                ActionNodeType.STORAGE_PREFERENCES_SET,
                config(ActionStorageConfigKey.KEY to "sample_favorite", ActionStorageConfigKey.VALUE to true, ActionStorageConfigKey.OUTPUT_KEY to "savedFavorite")
            )
        )
        add(
            linear(
                "storage_preferences_get",
                "读取工作流私有存储",
                ActionNodeType.STORAGE_PREFERENCES_GET,
                config(ActionStorageConfigKey.KEY to "sample_favorite", ActionStorageConfigKey.OUTPUT_KEY to "savedFavorite")
            )
        )

        // Action & HTTP 节点
        add(linear("action_show_toast", "显示提示", ActionNodeType.SHOW_TOAST, config(ActionToastConfigKey.MESSAGE to "工作流节点运行成功")))
        add(
            linear(
                "action_write_clipboard",
                "写入剪贴板",
                ActionNodeType.WRITE_CLIPBOARD,
                config(ActionClipboardConfigKey.TEXT to "Bangumi 工作流"),
                setOf(ActionCapability.CLIPBOARD_WRITE)
            )
        )
        add(linear("action_read_clipboard", "读取剪贴板", ActionNodeType.READ_CLIPBOARD, config(ActionClipboardConfigKey.OUTPUT_KEY to "clipContent")))
        add(
            linear(
                "action_open_external_url",
                "打开外部网页",
                ActionNodeType.OPEN_EXTERNAL_URL,
                config(ActionOpenUrlConfigKey.URL to "https://bgm.tv/subject/633836"),
                setOf(ActionCapability.OPEN_EXTERNAL_URL)
            )
        )
        add(
            linear(
                "action_open_external_app",
                "打开应用市场",
                ActionNodeType.OPEN_EXTERNAL_APP,
                config(ActionOpenAppConfigKey.URI to "market://details?id=com.xiaoyv.bangumi.multiplatform"),
                setOf(ActionCapability.OPEN_EXTERNAL_APP)
            )
        )
        add(
            linear(
                "action_open_internal_web",
                "打开应用内网页",
                ActionNodeType.OPEN_INTERNAL_WEB,
                config(ActionOpenWebConfigKey.URL to "https://hanime1.me"),
                setOf(ActionCapability.OPEN_INTERNAL_WEB)
            )
        )
        add(
            linear(
                "action_sync_cookie",
                "同步 Web Cookie",
                ActionNodeType.SYNC_COOKIE,
                config(
                    ActionSyncCookieConfigKey.URL to "https://hanime1.me",
                    ActionSyncCookieConfigKey.TITLE to "同步 hanime1 Cookie"
                ),
                setOf(ActionCapability.NETWORK_COOKIE_SYNC)
            )
        )
        add(
            linear(
                "ui_confirm",
                "二次确认弹窗",
                ActionNodeType.UI_CONFIRM,
                config(ActionConfirmConfigKey.TITLE to "温馨提示", ActionConfirmConfigKey.MESSAGE to "是否确认执行此操作？"),
                setOf(ActionCapability.CONFIRM_DIALOG)
            )
        )
        add(
            linear(
                "ui_input_dialog",
                "输入框弹窗",
                ActionNodeType.UI_INPUT_DIALOG,
                config(
                    ActionInputDialogConfigKey.TITLE to "请输入条目备注",
                    ActionInputDialogConfigKey.SUBTITLE to "支持自定义文本输入",
                    ActionInputDialogConfigKey.OUTPUT_KEY to "userNote"
                ),
                setOf(ActionCapability.INPUT_DIALOG)
            )
        )
        add(
            linear(
                "ui_select_dialog_single",
                "单选列表弹窗",
                ActionNodeType.UI_SELECT_DIALOG,
                buildJsonObject {
                    put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("请选择收藏状态"))
                    put(ActionSelectDialogConfigKey.SUBTITLE, JsonPrimitive("点击任意选项即可选中并完成"))
                    put(ActionSelectDialogConfigKey.IS_MULTI_SELECT, JsonPrimitive(false))
                    put(
                        ActionSelectDialogConfigKey.OPTIONS,
                        buildJsonArray {
                            add(buildJsonObject {
                                put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("想看"))
                                put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("wish"))
                            })
                            add(buildJsonObject {
                                put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("看过"))
                                put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("collect"))
                            })
                            add(buildJsonObject {
                                put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("在看"))
                                put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("do"))
                            })
                            add(buildJsonObject {
                                put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("搁置"))
                                put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("on_hold"))
                            })
                            add(buildJsonObject {
                                put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("抛弃"))
                                put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("dropped"))
                            })
                        }
                    )
                    put(ActionSelectDialogConfigKey.DEFAULT_VALUES, buildJsonArray { add(JsonPrimitive("collect")) })
                    put(ActionSelectDialogConfigKey.OUTPUT_KEY, JsonPrimitive("selectedStatus"))
                },
                setOf(ActionCapability.SELECT_DIALOG)
            )
        )
        add(
            linear(
                "ui_select_dialog_multi",
                "多选列表弹窗",
                ActionNodeType.UI_SELECT_DIALOG,
                buildJsonObject {
                    put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("请选择感兴趣的标签"))
                    put(ActionSelectDialogConfigKey.SUBTITLE, JsonPrimitive("勾选选项后点击确定提交"))
                    put(ActionSelectDialogConfigKey.IS_MULTI_SELECT, JsonPrimitive(true))
                    put(
                        ActionSelectDialogConfigKey.OPTIONS,
                        buildJsonArray {
                            add(buildJsonObject {
                                put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("原创"))
                                put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("original"))
                            })
                            add(buildJsonObject {
                                put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("搞笑"))
                                put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("comedy"))
                            })
                            add(buildJsonObject {
                                put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("日常"))
                                put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("slice_of_life"))
                            })
                            add(buildJsonObject {
                                put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("音乐"))
                                put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("music"))
                            })
                        }
                    )
                    put(
                        ActionSelectDialogConfigKey.DEFAULT_VALUES,
                        buildJsonArray {
                            add(JsonPrimitive("original"))
                            add(JsonPrimitive("music"))
                        }
                    )
                    put(ActionSelectDialogConfigKey.OUTPUT_KEY, JsonPrimitive("selectedTags"))
                },
                setOf(ActionCapability.SELECT_DIALOG)
            )
        )
        add(
            linear(
                "ui_select_dialog_index",
                "列表选择 (输出 Index 索引模式)",
                ActionNodeType.UI_SELECT_DIALOG,
                buildJsonObject {
                    put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("请选择评级 (输出 Index)"))
                    put(ActionSelectDialogConfigKey.SUBTITLE, JsonPrimitive("结果将直接输出选中的 0-based 数组索引"))
                    put(ActionSelectDialogConfigKey.OUTPUT_MODE, JsonPrimitive("index"))
                    put(
                        ActionSelectDialogConfigKey.OPTIONS,
                        buildJsonArray {
                            add(buildJsonObject {
                                put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("神作 (0)"))
                                put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("10"))
                            })
                            add(buildJsonObject {
                                put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("力荐 (1)"))
                                put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("9"))
                            })
                            add(buildJsonObject {
                                put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("推荐 (2)"))
                                put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("8"))
                            })
                        }
                    )
                    put(ActionSelectDialogConfigKey.DEFAULT_INDICES, buildJsonArray { add(JsonPrimitive(1)) })
                    put(ActionSelectDialogConfigKey.OUTPUT_KEY, JsonPrimitive("selectedIndex"))
                },
                setOf(ActionCapability.SELECT_DIALOG)
            )
        )
        add(linear("system_share", "系统分享", ActionNodeType.SYSTEM_SHARE, config(ActionShareConfigKey.TEXT to "Bangumi 多平台工作流引擎"), setOf(ActionCapability.SHARE)))
        add(
            linear(
                "system_notification",
                "发送系统通知",
                ActionNodeType.SYSTEM_NOTIFICATION,
                config(ActionNotificationConfigKey.CONTENT to "您有一条新的通知消息"),
                setOf(ActionCapability.NOTIFICATION)
            )
        )
        add(linear("system_vibrate", "设备震动", ActionNodeType.SYSTEM_VIBRATE))
        add(
            linear(
                "action_http_request",
                "HTTP GET 请求与重试",
                ActionNodeType.HTTP_REQUEST,
                config(
                    ActionHttpConfigKey.URL to "https://next.bgm.tv/p1/subjects/633836",
                    ActionHttpConfigKey.METHOD to "GET",
                    ActionHttpConfigKey.RETRY_COUNT to 2,
                    ActionHttpConfigKey.RETRY_DELAY_MILLIS to 300L,
                    ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE to true,
                ),
                setOf(ActionCapability.NETWORK, ActionCapability.NETWORK_LOCAL_COOKIE_ACCESS)
            )
        )
        add(
            linear(
                "bilibili_sign_url",
                "Bilibili WBI 链接加签",
                ActionNodeType.BILIBILI_SIGN_URL,
                config(
                    ActionBilibiliConfigKey.URL to "https://api.bilibili.com/x/space/wbi/acc/info?mid=123456",
                    ActionBilibiliConfigKey.OUTPUT_KEY to "signedUrl",
                )
            )
        )

        add(
            linear(
                "image_preview",
                "图片预览",
                ActionNodeType.IMAGE_PREVIEW,
                config(
                    ActionImagePreviewConfigKey.INDEX to 0,
                    ActionImagePreviewConfigKey.IMAGES to JsonArray(listOf(JsonPrimitive("https://lain.bgm.tv/pic/photo/l/47/7e/837364_do644.jpg"))),
                ),
                setOf(ActionCapability.IMAGE_PREVIEW)
            )
        )
    }.toPersistentList()

    // https://hanime1.me/search?query=らぶみー「楓と鈴」THE+ANIMATION
    /**
     * 按稳定 ID 查询测试工作流。
     */
    fun find(id: String): ActionWorkflow? = all.firstOrNull { it.id == id }


    private fun mathSamples(): List<ActionWorkflow> = listOf(
        math("math_add", "加法", ActionNodeType.MATH_ADD, 8, 2),
        math("math_subtract", "减法", ActionNodeType.MATH_SUBTRACT, 8, 2),
        math("math_multiply", "乘法", ActionNodeType.MATH_MULTIPLY, 8, 2),
        math("math_divide", "除法", ActionNodeType.MATH_DIVIDE, 8, 2),
        math("math_modulo", "余数", ActionNodeType.MATH_MODULO, 8, 3),
        math("math_min", "求最小值", ActionNodeType.MATH_MIN, 5, 9),
        math("math_max", "求最大值", ActionNodeType.MATH_MAX, 5, 9),
        math("math_pow", "幂运算", ActionNodeType.MATH_POW, 2, 3),
        linear("math_sqrt", "求平方根", ActionNodeType.MATH_SQRT, config(ActionMathConfigKey.VALUE to 16, ActionMathConfigKey.OUTPUT_KEY to "result")),
        linear("math_sum", "数值列表累加", ActionNodeType.MATH_SUM, buildJsonObject {
            put(ActionMathConfigKey.VALUES, JsonArray(listOf(JsonPrimitive(10), JsonPrimitive(20), JsonPrimitive(30))))
            put(ActionMathConfigKey.OUTPUT_KEY, JsonPrimitive("result"))
        }),
        linear("math_avg", "数值列表均值", ActionNodeType.MATH_AVG, buildJsonObject {
            put(ActionMathConfigKey.VALUES, JsonArray(listOf(JsonPrimitive(10), JsonPrimitive(20), JsonPrimitive(30))))
            put(ActionMathConfigKey.OUTPUT_KEY, JsonPrimitive("result"))
        }),
        linear("math_log", "自然对数", ActionNodeType.MATH_LOG, config(ActionMathConfigKey.VALUE to 10, ActionMathConfigKey.OUTPUT_KEY to "result")),
        linear("math_exp", "指数计算", ActionNodeType.MATH_EXP, config(ActionMathConfigKey.VALUE to 1, ActionMathConfigKey.OUTPUT_KEY to "result")),
        linear("math_negate", "取负数", ActionNodeType.MATH_NEGATE, config(ActionMathConfigKey.VALUE to 8, ActionMathConfigKey.OUTPUT_KEY to "result")),
        linear(
            "math_round",
            "四舍五入",
            ActionNodeType.MATH_ROUND,
            config(ActionMathConfigKey.VALUE to 3.14159, ActionMathConfigKey.DECIMALS to 2, ActionMathConfigKey.OUTPUT_KEY to "result")
        ),
        linear("math_floor", "向下取整", ActionNodeType.MATH_FLOOR, config(ActionMathConfigKey.VALUE to 3.9, ActionMathConfigKey.OUTPUT_KEY to "result")),
        linear("math_ceil", "向上取整", ActionNodeType.MATH_CEIL, config(ActionMathConfigKey.VALUE to 3.1, ActionMathConfigKey.OUTPUT_KEY to "result")),
        linear("math_abs", "绝对值", ActionNodeType.MATH_ABS, config(ActionMathConfigKey.VALUE to -5, ActionMathConfigKey.OUTPUT_KEY to "result")),
        linear(
            "math_random",
            "随机数",
            ActionNodeType.MATH_RANDOM,
            config(ActionMathConfigKey.MIN to 1, ActionMathConfigKey.MAX to 100, ActionMathConfigKey.OUTPUT_KEY to "result")
        ),
        linear(
            "math_clamp",
            "数值限幅",
            ActionNodeType.MATH_CLAMP,
            config(ActionMathConfigKey.VALUE to 150, ActionMathConfigKey.MIN to 0, ActionMathConfigKey.MAX to 100, ActionMathConfigKey.OUTPUT_KEY to "result")
        ),
    )

    /**
     * 构建 switch 节点命中案例的结构化示例。
     */
    private fun switchSample(): ActionWorkflow = workflow(
        id = "flow_switch",
        name = "测试：流程多值匹配",
        description = "匹配值在案例表中时从 matched 端口继续。",
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "target",
                ActionNodeType.FLOW_SWITCH,
                "匹配状态",
                buildJsonObject {
                    put(ActionFlowConfigKey.VALUE, JsonPrimitive("published"))
                    put(ActionFlowConfigKey.CASES, JsonObject(mapOf("published" to JsonPrimitive(true), "airing" to JsonPrimitive(true))))
                }
            ),
            node("end", ActionNodeType.FLOW_END, "结束")
        ),
        edges = listOf(edge("start", ActionControlPortId.NEXT, "target"), edge("target", ActionControlPortId.MATCHED, "end")),
    )

    /**
     * 构建没有输出边的正常提前结束流程。
     */
    private fun terminal(id: String, name: String, type: String): ActionWorkflow = workflow(
        id = id,
        name = "测试：$name",
        description = "覆盖 `$type` 的正常终止能力。",
        nodes = listOf(node("start", ActionNodeType.FLOW_START, "开始"), node("target", type, name)),
        edges = listOf(edge("start", ActionControlPortId.NEXT, "target")),
    )

    /**
     * 请求 Bangumi 条目标签，逐项读取标签名并组合为逗号分隔文本。
     */
    private fun subjectTagsToToast(): ActionWorkflow = workflow(
        id = "subject_tags_to_toast",
        name = "测试：请求条目标签并循环拼接",
        description = "请求条目 tags，循环读取每个 tag.name，以逗号拼接后显示 Toast。",
        capabilities = setOf(ActionCapability.NETWORK, ActionCapability.NETWORK_LOCAL_COOKIE_ACCESS),
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "request_subject", ActionNodeType.HTTP_REQUEST, "请求条目 JSON", config(
                    ActionHttpConfigKey.URL to "https://next.bgm.tv/p1/subjects/633836",
                    ActionHttpConfigKey.METHOD to "GET",
                    ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE to true,
                )
            ),
            node(
                "extract_tags", ActionNodeType.JSON_EXTRACT, "提取 tags 数组", config(
                    ActionJsonConfigKey.SOURCE to "${'$'}{steps.request_subject.body}",
                    ActionJsonConfigKey.PATH to "$.tags",
                    ActionJsonConfigKey.OUTPUT_KEY to "tags",
                )
            ),
            node(
                "initialize_tag_names", ActionNodeType.SET_VARIABLE, "初始化标签文本", config(
                    ActionDataConfigKey.KEY to "tagNames",
                    ActionDataConfigKey.VALUE to "",
                )
            ),
            node(
                "loop_tags", ActionNodeType.LOOP_FOR_EACH, "遍历标签", config(
                    ActionLoopConfigKey.ITEMS to "${'$'}{vars.tags}",
                )
            ),
            node(
                "is_first_tag", ActionNodeType.CONDITION_EQUALS, "是否第一个标签", config(
                    ActionControlConfigKey.LEFT to "${'$'}{loop.index}",
                    ActionControlConfigKey.RIGHT to 0,
                )
            ),
            node(
                "set_first_tag", ActionNodeType.SET_VARIABLE, "写入第一个标签", config(
                    ActionDataConfigKey.KEY to "tagNames",
                    ActionDataConfigKey.VALUE to "${'$'}{loop.item.name}",
                )
            ),
            node("append_tag", ActionNodeType.TEXT_JOIN, "追加标签名称", buildJsonObject {
                put(ActionTextConfigKey.VALUES, JsonArray(listOf(JsonPrimitive("${'$'}{vars.tagNames}"), JsonPrimitive("${'$'}{loop.item.name}"))))
                put(ActionTextConfigKey.SEPARATOR, JsonPrimitive(","))
                put(ActionTextConfigKey.OUTPUT_KEY, JsonPrimitive("tagNames"))
            }),
            node("next_tag", ActionNodeType.LOOP_NEXT, "继续遍历标签", config(ActionLoopConfigKey.LOOP_ID to "loop_tags")),
            node(
                "show_tags", ActionNodeType.SHOW_TOAST, "显示标签", config(
                    ActionToastConfigKey.MESSAGE to "条目标签：${'$'}{vars.tagNames}",
                )
            ),
            node("end", ActionNodeType.FLOW_END, "结束"),
        ),
        edges = listOf(
            edge("start", ActionControlPortId.NEXT, "request_subject"),
            edge("request_subject", ActionControlPortId.SUCCESS, "extract_tags"),
            edge("extract_tags", ActionControlPortId.NEXT, "initialize_tag_names"),
            edge("initialize_tag_names", ActionControlPortId.NEXT, "loop_tags"),
            edge("loop_tags", ActionControlPortId.BODY, "is_first_tag"),
            edge("is_first_tag", ActionControlPortId.TRUE, "set_first_tag"),
            edge("is_first_tag", ActionControlPortId.FALSE, "append_tag"),
            edge("set_first_tag", ActionControlPortId.NEXT, "next_tag"),
            edge("append_tag", ActionControlPortId.NEXT, "next_tag"),
            edge("loop_tags", ActionControlPortId.COMPLETED, "show_tags"),
            edge("show_tags", ActionControlPortId.SUCCESS, "end"),
        ),
    )

    /**
     * 通过内置的 Bilibili WBI 签名节点搜索 Bilibili 番剧后打开首个结果。
     *
     * 工作流首先初始化 Cookie 并请求 SPI 接口获取 buvid4，随后请求 nav 接口提取实时 WBI 密钥（img_key 与 sub_key），
     * 随后调用内置的 `bilibili.sign_url` 节点生成附带 `wts` 与 `w_rid` 的已加签 URL，
     * 最后发起搜索 HTTP 请求并调起首个搜索结果。
     */
    private fun searchBilibiliBangumiWithWebWbi(): ActionWorkflow = workflow(
        id = "search_bilibili_bangumi_and_open",
        name = "测试：纯工作流 WBI 签名并搜索 Bilibili 番剧",
        description = "输入关键词，调用 Bilibili 签名节点生成 WBI 签名，请求番剧搜索接口并打开首个结果。",
        capabilities = setOf(
            ActionCapability.INPUT_DIALOG,
            ActionCapability.NETWORK,
            ActionCapability.NETWORK_LOCAL_COOKIE_ACCESS,
            ActionCapability.OPEN_EXTERNAL_URL,
        ),
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "input_keyword",
                ActionNodeType.UI_INPUT_DIALOG,
                "输入搜索关键词",
                config(
                    ActionInputDialogConfigKey.TITLE to "搜索 Bilibili 番剧",
                    ActionInputDialogConfigKey.SUBTITLE to "请输入番剧名称",
                    ActionInputDialogConfigKey.OUTPUT_KEY to "keyword",
                ),
            ),
            node(
                "sanitize_keyword",
                ActionNodeType.TEXT_REPLACE_REGEX,
                "移除 WBI 禁止字符",
                config(
                    ActionTextConfigKey.TEXT to "${'$'}{steps.input_keyword.keyword}",
                    ActionTextConfigKey.PATTERN to "[!'()*]",
                    ActionTextConfigKey.REPLACEMENT to "",
                    ActionTextConfigKey.OUTPUT_KEY to "sanitizedKeyword",
                ),
            ),
            node(
                "init_bilibili_cookie",
                ActionNodeType.HTTP_REQUEST,
                "初始化 Bilibili Cookie",
                config(
                    ActionHttpConfigKey.URL to "https://www.bilibili.com/",
                    ActionHttpConfigKey.METHOD to "HEAD",
                    ActionHttpConfigKey.HEADERS to JsonObject(
                        mapOf("User-Agent" to JsonPrimitive(BILIBILI_WEB_USER_AGENT)),
                    ),
                    ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE to true,
                ),
            ),
            node(
                "request_bilibili_spi",
                ActionNodeType.HTTP_REQUEST,
                "请求 Bilibili 设备标识",
                config(
                    ActionHttpConfigKey.URL to "https://api.bilibili.com/x/frontend/finger/spi",
                    ActionHttpConfigKey.METHOD to "GET",
                    ActionHttpConfigKey.HEADERS to JsonObject(
                        mapOf(
                            "Referer" to JsonPrimitive("https://www.bilibili.com/"),
                            "User-Agent" to JsonPrimitive(BILIBILI_WEB_USER_AGENT),
                        ),
                    ),
                    ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE to true,
                ),
            ),
            node(
                "extract_bilibili_buvid4",
                ActionNodeType.JSON_EXTRACT,
                "提取 Bilibili buvid4",
                config(
                    ActionJsonConfigKey.SOURCE to "${'$'}{steps.request_bilibili_spi.body}",
                    ActionJsonConfigKey.PATH to "$.data.b_4",
                    ActionJsonConfigKey.OUTPUT_KEY to "buvid4",
                ),
            ),
            node(
                "request_wbi_keys",
                ActionNodeType.HTTP_REQUEST,
                "请求 WBI 实时密钥",
                config(
                    ActionHttpConfigKey.URL to "https://api.bilibili.com/x/web-interface/nav",
                    ActionHttpConfigKey.METHOD to "GET",
                    ActionHttpConfigKey.HEADERS to JsonObject(
                        mapOf(
                            "Cookie" to JsonPrimitive("buvid4=${'$'}{vars.buvid4}"),
                            "Referer" to JsonPrimitive("https://www.bilibili.com/"),
                            "User-Agent" to JsonPrimitive(BILIBILI_WEB_USER_AGENT),
                        ),
                    ),
                    ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE to true,
                ),
            ),
            node(
                "extract_img_url",
                ActionNodeType.JSON_EXTRACT,
                "提取 img key 地址",
                config(
                    ActionJsonConfigKey.SOURCE to "${'$'}{steps.request_wbi_keys.body}",
                    ActionJsonConfigKey.PATH to "$.data.wbi_img.img_url",
                    ActionJsonConfigKey.OUTPUT_KEY to "imgUrl",
                ),
            ),
            node(
                "extract_sub_url",
                ActionNodeType.JSON_EXTRACT,
                "提取 sub key 地址",
                config(
                    ActionJsonConfigKey.SOURCE to "${'$'}{steps.request_wbi_keys.body}",
                    ActionJsonConfigKey.PATH to "$.data.wbi_img.sub_url",
                    ActionJsonConfigKey.OUTPUT_KEY to "subUrl",
                ),
            ),
            node(
                "extract_img_key",
                ActionNodeType.TEXT_REGEX_MATCH,
                "提取 img key",
                config(
                    ActionTextConfigKey.TEXT to "${'$'}{vars.imgUrl}",
                    ActionTextConfigKey.PATTERN to "/([^/]+)\\.png${'$'}",
                    ActionTextConfigKey.OUTPUT_KEY to "imgKeyGroups",
                ),
            ),
            node(
                "extract_sub_key",
                ActionNodeType.TEXT_REGEX_MATCH,
                "提取 sub key",
                config(
                    ActionTextConfigKey.TEXT to "${'$'}{vars.subUrl}",
                    ActionTextConfigKey.PATTERN to "/([^/]+)\\.png${'$'}",
                    ActionTextConfigKey.OUTPUT_KEY to "subKeyGroups",
                ),
            ),
            node(
                "bilibili_sign_url",
                ActionNodeType.BILIBILI_SIGN_URL,
                "Bilibili WBI 链接加签",
                config(
                    ActionBilibiliConfigKey.URL to "https://api.bilibili.com/x/web-interface/wbi/search/type?search_type=media_bangumi&keyword=${'$'}{vars.sanitizedKeyword}",
                    ActionBilibiliConfigKey.IMG_KEY to "${'$'}{vars.imgKeyGroups.1}",
                    ActionBilibiliConfigKey.SUB_KEY to "${'$'}{vars.subKeyGroups.1}",
                    ActionBilibiliConfigKey.OUTPUT_KEY to "signedUrl",
                ),
            ),
            node(
                "search_bilibili",
                ActionNodeType.HTTP_REQUEST,
                "请求 Bilibili 番剧搜索接口",
                config(
                    ActionHttpConfigKey.URL to "${'$'}{steps.bilibili_sign_url.signedUrl}",
                    ActionHttpConfigKey.METHOD to "GET",
                    ActionHttpConfigKey.HEADERS to JsonObject(
                        mapOf(
                            "Referer" to JsonPrimitive("https://www.bilibili.com/"),
                            "User-Agent" to JsonPrimitive(BILIBILI_WEB_USER_AGENT),
                        ),
                    ),
                    ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE to true,
                ),
            ),
            node(
                "extract_first_result_url",
                ActionNodeType.JSON_EXTRACT,
                "提取首个结果链接",
                config(
                    ActionJsonConfigKey.SOURCE to "${'$'}{steps.search_bilibili.body}",
                    ActionJsonConfigKey.PATH to "$.data.result.0.url",
                    ActionJsonConfigKey.OUTPUT_KEY to "resultUrl",
                ),
            ),
            node(
                "is_result_url_null",
                ActionNodeType.CONDITION_IS_NULL,
                "是否找到结果链接",
                config(
                    ActionControlConfigKey.VALUE to "${'$'}{steps.extract_first_result_url.resultUrl}",
                ),
            ),
            node(
                "open_result",
                ActionNodeType.OPEN_EXTERNAL_URL,
                "打开首个搜索结果",
                config(ActionOpenUrlConfigKey.URL to "${'$'}{steps.extract_first_result_url.resultUrl}"),
            ),
            node(
                "show_no_result",
                ActionNodeType.SHOW_TOAST,
                "提示未找到结果",
                config(ActionToastConfigKey.MESSAGE to "未找到匹配的 Bilibili 番剧结果"),
            ),
            node("end_after_open", ActionNodeType.FLOW_END, "打开后结束"),
            node("end_after_no_result", ActionNodeType.FLOW_END, "无结果后结束"),
        ),
        edges = listOf(
            edge("start", ActionControlPortId.NEXT, "input_keyword"),
            edge("input_keyword", ActionControlPortId.SUCCESS, "sanitize_keyword"),
            edge("sanitize_keyword", ActionControlPortId.NEXT, "init_bilibili_cookie"),
            edge("init_bilibili_cookie", ActionControlPortId.SUCCESS, "request_bilibili_spi"),
            edge("request_bilibili_spi", ActionControlPortId.SUCCESS, "extract_bilibili_buvid4"),
            edge("extract_bilibili_buvid4", ActionControlPortId.NEXT, "request_wbi_keys"),
            edge("request_wbi_keys", ActionControlPortId.SUCCESS, "extract_img_url"),
            edge("extract_img_url", ActionControlPortId.NEXT, "extract_sub_url"),
            edge("extract_sub_url", ActionControlPortId.NEXT, "extract_img_key"),
            edge("extract_img_key", ActionControlPortId.NEXT, "extract_sub_key"),
            edge("extract_sub_key", ActionControlPortId.NEXT, "bilibili_sign_url"),
            edge("bilibili_sign_url", ActionControlPortId.NEXT, "search_bilibili"),
            edge("search_bilibili", ActionControlPortId.SUCCESS, "extract_first_result_url"),
            edge("extract_first_result_url", ActionControlPortId.NEXT, "is_result_url_null"),
            edge("is_result_url_null", ActionControlPortId.TRUE, "show_no_result"),
            edge("is_result_url_null", ActionControlPortId.FALSE, "open_result"),
            edge("open_result", ActionControlPortId.SUCCESS, "end_after_open"),
            edge("show_no_result", ActionControlPortId.SUCCESS, "end_after_no_result"),
        ),
    )

    /**
     * 搜索 Hanime 视频、下载最高画质播放源，并可压缩后复制压缩文件路径。
     */
    private fun searchHanimeVideo(): ActionWorkflow = workflow(
        id = "hanime_search_video",
        name = "测试：Hanime 视频检索与下载",
        description = "搜索 Hanime 视频，提取最高画质播放源；经确认后下载到工作流文件沙箱，可选压缩并复制压缩文件路径。",
        capabilities = setOf(
            ActionCapability.INPUT_DIALOG,
            ActionCapability.NETWORK,
            ActionCapability.NETWORK_LOCAL_COOKIE_ACCESS,
            ActionCapability.NETWORK_COOKIE_SYNC,
            ActionCapability.CONFIRM_DIALOG,
            ActionCapability.CLIPBOARD_WRITE,
        ),
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "input_keyword",
                ActionNodeType.UI_INPUT_DIALOG,
                "输入关键词",
                config(
                    ActionInputDialogConfigKey.TITLE to "搜索 Hanime 视频",
                    ActionInputDialogConfigKey.SUBTITLE to "请输入视频名称关键词",
                    ActionInputDialogConfigKey.OUTPUT_KEY to "keyword",
                ),
            ),
            node(
                "init_hanime_cookie",
                ActionNodeType.HTTP_REQUEST,
                "初始化 Hanime Cookie",
                config(
                    ActionHttpConfigKey.URL to "https://hanime1.me/",
                    ActionHttpConfigKey.METHOD to "HEAD",
                    ActionHttpConfigKey.HEADERS to JsonObject(
                        mapOf(
                            "User-Agent" to JsonPrimitive(System.userAgent()),
                        ),
                    ),
                    ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE to true,
                ),
            ),
            node(
                "is_hanime_cookie_forbidden",
                ActionNodeType.HTTP_STATUS,
                "Cookie 初始化是否被拒绝",
                config(
                    ActionControlConfigKey.STATUS_CODE to "${'$'}{steps.init_hanime_cookie.statusCode}",
                    ActionControlConfigKey.MIN_STATUS_CODE to 403,
                    ActionControlConfigKey.MAX_STATUS_CODE to 403,
                ),
            ),
            node(
                "sync_hanime_cookie",
                ActionNodeType.SYNC_COOKIE,
                "同步 Hanime Cookie",
                config(
                    ActionSyncCookieConfigKey.URL to "https://hanime1.me/",
                    ActionSyncCookieConfigKey.TITLE to "Hanime 需要验证",
                    ActionSyncCookieConfigKey.USER_AGENT to System.userAgent(),
                ),
            ),
            node(
                "search_hanime",
                ActionNodeType.HTTP_REQUEST,
                "请求 Hanime 搜索接口",
                config(
                    ActionHttpConfigKey.URL to "https://hanime1.me/search?query=${'$'}{steps.input_keyword.keyword}",
                    ActionHttpConfigKey.METHOD to "GET",
                    ActionHttpConfigKey.HEADERS to JsonObject(
                        mapOf(
                            "Referer" to JsonPrimitive("https://hanime1.me/"),
                            "User-Agent" to JsonPrimitive(System.userAgent()),
                        ),
                    ),
                    ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE to true,
                ),
            ),
            node(
                "extract_first_video_href",
                ActionNodeType.HTML_QUERY,
                "解析首个视频条目链接",
                config(
                    ActionHtmlConfigKey.HTML to "${'$'}{steps.search_hanime.body.html}",
                    ActionHtmlConfigKey.SELECTOR to ".horizontal-card > a:not([target=\"_blank\"])",
                    ActionHtmlConfigKey.OPERATION to ActionHtmlQueryOperation.ATTRIBUTE,
                    ActionHtmlConfigKey.ATTRIBUTE to "href",
                    ActionHtmlConfigKey.OUTPUT_KEY to "videoHref",
                ),
            ),
            node(
                "is_href_empty",
                ActionNodeType.CONDITION_IS_EMPTY,
                "校验搜索结果是否为空",
                config(
                    ActionControlConfigKey.VALUE to "${'$'}{vars.videoHref}",
                ),
            ),
            node(
                "build_detail_url",
                ActionNodeType.URL_BUILD,
                "构建详情页完整 URL",
                config(
                    ActionUrlConfigKey.BASE_URL to "${'$'}{vars.videoHref}",
                    ActionUrlConfigKey.OUTPUT_KEY to "detailUrl",
                ),
            ),
            node(
                "request_video_detail",
                ActionNodeType.HTTP_REQUEST,
                "请求视频详情页",
                config(
                    ActionHttpConfigKey.URL to "${'$'}{vars.detailUrl}",
                    ActionHttpConfigKey.METHOD to "GET",
                    ActionHttpConfigKey.HEADERS to JsonObject(
                        mapOf(
                            "Referer" to JsonPrimitive("https://hanime1.me/"),
                            "User-Agent" to JsonPrimitive(System.userAgent()),
                        ),
                    ),
                    ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE to true,
                ),
            ),
            node(
                "extract_all_sources",
                ActionNodeType.HTML_QUERY_ALL,
                "解析全部播放源地址",
                config(
                    ActionHtmlConfigKey.HTML to "${'$'}{steps.request_video_detail.body.html}",
                    ActionHtmlConfigKey.SELECTOR to "source",
                    ActionHtmlConfigKey.OPERATION to ActionHtmlQueryOperation.ATTRIBUTE,
                    ActionHtmlConfigKey.ATTRIBUTE to "src",
                    ActionHtmlConfigKey.OUTPUT_KEY to "sources",
                ),
            ),
            node(
                "get_last_source",
                ActionNodeType.ARRAY_LAST,
                "提取最高画质播放源",
                config(
                    ActionArrayConfigKey.VALUES to "${'$'}{vars.sources}",
                    ActionArrayConfigKey.OUTPUT_KEY to "lastSourceUrl",
                ),
            ),
            node(
                "is_source_null",
                ActionNodeType.CONDITION_IS_NULL,
                "校验播放源是否为空",
                config(
                    ActionControlConfigKey.VALUE to "${'$'}{vars.lastSourceUrl}",
                ),
            ),
            node(
                "confirm_download",
                ActionNodeType.UI_CONFIRM,
                "确认下载视频",
                config(
                    ActionConfirmConfigKey.TITLE to "下载 Hanime 视频",
                    ActionConfirmConfigKey.MESSAGE to "是否下载当前视频文件？",
                    ActionConfirmConfigKey.CONFIRM_TEXT to "下载",
                    ActionConfirmConfigKey.CANCEL_TEXT to "取消",
                ),
            ),
            node(
                "download_video",
                ActionNodeType.HTTP_DOWNLOAD,
                "下载视频文件",
                config(
                    ActionHttpConfigKey.URL to "${'$'}{vars.lastSourceUrl}",
                    ActionHttpConfigKey.METHOD to "GET",
                    ActionHttpConfigKey.HEADERS to JsonObject(
                        mapOf(
                            "Referer" to JsonPrimitive("${'$'}{vars.detailUrl}"),
                            "User-Agent" to JsonPrimitive(System.userAgent()),
                        ),
                    ),
                    ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE to true,
                    ActionHttpConfigKey.PATH to "hanime/downloads",
                    ActionHttpConfigKey.OUTPUT_KEY to "download",
                ),
            ),
            node(
                "input_archive_name",
                ActionNodeType.UI_INPUT_DIALOG,
                "输入压缩包名称",
                config(
                    ActionInputDialogConfigKey.TITLE to "是否压缩下载文件？",
                    ActionInputDialogConfigKey.SUBTITLE to "输入压缩包名称后确认压缩；取消或留空则跳过压缩",
                    ActionInputDialogConfigKey.OUTPUT_KEY to "archiveName",
                    ActionInputDialogConfigKey.CONFIRM_TEXT to "压缩",
                    ActionInputDialogConfigKey.CANCEL_TEXT to "跳过",
                ),
            ),
            node(
                "is_archive_name_empty",
                ActionNodeType.CONDITION_IS_EMPTY,
                "是否跳过压缩",
                config(ActionControlConfigKey.VALUE to "${'$'}{steps.input_archive_name.archiveName}"),
            ),
            node(
                "compress_video",
                ActionNodeType.FILE_COMPRESS_ZIP,
                "压缩下载的视频",
                config(
                    ActionFileConfigKey.PATHS to JsonArray(listOf(JsonPrimitive("${'$'}{steps.download_video.download.filePath}"))),
                    ActionFileConfigKey.TO_PATH to "hanime/archives/${'$'}{steps.input_archive_name.archiveName}.zip",
                    ActionFileConfigKey.OUTPUT_KEY to "compressed",
                ),
            ),
            node(
                "confirm_copy_archive_path",
                ActionNodeType.UI_CONFIRM,
                "确认复制压缩文件路径",
                config(
                    ActionConfirmConfigKey.TITLE to "压缩完成",
                    ActionConfirmConfigKey.MESSAGE to "压缩完成，是否复制压缩文件路径？",
                    ActionConfirmConfigKey.CONFIRM_TEXT to "复制路径",
                    ActionConfirmConfigKey.CANCEL_TEXT to "完成",
                ),
            ),
            node(
                "get_working_directory",
                ActionNodeType.FILE_GET_WORKING_DIRECTORY,
                "获取工作流文件目录",
                config(ActionFileConfigKey.OUTPUT_KEY to "workingDirectory"),
            ),
            node(
                "copy_archive_path",
                ActionNodeType.WRITE_CLIPBOARD,
                "复制压缩文件路径",
                config(
                    ActionClipboardConfigKey.TEXT to "${'$'}{vars.workingDirectory}/hanime/archives/${'$'}{steps.input_archive_name.archiveName}.zip",
                ),
            ),
            node("show_download_failed", ActionNodeType.SHOW_TOAST, "提示下载失败", config(ActionToastConfigKey.MESSAGE to "视频下载失败，请稍后重试")),
            node("show_compress_failed", ActionNodeType.SHOW_TOAST, "提示压缩失败", config(ActionToastConfigKey.MESSAGE to "视频压缩失败，已保留下载文件")),
            node(
                "show_no_video",
                ActionNodeType.SHOW_TOAST,
                "提示未找到视频",
                config(ActionToastConfigKey.MESSAGE to "未找到匹配的 Hanime 视频"),
            ),
            node(
                "show_no_source",
                ActionNodeType.SHOW_TOAST,
                "提示未找到播放源",
                config(ActionToastConfigKey.MESSAGE to "未找到有效的视频播放源"),
            ),
            node("end_after_download", ActionNodeType.FLOW_END, "下载后结束"),
            node("end_after_copy", ActionNodeType.FLOW_END, "复制后结束"),
            node("end_after_download_failed", ActionNodeType.FLOW_END, "下载失败后结束"),
            node("end_after_compress_failed", ActionNodeType.FLOW_END, "压缩失败后结束"),
            node("end_after_no_video", ActionNodeType.FLOW_END, "无视频后结束"),
            node("end_after_no_source", ActionNodeType.FLOW_END, "无源后结束"),
        ),
        edges = listOf(
            edge("start", ActionControlPortId.NEXT, "input_keyword"),
            edge("input_keyword", ActionControlPortId.SUCCESS, "init_hanime_cookie"),
            edge("init_hanime_cookie", ActionControlPortId.SUCCESS, "is_hanime_cookie_forbidden"),
            edge("is_hanime_cookie_forbidden", ActionControlPortId.TRUE, "sync_hanime_cookie"),
            edge("is_hanime_cookie_forbidden", ActionControlPortId.FALSE, "search_hanime"),
            edge("sync_hanime_cookie", ActionControlPortId.SUCCESS, "search_hanime"),
            edge("sync_hanime_cookie", ActionControlPortId.FAILURE, "search_hanime"),
            edge("search_hanime", ActionControlPortId.SUCCESS, "extract_first_video_href"),
            edge("extract_first_video_href", ActionControlPortId.NEXT, "is_href_empty"),
            edge("is_href_empty", ActionControlPortId.TRUE, "show_no_video"),
            edge("is_href_empty", ActionControlPortId.FALSE, "build_detail_url"),
            edge("build_detail_url", ActionControlPortId.NEXT, "request_video_detail"),
            edge("request_video_detail", ActionControlPortId.SUCCESS, "extract_all_sources"),
            edge("extract_all_sources", ActionControlPortId.NEXT, "get_last_source"),
            edge("get_last_source", ActionControlPortId.NEXT, "is_source_null"),
            edge("is_source_null", ActionControlPortId.TRUE, "show_no_source"),
            edge("is_source_null", ActionControlPortId.FALSE, "confirm_download"),
            edge("confirm_download", ActionControlPortId.SUCCESS, "download_video"),
            edge("confirm_download", ActionControlPortId.FAILURE, "end_after_download"),
            edge("download_video", ActionControlPortId.SUCCESS, "input_archive_name"),
            edge("download_video", ActionControlPortId.FAILURE, "show_download_failed"),
            edge("input_archive_name", ActionControlPortId.SUCCESS, "is_archive_name_empty"),
            edge("input_archive_name", ActionControlPortId.FAILURE, "end_after_download"),
            edge("is_archive_name_empty", ActionControlPortId.TRUE, "end_after_download"),
            edge("is_archive_name_empty", ActionControlPortId.FALSE, "compress_video"),
            edge("compress_video", ActionControlPortId.NEXT, "get_working_directory"),
            edge("compress_video", ActionControlPortId.FAILURE, "show_compress_failed"),
            edge("get_working_directory", ActionControlPortId.NEXT, "confirm_copy_archive_path"),
            edge("confirm_copy_archive_path", ActionControlPortId.SUCCESS, "copy_archive_path"),
            edge("confirm_copy_archive_path", ActionControlPortId.FAILURE, "end_after_copy"),
            edge("copy_archive_path", ActionControlPortId.SUCCESS, "end_after_copy"),
            edge("show_download_failed", ActionControlPortId.SUCCESS, "end_after_download_failed"),
            edge("show_compress_failed", ActionControlPortId.SUCCESS, "end_after_compress_failed"),
            edge("show_no_video", ActionControlPortId.SUCCESS, "end_after_no_video"),
            edge("show_no_source", ActionControlPortId.SUCCESS, "end_after_no_source"),
        ),
    )

    /**
     * 搜索 MangaDex 漫画、获取章节高清图片 URL 列表并调起图片预览。
     */
    private fun searchMangaDexAndPreviewImages(): ActionWorkflow = workflow(
        id = "search_mangadex_manga_and_preview",
        name = "测试：MangaDex 漫画搜索与章节图片预览",
        description = "搜索 MangaDex 漫画，获取目标章节并拼接高清图片 URL 列表后调起全屏图片预览。",
        capabilities = setOf(
            ActionCapability.INPUT_DIALOG,
            ActionCapability.NETWORK,
            ActionCapability.IMAGE_PREVIEW,
        ),
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "input_keyword",
                ActionNodeType.UI_INPUT_DIALOG,
                "输入漫画关键词",
                config(
                    ActionInputDialogConfigKey.TITLE to "搜索 MangaDex 漫画",
                    ActionInputDialogConfigKey.SUBTITLE to "请输入漫画名称关键词",
                    ActionInputDialogConfigKey.OUTPUT_KEY to "keyword",
                ),
            ),
            node(
                "search_manga",
                ActionNodeType.HTTP_REQUEST,
                "搜索 MangaDex 漫画",
                config(
                    ActionHttpConfigKey.URL to "https://api.mangadex.org/manga?title=${'$'}{steps.input_keyword.keyword}",
                    ActionHttpConfigKey.METHOD to "GET",
                ),
            ),
            node(
                "extract_manga_id",
                ActionNodeType.JSON_EXTRACT,
                "提取漫画 ID",
                config(
                    ActionJsonConfigKey.SOURCE to "${'$'}{steps.search_manga.body}",
                    ActionJsonConfigKey.PATH to "$.data.0.id",
                    ActionJsonConfigKey.OUTPUT_KEY to "mangaId",
                ),
            ),
            node(
                "is_manga_found",
                ActionNodeType.CONDITION_IS_NULL,
                "是否找到漫画",
                config(
                    ActionControlConfigKey.VALUE to "${'$'}{vars.mangaId}",
                ),
            ),
            node(
                "get_chapters",
                ActionNodeType.HTTP_REQUEST,
                "获取漫画章节列表",
                config(
                    ActionHttpConfigKey.URL to "https://api.mangadex.org/manga/${'$'}{vars.mangaId}/feed",
                    ActionHttpConfigKey.METHOD to "GET",
                ),
            ),
            node(
                "extract_chapter_id",
                ActionNodeType.JSON_EXTRACT,
                "提取首个章节 ID",
                config(
                    ActionJsonConfigKey.SOURCE to "${'$'}{steps.get_chapters.body}",
                    ActionJsonConfigKey.PATH to "$.data.0.id",
                    ActionJsonConfigKey.OUTPUT_KEY to "chapterId",
                ),
            ),
            node(
                "get_chapter_server",
                ActionNodeType.HTTP_REQUEST,
                "获取章节服务器信息",
                config(
                    ActionHttpConfigKey.URL to "https://api.mangadex.org/at-home/server/${'$'}{vars.chapterId}",
                    ActionHttpConfigKey.METHOD to "GET",
                ),
            ),
            node(
                "extract_base_url",
                ActionNodeType.JSON_EXTRACT,
                "提取基础 URL",
                config(
                    ActionJsonConfigKey.SOURCE to "${'$'}{steps.get_chapter_server.body}",
                    ActionJsonConfigKey.PATH to "$.baseUrl",
                    ActionJsonConfigKey.OUTPUT_KEY to "baseUrl",
                ),
            ),
            node(
                "extract_hash",
                ActionNodeType.JSON_EXTRACT,
                "提取章节 Hash",
                config(
                    ActionJsonConfigKey.SOURCE to "${'$'}{steps.get_chapter_server.body}",
                    ActionJsonConfigKey.PATH to "$.chapter.hash",
                    ActionJsonConfigKey.OUTPUT_KEY to "hash",
                ),
            ),
            node(
                "extract_filenames",
                ActionNodeType.JSON_EXTRACT,
                "提取图片文件名列表",
                config(
                    ActionJsonConfigKey.SOURCE to "${'$'}{steps.get_chapter_server.body}",
                    ActionJsonConfigKey.PATH to "$.chapter.data",
                    ActionJsonConfigKey.OUTPUT_KEY to "filenames",
                ),
            ),
            node(
                "init_image_list",
                ActionNodeType.SET_VARIABLE,
                "初始化图片 URL 列表",
                config(
                    ActionDataConfigKey.KEY to "imageUrlList",
                    ActionDataConfigKey.VALUE to JsonArray(emptyList()),
                ),
            ),
            node(
                "loop_filenames",
                ActionNodeType.LOOP_FOR_EACH,
                "遍历图片文件名",
                config(
                    ActionLoopConfigKey.ITEMS to "${'$'}{vars.filenames}",
                ),
            ),
            node(
                "build_image_url",
                ActionNodeType.TEMPLATE,
                "拼接图片全路径",
                config(
                    ActionDataConfigKey.TEMPLATE to "${'$'}{vars.baseUrl}/data/${'$'}{vars.hash}/${'$'}{loop.item}",
                    ActionDataConfigKey.OUTPUT_KEY to "imageUrl",
                ),
            ),
            node(
                "append_image_url",
                ActionNodeType.ARRAY_APPEND,
                "追加到图片列表",
                config(
                    ActionArrayConfigKey.VALUES to "${'$'}{vars.imageUrlList}",
                    ActionArrayConfigKey.VALUE to "${'$'}{vars.imageUrl}",
                    ActionArrayConfigKey.OUTPUT_KEY to "imageUrlList",
                ),
            ),
            node(
                "next_filename",
                ActionNodeType.LOOP_NEXT,
                "继续下一张图片",
                config(ActionLoopConfigKey.LOOP_ID to "loop_filenames"),
            ),
            node(
                "preview_images",
                ActionNodeType.IMAGE_PREVIEW,
                "打开漫画图片预览",
                config(
                    ActionImagePreviewConfigKey.INDEX to 0,
                    ActionImagePreviewConfigKey.IMAGES to "${'$'}{vars.imageUrlList}",
                ),
            ),
            node(
                "show_no_manga",
                ActionNodeType.SHOW_TOAST,
                "提示未找到漫画",
                config(ActionToastConfigKey.MESSAGE to "未找到匹配的 MangaDex 漫画"),
            ),
            node("end_after_preview", ActionNodeType.FLOW_END, "预览后结束"),
            node("end_after_no_manga", ActionNodeType.FLOW_END, "无漫画后结束"),
        ),
        edges = listOf(
            edge("start", ActionControlPortId.NEXT, "input_keyword"),
            edge("input_keyword", ActionControlPortId.SUCCESS, "search_manga"),
            edge("search_manga", ActionControlPortId.SUCCESS, "extract_manga_id"),
            edge("extract_manga_id", ActionControlPortId.NEXT, "is_manga_found"),
            edge("is_manga_found", ActionControlPortId.TRUE, "show_no_manga"),
            edge("is_manga_found", ActionControlPortId.FALSE, "get_chapters"),
            edge("get_chapters", ActionControlPortId.SUCCESS, "extract_chapter_id"),
            edge("extract_chapter_id", ActionControlPortId.NEXT, "get_chapter_server"),
            edge("get_chapter_server", ActionControlPortId.SUCCESS, "extract_base_url"),
            edge("extract_base_url", ActionControlPortId.NEXT, "extract_hash"),
            edge("extract_hash", ActionControlPortId.NEXT, "extract_filenames"),
            edge("extract_filenames", ActionControlPortId.NEXT, "init_image_list"),
            edge("init_image_list", ActionControlPortId.NEXT, "loop_filenames"),
            edge("loop_filenames", ActionControlPortId.BODY, "build_image_url"),
            edge("build_image_url", ActionControlPortId.NEXT, "append_image_url"),
            edge("append_image_url", ActionControlPortId.NEXT, "next_filename"),
            edge("loop_filenames", ActionControlPortId.COMPLETED, "preview_images"),
            edge("preview_images", ActionControlPortId.SUCCESS, "end_after_preview"),
            edge("show_no_manga", ActionControlPortId.SUCCESS, "end_after_no_manga"),
        ),
    )

    private fun math(id: String, name: String, type: String, left: Int, right: Int): ActionWorkflow {
        return linear(
            id = id,
            name = name,
            type = type,
            nodeConfig = config(
                ActionMathConfigKey.LEFT to left,
                ActionMathConfigKey.RIGHT to right,
                ActionMathConfigKey.OUTPUT_KEY to "result"
            )
        )
    }

    private fun forkAndJoinSample(): ActionWorkflow = workflow(
        id = "flow_fork_join",
        name = "多路分叉与合流 (Fork-Join)",
        description = "演示从单个入口节点分叉出两条并行分支（左路与右路），各自计算变量后再合流汇入 Join 节点合并数据并弹出 Toast 提示。",
        capabilities = emptySet(),
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "流程开始"),
            node("node_left", ActionNodeType.SET_VARIABLE, "左路分支", config(ActionDataConfigKey.KEY to "left_val", ActionDataConfigKey.VALUE to "左路数据")),
            node("node_right", ActionNodeType.SET_VARIABLE, "右路分支", config(ActionDataConfigKey.KEY to "right_val", ActionDataConfigKey.VALUE to "右路数据")),
            node(
                "node_join",
                ActionNodeType.TEMPLATE,
                "多路合流",
                config(
                    ActionDataConfigKey.TEMPLATE to "合流结果: \${vars.left_val} + \${vars.right_val}",
                    ActionDataConfigKey.OUTPUT_KEY to "result"
                )
            ),
            node("toast", ActionNodeType.SHOW_TOAST, "弹出结果", config(ActionToastConfigKey.MESSAGE to "\${steps.node_join.result}")),
            node("end", ActionNodeType.FLOW_END, "流程结束"),
        ),
        edges = listOf(
            edge("start", ActionControlPortId.NEXT, "node_left"),
            edge("start", ActionControlPortId.NEXT, "node_right"),
            edge("node_left", ActionControlPortId.NEXT, "node_join"),
            edge("node_right", ActionControlPortId.NEXT, "node_join"),
            edge("node_join", ActionControlPortId.NEXT, "toast"),
            edge("toast", ActionControlPortId.SUCCESS, "end"),
        ),
    )

    private fun complexWorkflowSample(): ActionWorkflow = workflow(
        id = "complex_dag_search",
        name = "超级DAG复杂综合工作流",
        description = "全功能综合演示：包含输入弹窗 -> Top级并发双分支(URL构造+UUID生成) -> Top级合流 -> 数组遍历(Loop.for_each) -> 条件过滤(Control.if) -> 循环内双分支(名称格式化+Math积分计算) -> 循环内合流 -> Toast提示 -> 全局确认弹窗",
        capabilities = setOf(
            ActionCapability.INPUT_DIALOG,
            ActionCapability.CONFIRM_DIALOG,
        ),
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "流程起始点"),
            node(
                "input_keyword",
                ActionNodeType.UI_INPUT_DIALOG,
                "关键词输入",
                config(
                    ActionInputDialogConfigKey.TITLE to "番剧搜索",
                    ActionInputDialogConfigKey.SUBTITLE to "请输入要查询的动画名称",
                    ActionInputDialogConfigKey.OUTPUT_KEY to "keyword",
                ),
            ),
            node(
                "fork_start",
                ActionNodeType.SET_VARIABLE,
                "保存搜索词",
                config(
                    ActionDataConfigKey.KEY to "search_kw",
                    ActionDataConfigKey.VALUE to "\${steps.input_keyword.keyword}",
                ),
            ),
            node(
                "branch_url",
                ActionNodeType.URL_BUILD,
                "分支A: 构造API接口",
                config(
                    ActionUrlConfigKey.BASE_URL to "https://api.bgm.tv/search/subject/\${vars.search_kw}",
                    ActionUrlConfigKey.OUTPUT_KEY to "api_url",
                ),
            ),
            node(
                "branch_meta",
                ActionNodeType.DATA_UUID,
                "分支B: 生成TraceID",
                config(
                    ActionDataConfigKey.OUTPUT_KEY to "req_id",
                ),
            ),
            node(
                "join_meta",
                ActionNodeType.TEMPLATE,
                "Top级分支合流",
                config(
                    ActionDataConfigKey.TEMPLATE to "请求摘要 [TraceID=\${steps.branch_meta.req_id}]: \${steps.branch_url.api_url}",
                    ActionDataConfigKey.OUTPUT_KEY to "summary",
                ),
            ),
            node(
                "create_array",
                ActionNodeType.ARRAY_CREATE,
                "初始化动画列表",
                buildJsonObject {
                    put(
                        ActionArrayConfigKey.VALUES,
                        buildJsonArray {
                            add(JsonPrimitive("孤独摇滚！"))
                            add(JsonPrimitive("轻音少女"))
                            add(JsonPrimitive("命运石之门"))
                            add(JsonPrimitive("攻壳机动队"))
                        },
                    )
                    put(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("items"))
                },
            ),
            node(
                "loop_items",
                ActionNodeType.LOOP_FOR_EACH,
                "遍历动画列表",
                config(
                    ActionLoopConfigKey.ITEMS to "\${steps.create_array.items}",
                    ActionLoopConfigKey.MAX_ITERATIONS to 10,
                ),
            ),
            node(
                "check_condition",
                ActionNodeType.CONDITION_IF,
                "条件过滤 (\${loop.index > 0})",
                config(
                    ActionControlConfigKey.CONDITION to "\${loop.index > 0}",
                ),
            ),
            node(
                "loop_fork_left",
                ActionNodeType.TEMPLATE,
                "循环内分支A: 格式化名称",
                config(
                    ActionDataConfigKey.TEMPLATE to "[No.\${loop.index + 1}] \${loop.item}",
                    ActionDataConfigKey.OUTPUT_KEY to "formatted_name",
                ),
            ),
            node(
                "loop_fork_right",
                ActionNodeType.MATH_ADD,
                "循环内分支B: 计算匹配分",
                config(
                    ActionMathConfigKey.LEFT to "\${loop.index}",
                    ActionMathConfigKey.RIGHT to 90,
                    ActionMathConfigKey.OUTPUT_KEY to "tag_score",
                ),
            ),
            node(
                "loop_join",
                ActionNodeType.TEMPLATE,
                "循环内分支合流",
                config(
                    ActionDataConfigKey.TEMPLATE to "\${steps.loop_fork_left.formatted_name} (匹配度: \${steps.loop_fork_right.tag_score}分)",
                    ActionDataConfigKey.OUTPUT_KEY to "item_desc",
                ),
            ),
            node(
                "show_item_toast",
                ActionNodeType.SHOW_TOAST,
                "显示列表项Toast",
                config(
                    ActionToastConfigKey.MESSAGE to "\${steps.loop_join.item_desc}",
                ),
            ),
            node(
                "loop_next",
                ActionNodeType.LOOP_NEXT,
                "进入下一次迭代",
                config(
                    ActionLoopConfigKey.LOOP_ID to "loop_items",
                ),
            ),
            node(
                "confirm_dialog",
                ActionNodeType.UI_CONFIRM,
                "执行完毕确认",
                config(
                    ActionConfirmConfigKey.TITLE to "工作流执行成功",
                    ActionConfirmConfigKey.MESSAGE to "\${steps.join_meta.summary}",
                ),
            ),
            node("end", ActionNodeType.FLOW_END, "流程结束"),
        ),
        edges = listOf(
            edge("start", ActionControlPortId.NEXT, "input_keyword"),
            edge("input_keyword", ActionControlPortId.SUCCESS, "fork_start"),

            // Top级多路分叉 Fan-Out
            edge("fork_start", ActionControlPortId.NEXT, "branch_url"),
            edge("fork_start", ActionControlPortId.NEXT, "branch_meta"),

            // Top级多路合流 Fan-In
            edge("branch_url", ActionControlPortId.NEXT, "join_meta"),
            edge("branch_meta", ActionControlPortId.NEXT, "join_meta"),

            edge("join_meta", ActionControlPortId.NEXT, "create_array"),
            edge("create_array", ActionControlPortId.NEXT, "loop_items"),

            // 循环体控制
            edge("loop_items", ActionControlPortId.BODY, "check_condition"),

            // 条件判断
            edge("check_condition", ActionControlPortId.TRUE, "loop_fork_left"),
            edge("check_condition", ActionControlPortId.TRUE, "loop_fork_right"),
            edge("check_condition", ActionControlPortId.FALSE, "loop_next"),

            // 循环内多路合流 Fan-In
            edge("loop_fork_left", ActionControlPortId.NEXT, "loop_join"),
            edge("loop_fork_right", ActionControlPortId.NEXT, "loop_join"),

            edge("loop_join", ActionControlPortId.NEXT, "show_item_toast"),
            edge("show_item_toast", ActionControlPortId.SUCCESS, "loop_next"),

            // 循环完成
            edge("loop_items", ActionControlPortId.COMPLETED, "confirm_dialog"),
            edge("confirm_dialog", ActionControlPortId.SUCCESS, "end"),
            edge("confirm_dialog", ActionControlPortId.FAILURE, "end"),
        ),
    )

    private fun linear(id: String, name: String, type: String, nodeConfig: JsonObject = JsonObject(emptyMap()), capabilities: Set<String> = emptySet()): ActionWorkflow {
        if (type == ActionNodeType.FLOW_END) {
            return workflow(
                id = id,
                name = "测试：$name",
                description = "覆盖 `${ActionNodeType.FLOW_START}` 与 `${ActionNodeType.FLOW_END}` 的基本流程。",
                capabilities = capabilities,
                nodes = listOf(node("start", ActionNodeType.FLOW_START, "开始"), node("end", ActionNodeType.FLOW_END, "结束")),
                edges = listOf(edge("start", ActionControlPortId.NEXT, "end")),
            )
        }
        val outputPort = if (type in sideEffectTypes) ActionControlPortId.SUCCESS else ActionControlPortId.NEXT
        return workflow(
            id = id,
            name = "测试：$name",
            description = "覆盖 `$type` 节点的可直接运行测试。",
            capabilities = capabilities,
            nodes = listOf(node("start", ActionNodeType.FLOW_START, "开始"), node("target", type, name, nodeConfig), node("end", ActionNodeType.FLOW_END, "结束")),
            edges = listOf(edge("start", ActionControlPortId.NEXT, "target"), edge("target", outputPort, "end")),
        )
    }

    /**
     * 在同一工作流沙箱中写入日志后读取，验证文件节点的隔离存储。
     */
    private fun fileReadWriteSample(): ActionWorkflow = workflow(
        id = "file_read_write",
        name = "测试：文件沙箱读写",
        description = "写入工作流专属日志文件后读取内容，文件不会暴露到其他工作流沙箱。",
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "write", ActionNodeType.FILE_WRITE_TEXT, "写入日志", config(
                    ActionFileConfigKey.PATH to "logs/workflow.log",
                    ActionFileConfigKey.TEXT to "Bangumi workflow started",
                    ActionFileConfigKey.OUTPUT_KEY to "written",
                )
            ),
            node(
                "read", ActionNodeType.FILE_READ_TEXT, "读取日志", config(
                    ActionFileConfigKey.PATH to "logs/workflow.log",
                    ActionFileConfigKey.OUTPUT_KEY to "logContent",
                )
            ),
            node("end", ActionNodeType.FLOW_END, "结束"),
        ),
        edges = listOf(
            edge("start", ActionControlPortId.NEXT, "write"),
            edge("write", ActionControlPortId.NEXT, "read"),
            edge("read", ActionControlPortId.NEXT, "end"),
        ),
    )

    private fun condition(id: String, name: String, type: String, nodeConfig: JsonObject): ActionWorkflow = workflow(
        id = id,
        name = "测试：$name",
        description = "覆盖 `$type` 的真值分支。",
        nodes = listOf(node("start", ActionNodeType.FLOW_START, "开始"), node("target", type, name, nodeConfig), node("end", ActionNodeType.FLOW_END, "结束")),
        edges = listOf(edge("start", ActionControlPortId.NEXT, "target"), edge("target", ActionControlPortId.TRUE, "end")),
    )

    private fun loop(id: String, name: String, loopType: String, controlType: String, loopConfig: JsonObject): ActionWorkflow = workflow(
        id = id,
        name = "测试：$name",
        description = "覆盖 `$loopType` 与 `$controlType` 的结构化循环执行。",
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"), node("loop", loopType, name, loopConfig),
            node("control", controlType, "循环控制", config(ActionLoopConfigKey.LOOP_ID to "loop")), node("end", ActionNodeType.FLOW_END, "结束"),
        ),
        edges = listOf(edge("start", ActionControlPortId.NEXT, "loop"), edge("loop", ActionControlPortId.BODY, "control"), edge("loop", ActionControlPortId.COMPLETED, "end")),
    )

    // 错误与异常故障测试样例构建方法
    private fun errorDivideByZeroSample(): ActionWorkflow = workflow(
        id = "error_divide_by_zero",
        name = "错误测试：除零故障",
        description = "验证 math.divide 节点在除数为 0 时的除零拦截与格式化日志报告。",
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "divide",
                ActionNodeType.MATH_DIVIDE,
                "除法运算",
                config(ActionMathConfigKey.LEFT to 100, ActionMathConfigKey.RIGHT to 0, ActionMathConfigKey.OUTPUT_KEY to "res")
            ),
        ),
        edges = listOf(edge("start", ActionControlPortId.NEXT, "divide")),
    )

    private fun errorSqrtNegativeSample(): ActionWorkflow = workflow(
        id = "error_sqrt_negative",
        name = "错误测试：负数平方根",
        description = "验证 math.sqrt 节点传入负数时的非法参数校验与排查建议提示。",
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "sqrt",
                ActionNodeType.MATH_SQRT,
                "平方根运算",
                config(ActionMathConfigKey.VALUE to -9.0, ActionMathConfigKey.OUTPUT_KEY to "res")
            ),
        ),
        edges = listOf(edge("start", ActionControlPortId.NEXT, "sqrt")),
    )

    private fun errorAssertFailedSample(): ActionWorkflow = workflow(
        id = "error_assert_failed",
        name = "错误测试：流程断言失败",
        description = "验证 flow.assert 表达式计算为 false 时的异常断言抛出与失败事件分发。",
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "assert",
                ActionNodeType.FLOW_ASSERT,
                "检查状态",
                config(ActionFlowConfigKey.CONDITION to "\${1 == 2}", ActionFlowConfigKey.MESSAGE to "用户权限校验失败")
            ),
        ),
        edges = listOf(edge("start", ActionControlPortId.NEXT, "assert")),
    )

    private fun errorJsonParseMalformedSample(): ActionWorkflow = workflow(
        id = "error_json_parse_malformed",
        name = "错误测试：损坏的 JSON 格式",
        description = "验证 json.parse 节点遇到非法 JSON 结构时的 JsonDecodingException 捕获。",
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "json_parse",
                ActionNodeType.JSON_PARSE,
                "解析 JSON",
                config(ActionJsonConfigKey.TEXT to "{invalid_json_text:", ActionJsonConfigKey.OUTPUT_KEY to "res")
            ),
        ),
        edges = listOf(edge("start", ActionControlPortId.NEXT, "json_parse")),
    )

    private fun errorArrayOutOfBoundsSample(): ActionWorkflow = workflow(
        id = "error_array_out_of_bounds",
        name = "错误测试：数组下标越界",
        description = "验证 array.remove_at 节点删除超限 index 时的越界防护。",
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "array_remove",
                ActionNodeType.ARRAY_REMOVE_AT,
                "删除元素",
                config(ActionArrayConfigKey.VALUES to buildJsonArray { add(JsonPrimitive("item1")) }, ActionArrayConfigKey.INDEX to 99, ActionArrayConfigKey.OUTPUT_KEY to "res")
            ),
        ),
        edges = listOf(edge("start", ActionControlPortId.NEXT, "array_remove")),
    )

    private fun errorUrlParseMalformedSample(): ActionWorkflow = workflow(
        id = "error_url_parse_malformed",
        name = "错误测试：非法 URL 解析",
        description = "验证 url.parse 节点处理损坏 URL 时的 URLParserException 异常捕获。",
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "url_parse",
                ActionNodeType.URL_PARSE,
                "解析 URL",
                config(ActionUrlConfigKey.URL to "ht tps://invalid url string", ActionUrlConfigKey.OUTPUT_KEY to "res")
            ),
        ),
        edges = listOf(edge("start", ActionControlPortId.NEXT, "url_parse")),
    )

    private fun errorHtmlSelectorInvalidSample(): ActionWorkflow = workflow(
        id = "error_html_selector_invalid",
        name = "错误测试：非法 CSS 选择器",
        description = "验证 html.query 节点在选择器语法错误时的 SelectorParseException 拦截。",
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "html_query",
                ActionNodeType.HTML_QUERY,
                "DOM 查询",
                config(
                    ActionHtmlConfigKey.HTML to "<div>text</div>",
                    ActionHtmlConfigKey.SELECTOR to ":::bad_selector:::",
                    ActionHtmlConfigKey.OPERATION to "text",
                    ActionHtmlConfigKey.OUTPUT_KEY to "res",
                )
            ),
        ),
        edges = listOf(edge("start", ActionControlPortId.NEXT, "html_query")),
    )

    private fun errorMissingConfigSample(): ActionWorkflow = workflow(
        id = "error_missing_config",
        name = "错误测试：缺少必需配置项",
        description = "验证节点缺少必需配置项时，校验器 (Validator) 在静态校验阶段的拦截。",
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "template",
                ActionNodeType.TEMPLATE,
                "模板替换",
                config(ActionDataConfigKey.OUTPUT_KEY to "res")
            ),
        ),
        edges = listOf(edge("start", ActionControlPortId.NEXT, "template")),
    )

    private fun workflow(id: String, name: String, description: String, capabilities: Set<String> = emptySet(), nodes: List<ActionNode>, edges: List<ActionEdge>) = ActionWorkflow(
        id = "workflow_sample_$id",
        name = name,
        description = description,
        requiredCapabilities = capabilities.toPersistentList(),
        entryNodeId = "start",
        nodes = nodes.toPersistentList(),
        edges = edges.toPersistentList(),
    )

    private fun node(id: String, type: String, label: String, config: JsonObject = JsonObject(emptyMap())) = ActionNode(id = id, type = type, label = label, config = config)

    private fun edge(sourceNodeId: String, sourcePortId: String, targetNodeId: String) = ActionEdge(
        id = "$sourceNodeId-${sourcePortId}-$targetNodeId", source = ActionPortRef(sourceNodeId, sourcePortId), target = ActionPortRef(targetNodeId, ActionControlPortId.IN),
    )

    private fun config(vararg values: Pair<String, Any?>): JsonObject = buildJsonObject {
        values.forEach { (key, value) -> put(key, value.toJsonElement()) }
    }

    private fun Any?.toJsonElement() = when (this) {
        null -> JsonNull
        is kotlinx.serialization.json.JsonElement -> this
        is Boolean -> JsonPrimitive(this)
        is Int -> JsonPrimitive(this)
        is Long -> JsonPrimitive(this)
        is Double -> JsonPrimitive(this)
        is String -> JsonPrimitive(this)
        else -> error("不支持的示例配置类型：${this::class}")
    }
}
