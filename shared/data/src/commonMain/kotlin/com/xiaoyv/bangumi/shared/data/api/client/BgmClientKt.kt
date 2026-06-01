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
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.LoggingFormat
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendIfNameAbsent
import io.ktor.utils.io.KtorDsl

@KtorDsl
class BgmProxyConfig(
    var proxyBaseUrl: String = "",
    var cookieStorage: CookiesStorage = AcceptAllCookiesStorage(),
)

val BgmProxyPlugin: ClientPlugin<BgmProxyConfig> =
    createClientPlugin("BgmProxyPlugin", ::BgmProxyConfig) {
        val config = pluginConfig

        onRequest { request, _ ->
            val proxyBaseUrl = config.proxyBaseUrl.trim()
            if (proxyBaseUrl.isBlank()) return@onRequest

            val normalizedProxyBaseUrl = if (proxyBaseUrl.endsWith("/")) proxyBaseUrl else "$proxyBaseUrl/"

            val originalUrl = request.url.toString()
            if (originalUrl.startsWith(normalizedProxyBaseUrl)) return@onRequest

            val host = request.url.host.lowercase()
            if (!host.isBgmHost()) return@onRequest

            val cookieHeader = buildProxyCookieHeader(
                requestCookieHeader = request.headers[HttpHeaders.Cookie],
                cookieStorage = config.cookieStorage,
                originalUrl = originalUrl,
            )
            if (cookieHeader.isNotBlank()) {
                request.headers[HttpHeaders.Cookie] = cookieHeader
            }

            request.url.parameters.clear()
            request.url.takeFrom(normalizedProxyBaseUrl + originalUrl)
        }
    }

private fun String.isBgmHost(): Boolean {
    return this == "bgm.tv" ||
        this.endsWith(".bgm.tv") ||
        this == "bangumi.tv" ||
        this.endsWith(".bangumi.tv") ||
        this == "chii.in" ||
        this.endsWith(".chii.in")
}

private suspend fun buildProxyCookieHeader(
    requestCookieHeader: String?,
    cookieStorage: CookiesStorage,
    originalUrl: String,
): String {
    val cookieMap = LinkedHashMap<String, String>()

    requestCookieHeader
        .orEmpty()
        .split(";")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .forEach { part ->
            val name = part.substringBefore("=").trim()
            val value = part.substringAfter("=", missingDelimiterValue = "").trim()
            if (name.isNotBlank() && value.isNotBlank()) {
                cookieMap[name] = value
            }
        }

    val storedCookies = runCatching { cookieStorage.get(Url(originalUrl)) }.getOrDefault(emptyList())
    storedCookies.forEach { cookie ->
        if (cookie.name.isNotBlank() && cookie.value.isNotBlank()) {
            cookieMap[cookie.name] = cookie.value
        }
    }

    cookieMap.putIfAbsent("kira", "4")

    return cookieMap.entries.joinToString("; ") { "${it.key}=${it.value}" }
}

fun createHttpClient(
    config: ComposeSetting.NetworkConfig,
    redirect: Boolean = true,
    logLevel: LogLevel = LogLevel.BODY,
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

        install(BgmProxyPlugin) {
            proxyBaseUrl = config.bgmProxy
            this.cookieStorage = cookieStorage
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
            format = LoggingFormat.Default
            level = logLevel
            logger = object : Logger {
                override fun log(message: String) {
                    message.lineSequence().forEach { line ->
                        var start = 0
                        while (start < line.length) {
                            val end = minOf(start + 2000, line.length)
                            debugLog {
                                setTag { "Network" }
                                line.substring(start, end)
                            }
                            start = end
                        }
                    }
                }
            }
        }

        defaultRequest {
            headers.appendIfNameAbsent(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            headers.appendIfNameAbsent(HttpHeaders.Pragma, "no-cache")
            headers.appendIfNameAbsent(HttpHeaders.CacheControl, "no-cache")
            headers.appendIfNameAbsent(HttpHeaders.TE, "trailers")
            headers.appendIfNameAbsent(HttpHeaders.AcceptLanguage, "zh-CN,zh;q=0.8,zh-TW;q=0.6,zh-HK;q=0.4,en;q=0.2")
            headers.appendIfNameAbsent(HttpHeaders.Cookie, "kira=4")
            headers.appendIfNameAbsent(HttpHeaders.UserAgent, System.userAgent())
        }

        block()
    }
}
