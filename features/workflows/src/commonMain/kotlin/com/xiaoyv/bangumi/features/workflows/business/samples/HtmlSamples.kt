package com.xiaoyv.bangumi.features.workflows.business.samples

import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionWorkflow
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHtmlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionToastConfigKey

/**
 * HTML 解析、选择器与 DOM 操作工作流样例集合。
 */
internal object HtmlSamples {
    val all: List<ActionWorkflow> = listOf(
        // HTML 节点
        linear(
            "html_parse",
            "HTML 文档解析",
            ActionNodeType.HTML_PARSE,
            config(
                ActionHtmlConfigKey.HTML to htmlSampleDocument,
                ActionHtmlConfigKey.OUTPUT_KEY to "doc",
            ),
        ),
        linear(
            "html_select",
            "HTML 元素集合提取",
            ActionNodeType.HTML_SELECT,
            config(
                ActionHtmlConfigKey.SOURCE to sampleHtmlElement,
                ActionHtmlConfigKey.SELECTOR to "a",
                ActionHtmlConfigKey.OUTPUT_KEY to "elements",
            ),
        ),
        linear(
            "html_select_first",
            "HTML 首个元素提取",
            ActionNodeType.HTML_SELECT_FIRST,
            config(
                ActionHtmlConfigKey.SOURCE to sampleHtmlElement,
                ActionHtmlConfigKey.SELECTOR to "a",
                ActionHtmlConfigKey.OUTPUT_KEY to "element",
            ),
        ),
        linear(
            "html_attr",
            "HTML 提取属性",
            ActionNodeType.HTML_ATTR,
            config(
                ActionHtmlConfigKey.SOURCE to sampleHtmlElement,
                ActionHtmlConfigKey.ATTRIBUTE to "class",
                ActionHtmlConfigKey.OUTPUT_KEY to "classAttr",
            ),
        ),
        linear(
            "html_tag",
            "HTML 提取标签名",
            ActionNodeType.HTML_TAG,
            config(
                ActionHtmlConfigKey.SOURCE to sampleHtmlElement,
                ActionHtmlConfigKey.OUTPUT_KEY to "tagName",
            ),
        ),
        linear(
            "html_text",
            "HTML 纯文本提取",
            ActionNodeType.HTML_TEXT,
            config(
                ActionHtmlConfigKey.SOURCE to sampleHtmlElement,
                ActionHtmlConfigKey.OUTPUT_KEY to "text",
            ),
        ),
        linear(
            "html_data",
            "HTML 提取数据内容",
            ActionNodeType.HTML_DATA,
            config(
                ActionHtmlConfigKey.SOURCE to sampleHtmlElement,
                ActionHtmlConfigKey.OUTPUT_KEY to "dataContent",
            ),
        ),
        linear(
            "html_value",
            "HTML 提取表单值",
            ActionNodeType.HTML_VALUE,
            config(
                ActionHtmlConfigKey.SOURCE to sampleHtmlElement,
                ActionHtmlConfigKey.OUTPUT_KEY to "valueContent",
            ),
        ),
        linear(
            "html_id",
            "HTML 提取 ID 属性",
            ActionNodeType.HTML_ID,
            config(
                ActionHtmlConfigKey.SOURCE to sampleHtmlElement,
                ActionHtmlConfigKey.OUTPUT_KEY to "idContent",
            ),
        ),
        linear(
            "html_html",
            "HTML 提取内部 HTML",
            ActionNodeType.HTML_HTML,
            config(
                ActionHtmlConfigKey.SOURCE to sampleHtmlElement,
                ActionHtmlConfigKey.OUTPUT_KEY to "innerHtml",
            ),
        ),
        linear(
            "html_outer_html",
            "HTML 提取外部 HTML",
            ActionNodeType.HTML_OUTER_HTML,
            config(
                ActionHtmlConfigKey.SOURCE to sampleHtmlElement,
                ActionHtmlConfigKey.OUTPUT_KEY to "outerHtml",
            ),
        ),
        linear(
            "html_has_class",
            "HTML 检查样式类名",
            ActionNodeType.HTML_HAS_CLASS,
            config(
                ActionHtmlConfigKey.SOURCE to sampleHtmlElement,
                ActionHtmlConfigKey.CLASS_NAME to "active",
                ActionHtmlConfigKey.OUTPUT_KEY to "hasActive",
            ),
        ),
        linear(
            "html_map",
            "HTML 集合字段映射",
            ActionNodeType.HTML_MAP,
            config(
                ActionHtmlConfigKey.SOURCE to sampleHtmlElements,
                ActionHtmlConfigKey.OPERATION to "text",
                ActionHtmlConfigKey.OUTPUT_KEY to "tagTexts",
            ),
        ),
        linear(
            "html_first",
            "HTML 提取首个元素",
            ActionNodeType.HTML_FIRST,
            config(
                ActionHtmlConfigKey.SOURCE to sampleHtmlElements,
                ActionHtmlConfigKey.OUTPUT_KEY to "firstItem",
            ),
        ),
        linear(
            "html_last",
            "HTML 提取末尾元素",
            ActionNodeType.HTML_LAST,
            config(
                ActionHtmlConfigKey.SOURCE to sampleHtmlElements,
                ActionHtmlConfigKey.OUTPUT_KEY to "lastItem",
            ),
        ),
        linear(
            "html_get",
            "HTML 按索引提取元素",
            ActionNodeType.HTML_GET,
            config(
                ActionHtmlConfigKey.SOURCE to sampleHtmlElements,
                ActionHtmlConfigKey.INDEX to 0,
                ActionHtmlConfigKey.OUTPUT_KEY to "getItem",
            ),
        ),
        linear(
            "html_size",
            "HTML 集合元素计数",
            ActionNodeType.HTML_SIZE,
            config(
                ActionHtmlConfigKey.SOURCE to sampleHtmlElements,
                ActionHtmlConfigKey.OUTPUT_KEY to "size",
            ),
        ),
        linear(
            "html_table_to_json",
            "HTML 表格转 JSON 数组",
            ActionNodeType.HTML_TABLE_TO_JSON,
            config(
                ActionHtmlConfigKey.HTML to "<table><tr><th>名称</th><th>评分</th></tr><tr><td>孤独摇滚</td><td>8.9</td></tr></table>",
                ActionHtmlConfigKey.OUTPUT_KEY to "tableJson",
            ),
        ),
        linear(
            "html_remove",
            "HTML 剔除标签清洗",
            ActionNodeType.HTML_REMOVE,
            config(
                ActionHtmlConfigKey.SOURCE to sampleHtmlElement,
                ActionHtmlConfigKey.SELECTOR to "a",
                ActionHtmlConfigKey.OUTPUT_KEY to "cleanedHtml",
            ),
        ),
        linear(
            "html_parent",
            "HTML 提取父级元素",
            ActionNodeType.HTML_PARENT,
            config(
                ActionHtmlConfigKey.SOURCE to "<div id='card'><a id='link'>标题</a></div>",
                ActionHtmlConfigKey.OUTPUT_KEY to "parentHtml",
            ),
        ),
        linear(
            "html_children",
            "HTML 提取直接子元素列表",
            ActionNodeType.HTML_CHILDREN,
            config(
                ActionHtmlConfigKey.SOURCE to "<ul id='list'><li>条目1</li><li>条目2</li></ul>",
                ActionHtmlConfigKey.OUTPUT_KEY to "children",
            ),
        ),
        ksoupHtmlPipelineSample(),
    )

