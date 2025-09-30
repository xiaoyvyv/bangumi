package com.xiaoyv.bangumi.shared.data.usecase

import com.xiaoyv.bangumi.shared.data.api.client.BgmCookieStorage
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ComposePixivToken
import com.xiaoyv.bangumi.shared.data.repository.PixivRepository

class PixivRepoUseCase(
    private val pixivRepository: PixivRepository,
    private val cookieStorage: BgmCookieStorage,
) {

    suspend fun sendAuthToken(code: String): Result<ComposePixivToken> {
        val param = pixivRepository.cacheChallengeParam.value
        if (param == null) return Result.failure(Exception("未获取到登录参数"))
        val token = pixivRepository.sendAuthToken(code, param.codeVerifier)
        return token
    }
}