package com.xiaoyv.bangumi.features.workflows.business.samples

import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionEdge
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionPortRef
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionWorkflow
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionLoopConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

internal const val BILIBILI_WEB_USER_AGENT =
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:154.0) Gecko/20100101 Firefox/154.0"

internal val htmlSampleDocument = """
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

internal const val sampleHtmlElement =
    "<div id=\"main-content\" class=\"active theme-dark\" value=\"bgm_val\" data-info=\"bgm_data\"><a href=\"https://bgm.tv\">Bangumi</a></div>"

internal val sampleHtmlElements = listOf(
    "<li id=\"tag-1\" class=\"tag-item\" data-type=\"anime\"><a href=\"/anime\">动画</a></li>",
    "<li id=\"tag-2\" class=\"tag-item\" data-type=\"game\"><a href=\"/game\">游戏</a></li>",
)

internal val sideEffectTypes = setOf(
    ActionNodeType.OPEN_EXTERNAL_URL,
    ActionNodeType.OPEN_EXTERNAL_APP,
    ActionNodeType.OPEN_INTERNAL_WEB,
    ActionNodeType.SHOW_TOAST,
    ActionNodeType.WRITE_CLIPBOARD,
    ActionNodeType.READ_CLIPBOARD,
    ActionNodeType.UI_CONFIRM,
    ActionNodeType.UI_INPUT_DIALOG,
    ActionNodeType.UI_PROGRESS_DIALOG,
    ActionNodeType.UI_PROGRESS_UPDATE,
    ActionNodeType.UI_PROGRESS_DISMISS,
    ActionNodeType.UI_SELECT_DIALOG,
    ActionNodeType.SYSTEM_SHARE,
    ActionNodeType.SYSTEM_NOTIFICATION,
    ActionNodeType.SYSTEM_VIBRATE,
    ActionNodeType.HTTP_REQUEST,
    ActionNodeType.IMAGE_PREVIEW,
    ActionNodeType.VIDEO_PREVIEW,
    ActionNodeType.SYNC_COOKIE,
)

internal fun workflow(
    id: String,
    name: String,
    description: String,
    capabilities: Set<String> = emptySet(),
    nodes: List<ActionNode>,
    edges: List<ActionEdge>,
) = ActionWorkflow(
    id = "workflow_sample_$id",
    name = name,
    description = description,
    requiredCapabilities = capabilities.toPersistentList(),
    entryNodeId = "start",
    nodes = nodes.toPersistentList(),
    edges = edges.toPersistentList(),
)

internal fun node(
    id: String,
    type: String,
    label: String,
    config: JsonObject = JsonObject(emptyMap()),
) = ActionNode(id = id, type = type, label = label, config = config)

internal fun edge(sourceNodeId: String, sourcePortId: String, targetNodeId: String) = ActionEdge(
    id = "$sourceNodeId-${sourcePortId}-$targetNodeId",
    source = ActionPortRef(sourceNodeId, sourcePortId),
    target = ActionPortRef(targetNodeId, ActionControlPortId.IN),
)

internal fun config(vararg values: Pair<String, Any?>): JsonObject = buildJsonObject {
    values.forEach { (key, value) -> put(key, value.toJsonElement()) }
}

internal fun linear(
    id: String,
    name: String,
    type: String,
    nodeConfig: JsonObject = JsonObject(emptyMap()),
    capabilities: Set<String> = emptySet(),
): ActionWorkflow {
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

internal fun condition(id: String, name: String, type: String, nodeConfig: JsonObject): ActionWorkflow = workflow(
    id = id,
    name = "测试：$name",
    description = "覆盖 `$type` 的真值分支。",
    nodes = listOf(node("start", ActionNodeType.FLOW_START, "开始"), node("target", type, name, nodeConfig), node("end", ActionNodeType.FLOW_END, "结束")),
    edges = listOf(edge("start", ActionControlPortId.NEXT, "target"), edge("target", ActionControlPortId.TRUE, "end")),
)

internal fun loop(id: String, name: String, loopType: String, controlType: String, loopConfig: JsonObject): ActionWorkflow = workflow(
    id = id,
    name = "测试：$name",
    description = "覆盖 `$loopType` 与 `$controlType` 的结构化循环执行。",
    nodes = listOf(
        node("start", ActionNodeType.FLOW_START, "开始"), node("loop", loopType, name, loopConfig),
        node("control", controlType, "循环控制", config(ActionLoopConfigKey.LOOP_ID to "loop")), node("end", ActionNodeType.FLOW_END, "结束"),
    ),
    edges = listOf(edge("start", ActionControlPortId.NEXT, "loop"), edge("loop", ActionControlPortId.BODY, "control"), edge("loop", ActionControlPortId.COMPLETED, "end")),
)

internal fun terminal(id: String, name: String, type: String): ActionWorkflow = workflow(
    id = id,
    name = "测试：$name",
    description = "覆盖 `$type` 的正常终止能力。",
    nodes = listOf(node("start", ActionNodeType.FLOW_START, "开始"), node("target", type, name)),
    edges = listOf(edge("start", ActionControlPortId.NEXT, "target")),
)

internal fun Any?.toJsonElement(): JsonElement = when (this) {
    null -> JsonNull
    is JsonElement -> this
    is Boolean -> JsonPrimitive(this)
    is Int -> JsonPrimitive(this)
    is Long -> JsonPrimitive(this)
    is Double -> JsonPrimitive(this)
    is String -> JsonPrimitive(this)
    is Iterable<*> -> JsonArray(this.map { it.toJsonElement() })
    is Array<*> -> JsonArray(this.map { it.toJsonElement() })
    else -> error("不支持的示例配置类型：${this::class}")
}
