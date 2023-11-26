package com.xiaoyv.common.api.api

import com.xiaoyv.common.config.annotation.BrowserSortType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.annotation.SuperType
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
     *
     * @param ajax 仅返回嵌套的 html
     */
    @GET("/{timeline}")
    suspend fun queryTimeline(
        @Path("timeline", encoded = true) path: String,
        @Query("type") @TimelineType type: String,
        @Query("ajax") ajax: Long = 1
    ): Document

    /**
     * 超展开
     */
    @GET("/rakuen/topiclist")
    suspend fun querySuperTopic(
        @Query("type") @SuperType type: String,
        @Query("filter") filter: String? = null
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

        /**
         * 时间胶囊路径
         */
        fun timelineUrl(userId: Long): String {
            return if (userId <= 0) "timeline"
            else "user/$userId/timeline"
        }
    }
}