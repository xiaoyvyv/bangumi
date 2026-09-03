package com.xiaoyv.bangumi.features.workflows.business.samples

import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionWorkflow
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionToastConfigKey
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

/**
 * 流程流转、条件分支与并发合流测试工作流样例集合。
 */
internal object FlowSamples {
    val all: List<ActionWorkflow> = listOf(
        // Flow 节点
        linear("flow_start_end", "流程开始与结束", ActionNodeType.FLOW_END),
        linear("flow_delay", "延迟等待", ActionNodeType.FLOW_DELAY, config(ActionFlowConfigKey.DELAY_MILLIS to 3000L)),
        linear("flow_log", "打印日志", ActionNodeType.FLOW_LOG, config(ActionFlowConfigKey.MESSAGE to "测试日志信息")),
        linear("flow_debug", "调试断点", ActionNodeType.FLOW_DEBUG, config(ActionFlowConfigKey.MESSAGE to "到达调试节点")),
        linear("flow_assert", "流程断言", ActionNodeType.FLOW_ASSERT, config(ActionFlowConfigKey.CONDITION to true)),
        terminal("flow_stop", "提前结束流程", ActionNodeType.FLOW_STOP),
        switchSample(),
        forkAndJoinSample(),
        parallelTimingSample(),

        // Control 节点
        condition("control_if", "条件分支", ActionNodeType.CONDITION_IF, config(ActionControlConfigKey.CONDITION to true)),
        condition("control_equals", "相等判断", ActionNodeType.CONDITION_EQUALS, config(ActionControlConfigKey.LEFT to 7, ActionControlConfigKey.RIGHT to 7)),
        condition("control_not_equals", "不相等判断", ActionNodeType.CONDITION_NOT_EQUALS, config(ActionControlConfigKey.LEFT to "A", ActionControlConfigKey.RIGHT to "B")),
        condition("control_greater_than", "大于判断", ActionNodeType.CONDITION_GREATER_THAN, config(ActionControlConfigKey.LEFT to 8, ActionControlConfigKey.RIGHT to 7)),
        condition(
            "control_greater_than_or_equals",
            "大于等于判断",
            ActionNodeType.CONDITION_GREATER_THAN_OR_EQUALS,
            config(ActionControlConfigKey.LEFT to 7, ActionControlConfigKey.RIGHT to 7)
        ),
        condition("control_less_than", "小于判断", ActionNodeType.CONDITION_LESS_THAN, config(ActionControlConfigKey.LEFT to 6, ActionControlConfigKey.RIGHT to 7)),
        condition(
            "control_less_than_or_equals",
            "小于等于判断",
            ActionNodeType.CONDITION_LESS_THAN_OR_EQUALS,
            config(ActionControlConfigKey.LEFT to 7, ActionControlConfigKey.RIGHT to 7)
        ),
        condition("control_and", "逻辑与判断", ActionNodeType.CONDITION_AND, config(ActionControlConfigKey.LEFT to true, ActionControlConfigKey.RIGHT to true)),
        condition("control_or", "逻辑或判断", ActionNodeType.CONDITION_OR, config(ActionControlConfigKey.LEFT to false, ActionControlConfigKey.RIGHT to true)),
        condition("control_not", "逻辑非判断", ActionNodeType.CONDITION_NOT, config(ActionControlConfigKey.VALUE to false)),
        condition("control_is_null", "空值判断", ActionNodeType.CONDITION_IS_NULL, buildJsonObject { put(ActionControlConfigKey.VALUE, JsonNull) }),
        condition("control_is_empty", "空判定", ActionNodeType.CONDITION_IS_EMPTY, config(ActionControlConfigKey.VALUE to "")),
    )

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

    private fun parallelTimingSample(): ActionWorkflow = workflow(
        id = "flow_parallel_timing",
        name = "并发分支耗时测试",
        description = "两条分支分别等待 1 秒与 3 秒，工作流会在共同 flow.join 汇合；总耗时应接近 3 秒，而非 4 秒。",
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node("parallel", ActionNodeType.FLOW_PARALLEL, "并发分支"),
            node("short_delay", ActionNodeType.FLOW_DELAY, "短任务", config(ActionFlowConfigKey.DELAY_MILLIS to 1_000L)),
            node("long_delay", ActionNodeType.FLOW_DELAY, "长任务", config(ActionFlowConfigKey.DELAY_MILLIS to 3_000L)),
            node(
                "join",
                ActionNodeType.FLOW_JOIN,
                "等待全部完成",
                config(ActionFlowConfigKey.VALUES to JsonArray(emptyList()), ActionFlowConfigKey.OUTPUT_KEY to "parallelResult"),
            ),
            node("end", ActionNodeType.FLOW_END, "结束"),
        ),
        edges = listOf(
            edge("start", ActionControlPortId.NEXT, "parallel"),
            edge("parallel", ActionControlPortId.BRANCHES, "short_delay"),
            edge("parallel", ActionControlPortId.BRANCHES, "long_delay"),
            edge("short_delay", ActionControlPortId.NEXT, "join"),
            edge("long_delay", ActionControlPortId.NEXT, "join"),
            edge("join", ActionControlPortId.NEXT, "end"),
        ),
    )
}
