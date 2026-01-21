package com.xiaoyv.bangumi.shared.data.api.client

import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.System.createHttpClient
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.defaultJson
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json

fun createHttpClient(
    config: ComposeSetting.NetworkConfig,
    redirect: Boolean = true,
    logLevel: LogLevel = LogLevel.HEADERS,
    cookieStorage: CookiesStorage = AcceptAllCookiesStorage(),
    block: HttpClientConfig<*>.() -> Unit = {},
): HttpClient {
    return createHttpClient {
        if (redirect) install(HttpRedirect) {
            checkHttpMethod = false
            allowHttpsDowngrade = true
        }

        install(HttpCookies) {
            storage = cookieStorage
        }

        install(HttpTimeout) {
            connectTimeoutMillis = config.connectTimeoutMillis
            socketTimeoutMillis = config.socketTimeoutMillis
            requestTimeoutMillis = config.connectTimeoutMillis + config.socketTimeoutMillis + 5_000
        }

        install(ContentNegotiation) {
            json(defaultJson)
        }

        install(ContentEncoding) {
            deflate(1f)
            gzip(0.9f)
            identity()
        }

        if (System.isDebugType) install(Logging) {
            level = logLevel
            logger = object : Logger {
                override fun log(message: String) {
                    message.lineSequence().forEach { line ->
                        var start = 0
                        while (start < line.length) {
                            val end = minOf(start + 2000, line.length)
                            debugLog { line.substring(start, end) }
                            start = end
                        }
                    }
                }
            }
        }

        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.Pragma, "no-cache")
            header(HttpHeaders.CacheControl, "no-cache")
            header(HttpHeaders.TE, "trailers")
            header(HttpHeaders.AcceptLanguage, "zh-CN,zh;q=0.8,zh-TW;q=0.6,zh-HK;q=0.4,en;q=0.2")
            header(HttpHeaders.Cookie, "kira=4")
            header(HttpHeaders.UserAgent, System.userAgent())
        }

        block()
    }
}