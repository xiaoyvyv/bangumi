package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.defaultJson
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement


@Immutable
@Serializable
data class ComposeReaction(
    @SerialName("type") val type: Int = 0,
    @SerialName("main_id") val mainId: Long = 0,
    @SerialName("value") val value: String = "",
    @SerialName("total") val total: Int = 0,
    @SerialName("emoji") val emoji: String = "",
    @SerialName("selected") val selected: Boolean = false,
    @SerialName("users") val users: SerializeList<ComposeUser> = persistentListOf(),
) {
    companion object {
        /**
         * 解析贴贴表情数据
         */
        fun fromJson(json: String): PersistentMap<String, SerializeList<ComposeReaction>> {
            val element = defaultJson.parseToJsonElement(json)
            if (element !is JsonObject) return persistentMapOf()
            val result = mutableMapOf<String, SerializeList<ComposeReaction>>()
            for ((key, value) in element) {
                val reactions: List<ComposeReaction> = when (value) {
                    is JsonArray -> value.map { defaultJson.decodeFromJsonElement<ComposeReaction>(it) }
                    is JsonObject -> value.values.map { defaultJson.decodeFromJsonElement<ComposeReaction>(it) }
                    else -> emptyList()
                }
                result[key] = reactions.toPersistentList()
            }
            return result.toPersistentMap()
        }
    }
}
