@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.common.api.interceptor

import android.net.Uri
import android.text.TextUtils
import android.util.Base64
import com.blankj.utilcode.util.EncryptUtils
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Class: [DouBanInterceptor]
 *
 * @author why
 * @since 11/24/23
 */
class DouBanInterceptor : Interceptor {

    /**
     * 豆瓣
     */
    private val douBanUserAgent =
        "api-client/1 com.douban.frodo/7.65.0(277) Android/33 product/coral vendor/Google model/Pixel 4 XL brand/google  rom/android  network/wifi  udid/0643fa6abfd3eaff076ff3ee603211ded11fc344  platform/mobile nd/1"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestUrl = request.url.toString()
        if (!requestUrl.contains("frodo.douban.com")) {
            return chain.proceed(request)
        }
        var header = request.header("Authorization").orEmpty()
        if (header.isNotBlank() && header.length > 7) {
            header = header.substring(7)
        }

        val (sig, ts) = generateSign(requestUrl, request.method, header)

        val httpUrl = request.url
            .newBuilder()
            .addQueryParameter("_sig", sig)
            .addQueryParameter("_ts", ts)
            .build()

        return chain.proceed(
            request.newBuilder()
                .removeHeader("User-Agent")
                .addHeader("User-Agent", douBanUserAgent)
                .url(httpUrl)
                .build()
        )
    }

    private fun generateSign(requestUrl: String, method: String, bearToken: String): Pair<String, String> {
        val key = "bf7dddc7c9cfe6f7"
        val encodedPath = requestUrl.toHttpUrl().encodedPath
        val decode = Uri.decode(encodedPath).trimEnd('/')

        val builder = StringBuilder(method)
        builder.append("&")
        builder.append(Uri.encode(decode))

        if (!TextUtils.isEmpty(bearToken)) {
            builder.append("&")
            builder.append(bearToken)
        }

        val currentTimeMillis = System.currentTimeMillis() / 1000
        builder.append("&")
        builder.append(currentTimeMillis)

        val paramString = builder.toString()
        val bytes = EncryptUtils.encryptHmacSHA1(paramString.toByteArray(), key.toByteArray())
        val sign = Base64.encodeToString(bytes, 2)
        return Pair(sign, currentTimeMillis.toString())
    }
}