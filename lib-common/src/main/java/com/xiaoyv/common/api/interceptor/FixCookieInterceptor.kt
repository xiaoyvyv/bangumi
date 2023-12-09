@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.common.api.interceptor

import com.blankj.utilcode.constant.TimeConstants
import com.xiaoyv.common.kts.debugLog
import okhttp3.Cookie
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Class: [FixCookieInterceptor]
 *
 * @author why
 * @since 11/24/23
 */
class FixCookieInterceptor : Interceptor {
    private val cookieHeader = "Set-Cookie"
    private val cookieAuthName = "chii_auth"

    /**
     * Auth 的授权 Cookie，修改为一年
     */
    private val cookieExpiresAt: Long
        get() = System.currentTimeMillis() + TimeConstants.DAY * 365L

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (!response.isSuccessful && !response.isRedirect) return response

        // 获取全部的 Cookie
        val strings = response.headers(cookieHeader)
        if (strings.isEmpty()) return response

        // 处理特定的 Cookie
        val httpUrl = response.request.url
        val cookies = strings.map { setCookie ->
            // 返回自定义的补充过期时间
            if (setCookie.contains(cookieAuthName)) {
                val cookie = Cookie.parse(httpUrl, setCookie)
                if (cookie != null) {
                    return@map cookie.newBuilder()
                        .expiresAt(cookieExpiresAt)
                        .build()
                        .toString()
                }
            }

            setCookie
        }

        val headers = response.headers.newBuilder()
            .removeAll(cookieHeader)
            .apply {
                cookies.forEach {
                    add(cookieHeader, it)
                }
            }
            .build()

        return response.newBuilder()
            .headers(headers)
            .build()
    }
}