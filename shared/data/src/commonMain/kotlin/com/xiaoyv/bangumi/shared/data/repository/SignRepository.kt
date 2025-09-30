package com.xiaoyv.bangumi.shared.data.repository

import com.xiaoyv.bangumi.shared.data.model.request.LoginParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeLoginForm
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeLoginResult

/**
 * [SignRepository]
 *
 * @author why
 * @since 2025/1/14
 */
interface SignRepository {

    suspend fun fetchLoginForm(): Result<ComposeLoginForm>

    suspend fun fetchVerifyCodeImage(): Result<ByteArray>

    suspend fun sendLogin(param: LoginParam): Result<ComposeLoginResult>
}