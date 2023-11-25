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

    /**
     * Host: api.bgm.tv
     * User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:109.0) Gecko/20100101 Firefox/119.0
     * Accept: application/json
     * Accept-Language: zh-CN,zh;q=0.8,zh-TW;q=0.6,zh-HK;q=0.4,en;q=0.2
     * Accept-Encoding: gzip, deflate, br
     * Referer: https://bangumi.github.io/
     * Origin: https://bangumi.github.io
     * Connection: keep-alive
     * Sec-Fetch-Dest: empty
     * Sec-Fetch-Mode: cors
     * Sec-Fetch-Site: cross-site
     * Pragma: no-cache
     * Cache-Control: no-cache
     * TE: trailers
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        return chain.proceed(
            request
                .newBuilder()
                .addHeader("Sec-Fetch-Dest", "empty")
                .addHeader("Sec-Fetch-Mode", "cors")
                .addHeader("Sec-Fetch-Site", "cross-site")
                .addHeader("Pragma", "no-cache")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("TE", "trailers")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.6,zh-HK;q=0.4,en;q=0.2")
                .addHeader("User-Agent", userAgent)
                .addHeader("Referer", request.url.toString())
                .build()
        )
    }
}