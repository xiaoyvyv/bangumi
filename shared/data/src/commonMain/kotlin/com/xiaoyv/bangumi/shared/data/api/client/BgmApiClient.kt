@file:Suppress("FunctionName", "SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.api.client

import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.System.createHttpClient
import com.xiaoyv.bangumi.shared.core.types.AppDsl
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.defaultJson
import com.xiaoyv.bangumi.shared.core.utils.uppercaseFirstChar
import com.xiaoyv.bangumi.shared.data.api.BgmJsonApi
import com.xiaoyv.bangumi.shared.data.api.BgmWebApi
import com.xiaoyv.bangumi.shared.data.api.DouBanApi
import com.xiaoyv.bangumi.shared.data.api.ImageApi
import com.xiaoyv.bangumi.shared.data.api.PixivApi
import com.xiaoyv.bangumi.shared.data.api.TraceApi
import com.xiaoyv.bangumi.shared.data.api.app.createAppApi
import com.xiaoyv.bangumi.shared.data.api.client.converter.HttpCodeConverterFactory
import com.xiaoyv.bangumi.shared.data.api.client.converter.HttpDocumentConverterFactory
import com.xiaoyv.bangumi.shared.data.api.client.plugin.DouBanPlugin
import com.xiaoyv.bangumi.shared.data.api.client.plugin.PixivProxyPlugin
import com.xiaoyv.bangumi.shared.data.api.createBgmJsonApi
import com.xiaoyv.bangumi.shared.data.api.createBgmWebApi
import com.xiaoyv.bangumi.shared.data.api.createDouBanApi
import com.xiaoyv.bangumi.shared.data.api.createImageApi
import com.xiaoyv.bangumi.shared.data.api.createPixivApi
import com.xiaoyv.bangumi.shared.data.api.createTraceApi
import com.xiaoyv.bangumi.shared.data.api.magnet.MikanApi
import com.xiaoyv.bangumi.shared.data.api.magnet.createMikanApi
import com.xiaoyv.bangumi.shared.data.api.next.CharacterApi
import com.xiaoyv.bangumi.shared.data.api.next.CollectionApi
import com.xiaoyv.bangumi.shared.data.api.next.EpisodeApi
import com.xiaoyv.bangumi.shared.data.api.next.GroupApi
import com.xiaoyv.bangumi.shared.data.api.next.PersonApi
import com.xiaoyv.bangumi.shared.data.api.next.RelationshipApi
import com.xiaoyv.bangumi.shared.data.api.next.SearchApi
import com.xiaoyv.bangumi.shared.data.api.next.SubjectApi
import com.xiaoyv.bangumi.shared.data.api.next.TimelineApi
import com.xiaoyv.bangumi.shared.data.api.next.UserApi
import com.xiaoyv.bangumi.shared.data.api.next.createCharacterApi
import com.xiaoyv.bangumi.shared.data.api.next.createCollectionApi
import com.xiaoyv.bangumi.shared.data.api.next.createEpisodeApi
import com.xiaoyv.bangumi.shared.data.api.next.createGroupApi
import com.xiaoyv.bangumi.shared.data.api.next.createPersonApi
import com.xiaoyv.bangumi.shared.data.api.next.createRelationshipApi
import com.xiaoyv.bangumi.shared.data.api.next.createSearchApi
import com.xiaoyv.bangumi.shared.data.api.next.createSubjectApi
import com.xiaoyv.bangumi.shared.data.api.next.createTimelineApi
import com.xiaoyv.bangumi.shared.data.api.next.createUserApi
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.data.manager.app.PreferenceStore
import com.xiaoyv.bangumi.shared.systemDevice
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.KtorDsl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * [BgmApiClient]
 *
 * @author why
 * @since 2025/1/14
 */
@AppDsl
class BgmApiClient(
    private val cookieStorage: BgmCookieStorage,
    private val preferenceStore: PreferenceStore,
) {
    val client by lazy {
        BgmHttpClientImpl(redirect = true, cookie = true)
    }

    private val clientNoCookie by lazy {
        BgmHttpClientImpl(redirect = true, cookie = false)
    }

    private val clientNoRedirect by lazy {
        BgmHttpClientImpl(redirect = false, cookie = true)
    }

    val baseUrl = preferenceStore.settings.network.bgmHost

    private val webRetrofit = ktorfit {
        httpClient(client)
        baseUrl(baseUrl)
        converterFactories(HttpDocumentConverterFactory(), HttpCodeConverterFactory())
    }

    private val webNoRedirectRetrofit = ktorfit {
        httpClient(clientNoRedirect)
        baseUrl(baseUrl)
        converterFactories(HttpDocumentConverterFactory(), HttpCodeConverterFactory())
    }

    private val webNoCookieRetrofit = ktorfit {
        httpClient(clientNoCookie)
        baseUrl(baseUrl)
        converterFactories(HttpDocumentConverterFactory(), HttpCodeConverterFactory())
    }

    private val jsonRetrofit = ktorfit {
        httpClient(clientNoRedirect)
        baseUrl(WebConstant.URL_BASE_API)
        converterFactories(HttpCodeConverterFactory())
    }

    private val jsonNextRetrofit = ktorfit {
        httpClient(clientNoRedirect)
        baseUrl(WebConstant.URL_BASE_NEXT_API)
        converterFactories(HttpCodeConverterFactory())
    }

    private val jsonAppRetrofit = ktorfit {
        httpClient(clientNoRedirect)
        baseUrl(WebConstant.URL_BASE_APP_API)
        converterFactories(HttpCodeConverterFactory())
    }

    val bgmWebApi = webRetrofit.createBgmWebApi()
    val bgmWebNoRedirectApi = webNoRedirectRetrofit.createBgmWebApi()
    val bgmWebNoCookieApi = webNoCookieRetrofit.createBgmWebApi()
    val bgmJsonApi = jsonRetrofit.createBgmJsonApi()
    val dbApi = jsonRetrofit.createDouBanApi()
    val traceApi = jsonRetrofit.createTraceApi()
    val imageApi = jsonRetrofit.createImageApi()
    val pixivApi = jsonRetrofit.createPixivApi()
    val appApi = jsonAppRetrofit.createAppApi()
    val mikanApi = webRetrofit.createMikanApi()

    val nextRelationshipApi = jsonNextRetrofit.createRelationshipApi()
    val nextUserApi = jsonNextRetrofit.createUserApi()
    val nextGroupApi = jsonNextRetrofit.createGroupApi()
    val nextCharacterApi = jsonNextRetrofit.createCharacterApi()
    val nextSubjectApi = jsonNextRetrofit.createSubjectApi()
    val nextPersonApi = jsonNextRetrofit.createPersonApi()
    val nextEpisodeApi = jsonNextRetrofit.createEpisodeApi()
    val nextCollectionApi = jsonNextRetrofit.createCollectionApi()
    val nextTimelineApi = jsonNextRetrofit.createTimelineApi()
    val nextSearchApi = jsonNextRetrofit.createSearchApi()

    suspend fun <R> requestTraceApi(block: suspend TraceApi.() -> R) = requestApi(traceApi, block = block)
    suspend fun <R> requestImageApi(block: suspend ImageApi.() -> R) = requestApi(imageApi, block = block)
    suspend fun <R> requestDouBanApi(block: suspend DouBanApi.() -> R) = requestApi(dbApi, block = block)
    suspend fun <R> requestPixivApi(block: suspend PixivApi.() -> R) = requestApi(pixivApi, block = block)
    suspend fun <R> requestMikanApi(block: suspend MikanApi.() -> R) = requestApi(mikanApi, block = block)

    suspend fun <R> requestNextGroupApi(block: suspend GroupApi.() -> R) = requestApi(nextGroupApi, block = block)
    suspend fun <R> requestNextUserApi(block: suspend UserApi.() -> R) = requestApi(nextUserApi, block = block)
    suspend fun <R> requestNextRelationshipApi(block: suspend RelationshipApi.() -> R) = requestApi(nextRelationshipApi, block = block)
    suspend fun <R> requestNextCharacterApi(block: suspend CharacterApi.() -> R) = requestApi(nextCharacterApi, block = block)
    suspend fun <R> requestNextSubjectApi(block: suspend SubjectApi.() -> R) = requestApi(nextSubjectApi, block = block)
    suspend fun <R> requestNextSearchApi(block: suspend SearchApi.() -> R) = requestApi(nextSearchApi, block = block)
    suspend fun <R> requestNextPersonApi(block: suspend PersonApi.() -> R) = requestApi(nextPersonApi, block = block)
    suspend fun <R> requestNextEpisodeApi(block: suspend EpisodeApi.() -> R) = requestApi(nextEpisodeApi, block = block)
    suspend fun <R> requestNextCollectionApi(block: suspend CollectionApi.() -> R) = requestApi(nextCollectionApi, block = block)
    suspend fun <R> requestNextTimelineApi(block: suspend TimelineApi.() -> R) = requestApi(nextTimelineApi, block = block)


    /**
     * 请求 BgmJsonApi 数据 DSL
     */
    suspend fun <R> requestJsonApi(context: CoroutineContext = Dispatchers.IO, block: suspend BgmJsonApi.() -> R) =
        runCatching { withContext(context) { bgmJsonApi.block() } }
            .onFailure { debugLog { it } }


    /**
     * 请求 BgmJsonApi 数据 DSL
     */
    suspend fun <R> requestWebApi(
        context: CoroutineContext = Dispatchers.IO,
        disableRedirect: Boolean = false,
        block: suspend BgmWebApi.() -> R,
    ) = runCatching { withContext(context) { (if (disableRedirect) bgmWebNoRedirectApi else bgmWebApi).block() } }
        .onFailure { debugLog { it } }

    suspend fun <API : Any, R> requestApi(
        api: API,
        context: CoroutineContext = Dispatchers.IO,
        block: suspend API.() -> R,
    ) = runCatching { withContext(context) { api.block() } }
        .onFailure { debugLog { it } }


    @KtorDsl
    private fun BgmHttpClientImpl(redirect: Boolean, cookie: Boolean): HttpClient {
        return createHttpClient {
            val config = preferenceStore.settings.network

            // 重定向配置，不检查方法，让POST的302也支持重定向
            if (redirect) {
                install(HttpRedirect) {
                    checkHttpMethod = false
                    allowHttpsDowngrade = true
                }
            }

            install(HttpTimeout) {
                connectTimeoutMillis = config.connectTimeoutMillis
                socketTimeoutMillis = config.socketTimeoutMillis
                requestTimeoutMillis = config.connectTimeoutMillis + config.socketTimeoutMillis + 5_000
            }

            install(PixivProxyPlugin) {
                network = config
                os = systemDevice.os
                userAgent = buildString {
                    append("PixivAndroidApp/${config.pixivVersion} (")
                    append(systemDevice.os.uppercaseFirstChar())
                    append(" ")
                    append(systemDevice.systemVersion)
                    append("; ")
                    append(systemDevice.deviceModel)
                    append(")")
                }
            }

            install(Auth) {
                // Bangumi Api 自动授权
                bearer {
                    loadTokens {
                        val token = preferenceStore.userToken
                        if (token.accessToken.isBlank() || token.refreshToken.isBlank()) null else BearerTokens(
                            accessToken = token.accessToken,
                            refreshToken = token.refreshToken
                        )
                    }

                    refreshTokens {
                        val refreshToken = oldTokens?.refreshToken.orEmpty()
                        if (refreshToken.isBlank()) null else {
                            val authToken = bgmWebNoRedirectApi.sendAuthJsonApiToken(
                                refreshToken = refreshToken,
                                grantType = "refresh_token"
                            )
                            preferenceStore.userToken = authToken
                            BearerTokens(authToken.accessToken, authToken.refreshToken)
                        }
                    }

                    sendWithoutRequest { request ->
                        request.url.host.contains("bgm.tv", true)
                                || request.url.host.contains("bangumi.tv", true)
                                || request.url.host.contains("chii.in", true)
                    }
                }

                // Pixiv 自动授权
                bearer {
                    loadTokens {
                        val token = preferenceStore.pixivToken
                        if (token.accessToken.isBlank() || token.refreshToken.isBlank()) null else BearerTokens(
                            accessToken = token.accessToken,
                            refreshToken = token.refreshToken
                        )
                    }

                    refreshTokens {
                        val refreshToken = oldTokens?.refreshToken.orEmpty()
                        if (refreshToken.isBlank()) null else {
                            val newToken = pixivApi.sendAuthTokenRefresh(
                                grantType = "refresh_token",
                                clientId = config.pixivClientId,
                                clientSecret = config.pixivClientSecret,
                                includePolicy = true,
                                refreshToken = refreshToken
                            )
                            preferenceStore.pixivToken = newToken
                            BearerTokens(newToken.accessToken, newToken.refreshToken)
                        }
                    }

                    sendWithoutRequest { request ->
                        request.url.host.contains("app-api.pixiv.net")
                    }
                }
            }

            install(DouBanPlugin) {
                agent = config.douBanUA
                key = config.douBanKey
            }

            install(ContentNegotiation) {
                json(defaultJson)
            }

            install(ContentEncoding) {
                deflate(1f)
                gzip(0.9f)
                identity()
            }

            if (cookie) install(HttpCookies) {
                storage = cookieStorage
            }

            if (System.isDebugType) install(Logging) {
                level = LogLevel.ALL
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
        }
    }
}

