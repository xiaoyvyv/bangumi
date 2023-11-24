package com.xiaoyv.common.api.api

import com.xiaoyv.common.api.BgmApiManager
import okhttp3.ResponseBody
import org.jsoup.nodes.Document
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

/**
 * Class: [BgmWebApi]
 *
 * @author why
 * @since 11/24/23
 */
interface BgmWebApi {

    @GET("${BgmApiManager.URL_BASE_WEB}/login")
    suspend fun queryLoginPage(): Document

    /**
     * 验证码
     */
    @GET("${BgmApiManager.URL_BASE_WEB}/signup/captcha")
    suspend fun queryLoginVerify(@QueryMap map: Map<String, String>): ResponseBody

    /**
     * 登录地址
     *
     * - formhash	"14a86209"
     * - referer	"https://bangumi.tv/"
     * - dreferer	"https://bangumi.tv/"
     * - email	"xxx@qq.com"
     * - password	"xxx"
     * - captcha_challenge_field	"dasdasd"
     * - cookietime	"0"
     * - loginsubmit	"登录"
     */
    @FormUrlEncoded
    @POST("${BgmApiManager.URL_BASE_WEB}/FollowTheRabbit")
    suspend fun doLogin(@FieldMap query: Map<String, String>): Document
}