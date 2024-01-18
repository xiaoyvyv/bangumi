package com.xiaoyv.common.helper

import com.xiaoyv.blueprint.kts.launchProcess
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.requireNoError
import com.xiaoyv.common.api.response.AuthTokenEntity
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.jsoup.Jsoup

/**
 * Class: [UserTokenHelper]
 *
 * @author why
 * @since 12/24/23
 */
object UserTokenHelper {
    private val empty = AuthTokenEntity()

    /**
     * 保存或获取 Token
     */
    var authToken: AuthTokenEntity
        get() = UserHelper.userSp.getString(UserHelper.KEY_USER_TOKEN).fromJson() ?: empty
        set(value) = UserHelper.userSp.put(
            UserHelper.KEY_USER_TOKEN,
            value.apply { saveAt = System.currentTimeMillis() }.toJson()
        )

    /**
     * 初始化
     */
    fun init() {
        launchProcess(Dispatchers.IO, error = { it.printStackTrace() }) {
            require(UserHelper.isLogin) { "获取授权Token需要登录，当前未登录跳过" }

            // 刷新 Token
            if (authToken.isExpire) {
                debugLog { "Token 授权过期或不存在" }

                retryToken()
            } else {
                debugLog { "Token 初步校验授权未过期！" }

                val result =
                    runCatching { BgmApiManager.bgmJsonApi.queryUserInfo(UserHelper.currentUser.id) }
                if (result.isFailure) {
                    debugLog { "Token 最终校验授权过期，重新拉取 Token" }
                    retryToken()
                } else {
                    debugLog { "Token 最终校验授权未过期！" }
                }
            }
        }
    }

    private suspend fun retryToken() {
        // 判断是否可以刷新 Token
        val refreshToken = authToken.refreshToken.orEmpty()
        if (refreshToken.isNotBlank()) {
            debugLog { "Token refresh start..." }

            val refreshSuccess = refreshAuthToken(refreshToken)
            if (refreshSuccess) {
                debugLog { "Token refresh success" }
                return
            }

            debugLog { "Token refresh fail!" }
        }

        debugLog { "Token re-fetch!" }

        // 重新拉取
        authToken = empty
        fetchAuthToken()
    }

    /**
     * 获取 Auth 授权
     */
    suspend fun fetchAuthToken() {
        return withContext(Dispatchers.IO) {
            // 获取授权 Code
            val response = BgmApiManager.bgmWebNoRedirectApi
                .authJsonApi(formhash = UserHelper.formHash)

            if (response.code() == 200) {
                Jsoup.parse(response.body()?.string().orEmpty()).requireNoError()
            }

            val location = response.headers()["Location"]?.toHttpUrlOrNull()
            val code = location?.queryParameter("code").orEmpty()

            require(code.isNotBlank()) { "授权失败" }

            // 返回授权结果
            val tokenEntity = BgmApiManager.bgmJsonApi.authToken(
                code = code,
                grantType = "authorization_code"
            )

            require(tokenEntity.accessToken.orEmpty().isNotBlank())
            require(tokenEntity.refreshToken.orEmpty().isNotBlank())

            // 保存 Token
            authToken = tokenEntity

            debugLog { "授权结果：${authToken.toJson()}" }
        }
    }

    /**
     * 刷新 Auth 授权
     *
     * @return 返回是否刷新成功
     */
    private suspend fun refreshAuthToken(refreshToken: String): Boolean {
        return withContext(Dispatchers.IO) {
            runCatching {
                require(refreshToken.isNotBlank())

                // 刷新授权结果
                val tokenEntity = BgmApiManager.bgmJsonApi.authToken(
                    refreshToken = refreshToken,
                    grantType = "refresh_token"
                )
                require(tokenEntity.accessToken.orEmpty().isNotBlank())
                require(tokenEntity.refreshToken.orEmpty().isNotBlank())

                // 保存
                authToken = tokenEntity

                return@withContext true
            }
            return@withContext false
        }
    }
}