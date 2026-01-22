package com.xiaoyv.bangumi.shared.avif

import coil3.Bitmap
import coil3.ImageLoader
import coil3.asImage
import coil3.decode.DecodeResult
import coil3.decode.Decoder
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import kotlinx.coroutines.runInterruptible
import org.jetbrains.skiko.toImage
import javax.imageio.ImageIO

actual class AvifDecoder(
    private val source: SourceFetchResult,
    private val options: Options,
) : Decoder {
    actual override suspend fun decode(): DecodeResult? {
        val image = runInterruptible {
            ImageIO.read(source.source.source().inputStream()).toImage()
        }

        val isSampled: Boolean
        val bitmap: Bitmap

        try {
            bitmap = Bitmap.makeFromImage(image)
            bitmap.setImmutable()
            isSampled = bitmap.width < image.width || bitmap.height < image.height
        } finally {
            image.close()
        }

        return DecodeResult(
            image = bitmap.asImage(),
            isSampled = isSampled,
        )
    }

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