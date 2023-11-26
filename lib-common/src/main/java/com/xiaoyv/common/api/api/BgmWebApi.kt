package com.xiaoyv.common.api.api

import com.xiaoyv.common.config.annotation.BrowserSortType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.annotation.TimelineType
import okhttp3.ResponseBody
import org.jsoup.nodes.Document
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 * Class: [BgmWebApi]
 *
 * @author why
 * @since 11/24/23
 */
interface BgmWebApi {

    @GET("/login")
    suspend fun queryLoginPage(): Document

    /**
     * 验证码
     */
    @GET("/signup/captcha")
    suspend fun queryLoginVerify(@QueryMap map: Map<String, String>): ResponseBody

    /**
     * 登录地址
     *
     * - progress
     */
    @FormUrlEncoded
    @POST("/FollowTheRabbit")
    suspend fun doLogin(@FieldMap query: Map<String, String>): Document

    /**
     * 查询事件时间胶囊
     *
     * - User Path: /user/837364/timeline
     * - Public Path: /timeline
     */
    @GET("/{timeline}")
    suspend fun queryTimeline(
        @Path("timeline") path: String,
        @Query("type") @TimelineType type: String,
        @Query("ajax") ajax: String = "1"
    ): Document


    /**
     * @param orderby 按拼音排序，和 sort 互斥
     */
    @GET("/{mediaType}/browser/{subPath}")
    suspend fun browserRank(
        @Path("mediaType", encoded = true) @MediaType mediaType: String,
        @Path("subPath", encoded = true) subPath: String,
        @Query("sort") @BrowserSortType sortType: String? = null,
        @Query("page") page: Int = 1,
        @Query("orderby") orderby: String? = null,
    ): Document

    companion object {
        fun timelineUrl(userId: String? = null): String {
            return if (userId == null) "timeline"
            else "/user/$userId/timeline"
        }
    }
}