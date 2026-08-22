@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.api.client

import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.defaultJson
import com.xiaoyv.bangumi.shared.core.utils.uppercaseFirstChar
import com.xiaoyv.bangumi.shared.data.api.client.converter.HttpCodeConverterFactory
import com.xiaoyv.bangumi.shared.data.api.client.converter.HttpDocumentConverterFactory
import com.xiaoyv.bangumi.shared.data.api.client.plugin.AuthBgmProxyPlugin
import com.xiaoyv.bangumi.shared.data.api.client.plugin.AuthCompat
import com.xiaoyv.bangumi.shared.data.api.client.plugin.AuthDouBanPlugin
import com.xiaoyv.bangumi.shared.data.api.client.plugin.AuthPixivPlugin
import com.xiaoyv.bangumi.shared.data.api.client.plugin.JsonContentTypePlugin
import com.xiaoyv.bangumi.shared.data.manager.app.PreferenceStore
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeAuthToken
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ComposePixivToken
import com.xiaoyv.bangumi.shared.systemDevice
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
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
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendIfNameAbsent

private const val NETWORK_LOG_CHUNK_SIZE = 2_000

/**
 * 创建应用统一配置的 [HttpClient]。
 *
 * @param config 网络域名、TLS 分片和超时配置。
 * @param redirect 是否允许客户端自动处理重定向。
 * @param logLevel Debug 构建下的网络日志级别。
 * @param cookieStorage 当前客户端使用的 Cookie 存储。
 * @param enableJsonContentNegotiation 是否安装 JSON 序列化支持。
 * @param block 在通用插件之后追加的业务插件配置。
 */
fun createHttpClient(
    config: ComposeSetting.NetworkConfig,
    redirect: Boolean = true,
    logLevel: LogLevel = LogLevel.ALL,
    cookieStorage: CookiesStorage = AcceptAllCookiesStorage(),
    enableJsonContentNegotiation: Boolean = true,
    block: HttpClientConfig<*>.() -> Unit = {},
): HttpClient = System.createHttpClient(
    hosts = config.configHosts,
    tlsFragmentationDomains = config.tlsFragmentationDomains,
) {
    installRedirect(redirect)
    installCookies(cookieStorage)
    installTimeout(config)
    installContentNegotiation(enableJsonContentNegotiation)
    installContentEncoding()

    block()

    installDefaultRequestHeaders()
    installNetworkLogging(logLevel)
}

/**
 * 创建用于 JSON API 的 [Ktorfit]，统一处理非成功 HTTP 状态码。
 *
 * @param client 执行请求的客户端。
 * @param url API 根地址。
 */
internal fun createApiKtorfit(client: HttpClient, url: String): Ktorfit = ktorfit {
    httpClient(client)
    baseUrl(url)
    converterFactories(HttpCodeConverterFactory())
}

/**
 * 创建可同时解析 HTML 文档与 HTTP 状态码的 [Ktorfit]。
 *
 * @param client 执行请求的客户端。
 * @param url API 根地址。
 */
internal fun createDocumentKtorfit(client: HttpClient, url: String): Ktorfit = ktorfit {
    httpClient(client)
    baseUrl(url)
    converterFactories(HttpDocumentConverterFactory(), HttpCodeConverterFactory())
}

/**
 * Douban Api 自动授权
 *
 * @param config 提供豆瓣 User-Agent 与签名密钥的网络配置。
 */
internal fun HttpClientConfig<*>.installDbAuth(config: ComposeSetting.NetworkConfig) {
    install(AuthDouBanPlugin) {
        agent = config.douBanUA
        key = config.douBanKey
    }
}

/**
 * Pixiv Api 自动授权
 *
 * @param config Pixiv 客户端及代理配置。
 * @param preferenceStore Token 持久化存储。
 * @param refreshToken 使用 Refresh Token 换取新 Token 的请求实现。
 */
internal fun HttpClientConfig<*>.installPixivAuth(
    config: ComposeSetting.NetworkConfig,
    preferenceStore: PreferenceStore,
    refreshToken: suspend (String) -> ComposePixivToken,
) {
    install(AuthCompat)
    install(AuthPixivPlugin) {
        network = config
        os = systemDevice.os
        userAgent = buildString {
            append("PixivAndroidApp/${config.pixivVersion} (")
            append(systemDevice.os.uppercaseFirstChar())
            append(" ${systemDevice.systemVersion}; ")
            append(systemDevice.deviceModel)
            append(")")
        }
    }
    install(Auth) {
        // Pixiv 自动授权
        bearer {
            cacheTokens = false
            sendWithoutRequest { request -> request.url.host.contains("app-api.pixiv.net") }
            loadTokens {
                preferenceStore.pixivToken.toBearerTokensOrNull()
            }
            refreshTokens {
                val oldRefreshToken = oldTokens?.refreshToken.orEmpty()
                if (oldRefreshToken.isBlank()) return@refreshTokens null

                runCatching { refreshToken(oldRefreshToken) }
                    .onSuccess { preferenceStore.pixivToken = it }
                    .onFailure { preferenceStore.pixivToken = ComposePixivToken.Empty }
                    .getOrNull()
                    ?.let { BearerTokens(it.accessToken, it.refreshToken) }
            }
        }
    }
}

