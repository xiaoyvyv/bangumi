package com.xiaoyv.bangumi.shared.avif

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import coil3.ImageLoader
import coil3.asImage
import coil3.decode.DecodeResult
import coil3.decode.Decoder
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import coil3.request.allowRgb565
import coil3.request.bitmapConfig
import coil3.size.Scale
import coil3.size.Size
import coil3.size.pxOrElse
import com.radzivon.bartoshyk.avif.coder.HeifCoder
import com.radzivon.bartoshyk.avif.coder.PreferredColorConfig
import com.radzivon.bartoshyk.avif.coder.ScaleMode
import kotlinx.coroutines.runInterruptible

actual class AvifDecoder(
    context: Context?,
    private val source: SourceFetchResult,
    private val options: Options,
) : Decoder {
    private val coder = HeifCoder()

    actual override suspend fun decode(): DecodeResult? = runInterruptible {
        // ColorSpace is preferred to be ignored due to lib is trying to handle all color profile by itself
        val sourceData = source.source.source().readByteArray()

        var mPreferredColorConfig: PreferredColorConfig = when (options.bitmapConfig) {
            Bitmap.Config.ALPHA_8 -> PreferredColorConfig.RGBA_8888
            Bitmap.Config.RGB_565 -> if (options.allowRgb565) PreferredColorConfig.RGB_565 else PreferredColorConfig.DEFAULT
            Bitmap.Config.ARGB_8888 -> PreferredColorConfig.RGBA_8888
            else -> PreferredColorConfig.DEFAULT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && options.bitmapConfig == Bitmap.Config.RGBA_F16) {
            mPreferredColorConfig = PreferredColorConfig.RGBA_F16
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && options.bitmapConfig == Bitmap.Config.HARDWARE) {
            mPreferredColorConfig = PreferredColorConfig.HARDWARE
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && options.bitmapConfig == Bitmap.Config.RGBA_1010102) {
            mPreferredColorConfig = PreferredColorConfig.RGBA_1010102
        }

        if (options.size == Size.ORIGINAL) {
            val originalImage = coder.decode(
                sourceData,
                preferredColorConfig = mPreferredColorConfig
            )
            return@runInterruptible DecodeResult(originalImage.asImage(), false)
        }

        val dstWidth = options.size.width.pxOrElse { 0 }
        val dstHeight = options.size.height.pxOrElse { 0 }
        val scaleMode = when (options.scale) {
            Scale.FILL -> ScaleMode.FILL
            Scale.FIT -> ScaleMode.FIT
        }

        val originalImage = coder.decodeSampled(
            sourceData,
            dstWidth,
            dstHeight,
            preferredColorConfig = mPreferredColorConfig,
            scaleMode,
        )
        return@runInterruptible DecodeResult(originalImage.asImage(), true)
    }

    /**
     * @param context is preferred to be set when displaying an HDR content to apply Vulkan shaders
     */
    class Factory(private val context: Context?) : Decoder.Factory {
        override fun create(
            result: SourceFetchResult,
            options: Options,
            imageLoader: ImageLoader,
        ): Decoder? {
            val enable = AVAILABLE_BRANDS.any {
                result.source.source().rangeEquals(4, it)
            }
            return if (enable) AvifDecoder(context, result, options) else null
        }
    }
}