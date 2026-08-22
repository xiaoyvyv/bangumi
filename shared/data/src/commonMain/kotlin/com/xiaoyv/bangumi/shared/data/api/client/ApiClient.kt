@file:Suppress("FunctionName", "SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.api.client

import com.xiaoyv.bangumi.shared.core.types.AppDsl
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.data.api.AuthApi
import com.xiaoyv.bangumi.shared.data.api.BgmJsonApi
import com.xiaoyv.bangumi.shared.data.api.BgmWebApi
import com.xiaoyv.bangumi.shared.data.api.DouBanApi
import com.xiaoyv.bangumi.shared.data.api.ImageApi
import com.xiaoyv.bangumi.shared.data.api.TraceApi
import com.xiaoyv.bangumi.shared.data.api.app.AppApi
import com.xiaoyv.bangumi.shared.data.api.app.createAppApi
import com.xiaoyv.bangumi.shared.data.api.client.cookie.BgmCookiesStorage
import com.xiaoyv.bangumi.shared.data.api.client.cookie.EmptyCookiesStorage
import com.xiaoyv.bangumi.shared.data.api.client.plugin.BmoPlugin
import com.xiaoyv.bangumi.shared.data.api.createAuthApi
import com.xiaoyv.bangumi.shared.data.api.createBgmJsonApi
import com.xiaoyv.bangumi.shared.data.api.createBgmWebApi
import com.xiaoyv.bangumi.shared.data.api.createDouBanApi
import com.xiaoyv.bangumi.shared.data.api.createImageApi
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
import com.xiaoyv.bangumi.shared.data.api.pixiv.PixivAjaxApi
import com.xiaoyv.bangumi.shared.data.api.pixiv.PixivApi
import com.xiaoyv.bangumi.shared.data.api.pixiv.createPixivAjaxApi
import com.xiaoyv.bangumi.shared.data.api.pixiv.createPixivApi
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.data.manager.app.PreferenceStore
import com.xiaoyv.bangumi.shared.data.repository.impl.UserRepositoryImpl.Companion.createBgmToken
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.LogLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * [ApiClient]
 *
 * @author why
 * @since 2025/1/14
 */
@AppDsl
class ApiClient(
    private val cookieStorage: BgmCookiesStorage,
    private val preferenceStore: PreferenceStore,
) {
    val config get() = preferenceStore.settings.network

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
        createBgmHttpClient()
    }

    private val bgmHttpClientNoRedirect by lazy {
        createBgmHttpClient(redirect = false)
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

    private val authKtorfit by lazy {
        createApiKtorfit(authClient, config.bgmHost)
    }

    private val webKtorfit by lazy {
        createDocumentKtorfit(bgmHttpClient, config.bgmHost)
    }

    private val webKtorfitNoRedirect by lazy {
        createDocumentKtorfit(bgmHttpClientNoRedirect, config.bgmHost)
    }

    private val nextApiKtorfit by lazy {
        createApiKtorfit(bgmHttpClientNoRedirect, WebConstant.URL_BASE_NEXT_API)
    }

    private val publicApiKtorfit by lazy {
        createApiKtorfit(bgmHttpClientNoRedirect, WebConstant.URL_BASE_API)
    }

    private val appApiKtorfit by lazy {
        createDocumentKtorfit(createHttpClient(config), WebConstant.URL_BASE_APP_API)
    }

    private val dbApiKtorfit by lazy {
        createApiKtorfit(
            client = createHttpClient(config) { installDbAuth(config) },
            url = WebConstant.URL_BASE_API_DOUBAN,
        )
    }
    private val pixivKtorfit by lazy {
        createApiKtorfit(
            client = createHttpClient(config, cookieStorage = cookieStorage) {
                installPixivAuth(
                    config = config,
                    preferenceStore = preferenceStore,
                    refreshToken = { refreshToken ->
                        authApi.sendPixivAuthTokenRefresh(
                            grantType = "refresh_token",
                            clientId = config.pixivClientId,
                            clientSecret = config.pixivClientSecret,
                            includePolicy = true,
                            refreshToken = refreshToken,
                        )
                    },
                )
            },
            url = WebConstant.URL_BASE_PIXIV,
        )
    }

    val authApi by lazy { authKtorfit.createAuthApi() }

    val bgmWebApi by lazy { webKtorfit.createBgmWebApi() }
    val bgmWebApiNoRedirect by lazy { webKtorfitNoRedirect.createBgmWebApi() }
    val bgmJsonApi by lazy { publicApiKtorfit.createBgmJsonApi() }

    val nextRelationshipApi by lazy { nextApiKtorfit.createRelationshipApi() }
    val nextUserApi by lazy { nextApiKtorfit.createUserApi() }
    val nextGroupApi by lazy { nextApiKtorfit.createGroupApi() }
    val nextCharacterApi by lazy { nextApiKtorfit.createCharacterApi() }
    val nextSubjectApi by lazy { nextApiKtorfit.createSubjectApi() }
    val nextPersonApi by lazy { nextApiKtorfit.createPersonApi() }
    val nextEpisodeApi by lazy { nextApiKtorfit.createEpisodeApi() }
    val nextCollectionApi by lazy { nextApiKtorfit.createCollectionApi() }
    val nextTimelineApi by lazy { nextApiKtorfit.createTimelineApi() }
    val nextSearchApi by lazy { nextApiKtorfit.createSearchApi() }
    val nextIndexApi by lazy { nextApiKtorfit.createIndexApi() }
    val nextTopicApi by lazy { nextApiKtorfit.createTopicApi() }
    val nextBlogApi by lazy { nextApiKtorfit.createBlogApi() }

    /**
     * 第三方 API
     */
    val mikanApi: MikanApi by lazy { appApiKtorfit.createMikanApi() }
    val appApi: AppApi by lazy { appApiKtorfit.createAppApi() }
    val traceApi: TraceApi by lazy { appApiKtorfit.createTraceApi() }
    val imageApi: ImageApi by lazy { appApiKtorfit.createImageApi() }
    val pixivApi: PixivApi by lazy { pixivKtorfit.createPixivApi() }
    val pixivAjaxApi: PixivAjaxApi by lazy { pixivKtorfit.createPixivAjaxApi() }
    val dbApi: DouBanApi by lazy { dbApiKtorfit.createDouBanApi() }

    suspend fun <R> requestAuthApi(block: suspend AuthApi.() -> R) = requestApi(authApi, block = block)

    suspend fun <R> requestTraceApi(block: suspend TraceApi.() -> R) = requestApi(traceApi, block = block)
    suspend fun <R> requestImageApi(block: suspend ImageApi.() -> R) = requestApi(imageApi, block = block)
    suspend fun <R> requestDouBanApi(block: suspend DouBanApi.() -> R) = requestApi(dbApi, block = block)
    suspend fun <R> requestPixivApi(block: suspend PixivApi.() -> R) = requestApi(pixivApi, block = block)
    suspend fun <R> requestPixivAjaxApi(block: suspend PixivAjaxApi.() -> R) = requestApi(pixivAjaxApi, block = block)
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
    suspend fun <R> requestJsonApi(
        context: CoroutineContext = Dispatchers.IO,
        block: suspend BgmJsonApi.() -> R,
    ) = requestApi(bgmJsonApi, context, block)


    /**
     * 请求 BgmJsonApi 数据 DSL
     */
    suspend fun <R> requestWebApi(
        context: CoroutineContext = Dispatchers.IO,
        disableRedirect: Boolean = false,
        block: suspend BgmWebApi.() -> R,
    ) = requestApi(
        api = if (disableRedirect) bgmWebApiNoRedirect else bgmWebApi,
        context = context,
        block = block,
    )

    suspend fun <API : Any, R> requestApi(
        api: API,
        context: CoroutineContext = Dispatchers.IO,
        block: suspend API.() -> R,
    ) = runCatching { withContext(context) { block(api) } }
        .onFailure { debugLog { it } }


    private fun createBgmHttpClient(redirect: Boolean = true): HttpClient =
        createHttpClient(
            redirect = redirect,
            config = config,
            cookieStorage = cookieStorage,
            block = {
                installBgmAuth(
                    preferenceStore = preferenceStore,
                    refreshToken = { refreshToken ->
                        authApi.sendBgmAuthToken(
                            grantType = "refresh_token",
                            refreshToken = refreshToken,
                        )
                    },
                    reauthorize = {
                        createBgmToken(preferenceStore.userInfo.formHash).getOrThrow()
                    },
                )
            },
        )
}
