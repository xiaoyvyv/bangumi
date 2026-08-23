package com.xiaoyv.bangumi.shared.data.repository

import com.xiaoyv.bangumi.shared.core.types.pixiv.PixivRankingContentType
import com.xiaoyv.bangumi.shared.core.types.pixiv.PixivRankingMode
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.request.ChallengeParam
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ComposePixivToken
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivIllustDetailBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivPageInfo
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivRankingContent
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivUserInfoBody
import com.xiaoyv.bangumi.shared.data.repository.datasource.MemoryPagingController
import kotlinx.atomicfu.AtomicRef

/**
 * [PixivRepository]
 *
 * @since 2025/5/26
 */
interface PixivRepository {
    val cacheChallengeParam: AtomicRef<ChallengeParam?>

    suspend fun fetchLoginChallenge(): Result<ChallengeParam>

    suspend fun sendAuthToken(code: String, codeVerifier: String): Result<ComposePixivToken>

    fun fetchIllustRankingPager(
        @PixivRankingContentType content: String = PixivRankingContentType.ALL,
        @PixivRankingMode mode: String = PixivRankingMode.DAILY,
        date: String? = null,
    ): MemoryPagingController<ComposePixivRankingContent, Long>

    suspend fun fetchIllustDetail(illustId: Long): Result<ComposePixivIllustDetailBody>

    suspend fun fetchIllustPages(illustId: Long): Result<SerializeList<ComposePixivPageInfo>>

    suspend fun fetchUserInfo(uid: Long): Result<ComposePixivUserInfoBody>
}