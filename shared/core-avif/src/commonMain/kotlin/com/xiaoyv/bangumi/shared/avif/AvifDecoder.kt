package com.xiaoyv.bangumi.shared.avif

import coil3.decode.DecodeResult
import coil3.decode.Decoder

/**
 * [AvifDecoder]
 *
 * @since 2025/5/20
 */
expect class AvifDecoder : Decoder {
    override suspend fun decode(): DecodeResult?
}