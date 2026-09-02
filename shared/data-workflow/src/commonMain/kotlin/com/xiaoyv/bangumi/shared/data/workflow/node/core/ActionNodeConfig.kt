package com.xiaoyv.bangumi.shared.data.workflow.node.core

import com.xiaoyv.bangumi.shared.data.workflow.node.core.*
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.*
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * 从节点 JSON 配置读取字符串值的内部工具。
 */
internal fun JsonObject.string(key: String): String = this[key]?.jsonPrimitive?.contentOrNull.orEmpty()