/**
 * Bgm Public Api 自动授权
 *
 * @param preferenceStore 登录状态和 Token 持久化存储。
 * @param refreshToken 使用 Refresh Token 换取新 Token 的请求实现。
 * @param reauthorize Refresh Token 失效后通过 Cookie 重新授权的实现。
 */
internal fun HttpClientConfig<*>.installBgmAuth(
    preferenceStore: PreferenceStore,
    refreshToken: suspend (String) -> ComposeAuthToken,
    reauthorize: suspend () -> ComposeAuthToken,
) {
    install(AuthCompat)
    install(Auth) {
        bearer {
            cacheTokens = false
            sendWithoutRequest { request ->
                request.url.host == "api.bgm.tv" || request.url.host == "next.bgm.tv"
            }
            loadTokens {
                preferenceStore.userToken.toBearerTokensOrNull()
            }
            refreshTokens {
                val oldRefreshToken = oldTokens?.refreshToken.orEmpty()
                if (oldRefreshToken.isBlank()) return@refreshTokens null

                val newToken = try {
                    refreshToken(oldRefreshToken)
                } catch (_: Exception) {
                    // 意外情况，Cookie 还有登录信息，但是 Token 和 RefreshToken 都失效了，重新自动授权
                    if (preferenceStore.userInfo == ComposeUser.Empty) {
                        preferenceStore.userToken = ComposeAuthToken.Empty
                        return@refreshTokens null
                    }
                    reauthorize()
                }

                // 保存新的 Token
                preferenceStore.userToken = newToken
                BearerTokens(newToken.accessToken, newToken.refreshToken)
            }
        }
    }
    install(AuthBgmProxyPlugin) {
        bgmUrl = preferenceStore.settings.network.bgmHost
    }
}

private fun ComposeAuthToken.toBearerTokensOrNull(): BearerTokens? =
    if (accessToken.isBlank() || refreshToken.isBlank()) null else BearerTokens(accessToken, refreshToken)

private fun ComposePixivToken.toBearerTokensOrNull(): BearerTokens? =
    if (accessToken.isBlank() || refreshToken.isBlank()) null else BearerTokens(accessToken, refreshToken)

private fun HttpClientConfig<*>.installRedirect(enabled: Boolean) {
    if (!enabled) return

    install(HttpRedirect) {
        checkHttpMethod = false
        allowHttpsDowngrade = true
    }
}

private fun HttpClientConfig<*>.installCookies(cookieStorage: CookiesStorage) {
    install(HttpCookies) {
        storage = cookieStorage
    }
}

private fun HttpClientConfig<*>.installTimeout(config: ComposeSetting.NetworkConfig) {
    install(HttpTimeout) {
        connectTimeoutMillis = config.connectTimeoutMillis
        socketTimeoutMillis = config.socketTimeoutMillis
        requestTimeoutMillis = config.connectTimeoutMillis + config.socketTimeoutMillis + 5_000
    }
}

private fun HttpClientConfig<*>.installContentNegotiation(enabled: Boolean) {
    if (!enabled) return

    install(JsonContentTypePlugin)
    install(ContentNegotiation) {
        json(defaultJson)
    }
}

private fun HttpClientConfig<*>.installContentEncoding() {
    install(ContentEncoding) {
        deflate(1f)
        gzip(0.9f)
        identity()
    }
}

private fun HttpClientConfig<*>.installDefaultRequestHeaders() {
    defaultRequest {
        headers.appendIfNameAbsent(HttpHeaders.Pragma, "no-cache")
        headers.appendIfNameAbsent(HttpHeaders.CacheControl, "no-cache")
        headers.appendIfNameAbsent(HttpHeaders.TE, "trailers")
        headers.appendIfNameAbsent(HttpHeaders.AcceptLanguage, "zh-CN,zh;q=0.8,zh-TW;q=0.6,zh-HK;q=0.4,en;q=0.2")
        headers.appendIfNameAbsent(HttpHeaders.Cookie, "kira=4")
        headers.appendIfNameAbsent(HttpHeaders.UserAgent, System.userAgent())
    }
}

private fun HttpClientConfig<*>.installNetworkLogging(logLevel: LogLevel) {
    if (!System.isDebugType) return

    // 日志放最后，保证能打印前面的修改内容
    install(Logging) {
        format = LoggingFormat.OkHttp
        level = logLevel
        sanitizeHeader { false }
        logger = NetworkLogger
    }
}

private object NetworkLogger : Logger {
    override fun log(message: String) {
        message.lineSequence().forEach { line ->
            var start = 0
            while (start < line.length) {
                val end = minOf(start + NETWORK_LOG_CHUNK_SIZE, line.length)
                debugLog {
                    setTag { "Network" }
                    line.substring(start, end)
                }
                start = end
            }
        }
    }
}
