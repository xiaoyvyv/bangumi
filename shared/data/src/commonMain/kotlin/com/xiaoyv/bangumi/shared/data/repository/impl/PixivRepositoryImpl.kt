@file:OptIn(ExperimentalStdlibApi::class)

package com.xiaoyv.bangumi.shared.data.repository.impl

import com.appmattus.crypto.Algorithm
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.utils.runResult
import com.xiaoyv.bangumi.shared.data.api.client.BgmApiClient
import com.xiaoyv.bangumi.shared.data.manager.app.PreferenceStore
import com.xiaoyv.bangumi.shared.data.model.request.ChallengeParam
import com.xiaoyv.bangumi.shared.data.repository.PixivRepository
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import org.kotlincrypto.random.CryptoRand
import kotlin.io.encoding.Base64

/**
 * [PixivRepositoryImpl]
 *
 * @since 2025/5/26
 */
class PixivRepositoryImpl(
    private val client: BgmApiClient,
    private val preferenceStore: PreferenceStore,
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

    override suspend fun sendAuthToken(code: String, codeVerifier: String) = client.requestPixivApi {
        sendAuthToken(
            code = code,
            codeVerifier = codeVerifier,
            grantType = "authorization_code",
            clientId = preferenceStore.settings.network.pixivClientId,
            clientSecret = preferenceStore.settings.network.pixivClientSecret,
            includePolicy = true,
            redirectUri = "https://app-api.pixiv.net/web/v1/users/auth/pixiv/callback"
        ).let { it.copy(expiresAt = System.currentTimeMillis() + it.expiresIn * 1000) }
    }
}