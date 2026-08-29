package com.xiaoyv.bangumi.shared.data.repository.impl

import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.utils.runResult
import com.xiaoyv.bangumi.shared.data.api.client.ApiClient
import com.xiaoyv.bangumi.shared.data.model.request.bgm.LoginParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeLoginForm
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeLoginResult
import com.xiaoyv.bangumi.shared.data.parser.SignParser
import com.xiaoyv.bangumi.shared.data.repository.SignRepository
import io.ktor.client.statement.bodyAsBytes

/**
 * [SignRepositoryImpl]
 *
 * @author why
 * @since 2025/1/14
 */
class SignRepositoryImpl(
    private val client: ApiClient,
    private val signParser: SignParser,
) : SignRepository {

    override suspend fun fetchOnlineCount(): Result<Int> = runResult {
        with(signParser) {
            client.bgmWebApi
                .fetchMain()
                .fetchMainConverted()
        }
    }

    override suspend fun fetchLoginForm(): Result<ComposeLoginForm> = runResult {
        with(signParser) {
            client.bgmWebApi
                .fetchLoginForm()
                .fetchLoginFormConverted(client.config.bgmHost)
        }
    }

    override suspend fun fetchVerifyCodeImage(): Result<ByteArray> = runResult {
        client.bgmWebApi
            .fetchVerifyCodeImage(mapOf(System.currentTimeMillis().toString() to ""))
            .bodyAsBytes()
    }

    override suspend fun sendLogin(param: LoginParam): Result<ComposeLoginResult> = runResult {
        val forms = param.otherForms.toMutableMap()
        forms["email"] = param.email
        forms["password"] = param.password
        forms["captcha_challenge_field"] = param.code

        with(signParser) {
            client.bgmWebApi
                .sendLogin(referer = client.config.bgmHost, param = forms)
                .sendLoginConverted()
        }
    }
}