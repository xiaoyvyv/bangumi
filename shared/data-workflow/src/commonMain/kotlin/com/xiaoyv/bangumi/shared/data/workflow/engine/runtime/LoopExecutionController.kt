package com.xiaoyv.bangumi.shared.data.workflow.engine.runtime

import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionLoopConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionLoopContextKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.core.string
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionTemplateResolver
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * 循环帧栈与迭代状态的集中管理器。
 */
internal class LoopExecutionController {
    private val frames = mutableListOf<LoopFrame>()

    fun enter(node: ActionNode, context: ActionExecutionContext, bodyNodeId: String?, completedNodeId: String?): LoopTransition {
        requireNotNull(bodyNodeId) { "循环节点缺少 body 连线" }
        val configuredMaxIterations = node.config[ActionLoopConfigKey.MAX_ITERATIONS]?.let {
            ActionTemplateResolver.resolveElement(it, context).jsonPrimitive.intOrNull
                ?: error("maxIterations 必须是整数")
        }
        val items = if (node.type == ActionNodeType.LOOP_FOR_EACH) {
            ActionTemplateResolver.resolveElement(node.config[ActionLoopConfigKey.ITEMS], context) as? JsonArray
                ?: error("items 必须是数组")
        } else {
            null
        }
        val maxIterations = configuredMaxIterations ?: items?.size ?: error("循环节点缺少 maxIterations 配置")
        require(maxIterations >= 0) { "maxIterations 不能小于 0" }
        if (node.type != ActionNodeType.LOOP_FOR_EACH) {
            require(maxIterations > 0) { "maxIterations 必须大于 0" }
        }
        val frame = LoopFrame(node.id, node.type, bodyNodeId, completedNodeId, maxIterations, context.loop)
        when (node.type) {
            ActionNodeType.LOOP_REPEAT -> frame.limit = ActionTemplateResolver.resolveElement(node.config[ActionLoopConfigKey.COUNT], context).jsonPrimitive.intOrNull
                ?: error("count 必须是整数")

            ActionNodeType.LOOP_FOR_EACH -> frame.items = requireNotNull(items)
            ActionNodeType.LOOP_WHILE -> frame.condition = node.config.string(ActionLoopConfigKey.CONDITION)
        }
        if (node.type == ActionNodeType.LOOP_REPEAT) {
            require(frame.limit >= 0) { "count 不能小于 0" }
        }
        frames += frame
        return advance(context)
    }

    fun next(loopId: String, context: ActionExecutionContext): LoopTransition {
        requireActive(loopId)
        return advance(context)
    }

    fun breakLoop(loopId: String, context: ActionExecutionContext): LoopTransition {
        requireActive(loopId)
        val frame = requireNotNull(frames.removeLastOrNull())
        return LoopTransition(ActionControlPortId.COMPLETED, frame.completedNodeId, context.copy(loop = frame.outerLoop))
    }

    fun abort(loopId: String, context: ActionExecutionContext): ActionExecutionContext {
        val frame = frames.lastOrNull()?.takeIf { it.loopNodeId == loopId } ?: return context
        frames.removeLastOrNull()
        return context.copy(loop = frame.outerLoop)
    }

    fun activeLoopId(): String? = frames.lastOrNull()?.loopNodeId

    private fun advance(context: ActionExecutionContext): LoopTransition {
        val frame = frames.lastOrNull() ?: error("loop.next 或 loop.continue 只能在循环体内使用")
        val hasNext = when (frame.type) {
            ActionNodeType.LOOP_REPEAT -> frame.iteration < frame.limit
            ActionNodeType.LOOP_FOR_EACH -> frame.iteration < frame.items.size
            ActionNodeType.LOOP_WHILE -> ActionTemplateResolver.resolveText(frame.condition, context).toBooleanStrictOrNull()
                ?: error("while condition 必须解析为 true 或 false")

            else -> false
        }
        if (!hasNext) {
            frames.removeLastOrNull()
            return LoopTransition(ActionControlPortId.COMPLETED, frame.completedNodeId, context.copy(loop = frame.outerLoop))
        }
        require(frame.iteration < frame.maxIterations) { "循环超过 maxIterations 上限" }
        val item: JsonElement = frame.items.getOrNull(frame.iteration) ?: JsonNull
        val loop = JsonObject(
            mapOf(
                ActionLoopContextKey.ITEM to item,
                ActionLoopContextKey.INDEX to JsonPrimitive(frame.iteration),
                ActionLoopContextKey.ITERATION to JsonPrimitive(frame.iteration + 1),
            ),
        )
        frame.iteration++
        return LoopTransition(ActionControlPortId.BODY, frame.bodyNodeId, context.copy(loop = loop))
    }

    private fun requireActive(loopId: String) {
        require(loopId.isNotBlank()) { "循环控制节点缺少 loopId" }
        require(frames.lastOrNull()?.loopNodeId == loopId) { "循环控制节点不属于当前最内层循环" }
    }
}

private data class LoopFrame(
    val loopNodeId: String,
    val type: String,
    val bodyNodeId: String,
    val completedNodeId: String?,
    val maxIterations: Int,
    val outerLoop: JsonObject,
    var iteration: Int = 0,
    var limit: Int = 0,
    var items: JsonArray = JsonArray(emptyList()),
    var condition: String = "",
)

internal data class LoopTransition(val outputPortId: String, val nodeId: String?, val context: ActionExecutionContext)
