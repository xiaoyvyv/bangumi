package com.xiaoyv.common.config.glide

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.tencent.libavif.AvifSequenceDrawable
import com.tencent.qcloud.image.avif.glide.avif.ByteBufferAvifDecoder
import com.tencent.qcloud.image.avif.glide.avif.ByteBufferAvifSequenceDecoder
import com.tencent.qcloud.image.avif.glide.avif.StreamAvifDecoder
import com.tencent.qcloud.image.avif.glide.avif.StreamAvifSequenceDecoder
import com.xiaoyv.common.api.BgmApiManager
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Source
import okio.buffer
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentHashMap


/**
 * Created by Jelly on 2017/8/16.
 */
@GlideModule
class OkHttpProgressGlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {}

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(
                BgmApiManager.httpClient
                    .newBuilder()
                    .apply { networkInterceptors().removeIf { it is HttpLoggingInterceptor } }
                    .addNetworkInterceptor(createInterceptor(DispatchingProgressListener()))
                    .build()
            )
        )
        registry.prepend(
            Registry.BUCKET_BITMAP,
            InputStream::class.java,
            Bitmap::class.java, StreamAvifDecoder(glide.bitmapPool, glide.arrayPool)
        )
        registry.prepend(
            Registry.BUCKET_BITMAP,
            ByteBuffer::class.java,
            Bitmap::class.java, ByteBufferAvifDecoder(glide.bitmapPool)
        )
        registry.prepend(
            InputStream::class.java,
            AvifSequenceDrawable::class.java,
            StreamAvifSequenceDecoder(glide.bitmapPool, glide.arrayPool)
        )
        registry.prepend(
            ByteBuffer::class.java,
            AvifSequenceDrawable::class.java, ByteBufferAvifSequenceDecoder(glide.bitmapPool)
        )
    }

    interface UIProgressListener {
        fun onProgress(bytesRead: Long, expectedLength: Long)

        /**
         * Control how often the listener needs an update. 0% and 100% will always be dispatched.
         * @return in percentage (0.2 = call [.onProgress] around every 0.2 percent of progress)
         */
        val granualityPercentage: Float
    }

    private interface ResponseProgressListener {
        fun update(url: HttpUrl, bytesRead: Long, contentLength: Long)
    }

    private class DispatchingProgressListener : ResponseProgressListener {
        private val handler = Handler(Looper.getMainLooper())

        override fun update(url: HttpUrl, bytesRead: Long, contentLength: Long) {
            val key = url.toString()
            val listener = LISTENERS[key] ?: return
            if (contentLength <= bytesRead) {
                forget(key)
            }

            if (needsDispatch(key, bytesRead, contentLength, listener.granualityPercentage)) {
                handler.post { listener.onProgress(bytesRead, contentLength) }
            }
        }

        private fun needsDispatch(
            key: String,
            current: Long,
            total: Long,
            granularity: Float,
        ): Boolean {
            if (granularity == 0f || current == 0L || total == current) {
                return true
            }
            val percent = 100f * current / total
            val currentProgress = (percent / granularity).toLong()
            val lastProgress = PROGRESSES[key]
            return if (lastProgress == null || currentProgress != lastProgress) {
                PROGRESSES[key] = currentProgress
                true
            } else {
                false
            }
        }

        companion object {
            private val LISTENERS = ConcurrentHashMap<String, UIProgressListener>()
            private val PROGRESSES = ConcurrentHashMap<String, Long>()

            fun forget(url: String) {
                LISTENERS.remove(url)
                PROGRESSES.remove(url)
            }

            fun expect(url: String, listener: UIProgressListener) {
                LISTENERS[url] = listener
            }
        }
    }

    private class OkHttpProgressResponseBody(
        private val url: HttpUrl,
        private val responseBody: ResponseBody,
        private val progressListener: ResponseProgressListener,
    ) : ResponseBody() {
        private var bufferedSource: BufferedSource? = null

        override fun contentType(): MediaType? {
            return responseBody.contentType()
        }

        override fun contentLength(): Long {
            return responseBody.contentLength()
        }

        override fun source(): BufferedSource {
            return bufferedSource ?: source(responseBody.source()).buffer()
                .also { bufferedSource = it }
        }

        private fun source(source: Source): Source {
            return object : ForwardingSource(source) {
                var totalBytesRead = 0L

                @Throws(IOException::class)
                override fun read(sink: Buffer, byteCount: Long): Long {
                    val bytesRead = super.read(sink, byteCount)
                    val fullLength = responseBody.contentLength()
                    if (bytesRead == -1L) { // this source is exhausted
                        totalBytesRead = fullLength
                    } else {
                        totalBytesRead += bytesRead
                    }
                    progressListener.update(url, totalBytesRead, fullLength)
                    return bytesRead
                }
            }
        }
    }

    companion object {
        const val TAG = "GlideModule"

        @JvmStatic
        private fun createInterceptor(listener: ResponseProgressListener): Interceptor {
            return Interceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(request)

                response.newBuilder()
                    .body(OkHttpProgressResponseBody(request.url, response.body, listener))
                    .build()
            }
        }

        @JvmStatic
        fun forget(url: String) {
            DispatchingProgressListener.forget(url)
        }

        @JvmStatic
        fun expect(url: String, listener: UIProgressListener) {
            DispatchingProgressListener.expect(url, listener)
        }
    }
}
