package com.xiaoyv.bangumi.shared.data.api

import com.xiaoyv.bangumi.shared.core.types.AppDsl
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeAuthToken
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ComposePixivToken
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.POST
import io.ktor.util.date.getTimeMillis

@AppDsl
interface AuthApi {
    /**
     * 番组计划
     *
     * - 获取 Token 和 Refresh Token
     */
    @FormUrlEncoded
    @POST("oauth/access_token")
    suspend fun sendBgmAuthToken(
        @Field("code") code: String? = null,
        @Field("grant_type") grantType: String,
        @Field("refresh_token") refreshToken: String? = null,
        @Field("redirect_uri") redirectUri: String = WebConstant.APP_CALLBACK,
        @Field("state") state: String = getTimeMillis().toString(),
        @Field("client_id") clientId: String = WebConstant.APP_ID,
        @Field("client_secret") clientSecret: String = WebConstant.APP_SECRET,
    ): ComposeAuthToken

    @FormUrlEncoded
    @POST("https://oauth.secure.pixiv.net/auth/token")
    suspend fun sendPixivAuthToken(
        @Field("client_id") clientId: String?,
        @Field("client_secret") clientSecret: String?,
        @Field("grant_type") grantType: String?,
        @Field("code") code: String?,
        @Field("code_verifier") codeVerifier: String?,
        @Field("redirect_uri") redirectUri: String?,
        @Field("include_policy") includePolicy: Boolean,
    ): ComposePixivToken

    @FormUrlEncoded
    @POST("https://oauth.secure.pixiv.net/auth/token")
    suspend fun sendPixivAuthTokenRefresh(
        @Field("client_id") clientId: String?,
        @Field("client_secret") clientSecret: String?,
        @Field("include_policy") includePolicy: Boolean,
        @Field("grant_type") grantType: String? = "refresh_token",
        @Field("refresh_token") refreshToken: String?,
    ): ComposePixivToken


}