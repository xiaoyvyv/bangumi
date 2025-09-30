package com.xiaoyv.bangumi.shared.core.utils.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Instant

typealias SerializeDateLong = @Serializable(FlexibleInstantSerializer::class) Long

object FlexibleInstantSerializer : KSerializer<Long> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("FlexibleInstant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Long) {
        encoder.encodeLong(value)
    }

    override fun deserialize(decoder: Decoder): Long {
        return try {
            val longValue = decoder.decodeLong()
            // 判断长度：秒级 (约 10 位) → 转毫秒
            if (longValue < 1_000_000_000_000L) {
                longValue * 1000
            } else {
                longValue
            }
        } catch (e: Exception) {
            val string = decoder.decodeString()
            Instant.parse(string).toEpochMilliseconds()
        }
    }
}

