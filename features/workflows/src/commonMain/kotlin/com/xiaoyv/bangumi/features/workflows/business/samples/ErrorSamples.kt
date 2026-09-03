package com.xiaoyv.bangumi.features.workflows.business.samples

import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionWorkflow
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHtmlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionJsonConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionMathConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionUrlConfigKey
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray

/**
 * 错误与异常故障拦截测试工作流样例集合。
 */
internal object ErrorSamples {
    val all: List<ActionWorkflow> = listOf(
        errorDivideByZeroSample(),
        errorSqrtNegativeSample(),
        errorAssertFailedSample(),
        errorJsonParseMalformedSample(),
        errorArrayOutOfBoundsSample(),
        errorUrlParseMalformedSample(),
        errorHtmlSelectorInvalidSample(),
        errorHtmlParseNonStringSample(),
        errorHtmlSelectNonElementSample(),
        errorHtmlMapNonElementsSample(),
        errorMissingConfigSample(),
    )

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
        description = "验证 html.select 节点在选择器语法错误时的 SelectorParseException 拦截。",
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "html_parse",
                ActionNodeType.HTML_PARSE,
                "解析 HTML",
                config(
                    ActionHtmlConfigKey.HTML to "<div>text</div>",
                    ActionHtmlConfigKey.OUTPUT_KEY to "doc",
                )
            ),
            node(
                "html_select",
                ActionNodeType.HTML_SELECT,
                "DOM 选择",
                config(
                    ActionHtmlConfigKey.SOURCE to "\${steps.html_parse.doc}",
                    ActionHtmlConfigKey.SELECTOR to ":::bad_selector:::",
                    ActionHtmlConfigKey.OUTPUT_KEY to "res",
                )
            ),
        ),
        edges = listOf(
            edge("start", ActionControlPortId.NEXT, "html_parse"),
            edge("html_parse", ActionControlPortId.NEXT, "html_select"),
        ),
    )

    private fun errorHtmlParseNonStringSample(): ActionWorkflow = workflow(
        id = "error_html_parse_non_string",
        name = "错误测试：HTML 解析非字符串输入",
        description = "验证 html.parse 节点在输入类型非字符串时的拦截保护。",
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "html_parse",
                ActionNodeType.HTML_PARSE,
                "解析数字对象",
                config(
                    ActionHtmlConfigKey.HTML to 12345,
                    ActionHtmlConfigKey.OUTPUT_KEY to "doc",
                )
            ),
        ),
        edges = listOf(edge("start", ActionControlPortId.NEXT, "html_parse")),
    )

    private fun errorHtmlSelectNonElementSample(): ActionWorkflow = workflow(
        id = "error_html_select_non_element",
        name = "错误测试：HTML 选择非 Element 输入",
        description = "验证 html.select 节点在源对象非 Element / Elements 时的拦截保护。",
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "html_select",
                ActionNodeType.HTML_SELECT,
                "选择非 DOM 对象",
                config(
                    ActionHtmlConfigKey.SOURCE to 12345,
                    ActionHtmlConfigKey.SELECTOR to "div",
                    ActionHtmlConfigKey.OUTPUT_KEY to "res",
                )
            ),
        ),
        edges = listOf(edge("start", ActionControlPortId.NEXT, "html_select")),
    )

    private fun errorHtmlMapNonElementsSample(): ActionWorkflow = workflow(
        id = "error_html_map_non_elements",
        name = "错误测试：HTML 集合映射非集合输入",
        description = "验证 html.map 节点在源对象非 Elements 集合（如单个 Element）时的拦截保护。",
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "html_map",
                ActionNodeType.HTML_MAP,
                "映射非集合对象",
                config(
                    ActionHtmlConfigKey.SOURCE to 12345,
                    ActionHtmlConfigKey.OPERATION to "text",
                    ActionHtmlConfigKey.OUTPUT_KEY to "res",
                )
            ),
        ),
        edges = listOf(edge("start", ActionControlPortId.NEXT, "html_map")),
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
}
