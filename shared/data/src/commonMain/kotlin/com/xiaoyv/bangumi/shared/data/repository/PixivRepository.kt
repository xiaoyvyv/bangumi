package com.xiaoyv.bangumi.shared.data.repository

import com.xiaoyv.bangumi.shared.data.model.request.ChallengeParam
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ComposePixivToken
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivRankingContent
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

    fun fetchIllustRankingPager(content: String = "all", mode: String = "daily"): MemoryPagingController<ComposePixivRankingContent, Long>
}