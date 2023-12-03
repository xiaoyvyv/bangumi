package com.xiaoyv.common.config.glide

import android.util.Log
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.HttpException
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.util.ContentLengthInputStream
import com.bumptech.glide.util.Synthetic
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException
import java.io.InputStream
import kotlin.concurrent.Volatile

/**
 * Fetches an [InputStream] using the okhttp library.
 */
class OkHttpStreamFetcher(
    private val client: Call.Factory,
    private val url: GlideUrl
) : DataFetcher<InputStream> {

    @Synthetic
    var stream: InputStream? = null

    @Synthetic
    var responseBody: ResponseBody? = null

    @Volatile
    private var call: Call? = null

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream?>) {
        val requestBuilder = Request.Builder().url(url.toStringUrl())

        for ((key, value) in url.headers) {
            requestBuilder.addHeader(key, value)
        }

        val request: Request = requestBuilder.build()
        call = client.newCall(request)

        call?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "OkHttp failed to obtain result", e)
                }
                callback.onLoadFailed(e)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                responseBody = response.body

                if (response.isSuccessful) {
                    val contentLength = response.body.contentLength()
                    stream =
                        ContentLengthInputStream.obtain(response.body.byteStream(), contentLength)
                    callback.onDataReady(stream)
                } else {
                    callback.onLoadFailed(HttpException(response.message, response.code))
                }
            }
        })
    }

    override fun cleanup() {
        runCatching { stream?.close() }
        runCatching { responseBody?.close() }
    }

    override fun cancel() {
        call?.cancel()
    }

    override fun getDataClass(): Class<InputStream> {
        return InputStream::class.java
    }

    override fun getDataSource(): DataSource {
        return DataSource.REMOTE
    }

    companion object {
        private const val TAG = "OkHttpFetcher"
    }
}