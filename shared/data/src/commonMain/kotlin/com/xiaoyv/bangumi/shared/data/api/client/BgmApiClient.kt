@file:Suppress("FunctionName", "SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.api.client

import com.fleeksoft.ksoup.Ksoup
import com.xiaoyv.bangumi.shared.core.types.AppDsl
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.requireNoError
import com.xiaoyv.bangumi.shared.core.utils.runResult
import com.xiaoyv.bangumi.shared.core.utils.uppercaseFirstChar
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
import com.xiaoyv.bangumi.shared.data.api.client.cookie.BgmCookieStorage
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
import com.xiaoyv.bangumi.shared.data.api.next.IndexApi
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
import com.xiaoyv.bangumi.shared.data.api.next.createIndexApi
import com.xiaoyv.bangumi.shared.data.api.next.createPersonApi
import com.xiaoyv.bangumi.shared.data.api.next.createRelationshipApi
import com.xiaoyv.bangumi.shared.data.api.next.createSearchApi
import com.xiaoyv.bangumi.shared.data.api.next.createSubjectApi
import com.xiaoyv.bangumi.shared.data.api.next.createTimelineApi
import com.xiaoyv.bangumi.shared.data.api.next.createUserApi
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.data.manager.app.PreferenceStore
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeAuthToken
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUser
import com.xiaoyv.bangumi.shared.systemDevice
import de.jensklingenberg.ktorfit.ktorfit
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.statement.bodyAsText
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
    private val config get() = preferenceStore.settings.network

    val baseUrl get() = config.bgmHost

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

    private val pixivApiRetrofit = ktorfit {
        httpClient(createHttpClient(config) { installPixivAuth() })
        baseUrl(WebConstant.URL_BASE_API_PIXIV)
        converterFactories(HttpCodeConverterFactory())
    }

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

    /**
     * 第三方 API
     */
    val mikanApi: MikanApi = appApiRetrofit.createMikanApi()
    val appApi: AppApi = appApiRetrofit.createAppApi()
    val traceApi: TraceApi = appApiRetrofit.createTraceApi()
    val imageApi: ImageApi = appApiRetrofit.createImageApi()
    val pixivApi: PixivApi = pixivApiRetrofit.createPixivApi()
    val dbApi: DouBanApi = dbApiRetrofit.createDouBanApi()

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
    ) = runCatching { withContext(context) { api.block() } }
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
            }
        }
    }

    /**
     * Bgm Public Api 自动授权
     */
    private fun HttpClientConfig<*>.installBgmAuth(preferenceStore: PreferenceStore) {
        install(Auth) {
            bearer {
                sendWithoutRequest { builder ->
                    builder.url.host.equals("api.bgm.tv", true)
                }

                loadTokens {
                    val token = preferenceStore.userToken
                    if (token.accessToken.isBlank() || token.refreshToken.isBlank()) null else {
                        BearerTokens(accessToken = token.accessToken, refreshToken = token.refreshToken)
                    }
                }

                refreshTokens {
                    val refreshToken = oldTokens?.refreshToken.orEmpty()
                    var token = ComposeAuthToken.Empty
                    if (refreshToken.isNotBlank()) {
                        val authToken = runResult {
                            bgmWebApi.sendAuthJsonApiToken(refreshToken = refreshToken, grantType = "refresh_token")
                        }
                        if (authToken.isSuccess) token = authToken.getOrThrow()
                    }

                    if (token == ComposeAuthToken.Empty) {
                        token = createBgmToken(preferenceStore.userInfo.formHash).getOrThrow()
                    }

                    if (token == ComposeAuthToken.Empty) {
                        preferenceStore.userInfo = ComposeUser.Empty
                        null
                    } else {
                        preferenceStore.userToken = token
                        BearerTokens(token.accessToken, token.refreshToken)
                    }
                }
            }
        }
    }

    suspend fun createBgmToken(formHash: String) = runResult {
        val response = bgmWebApiNoRedirect.sendAuthJsonApi(formhash = formHash)
        if (response.status.value == 200) {
            Ksoup.parse(response.bodyAsText()).requireNoError()
        }

        val location = response.headers["Location"].orEmpty()
        val code = location
            .substringAfter("code=")
            .substringBefore("=")

        require(code.isNotBlank()) { "授权失败" }

        // 返回授权结果
        val tokenEntity = bgmWebApiNoRedirect.sendAuthJsonApiToken(
            code = code,
            grantType = "authorization_code"
        )

        require(tokenEntity.accessToken.isNotBlank())
        require(tokenEntity.refreshToken.isNotBlank())

        debugLog { "AuthToken ：${tokenEntity}" }

        tokenEntity
    }
}

