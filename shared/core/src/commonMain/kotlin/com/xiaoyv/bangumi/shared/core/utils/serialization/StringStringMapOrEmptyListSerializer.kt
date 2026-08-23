package com.xiaoyv.bangumi.shared.core.utils.serialization

import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTransformingSerializer

/**
 * [StringStringMapOrEmptyListSerializer]
 *
 * Handles Pixiv API fields like `alternateLanguages` which return `{}` (JsonObject) when populated
 * but return `[]` (JsonArray) when empty.
 */
object StringStringMapOrEmptyListSerializer : JsonTransformingSerializer<SerializeMap<String, String>>(
    ImmutableMapSerializer(String.serializer(), String.serializer())
) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        if (element is JsonArray) {
            return JsonObject(emptyMap())
        }
        return element
    }
}
