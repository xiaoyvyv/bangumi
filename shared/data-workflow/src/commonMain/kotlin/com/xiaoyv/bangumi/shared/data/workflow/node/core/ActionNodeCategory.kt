package com.xiaoyv.bangumi.shared.data.workflow.node.core

import com.xiaoyv.bangumi.shared.data.workflow.node.core.*
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.*
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.*
/**
 * 内置节点面向编辑器展示与筛选的稳定分类。
 *
 * 分类值会被导出的工作流编辑器配置、节点面板和搜索索引使用；节点定义不得直接书写分类字面量。
 */
object ActionNodeCategory {
    /** 流程的入口、终止、延迟与断言。 */
    const val FLOW = "flow"

    /** 条件、比较及分支判断。 */
    const val CONTROL = "control"

    /** 循环及循环体控制。 */
    const val LOOP = "loop"

    /** 基础数据读写与数据合并。 */
    const val DATA = "data"

    /** 日期与时间转换。 */
    const val DATE = "date"

    /** HTML 文档和 CSS 选择器解析。 */
    const val HTML = "html"

    /** HTTP 请求及响应处理。 */
    const val HTTP = "http"

    /** 工作流私有持久化存储。 */
    const val STORAGE = "storage"

    /** JSON 对象字段操作。 */
    const val OBJECT = "object"

    /** JSON 文本与结构化数据转换。 */
    const val JSON = "json"

    /** 二进制与文本编解码。 */
    const val CODEC = "codec"

    /** 不可逆摘要计算。 */
    const val CRYPTO = "crypto"

    /** 字符串、正则与 URL 文本处理。 */
    const val TEXT = "text"

    /** JSON 数组处理。 */
    const val ARRAY = "array"

    /** 数值运算。 */
    const val MATH = "math"

    /** 需要宿主授权或执行的网络、导航与提示动作。 */
    const val ACTION = "action"

    /** URL 解析与构建。 */
    const val URL = "url"

    /** CSV 格式转换。 */
    const val CSV = "csv"

    /** XML / RSS 转换。 */
    const val XML = "xml"

    /** 哔哩哔哩专用节点。 */
    const val BILIBILI = "bilibili"
}

