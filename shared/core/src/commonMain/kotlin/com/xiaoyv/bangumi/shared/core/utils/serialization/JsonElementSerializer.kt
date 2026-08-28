package com.xiaoyv.bangumi.shared.core.utils.serialization

import com.xiaoyv.bangumi.shared.core.utils.defaultJson
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder

/**
 * Keeps API payloads as JSON while storing JSON trees in binary formats as strings.
 */
object JsonElementSerializer : KSerializer<JsonElement> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("JsonElement", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: JsonElement) {
        if (encoder is JsonEncoder) encoder.encodeJsonElement(value)
        else encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): JsonElement {
        return if (decoder is JsonDecoder) decoder.decodeJsonElement()
        else defaultJson.parseToJsonElement(decoder.decodeString())
    }
}
