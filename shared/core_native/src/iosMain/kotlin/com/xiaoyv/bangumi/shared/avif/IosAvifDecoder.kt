package com.xiaoyv.bangumi.shared.avif

import cocoapods.libavif.AVIF_RESULT_OK
import cocoapods.libavif.AVIF_TRUE
import cocoapods.libavif.avifCodecVersions
import cocoapods.libavif.avifDecoder
import cocoapods.libavif.avifDecoderCreate
import cocoapods.libavif.avifDecoderDestroy
import cocoapods.libavif.avifDecoderNextImage
import cocoapods.libavif.avifDecoderNthImage
import cocoapods.libavif.avifDecoderParse
import cocoapods.libavif.avifDecoderReset
import cocoapods.libavif.avifDecoderSetIOMemory
import cocoapods.libavif.avifLibYUVVersion
import cocoapods.libavif.avifPeekCompatibleFileType
import cocoapods.libavif.avifROData
import cocoapods.libavif.avifResultToString
import cocoapods.libavif.avifVersion
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.refTo
import kotlinx.cinterop.set
import kotlinx.cinterop.toKString
import kotlinx.cinterop.toKStringFromUtf8
import okio.Closeable
import platform.posix.snprintf
import platform.posix.sprintf
import platform.posix.uint8_tVar

@OptIn(ExperimentalForeignApi::class)
class IosAvifDecoder private constructor(
    private val decoderPtr: CPointer<avifDecoder>,
) : Closeable {

    private val decoder: avifDecoder
        get() = decoderPtr.pointed

    fun reset(): Boolean {
        return avifDecoderReset(decoderPtr) == AVIF_RESULT_OK
    }

    fun nthFrame(index: Int): Boolean {
        return avifDecoderNthImage(decoderPtr, index.toUInt()) == AVIF_RESULT_OK
    }

    fun nextFrame(): Boolean {
        return avifDecoderNextImage(decoderPtr) == AVIF_RESULT_OK
    }

    fun getFrame(): AvifFrame {
        val avifImagePtr = requireNotNull(decoder.image)
        return AvifFrame(avifImagePtr)
    }

    fun getFrameIndex(): Int {
        return decoder.imageIndex
    }

    fun getFrameDurationMs(): Int {
        return (decoder.imageTiming.duration * 1000).toInt() // ms
    }

    fun getFrameCount(): Int {
        return decoder.imageCount
    }

    fun getAlphaPresent(): Boolean {
        return decoder.alphaPresent == AVIF_TRUE
    }

    fun getRepetitionCount(): Int {
        return decoder.imageCount
    }

    override fun close() {
        avifDecoderDestroy(decoderPtr)
    }

    companion object Companion {

        @Suppress("UNCHECKED_CAST")
        fun isAvifImage(bytes: ByteArray): Boolean = memScoped {
            val avif = alloc<avifROData>()
            avif.data = bytes.refTo(0).getPointer(this) as CPointer<uint8_tVar>
            avif.size = bytes.size.convert()
            avifPeekCompatibleFileType(avif.ptr) == AVIF_TRUE
        }

        @Suppress("UNCHECKED_CAST")
        fun create(bytes: ByteArray, threads: Int): IosAvifDecoder = memScoped {
            val decoderPtr = requireNotNull(avifDecoderCreate())
            with(decoderPtr.pointed) {
                maxThreads = threads
                ignoreExif = AVIF_TRUE
                ignoreXMP = AVIF_TRUE
            }
            var result = avifDecoderSetIOMemory(
                decoderPtr,
                bytes.refTo(0).getPointer(this) as CValuesRef<uint8_tVar>,
                bytes.size.convert(),
            )
            if (result != AVIF_RESULT_OK) {
                avifDecoderDestroy(decoderPtr)
                error("Failed to set AVIF IO to a memory reader: ${avifResultToString(result)?.toKString()}.")
            }
            result = avifDecoderParse(decoderPtr)
            if (result != AVIF_RESULT_OK) {
                avifDecoderDestroy(decoderPtr)
                error("Failed to parse AVIF image: ${avifResultToString(result)?.toKString()}")
            }
            return IosAvifDecoder(decoderPtr)
        }

        fun versionString(): String = memScoped {
            val codecVersionsPtr = allocArray<ByteVar>(256)
            avifCodecVersions(codecVersionsPtr)

            val libyuvVersionPtr = allocArray<ByteVar>(64)
            if (avifLibYUVVersion() > 0u) {
                sprintf(libyuvVersionPtr, "%u", avifLibYUVVersion())
            } else {
                libyuvVersionPtr[0] = '\n'.code.toByte()
            }

            val versionStringPtr = allocArray<ByteVar>(512)
            snprintf(
                versionStringPtr,
                512u,
                "libavif: %s\nCodecs: %s\nlibyuv: %s",
                avifVersion(),
                codecVersionsPtr,
                libyuvVersionPtr,
            )
            versionStringPtr.toKStringFromUtf8()
        }
    }
}