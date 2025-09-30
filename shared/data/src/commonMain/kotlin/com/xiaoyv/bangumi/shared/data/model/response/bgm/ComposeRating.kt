package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

@Immutable
@Serializable
data class ComposeRating(
    @SerialName("rank") val rank: Int = 0,
    @SerialName("score") val score: Double = 0.0,
    @SerialName("total") val total: Int = 0,

    /**
     * private api 结构为 list
     * public  api 结构为 map
     */
    @SerialName("count") val element: JsonElement? = null,
) {

    val count: List<Int>
        get() = when (element) {
            is JsonArray -> element.map { it.jsonPrimitive.intOrNull ?: 0 }
            is JsonObject -> element.values.map { it.jsonPrimitive.intOrNull ?: 0 }
            else -> emptyList()
        }

    companion object {
        val Empty = ComposeRating()
    }
}