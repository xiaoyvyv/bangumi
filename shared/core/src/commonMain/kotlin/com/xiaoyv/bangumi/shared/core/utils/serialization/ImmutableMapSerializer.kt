package com.xiaoyv.bangumi.shared.core.utils.serialization

import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

typealias SerializeMap<K, V> = @Serializable(ImmutableMapSerializer::class) ImmutableMap<K, V>

class ImmutableMapSerializer<K, V>(
    private val keySerializer: KSerializer<K>,
    private val valueSerializer: KSerializer<V>,
) : KSerializer<ImmutableMap<K, V>> {

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        MapSerializer(keySerializer, valueSerializer).descriptor

    override fun serialize(encoder: Encoder, value: SerializeMap<K, V>) {
        MapSerializer(keySerializer, valueSerializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): SerializeMap<K, V> {
        return MapSerializer(keySerializer, valueSerializer).deserialize(decoder).toImmutableMap()
    }
}