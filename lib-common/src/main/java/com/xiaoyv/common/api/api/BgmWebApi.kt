package com.xiaoyv.common.api.api

import com.xiaoyv.common.api.request.CreateTokenParam
import com.xiaoyv.common.config.annotation.BrowserSortType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.annotation.SuperType
import com.xiaoyv.common.config.annotation.TimelineType
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.jsoup.nodes.Document
import retrofit2.http.Body
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

    /**
     * 用户收藏查询
     */
    @GET("/{mediaType}/list/{userId}/{listType}")
    suspend fun queryUserCollect(
        @Path("mediaType", encoded = true) @MediaType mediaType: String,
        @Path("userId", encoded = true) userId: String,
        @Path("listType", encoded = true) listType: String,
        @Query("sort") @BrowserSortType sortType: String? = null,
        @Query("page") page: Int = 1
    ): Document

    /**
     * 创建 Token
     */
    @POST("https://next.bgm.tv/demo/access-tokens")
    suspend fun createToken(@Body param: CreateTokenParam = CreateTokenParam()): String

    /**
     * 查询用户信息
     */
    @GET("/settings")
    suspend fun querySettings(): Document

    /**
     * 修改用户信息
     */
    @POST("/settings")
    suspend fun updateSettings(@Body body: MultipartBody): Document

    /**
     * 日志
     *
     * @param queryPath 日志查询路径
     *
     * - 用户："user/$userId"
     * - 媒体："anime" | "book" | ...
     *
     * @param tagPath tag 拼接路径，注意 `tag` 需要拼接 `/` 开头
     *
     * - 例：tag/xxx
     */
    @GET("/{queryPath}/blog{tag}")
    suspend fun queryBlogList(
        @Path("queryPath", encoded = true) queryPath: String,
        @Path("tag", encoded = true) tagPath: String,
        @Query("page") page: Int = 1
    ): Document

    companion object {

        /**
         * 时间胶囊路径
         */
        fun timelineUrl(userId: String): String {
            return if (userId.isBlank()) "timeline"
            else "user/$userId/timeline"
        }
    }
}