package com.xiaoyv.bangumi.shared.gif

import android.graphics.drawable.AnimatedImageDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import coil3.DrawableImage
import coil3.Image
import coil3.ImageLoader
import coil3.decode.DecodeResult
import coil3.decode.Decoder
import coil3.fetch.SourceFetchResult
import coil3.gif.AnimatedImageDecoder
import coil3.request.Options
import coil3.size.ScaleDrawable

@RequiresApi(Build.VERSION_CODES.P)
class TwinklyAnimatedImageDecoder(private val decoder: AnimatedImageDecoder) : Decoder {

    override suspend fun decode(): DecodeResult {
        return decoder.decode().also {
            removeFilterBitmap(it.image)
        }
    }

    private fun removeFilterBitmap(image: Image) {
        if (image !is DrawableImage) return

        val drawable = if (image.drawable is ScaleDrawable) {
            (image.drawable as ScaleDrawable).child
        } else {
            image.drawable
        }

        if (drawable is AnimatedImageDrawable) {
            drawable.isFilterBitmap = false
        }
    }

    class Factory : Decoder.Factory {
        override fun create(
            result: SourceFetchResult,
            options: Options,
            imageLoader: ImageLoader,
        ): Decoder? {
            val factory = AnimatedImageDecoder.Factory()
            val animatedDecoder = factory.create(result, options, imageLoader) ?: return null
            return TwinklyAnimatedImageDecoder(animatedDecoder as AnimatedImageDecoder)
        }

    }
}