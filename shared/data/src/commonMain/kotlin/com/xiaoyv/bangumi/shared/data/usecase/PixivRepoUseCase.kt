package com.xiaoyv.bangumi.shared.data.usecase

import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.data.api.client.cookie.ApiCookiesStorage
import com.xiaoyv.bangumi.shared.data.manager.app.PreferenceStore
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ComposePixivToken
import com.xiaoyv.bangumi.shared.data.repository.PixivRepository

class PixivRepoUseCase(
    private val pixivRepository: PixivRepository,
    private val cookieStorage: ApiCookiesStorage,
    private val preferenceStore: PreferenceStore,
) {
    /**
     * 通过 Pixiv API 校验当前 Cookie 登录凭证是否有效，失效则清理 Token 登录标记
     */
    suspend fun checkAndSyncPixivLoginStatus(): Boolean {
        val currentToken = preferenceStore.pixivTokenData
        // 未包含 Token 或未登录直接返回
        if (currentToken.refreshToken.isBlank() && currentToken.accessToken.isBlank()) {
            return false
        }

        val userId = currentToken.currentUser.id.toLongOrNull()
        if (userId == null || userId <= 0) {
            preferenceStore.clearPixivToken()
            return false
        }

        // 通过请求用户详情 API 校验 Cookie 状态
        return pixivRepository.fetchUserInfo(userId).fold(
            onSuccess = {
                true
            },
            onFailure = { error ->
                debugLog { "Pixiv Cookie 接口校验失败: ${error.message}" }
                // 接口报错 (401/403/Cookie失效)，清理 Token 标记
                preferenceStore.clearPixivToken()
                false
            }
        )
    }

    suspend fun sendAuthToken(code: String): Result<ComposePixivToken> {
        val param = pixivRepository.cacheChallengeParam.value ?: return Result.failure(Exception("未获取到登录参数"))
        val token = pixivRepository.sendAuthToken(code, param.codeVerifier)
        return token
    }
}