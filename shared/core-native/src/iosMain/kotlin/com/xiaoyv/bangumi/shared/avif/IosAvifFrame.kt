package com.xiaoyv.bangumi.shared.avif

import cocoapods.libavif.AVIF_RGB_FORMAT_RGBA
import cocoapods.libavif.AVIF_RGB_FORMAT_RGB_565
import cocoapods.libavif.AVIF_TRUE
import cocoapods.libavif.avifImage
import cocoapods.libavif.avifImageYUVToRGB
import cocoapods.libavif.avifRGBImage
import cocoapods.libavif.avifRGBImageAllocatePixels
import cocoapods.libavif.avifRGBImageFreePixels
import cocoapods.libavif.avifRGBImageSetDefaults
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.alloc
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorType

typealias PlatformBitmap = Bitmap

class AvifFrame internal constructor(
    private val avifImagePtr: CPointer<avifImage>,
) {
    private val avifImage: avifImage
        get() = avifImagePtr.pointed

    fun getWidth(): Int {
        return avifImage.width.toInt()
    }

    fun getHeight(): Int {
        return avifImage.height.toInt()
    }

    fun getDepth(): Int {
        return avifImage.depth.toInt()
    }

    fun decodeFrame(bitmap: PlatformBitmap) = memScoped {
        if (bitmap.colorType != ColorType.RGBA_8888 &&
            bitmap.colorType != ColorType.RGB_565 &&
            bitmap.colorType != ColorType.RGBA_F16
        ) {
            error("Bitmap colorType (${bitmap.colorType}) is not supported.")
        }

        val rgbImage = alloc<avifRGBImage>()
        avifRGBImageSetDefaults(rgbImage.ptr, avifImagePtr)

        when (bitmap.colorType) {
            ColorType.RGBA_F16 -> {
                rgbImage.depth = 16u
                rgbImage.isFloat = AVIF_TRUE
            }

            ColorType.RGB_565 -> {
                rgbImage.format = AVIF_RGB_FORMAT_RGB_565
                rgbImage.depth = 8u
            }

            else -> {
                rgbImage.format = AVIF_RGB_FORMAT_RGBA
                rgbImage.depth = 8u
            }
        }

        avifRGBImageAllocatePixels(rgbImage.ptr)
        avifImageYUVToRGB(avifImagePtr, rgbImage.ptr)

        with(rgbImage) {
            pixels?.let { pixels ->
                val pixelsByteArray = ByteArray((rowBytes * height).toInt()) {
                    pixels[it].toByte()
                }
                bitmap.installPixels(pixelsByteArray)
            }
        }
        avifRGBImageFreePixels(rgbImage.ptr)
    }
}