package com.xiaoyv.bangumi.shared.avif

import coil3.PlatformContext
import coil3.decode.Decoder

/**
 * [AvifDecoderFactory]
 *
 * @since 2025/5/20
 */
actual object AvifDecoderFactory {

    actual fun create(context: PlatformContext): Decoder.Factory {
        return AvifDecoder.Factory()
    }
}