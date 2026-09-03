package com.xiaoyv.bangumi.shared.data.workflow.node.resolver

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject

/**
 * 受限 JSON 路径解析器，仅支持 `$.field.nestedField` 格式。
 */
object ActionJsonPath {
    /**
     * 从 JSON 对象读取路径对应的值。
     *
     * @param source 待读取的 JSON 对象。
     * @param path 以 `$.` 开头的字段路径。
     * @return 找不到字段时返回 JSON null。
     */
    fun resolve(source: JsonObject, path: String): JsonElement = resolve(source as JsonElement, path)

    /**
     * 从任意 JSON 元素读取路径对应的值。
     *
     * `$` 表示元素本身；`$.field` 表示对象字段。数组元素可通过数字路径段访问。
     *
     * @param source 待读取的 JSON 数据。
     * @param path 以 `$` 或 `$.` 开头的字段路径。
     * @return 找不到字段时返回 JSON null。
     */
    fun resolve(source: JsonElement, path: String): JsonElement {
        require(path == "$" || path.startsWith("$.")) { "JSON 路径必须以 $ 或 $. 开头" }
        if (path == "$") return source
        var value: JsonElement = source
        path.removePrefix("$.").split('.').filter { it.isNotBlank() }.forEach { key ->
            value = when (value) {
                is JsonObject -> value[key] ?: JsonNull
                is kotlinx.serialization.json.JsonArray -> key.toIntOrNull()?.let(value::getOrNull) ?: JsonNull
                else -> JsonNull
            }
        }
        return value
    }
}
