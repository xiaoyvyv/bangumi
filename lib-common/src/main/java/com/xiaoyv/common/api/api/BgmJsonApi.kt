package com.xiaoyv.common.api.api

import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.response.AuthStatusEntity
import com.xiaoyv.common.api.response.AuthTokenEntity
import com.xiaoyv.common.api.response.BaiduTranslateEntity
import com.xiaoyv.common.api.response.CalendarEntity
import com.xiaoyv.common.api.response.GithubLatestEntity
import com.xiaoyv.common.api.response.MediaJsonEntity
import com.xiaoyv.common.api.response.anime.ImageGalleryEntity
import com.xiaoyv.common.api.response.douban.DouBanPhotoEntity
import com.xiaoyv.common.api.response.douban.DouBanSearchEntity
import com.xiaoyv.common.api.response.douban.DouBanSuggestEntity
import com.xiaoyv.common.config.annotation.TimelineType
import org.jsoup.nodes.Document
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Class: [BgmJsonApi]
 *
 * @author why
 * @since 11/24/23
 */
interface BgmJsonApi {

    @GET(BgmApiManager.URL_BASE_WEB)
    suspend fun queryMainPage(): Document


    @GET("https://api.github.com/repos/xiaoyvyv/Bangumi-for-Android/releases/latest")
    suspend fun queryGithubLatest(): GithubLatestEntity

    @GET("${BgmApiManager.URL_BASE_WEB}/timeline")
    suspend fun queryWholeTimeline(
        @Query("type") @TimelineType type: String,
        @Query("page") page: Int? = null,
        @Query("ajax") ajax: Long = 1,
    ): Document


    @GET("/v0/subjects/{mediaId}")
    suspend fun queryMediaDetail(@Path("mediaId", encoded = true) mediaId: String): MediaJsonEntity

    @FormUrlEncoded
    @POST("http://api.fanyi.baidu.com/api/trans/vip/translate")
    suspend fun postBaiduTranslate(
        @Field("q") q: String,
        @Field("appid") appId: String,
        @Field("salt") salt: String,
        @Field("secret") secret: String,
        @Field("sign") sign: String,
        @Field("from") from: String = "auto",
        @Field("to") to: String = "zh",
    ): BaiduTranslateEntity

    @GET("https://frodo.douban.com/api/v2/search/subjects")
    suspend fun queryDouBanSearchHint(
        @Query("q") q: String,
        @Query("count") count: Int = 10,
        @Query("apikey") apikey: String = "0dad551ec0f84ed02907ff5c42e8ec70",
    ): DouBanSearchEntity

    @GET("https://frodo.douban.com/api/v2/search/suggestion")
    suspend fun queryDouBanSuggestion(
        @Query("q") q: String,
        @Query("apikey") apikey: String = "0dad551ec0f84ed02907ff5c42e8ec70",
    ): DouBanSuggestEntity

    @GET("https://frodo.douban.com/api/v2/tv/{mediaId}/photos")
    suspend fun queryDouBanPhotoList(
        @Path("mediaId") mediaId: String,
        @Query("start") start: Int = 0,
        @Query("count") count: Int = 10,
        @Query("apikey") apikey: String = "0dad551ec0f84ed02907ff5c42e8ec70",
    ): DouBanPhotoEntity

    @GET("https://api.anime-pictures.net/api/v3/posts")
    suspend fun queryAnimePicture(
        @Query("lang") lang: String = "zh_CN",
        @Query("ldate") lDate: String = "0",
        @Query("order_by") orderBy: String = "date",
        @Query("page") page: Int = 0,
    ): ImageGalleryEntity

    /**
     * 获取 Token
     */
    @FormUrlEncoded
    @POST("${BgmApiManager.URL_BASE_WEB}/oauth/access_token")
    suspend fun authToken(
        @Field("code") code: String? = null,
        @Field("grant_type") grantType: String,
        @Field("refresh_token") refreshToken: String? = null,
        @Field("redirect_uri") redirectUri: String = BgmApiManager.APP_CALLBACK,
        @Field("state") state: String = System.currentTimeMillis().toString(),
        @Field("client_id") clientId: String = BgmApiManager.APP_ID,
        @Field("client_secret") clientSecret: String = BgmApiManager.APP_SECRET,
    ): AuthTokenEntity

    /**
     * Token 状态
     */
    @FormUrlEncoded
    @POST("${BgmApiManager.URL_BASE_WEB}/oauth/token_status")
    suspend fun authStatus(@Field("access_token") accessToken: String): AuthStatusEntity

    /**
     * 每日放送
     */
    @GET("/calendar")
    suspend fun queryCalendar(): CalendarEntity
}