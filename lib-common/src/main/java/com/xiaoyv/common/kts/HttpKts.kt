package com.xiaoyv.common.kts

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * timeout
 *
 * @author why
 * @since 12/25/23
 */
fun OkHttpClient.Builder.timeout(seconds: Long): OkHttpClient.Builder {
    return this
        .callTimeout(seconds, TimeUnit.SECONDS)
        .readTimeout(seconds, TimeUnit.SECONDS)
        .writeTimeout(seconds, TimeUnit.SECONDS)
        .connectTimeout(seconds, TimeUnit.SECONDS)
}