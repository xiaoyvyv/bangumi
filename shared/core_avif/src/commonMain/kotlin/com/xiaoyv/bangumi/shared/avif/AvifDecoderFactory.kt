package com.xiaoyv.bangumi.shared.avif

import coil3.PlatformContext
import coil3.decode.Decoder
import okio.ByteString.Companion.encodeUtf8


internal val MIF = "ftypmif1".encodeUtf8()
internal val MSF = "ftypmsf1".encodeUtf8()
internal val HEIC = "ftypheic".encodeUtf8()
internal val HEIX = "ftypheix".encodeUtf8()
internal val HEVC = "ftyphevc".encodeUtf8()
internal val HEVX = "ftyphevx".encodeUtf8()
internal val AVIF = "ftypavif".encodeUtf8()
internal val AVIS = "ftypavis".encodeUtf8()

internal val AVAILABLE_BRANDS = listOf(MIF, MSF, HEIC, HEIX, HEVC, HEVX, AVIF, AVIS)

/**
 * [AvifDecoderFactory]
 *
 * @since 2025/5/20
 */
expect object AvifDecoderFactory {
    fun create(context: PlatformContext): Decoder.Factory
}