@file:OptIn(SealedSerializationApi::class)

package com.xiaoyv.bangumi.shared.core.utils.serialization

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SealedSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonNull

typealias SerializeList<T> = @Serializable(ImmutableListSerializer::class) ImmutableList<T>

class ImmutableListSerializer<T>(private val dataSerializer: KSerializer<T>) : KSerializer<ImmutableList<T>> {
    private class PersistentListDescriptor : SerialDescriptor by serialDescriptor<List<String>>() {
        override val serialName: String = "kotlinx.serialization.immutable.ImmutableList"
    }

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: SerializeList<T>) =
        ListSerializer(dataSerializer).serialize(encoder, value.toList())

    override fun deserialize(decoder: Decoder): SerializeList<T> {
        if (decoder !is JsonDecoder) {
            return ListSerializer(dataSerializer).deserialize(decoder).toImmutableList()
        }

        val element = decoder.decodeJsonElement()
        if (element is JsonArray) {
            return decoder.json.decodeFromJsonElement(ListSerializer(dataSerializer), element).toImmutableList()
        }

        if (element is JsonNull) {
            return persistentListOf()
        }

        return persistentListOf(decoder.json.decodeFromJsonElement(dataSerializer, element))
    }
}

