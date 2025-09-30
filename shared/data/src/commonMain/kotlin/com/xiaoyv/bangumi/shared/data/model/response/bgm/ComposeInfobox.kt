@file:OptIn(ExperimentalSerializationApi::class)

package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.withChinese
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

@Immutable
@Serializable
data class ComposeInfobox(
    @SerialName("key") val key: String? = null,
    @SerialName("value") @JsonNames("values", "value") val value: JsonElement? = null,
) {
    val displayInfo: String
        get() = "$key：$displayValue"

    private val displayValue: String
        get() = when (value) {
            is JsonPrimitive -> value.contentOrNull.orEmpty()
            is JsonArray -> value.joinToString("、") {
                when (it) {
                    is JsonPrimitive -> it.contentOrNull.orEmpty()
                    is JsonArray -> it.joinToString("、") { sub -> sub.toString() }
                    is JsonObject -> it["v"]?.jsonPrimitive?.contentOrNull.orEmpty()
                }
            }

            else -> value.toString()
        }
}

/**
 * 获取全部名称
 */
fun List<ComposeInfobox>.chineseNames(nameCn: String): List<String> {
    val names = arrayListOf<String>()
    val nameInfo = find { it.key == "别名" }?.value
    if (nameInfo !is JsonArray) {
        val string = nameInfo?.jsonPrimitive?.contentOrNull.orEmpty()
        if (string.isNotBlank()) names.add(string)
    } else {
        names.addAll(nameInfo.mapNotNull { (it as? JsonObject)?.getValue("v")?.jsonPrimitive?.contentOrNull })
    }

    val aliasName = names
        .filter { it.isNotBlank() }
        .distinct()
        .withChinese()
        .toMutableList()
    if (nameCn.isNotBlank()) aliasName.add(nameCn)
    return aliasName.toImmutableList()
}