package com.xiaoyv.bangumi.shared.gif

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import coil3.Canvas
import coil3.Image
import coil3.ImageLoader
import coil3.decode.DecodeResult
import coil3.decode.Decoder
import coil3.decode.ImageSource
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import okio.BufferedSource
import okio.ByteString.Companion.encodeUtf8
import okio.use
import org.jetbrains.skia.AnimationFrameInfo
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Codec
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorInfo
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.Data
import org.jetbrains.skia.ImageInfo
import kotlin.time.TimeSource
import org.jetbrains.skia.Image as SkiaImage

internal class AnimatedSkiaImageDecoder(
    private val source: ImageSource,
    private val prerenderFrames: Boolean = true,
) : Decoder {

    override suspend fun decode(): DecodeResult {
        val bytes = source.source().use { it.readByteArray() }
        val codec = Codec.makeFromData(Data.makeFromBytes(bytes))
        return DecodeResult(
            image = AnimatedSkiaImage(codec, prerenderFrames),
            isSampled = false,
        )
    }

    class Factory(
        private val prerenderFrames: Boolean = false,
    ) : Decoder.Factory {

        override fun create(
            result: SourceFetchResult,
            options: Options,
            imageLoader: ImageLoader,
        ): Decoder? {
            if (!isGif(result.source.source())) return null
            return AnimatedSkiaImageDecoder(
                source = result.source,
                prerenderFrames = prerenderFrames
            )
        }
    }
}

private class AnimatedSkiaImage(
    private val codec: Codec,
    prerenderFrames: Boolean,
) : Image {
    private val imageInfo = ImageInfo(
        colorInfo = ColorInfo(
            colorType = ColorType.BGRA_8888,
            alphaType = ColorAlphaType.UNPREMUL,
            colorSpace = ColorSpace.sRGB,
        ),
        width = codec.width,
        height = codec.height,
    )

    private val bitmap = Bitmap().apply { allocPixels(codec.imageInfo) }
    private val frames = Array(codec.frameCount) { index ->
        if (prerenderFrames) decodeFrame(index) else null
    }

    private var invalidateTick by mutableIntStateOf(0)

    private var currentRepetitionStartTime: TimeSource.Monotonic.ValueTimeMark? = null
    private var currentRepetitionCount = 0
    private var lastDrawnFrameIndex = 0
    private var isAnimationComplete = false

    override val size: Long
        get() {
            var size = codec.imageInfo.computeMinByteSize().toLong()
            if (size <= 0L) {
                // Estimate 4 bytes per pixel.
                size = 4L * codec.width * codec.height
            }
            return size.coerceAtLeast(0)
        }

    override val width: Int
        get() = codec.width

    override val height: Int
        get() = codec.height

    override val shareable: Boolean
        get() = false

    override fun draw(canvas: Canvas) {
        if (codec.frameCount == 0) {
            // The image is empty, nothing to draw.
            return
        }

        if (codec.frameCount == 1) {
            // This is a static image, simply draw it.
            canvas.drawFrame(0)
            return
        }

        if (isAnimationComplete) {
            // The animation is complete, freeze on the last frame.
            canvas.drawFrame(lastDrawnFrameIndex)
            return
        }

        val startTime = currentRepetitionStartTime
            ?: TimeSource.Monotonic.markNow().also { currentRepetitionStartTime = it }
        val elapsedTime = startTime.elapsedNow().inWholeMilliseconds

        var accumulatedDuration = 0
        var frameIndexToDraw = codec.frameCount - 1

        // Find the right frame to draw based on the elapsed time.
        for ((index, frame) in codec.framesInfo.withIndex()) {
            if (accumulatedDuration > elapsedTime) {
                frameIndexToDraw = (index - 1).coerceAtLeast(0)
                break
            }

            accumulatedDuration += frame.safeFrameDuration
        }

        // Remember the last frame we drew; the next time we draw, we'll start from here.
        lastDrawnFrameIndex = frameIndexToDraw

        // Check if we've reached the last frame of the last repetition. If so, we're done.
        isAnimationComplete = codec.repetitionCount in 1..currentRepetitionCount &&
                frameIndexToDraw == (codec.frameCount - 1)

        canvas.drawFrame(frameIndexToDraw)

        // We still need to wait for the last frame's duration before we start with the next repetition.
        val drewLastFrame = frameIndexToDraw == codec.frameCount - 1
        val lastFrameDuration = codec.framesInfo[frameIndexToDraw].safeFrameDuration
        val hasLastFrameDurationElapsed = elapsedTime >= accumulatedDuration + lastFrameDuration

        if (!isAnimationComplete && drewLastFrame && hasLastFrameDurationElapsed) {
            // We've reached the last frame of the current repetition, but we can still loop.
            // Reset the state and start over from the first frame.
            lastDrawnFrameIndex = 0
            currentRepetitionCount++
            currentRepetitionStartTime = null
        }

        if (!isAnimationComplete) {
            // Increment this value to force the image to be redrawn.
            invalidateTick++
        }
    }

    private fun decodeFrame(frameIndex: Int): ByteArray {
        codec.readPixels(bitmap, frameIndex)
        return bitmap.readPixels(imageInfo, imageInfo.minRowBytes)!!
    }

    private fun Canvas.drawFrame(frameIndex: Int) {
        val frame = frames[frameIndex] ?: decodeFrame(frameIndex).also { frames[frameIndex] = it }
        drawImage(
            image = SkiaImage.makeRaster(
                imageInfo = imageInfo,
                bytes = frame,
                rowBytes = imageInfo.minRowBytes,
            ),
            left = 0f,
            top = 0f,
        )
    }
}

private val AnimationFrameInfo.safeFrameDuration: Int
    get() = duration.let { if (it <= 0) DEFAULT_FRAME_DURATION else it }

private const val DEFAULT_FRAME_DURATION = 100

// Copied from coil3.gif

// https://www.matthewflickinger.com/lab/whatsinagif/bits_and_bytes.asp
private val GIF_HEADER_87A = "GIF87a".encodeUtf8()
private val GIF_HEADER_89A = "GIF89a".encodeUtf8()

/**
 * Return 'true' if the [source] contains a GIF image. The [source] is not consumed.
 */
private fun isGif(source: BufferedSource): Boolean {
    return source.rangeEquals(0, GIF_HEADER_89A) ||
            source.rangeEquals(0, GIF_HEADER_87A)
}