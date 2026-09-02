package com.xiaoyv.bangumi.shared.data.workflow.node.resolver

import com.xiaoyv.bangumi.shared.data.workflow.node.core.*
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.*
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.*
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.model.ActionTemplateRoot
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

/**
 * 仅支持上下文路径和文本插值的模板解析器，不执行任意脚本。
 *
 * 路径既可读取对象字段，也可通过数字段读取数组元素，例如 `${vars.matchGroups.1}`。
 */
object ActionTemplateResolver {
    private val placeholder = Regex("\\$\\{([^}]+)\\}")

    /**
     * 解析文本中的 `${...}` 上下文占位符。
     *
     * @param template 模板文本。
     * @param context 当前执行上下文。
     * @return 占位符已替换的文本。
     */
    fun resolveText(template: String, context: ActionExecutionContext): String {
        return placeholder.replace(template) { match -> resolvePath(match.groupValues[1], context).asText() }
    }

    /**
     * 解析 JSON 值；完全由单个占位符组成时保留原始 JSON 类型。
     *
     * @param value 节点配置中的原始值。
     * @param context 当前执行上下文。
     * @return 解析后的 JSON 值。
     */
    fun resolveElement(value: JsonElement?, context: ActionExecutionContext): JsonElement {
        if (value is JsonObject) return JsonObject(value.mapValues { (_, element) -> resolveElement(element, context) })
        if (value is JsonArray) return JsonArray(value.map { resolveElement(it, context) })
        val primitive = value as? JsonPrimitive ?: return value ?: JsonNull
        val content = primitive.contentOrNull ?: return primitive
        val exact = placeholder.matchEntire(content)
        return when {
            exact != null -> resolvePath(exact.groupValues[1], context)
            $$"${" in content -> JsonPrimitive(resolveText(content, context))
            else -> primitive
        }
    }

    private fun resolvePath(path: String, context: ActionExecutionContext): JsonElement {
        val parts = path.split('.').filter(String::isNotBlank)
        if (parts.isEmpty()) return JsonNull
        val root: JsonElement = when (parts.first()) {
            ActionTemplateRoot.INPUT -> context.input
            ActionTemplateRoot.ENVIRONMENT -> context.environment
            ActionTemplateRoot.TRIGGER -> context.trigger
            ActionTemplateRoot.VARIABLES -> JsonObject(context.variables)
            ActionTemplateRoot.STEP_OUTPUTS -> JsonObject(context.stepOutputs)
            ActionTemplateRoot.LOOP -> context.loop
            else -> return JsonNull
        }
        return parts.drop(1).fold(root) { value, key ->
            when (value) {
                is JsonObject -> value[key] ?: JsonNull
                is JsonArray -> key.toIntOrNull()?.let(value::getOrNull) ?: JsonNull
                else -> JsonNull
            }
        }
    }

    private fun JsonElement.asText(): String = when (this) {
        is JsonPrimitive -> contentOrNull.orEmpty()
        JsonNull -> ""
        else -> toString()
    }
}