    /**
     * Ksoup HTML 管道解析、选择与映射全流程样例。
     */
    private fun ksoupHtmlPipelineSample(): ActionWorkflow = workflow(
        id = "ksoup_html_pipeline",
        name = "Ksoup 链式 HTML 解析与提取",
        description = "演示通过 html.parse -> html.select -> html.first -> html.attr / html.tag / html.text / html.map 等节点对 HTML 文档进行结构化解析与字段提取。",
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "parse_html",
                ActionNodeType.HTML_PARSE,
                "解析 HTML 文档",
                config(
                    ActionHtmlConfigKey.HTML to htmlSampleDocument,
                    ActionHtmlConfigKey.OUTPUT_KEY to "doc",
                ),
            ),
            node(
                "select_tags",
                ActionNodeType.HTML_SELECT,
                "查询标签集合",
                config(
                    ActionHtmlConfigKey.SOURCE to "\${steps.parse_html.doc}",
                    ActionHtmlConfigKey.SELECTOR to "ul.tags > li",
                    ActionHtmlConfigKey.OUTPUT_KEY to "tags",
                ),
            ),
            node(
                "map_tag_texts",
                ActionNodeType.HTML_MAP,
                "提取标签文本列表",
                config(
                    ActionHtmlConfigKey.SOURCE to "\${steps.select_tags.tags}",
                    ActionHtmlConfigKey.OPERATION to "text",
                    ActionHtmlConfigKey.OUTPUT_KEY to "tagNames",
                ),
            ),
            node(
                "select_title_el",
                ActionNodeType.HTML_SELECT_FIRST,
                "查询标题元素",
                config(
                    ActionHtmlConfigKey.SOURCE to "\${steps.parse_html.doc}",
                    ActionHtmlConfigKey.SELECTOR to "h1.nameSingle",
                    ActionHtmlConfigKey.OUTPUT_KEY to "titleEl",
                ),
            ),
            node(
                "extract_title_text",
                ActionNodeType.HTML_TEXT,
                "提取标题文本",
                config(
                    ActionHtmlConfigKey.SOURCE to "\${steps.select_title_el.titleEl}",
                    ActionHtmlConfigKey.OUTPUT_KEY to "titleText",
                ),
            ),
            node(
                "show_result",
                ActionNodeType.SHOW_TOAST,
                "展示提取结果",
                config(
                    ActionToastConfigKey.MESSAGE to "标题：\${steps.extract_title_text.titleText}，标签：\${steps.map_tag_texts.tagNames}",
                ),
            ),
            node("end", ActionNodeType.FLOW_END, "结束"),
        ),
        edges = listOf(
            edge("start", ActionControlPortId.NEXT, "parse_html"),
            edge("parse_html", ActionControlPortId.NEXT, "select_tags"),
            edge("select_tags", ActionControlPortId.NEXT, "map_tag_texts"),
            edge("map_tag_texts", ActionControlPortId.NEXT, "select_title_el"),
            edge("select_title_el", ActionControlPortId.NEXT, "extract_title_text"),
            edge("extract_title_text", ActionControlPortId.NEXT, "show_result"),
            edge("show_result", ActionControlPortId.SUCCESS, "end"),
        ),
    )
}
