package com.xiaoyv.common.api.api

import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.request.EpCollectParam
import com.xiaoyv.common.api.response.AuthStatusEntity
import com.xiaoyv.common.api.response.AuthTokenEntity
import com.xiaoyv.common.api.response.BaiduTranslateEntity
import com.xiaoyv.common.api.response.GithubLatestEntity
import com.xiaoyv.common.api.response.anime.AnimeMagnetEntity
import com.xiaoyv.common.api.response.anime.AnimeMagnetTypeEntity
import com.xiaoyv.common.api.response.anime.AnimeSourceEntity
import com.xiaoyv.common.api.response.anime.AnimeTourEntity
import com.xiaoyv.common.api.response.anime.DetectCharacterEntity
import com.xiaoyv.common.api.response.anime.ImageGalleryEntity
import com.xiaoyv.common.api.response.api.ApiCalendarEntity
import com.xiaoyv.common.api.response.api.ApiEpisodeEntity
import com.xiaoyv.common.api.response.api.ApiUserEntity
import com.xiaoyv.common.api.response.api.ApiUserEpEntity
import com.xiaoyv.common.api.response.base.BaseListResponse
import com.xiaoyv.common.api.response.douban.DouBanPhotoEntity
import com.xiaoyv.common.api.response.douban.DouBanSearchEntity
import com.xiaoyv.common.api.response.douban.DouBanSuggestEntity
import com.xiaoyv.common.config.annotation.EpApiType
import com.xiaoyv.common.config.annotation.TimelineType
import com.xiaoyv.common.kts.randId
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.jsoup.nodes.Document
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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

    @Multipart
    @POST("https://api.trace.moe/search")
    suspend fun queryAnimeByImage(@Part file: MultipartBody.Part): AnimeSourceEntity

    /**
     * 识别动漫图片人物
     *
     * @param model 模型
     * @param forceOne 是否仅识别单个，0：仅识别单个人物；1：识别多个人物
     * @param aiDetect 是否识别 AI-GC
     */
    @Multipart
    @POST("https://aiapiv2.animedb.cn/ai/api/detect")
    suspend fun queryAnimeCharacter(
        @Query("model") model: String,
        @Query("force_one") forceOne: Int,
        @Query("ai_detect") aiDetect: Int,
        @Part file: MultipartBody.Part,
    ): BaseListResponse<DetectCharacterEntity>


    @GET("${BgmApiManager.URL_BASE_WEB}/timeline")
    suspend fun queryWholeTimeline(
        @Query("type") @TimelineType type: String,
        @Query("page") page: Int? = null,
        @Query("ajax") ajax: Long = 1,
    ): Document

    @GET("https://api.anitabi.cn/bangumi/{mediaId}/lite")
    suspend fun queryMediaTour(@Path("mediaId") mediaId: String): AnimeTourEntity

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

    /**
     * Anime-Pictures
     */
    @GET("https://api.anime-pictures.net/api/v3/posts")
    suspend fun queryAnimePicture(
        @Query("denied_tags") deniedTags: String,
        @Query("lang") lang: String = "zh_CN",
        @Query("ldate") lDate: String = "0",
        @Query("order_by") orderBy: String = "date",
        @Query("page") page: Int = 0,
    ): ImageGalleryEntity

    @GET("{magnetApi}/subgroup")
    suspend fun queryAnimeMagnetSubGroup(
        @Path("magnetApi", encoded = true) magnetApi: String,
    ): AnimeMagnetTypeEntity

    @GET("{magnetApi}/type")
    suspend fun queryAnimeMagnetType(
        @Path("magnetApi", encoded = true) magnetApi: String,
    ): AnimeMagnetTypeEntity

    @GET("{magnetApi}/list")
    suspend fun queryAnimeMagnetList(
        @Path("magnetApi", encoded = true) magnetApi: String,
        @Query("keyword") keyword: String,
        @Query("subgroup") subgroup: String? = null,
        @Query("type") type: String? = null,
        @Query("r") r: Long = System.currentTimeMillis(),
    ): AnimeMagnetEntity

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
        @Field("state") state: String = randId(),
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
    suspend fun queryCalendar(): ApiCalendarEntity

    /**
     * 查询媒体的章节，适用于未登录
     */
    @GET("/v0/episodes")
    suspend fun querySubjectEp(
        @Query("subject_id") subjectId: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("episode_type") @EpApiType episodeType: Int? = null,
    ): BaseListResponse<ApiEpisodeEntity>

    /**
     * 查询用户的章节收藏
     */
    @GET("/v0/users/-/collections/{subject_id}/episodes")
    suspend fun queryUserEp(
        @Path("subject_id") subjectId: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("episode_type") @EpApiType episodeType: Int? = null,
    ): BaseListResponse<ApiUserEpEntity>

    /**
     * 更新章节收藏状态
     */
    @PUT("/v0/users/-/collections/-/episodes/{episode_id}")
    suspend fun putEpState(
        @Path("episode_id") episodeId: String,
        @Body param: EpCollectParam,
    ): Response<ResponseBody>

    /**
     * 更新章节收藏状态
     */
    @PATCH("/v0/users/-/collections/{subject_id}/episodes")
    suspend fun putEpStateBatch(
        @Path("subject_id") subjectId: String,
        @Body param: EpCollectParam,
    ): Response<ResponseBody>

    /**
     * 根据用户名查询用户信息
     *
     * @param userId 为用户名，不是数字ID
     */
    @GET("/v0/users/{userId}")
    suspend fun queryUserInfo(@Path("userId") userId: String): ApiUserEntity
}