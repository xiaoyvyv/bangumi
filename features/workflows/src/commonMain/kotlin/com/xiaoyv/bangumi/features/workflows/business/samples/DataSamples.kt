package com.xiaoyv.bangumi.features.workflows.business.samples

import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionWorkflow
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayFilterOperator
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionBilibiliConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCodecConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCryptoConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCsvConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDateConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionJsonConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionMathConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionObjectConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionUrlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionXmlConfigKey
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

/**
 * 基础数据、对象操作、数组处理、文本正则、数值计算、时间与格式转换工作流样例集合。
 */
internal object DataSamples {
    val all: List<ActionWorkflow> = buildList {
        // Data 节点
        add(linear("data_set_var", "写入临时变量", ActionNodeType.SET_VARIABLE, config(ActionDataConfigKey.KEY to "enabled", ActionDataConfigKey.VALUE to true)))
        add(linear("data_remove_var", "删除变量", ActionNodeType.DATA_REMOVE, config(ActionDataConfigKey.KEY to "enabled")))
        add(
            linear(
                "data_template",
                "模板插值",
                ActionNodeType.TEMPLATE,
                config(ActionDataConfigKey.TEMPLATE to "你好，\${input.nameCn}", ActionDataConfigKey.OUTPUT_KEY to "greeting")
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
                config(ActionObjectConfigKey.OBJECT to "\${input}", ActionObjectConfigKey.PATH to "$.nameCn", ActionObjectConfigKey.OUTPUT_KEY to "name")
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

        // Bilibili WBI 加签
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
    }

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
}
