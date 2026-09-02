package com.xiaoyv.bangumi.shared.libnative.avif

import coil3.PlatformContext
import coil3.decode.Decoder
import com.github.awxkee.avifcoil.decoder.HeifDecoder

/**
 * [AvifDecoderFactory]
 *
 * @since 2025/5/20
 */
actual object AvifDecoderFactory {

    actual fun create(context: PlatformContext): Decoder.Factory {
        return HeifDecoder.Factory()
    }
}