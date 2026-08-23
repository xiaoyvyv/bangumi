@file:OptIn(ExperimentalStdlibApi::class)

package com.xiaoyv.bangumi.shared.data.repository.impl

import androidx.paging.PagingConfig
import com.appmattus.crypto.Algorithm
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.types.pixiv.PixivArtworkSearchType
import com.xiaoyv.bangumi.shared.core.utils.runResult
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.api.client.ApiClient
import com.xiaoyv.bangumi.shared.data.manager.app.PreferenceStore
import com.xiaoyv.bangumi.shared.data.model.request.ChallengeParam
import com.xiaoyv.bangumi.shared.data.model.request.list.pixiv.IllustSearchBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivIllustDetailBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivPageInfo
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivRankingContent
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivTagInfoBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivIllustSimple
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivUserInfoBody
import com.xiaoyv.bangumi.shared.data.repository.PixivRepository
import com.xiaoyv.bangumi.shared.data.repository.datasource.MemoryPagingController
import com.xiaoyv.bangumi.shared.data.repository.datasource.createMemoryPageLimitPagingController
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import org.kotlincrypto.random.CryptoRand
import kotlin.io.encoding.Base64

/**
 * [PixivRepositoryImpl]
 *
 * @since 2025/5/26
 */
class PixivRepositoryImpl(
    private val client: ApiClient,
    private val preferenceStore: PreferenceStore,
    private val pagingConfig: PagingConfig,
) : PixivRepository {
    override val cacheChallengeParam = atomic<ChallengeParam?>(null)

    override suspend fun fetchLoginChallenge(): Result<ChallengeParam> = runResult {
        val encoder = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)
        val codeVerifier = encoder.encode(CryptoRand.nextBytes(ByteArray(32)))
        val codeChallenge = encoder.encode(Algorithm.SHA_256.hash(codeVerifier.encodeToByteArray()))
        ChallengeParam(codeVerifier, codeChallenge).apply {
            cacheChallengeParam.update { this }
        }
    }

    override suspend fun sendAuthToken(code: String, codeVerifier: String) = client.requestAuthApi {
        sendPixivAuthToken(
            code = code,
            codeVerifier = codeVerifier,
            grantType = "authorization_code",
            clientId = preferenceStore.settings.network.pixivClientId,
            clientSecret = preferenceStore.settings.network.pixivClientSecret,
            includePolicy = true,
            redirectUri = "https://app-api.pixiv.net/web/v1/users/auth/pixiv/callback"
        ).let { it.copy(expiresAt = System.currentTimeMillis() + it.expiresIn * 1000) }
    }

    override fun fetchIllustRankingPager(
        content: String,
        mode: String,
        date: String?,
    ): MemoryPagingController<ComposePixivRankingContent, Long> {
        return createMemoryPageLimitPagingController(
            pagingConfig = pagingConfig,
            idSelector = { it.illust_id },
            onLoadData = { page ->
                client.requestPixivAjaxApi {
                    getIllustRanking(
                        mode = mode,
                        content = content,
                        date = date.takeIf { it.orEmpty().isNotBlank() },
                        page = page
                    ).contents
                }.getOrThrow()
            }
        )
    }

    override fun fetchIllustSearchPager(search: IllustSearchBody): MemoryPagingController<ComposePixivRankingContent, Long> {
        return createMemoryPageLimitPagingController(
            pagingConfig = pagingConfig,
            idSelector = { it.illust_id },
            onLoadData = { page ->
                client.requestPixivAjaxApi {
                    val response = when (search.artworkType) {
                        PixivArtworkSearchType.MANGA -> searchManga(
                            keyword = search.keyword,
                            searchMode = search.searchMode,
                            order = search.order,
                            mode = search.rating,
                            aiType = search.aiType,
                            csw = search.csw,
                            ratio = search.ratio,
                            startDate = search.startDate,
                            endDate = search.endDate,
                            workLanguage = search.workLanguage,
                            minWidth = search.minWidth,
                            maxWidth = search.maxWidth,
                            minHeight = search.minHeight,
                            maxHeight = search.maxHeight,
                            language = search.language,
                            page = page,
                        )

                        else -> searchIllustrations(
                            keyword = search.keyword,
                            searchMode = search.searchMode,
                            order = search.order,
                            mode = search.rating,
                            aiType = search.aiType,
                            csw = search.csw,
                            ratio = search.ratio,
                            startDate = search.startDate,
                            endDate = search.endDate,
                            workLanguage = search.workLanguage,
                            minWidth = search.minWidth,
                            maxWidth = search.maxWidth,
                            minHeight = search.minHeight,
                            maxHeight = search.maxHeight,
                            type = search.illustrationType,
                            language = search.language,
                            page = page,
                        )
                    }
                    val artwork = if (search.artworkType == PixivArtworkSearchType.MANGA) {
                        response.body?.manga
                    } else {
                        response.body?.illust
                    }
                    artwork?.data.orEmpty().map { it.toRankingContent() }
                }.getOrThrow()
            },
        )
    }

    override suspend fun fetchIllustDetail(illustId: Long): Result<ComposePixivIllustDetailBody> {
        return client.requestPixivAjaxApi {
            getIllustDetail(illustId).body ?: throw IllegalStateException("Illust detail body is null")
        }
    }

    override suspend fun fetchIllustPages(illustId: Long): Result<SerializeList<ComposePixivPageInfo>> {
        return client.requestPixivAjaxApi {
            getIllustPages(illustId).body?.toPersistentList() ?: persistentListOf()
        }
    }

    override suspend fun fetchUserInfo(uid: Long): Result<ComposePixivUserInfoBody> {
        return client.requestPixivAjaxApi {
            val response = getUserInfo(uid)
            if (response.error) {
                throw IllegalStateException(response.message.ifBlank { "Fetch user info failed" })
            }
            response.body ?: throw IllegalStateException("User info body is null")
        }
    }

    override suspend fun fetchTagInfo(tag: String): Result<ComposePixivTagInfoBody> {
        return client.requestPixivAjaxApi {
            getTagInfo(tag).body ?: throw IllegalStateException("Tag info body is null")
        }
    }

    private fun ComposePixivIllustSimple.toRankingContent(): ComposePixivRankingContent {
        return ComposePixivRankingContent(
            title = title,
            date = createDate,
            tags = tags,
            url = url,
            illust_type = illustType.toString(),
            illust_page_count = pageCount.toString(),
            user_name = userName,
            profile_img = profileImageUrl,
            illust_id = id,
            width = width,
            height = height,
            user_id = userId,
            is_masked = isMasked,
            is_bookmarked = (bookmarkData?.id ?: 0) > 0,
            bookmarkable = isBookmarkable,
            bookmark_id = bookmarkData?.id ?: 0,
        )
    }
}
