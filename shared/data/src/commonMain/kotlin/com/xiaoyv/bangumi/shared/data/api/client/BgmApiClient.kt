@file:Suppress("FunctionName", "SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.api.client

import com.xiaoyv.bangumi.shared.core.types.AppDsl
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.uppercaseFirstChar
import com.xiaoyv.bangumi.shared.data.api.AuthApi
import com.xiaoyv.bangumi.shared.data.api.BgmJsonApi
import com.xiaoyv.bangumi.shared.data.api.BgmWebApi
import com.xiaoyv.bangumi.shared.data.api.DouBanApi
import com.xiaoyv.bangumi.shared.data.api.ImageApi
import com.xiaoyv.bangumi.shared.data.api.PixivApi
import com.xiaoyv.bangumi.shared.data.api.TraceApi
import com.xiaoyv.bangumi.shared.data.api.app.AppApi
import com.xiaoyv.bangumi.shared.data.api.app.createAppApi
import com.xiaoyv.bangumi.shared.data.api.client.converter.HttpCodeConverterFactory
import com.xiaoyv.bangumi.shared.data.api.client.converter.HttpDocumentConverterFactory
import com.xiaoyv.bangumi.shared.data.api.client.cookie.BgmCookiesStorage
import com.xiaoyv.bangumi.shared.data.api.client.cookie.EmptyCookiesStorage
import com.xiaoyv.bangumi.shared.data.api.client.plugin.AuthCompat
import com.xiaoyv.bangumi.shared.data.api.client.plugin.BmoPlugin
import com.xiaoyv.bangumi.shared.data.api.client.plugin.DouBanPlugin
import com.xiaoyv.bangumi.shared.data.api.client.plugin.PixivProxyPlugin
import com.xiaoyv.bangumi.shared.data.api.createAuthApi
import com.xiaoyv.bangumi.shared.data.api.createBgmJsonApi
import com.xiaoyv.bangumi.shared.data.api.createBgmWebApi
import com.xiaoyv.bangumi.shared.data.api.createDouBanApi
import com.xiaoyv.bangumi.shared.data.api.createImageApi
import com.xiaoyv.bangumi.shared.data.api.createPixivApi
import com.xiaoyv.bangumi.shared.data.api.createTraceApi
import com.xiaoyv.bangumi.shared.data.api.magnet.MikanApi
import com.xiaoyv.bangumi.shared.data.api.magnet.createMikanApi
import com.xiaoyv.bangumi.shared.data.api.next.BlogApi
import com.xiaoyv.bangumi.shared.data.api.next.CharacterApi
import com.xiaoyv.bangumi.shared.data.api.next.CollectionApi
import com.xiaoyv.bangumi.shared.data.api.next.EpisodeApi
import com.xiaoyv.bangumi.shared.data.api.next.GroupApi
import com.xiaoyv.bangumi.shared.data.api.next.IndexApi
import com.xiaoyv.bangumi.shared.data.api.next.PersonApi
import com.xiaoyv.bangumi.shared.data.api.next.RelationshipApi
import com.xiaoyv.bangumi.shared.data.api.next.SearchApi
import com.xiaoyv.bangumi.shared.data.api.next.SubjectApi
import com.xiaoyv.bangumi.shared.data.api.next.TimelineApi
import com.xiaoyv.bangumi.shared.data.api.next.TopicApi
import com.xiaoyv.bangumi.shared.data.api.next.UserApi
import com.xiaoyv.bangumi.shared.data.api.next.createBlogApi
import com.xiaoyv.bangumi.shared.data.api.next.createCharacterApi
import com.xiaoyv.bangumi.shared.data.api.next.createCollectionApi
import com.xiaoyv.bangumi.shared.data.api.next.createEpisodeApi
import com.xiaoyv.bangumi.shared.data.api.next.createGroupApi
import com.xiaoyv.bangumi.shared.data.api.next.createIndexApi
import com.xiaoyv.bangumi.shared.data.api.next.createPersonApi
import com.xiaoyv.bangumi.shared.data.api.next.createRelationshipApi
import com.xiaoyv.bangumi.shared.data.api.next.createSearchApi
import com.xiaoyv.bangumi.shared.data.api.next.createSubjectApi
import com.xiaoyv.bangumi.shared.data.api.next.createTimelineApi
import com.xiaoyv.bangumi.shared.data.api.next.createTopicApi
import com.xiaoyv.bangumi.shared.data.api.next.createUserApi
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.data.manager.app.PreferenceStore
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeAuthToken
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ComposePixivToken
import com.xiaoyv.bangumi.shared.data.repository.impl.UserRepositoryImpl.Companion.createBgmToken
import com.xiaoyv.bangumi.shared.systemDevice
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.logging.LogLevel
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
    private val cookieStorage: BgmCookiesStorage,
    private val preferenceStore: PreferenceStore,
) {
    private val config get() = preferenceStore.settings.network

    val baseUrl get() = config.bgmHost

    /**
     * 在 Auth 插件中刷新 Token 用，未安装任何 Auth 避免递归死锁
     */
    val authClient by lazy {
        createHttpClient(
            config = config,
            cookieStorage = EmptyCookiesStorage(),
        )
    }

    val bgmHttpClient by lazy {
        createHttpClient(
            config = config,
            cookieStorage = cookieStorage,
            block = { installBgmAuth(preferenceStore) }
        )
    }

    private val bgmHttpClientNoRedirect by lazy {
        createHttpClient(
            redirect = false,
            config = config,
            cookieStorage = cookieStorage,
            block = { installBgmAuth(preferenceStore) }
        )
    }

    val imageHttpClient by lazy {
        createHttpClient(
            config = config,
            cookieStorage = cookieStorage,
            logLevel = LogLevel.HEADERS,
            enableJsonContentNegotiation = false,
            block = { install(BmoPlugin) }
        )
    }

    val dnsHttpClient by lazy {
        createHttpClient(
            config = config.copy(connectTimeoutMillis = 10_000L, socketTimeoutMillis = 10_000L),
            enableJsonContentNegotiation = false,
        )
    }

    private val authRetrofit = ktorfit {
        httpClient(authClient)
        baseUrl(baseUrl)
        converterFactories(HttpCodeConverterFactory())
    }

    private val webRetrofit = ktorfit {
        httpClient(bgmHttpClient)
        baseUrl(baseUrl)
        converterFactories(HttpDocumentConverterFactory(), HttpCodeConverterFactory())
    }

    private val webRetrofitNoRedirect = ktorfit {
        httpClient(bgmHttpClientNoRedirect)
        baseUrl(baseUrl)
        converterFactories(HttpDocumentConverterFactory(), HttpCodeConverterFactory())
    }

    private val webRetrofitAnonymous = ktorfit {
        httpClient(createHttpClient(config))
        baseUrl(baseUrl)
        converterFactories(HttpDocumentConverterFactory(), HttpCodeConverterFactory())
    }

    private val nextApiRetrofit = ktorfit {
        httpClient(bgmHttpClientNoRedirect)
        baseUrl(WebConstant.URL_BASE_NEXT_API)
        converterFactories(HttpCodeConverterFactory())
    }

    private val publicApiRetrofit = ktorfit {
        httpClient(bgmHttpClientNoRedirect)
        baseUrl(WebConstant.URL_BASE_API)
        converterFactories(HttpCodeConverterFactory())
    }

    private val appApiRetrofit = ktorfit {
        httpClient(createHttpClient(config))
        baseUrl(WebConstant.URL_BASE_APP_API)
        converterFactories(HttpDocumentConverterFactory(), HttpCodeConverterFactory())
    }

    private val dbApiRetrofit = ktorfit {
        httpClient(createHttpClient(config) { installDbAuth() })
        baseUrl(WebConstant.URL_BASE_API_DOUBAN)
        converterFactories(HttpCodeConverterFactory())
    }

    val pixivApiRetrofit = ktorfit {
        httpClient(createHttpClient(config) { installPixivAuth() })
        baseUrl(WebConstant.URL_BASE_API_PIXIV)
        converterFactories(HttpCodeConverterFactory())
    }

    val authApi = authRetrofit.createAuthApi()

    val bgmWebApi = webRetrofit.createBgmWebApi()
    val bgmWebApiNoRedirect = webRetrofitNoRedirect.createBgmWebApi()
    val bgmWebApiNoCookie = webRetrofitAnonymous.createBgmWebApi()
    val bgmJsonApi = publicApiRetrofit.createBgmJsonApi()

    val nextRelationshipApi = nextApiRetrofit.createRelationshipApi()
    val nextUserApi = nextApiRetrofit.createUserApi()
    val nextGroupApi = nextApiRetrofit.createGroupApi()
    val nextCharacterApi = nextApiRetrofit.createCharacterApi()
    val nextSubjectApi = nextApiRetrofit.createSubjectApi()
    val nextPersonApi = nextApiRetrofit.createPersonApi()
    val nextEpisodeApi = nextApiRetrofit.createEpisodeApi()
    val nextCollectionApi = nextApiRetrofit.createCollectionApi()
    val nextTimelineApi = nextApiRetrofit.createTimelineApi()
    val nextSearchApi = nextApiRetrofit.createSearchApi()
    val nextIndexApi = nextApiRetrofit.createIndexApi()
    val nextTopicApi = nextApiRetrofit.createTopicApi()
    val nextBlogApi = nextApiRetrofit.createBlogApi()

    /**
     * 第三方 API
     */
    val mikanApi: MikanApi = appApiRetrofit.createMikanApi()
    val appApi: AppApi = appApiRetrofit.createAppApi()
    val traceApi: TraceApi = appApiRetrofit.createTraceApi()
    val imageApi: ImageApi = appApiRetrofit.createImageApi()
    val pixivApi: PixivApi = pixivApiRetrofit.createPixivApi()
    val dbApi: DouBanApi = dbApiRetrofit.createDouBanApi()

    suspend fun <R> requestAuthApi(block: suspend AuthApi.() -> R) = requestApi(authApi, block = block)

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
    suspend fun <R> requestNextIndexApi(block: suspend IndexApi.() -> R) = requestApi(nextIndexApi, block = block)
    suspend fun <R> requestNextTopicApi(block: suspend TopicApi.() -> R) = requestApi(nextTopicApi, block = block)
    suspend fun <R> requestNextBlogApi(block: suspend BlogApi.() -> R) = requestApi(nextBlogApi, block = block)


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
    ) = runCatching { withContext(context) { (if (disableRedirect) bgmWebApiNoRedirect else bgmWebApi).block() } }
        .onFailure { debugLog { it } }

    suspend fun <API : Any, R> requestApi(
        api: API,
        context: CoroutineContext = Dispatchers.IO,
        block: suspend API.() -> R,
    ) = runCatching { withContext(context) { block(api) } }
        .onFailure { debugLog { it } }


    /**
     * Douban Api 自动授权
     */
    private fun HttpClientConfig<*>.installDbAuth() {
        install(DouBanPlugin) {
            agent = config.douBanUA
            key = config.douBanKey
        }
    }

    /**
     * Pixiv Api 自动授权
     */
    private fun HttpClientConfig<*>.installPixivAuth() {
        install(AuthCompat)

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
            // Pixiv 自动授权
            bearer {
                cacheTokens = false

                sendWithoutRequest { request ->
                    request.url.host.contains("app-api.pixiv.net")
                }

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
                        try {
                            val newToken = authApi.sendPixivAuthTokenRefresh(
                                grantType = "refresh_token",
                                clientId = config.pixivClientId,
                                clientSecret = config.pixivClientSecret,
                                includePolicy = true,
                                refreshToken = refreshToken
                            )
                            preferenceStore.pixivToken = newToken
                            BearerTokens(newToken.accessToken, newToken.refreshToken)
                        } catch (_: Exception) {
                            preferenceStore.pixivToken = ComposePixivToken.Empty
                            null
                        }
                    }
                }
            }
        }
    }

    /**
     * Bgm Public Api 自动授权
     */
    private fun HttpClientConfig<*>.installBgmAuth(preferenceStore: PreferenceStore) {
        install(AuthCompat)

        install(Auth) {
            bearer {
                cacheTokens = false

                sendWithoutRequest { builder ->
                    builder.url.host == "api.bgm.tv" || builder.url.host == "next.bgm.tv"
                }

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
                        try {
                            val newToken = authApi.sendBgmAuthToken(
                                grantType = "refresh_token",
                                refreshToken = refreshToken
                            )
                            preferenceStore.userToken = newToken
                            BearerTokens(newToken.accessToken, newToken.refreshToken)
                        } catch (_: Exception) {
                            // 意外情况，Cookie 还有登录信息，但是 Token 和 RefreshToken 都失效了，重新自动授权
                            if (preferenceStore.userInfo != ComposeUser.Empty) {
                                val newToken = createBgmToken(preferenceStore.userInfo.formHash).getOrThrow()
                                // 保存新的 Token
                                preferenceStore.userToken = newToken

                                BearerTokens(accessToken = newToken.accessToken, refreshToken = newToken.refreshToken)
                            } else {
                                preferenceStore.userToken = ComposeAuthToken.Empty
                                null
                            }
                        }
                    }
                }
            }
        }
    }
}
