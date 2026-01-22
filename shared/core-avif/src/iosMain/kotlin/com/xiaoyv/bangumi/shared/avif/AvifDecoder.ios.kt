@file:OptIn(BetaInteropApi::class)

package com.xiaoyv.bangumi.shared.avif

import coil3.ImageLoader
import coil3.decode.Decoder
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import com.xiaoyv.bangumi.shared.System
import kotlinx.cinterop.BetaInteropApi


actual class AvifDecoder(
    private val result: SourceFetchResult,
    options: Options,
) : Decoder {

    actual override suspend fun decode() = System.decodeAvif(result)

    class Factory : Decoder.Factory {
        override fun create(
            result: SourceFetchResult,
            options: Options,
            imageLoader: ImageLoader,
        ): Decoder? {
            val enable = AVAILABLE_BRANDS.any {
                result.source.source().rangeEquals(4, it)
            }
            return if (enable) AvifDecoder(result, options) else null
        }
    }
}