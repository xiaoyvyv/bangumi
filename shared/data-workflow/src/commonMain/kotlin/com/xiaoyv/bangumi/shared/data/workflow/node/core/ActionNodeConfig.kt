package com.xiaoyv.bangumi.shared.data.workflow.node.core

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * 从节点 JSON 配置读取字符串值的内部工具。
 */
internal fun JsonObject.string(key: String): String = this[key]?.jsonPrimitive?.contentOrNull.orEmpty()

/**
 * 从节点 JSON 配置读取整型值的内部工具。
 */
internal fun JsonObject.int(key: String, default: Int = 0): Int = this[key]?.jsonPrimitive?.intOrNull ?: default

/**
 * 从节点 JSON 配置读取布尔值的内部工具。
 */
internal fun JsonObject.boolean(key: String, default: Boolean = false): Boolean = this[key]?.jsonPrimitive?.booleanOrNull ?: default
