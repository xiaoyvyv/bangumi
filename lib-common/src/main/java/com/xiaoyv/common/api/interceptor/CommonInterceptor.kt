package com.xiaoyv.common.api.interceptor

import com.blankj.utilcode.util.AppUtils
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Class: [CommonInterceptor]
 *
 * @author why
 * @since 11/24/23
 */
class CommonInterceptor : Interceptor {
    private val userAgent: String by lazy {
        "xiaoyvyv/Bangumi-for-Android/${AppUtils.getAppVersionName()} (Android) (https://github.com/xiaoyvyv/Bangumi-for-Android)"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        return chain.proceed(
            request
                .newBuilder()
                .addHeader("User-Agent", userAgent)
                .addHeader("Referer", request.url.toString())
                .build()
        )
    }
}