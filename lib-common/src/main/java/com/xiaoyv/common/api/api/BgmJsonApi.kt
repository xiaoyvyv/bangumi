package com.xiaoyv.common.api.api

import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.request.EpCollectParam
import com.xiaoyv.common.api.request.GithubUpdateParam
import com.xiaoyv.common.api.response.AuthStatusEntity
import com.xiaoyv.common.api.response.AuthTokenEntity
import com.xiaoyv.common.api.response.BaiduTranslateEntity
import com.xiaoyv.common.api.response.GithubContent
import com.xiaoyv.common.api.response.GithubLatestEntity
import com.xiaoyv.common.api.response.GithubPutEntity
import com.xiaoyv.common.api.response.SearchApiIndexEntity
import com.xiaoyv.common.api.response.SearchApiTopicEntity
import com.xiaoyv.common.api.response.anime.AnimeBilibiliEntity
import com.xiaoyv.common.api.response.anime.AnimeMagnetEntity
import com.xiaoyv.common.api.response.anime.AnimeMagnetTypeEntity
import com.xiaoyv.common.api.response.anime.AnimeMalSearchEntity
import com.xiaoyv.common.api.response.anime.AnimeSourceEntity
import com.xiaoyv.common.api.response.anime.AnimeTourEntity
import com.xiaoyv.common.api.response.anime.DetectCharacterEntity
import com.xiaoyv.common.api.response.anime.ImageBooruEntity
import com.xiaoyv.common.api.response.anime.ImageGalleryEntity
import com.xiaoyv.common.api.response.api.ApiCalendarEntity
import com.xiaoyv.common.api.response.api.ApiCharacterEntity
import com.xiaoyv.common.api.response.api.ApiEpisodeEntity
import com.xiaoyv.common.api.response.api.ApiUserEntity
import com.xiaoyv.common.api.response.api.ApiUserEpEntity
import com.xiaoyv.common.api.response.base.BaseListResponse
import com.xiaoyv.common.api.response.base.BasePage
import com.xiaoyv.common.api.response.base.BaseResponse
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
import retrofit2.http.Header
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

    @GET("{bgmUrl}")
    suspend fun queryMainPage(
        @Path("bgmUrl", encoded = true) bgmUrl: String = BgmApiManager.URL_BASE_WEB,
    ): Document

    @GET("https://api.github.com/repos/xiaoyvyv/bangumi/releases/latest")
    suspend fun queryGithubLatest(): GithubLatestEntity

    @GET("https://api.github.com/repos/{user}/{repo}/contents/{path}")
    suspend fun queryGithubFileContent(
        @Path("user", encoded = true) user: String,
        @Path("repo", encoded = true) repo: String,
        @Path("path", encoded = true) path: String,
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "application/vnd.github+json",
        @Header("X-GitHub-Api-Version") version: String = "2022-11-28",
    ): GithubContent

    @PUT("https://api.github.com/repos/{user}/{repo}/contents/{path}")
    suspend fun putGithubFileContent(
        @Path("user", encoded = true) user: String,
        @Path("repo", encoded = true) repo: String,
        @Path("path", encoded = true) path: String,
        @Body param: GithubUpdateParam,
        @Header("Authorization") authorization: String,
        @Header("Accept") accept: String = "application/vnd.github+json",
        @Header("X-GitHub-Api-Version") version: String = "2022-11-28",
    ): GithubPutEntity

    @Multipart
    @POST("https://api.trace.moe/search")
    suspend fun queryAnimeByImage(@Part file: MultipartBody.Part): AnimeSourceEntity

    /**
     * MAL 条目搜索
     */
    @GET("https://myanimelist.net/search/prefix.json")
    suspend fun queryMalItems(
        @Query("keyword") keyword: String,
        @Query("type") type: String = "anime",
        @Query("v") v: Int = 1,
    ): AnimeMalSearchEntity

    @GET("https://main.xiaoyv.com.cn/api/bgm/rakuen/search")
    suspend fun querySearchTopic(
        @Query("keyword") keyword: String,
        @Query("exact") exact: Boolean,
        @Query("order") order: String? = null,
        @Query("current") current: Int = 1,
    ): BaseResponse<BasePage<SearchApiTopicEntity>>

    @GET("https://main.xiaoyv.com.cn/api/bgm/index/search")
    suspend fun querySearchIndex(
        @Query("keyword") keyword: String,
        @Query("exact") exact: Boolean,
        @Query("order") order: String? = null,
        @Query("current") current: Int = 1,
    ): BaseResponse<BasePage<SearchApiIndexEntity>>

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


    @GET("{bgmUrl}/timeline")
    suspend fun queryWholeTimeline(
        @Path("bgmUrl", encoded = true) bgmUrl: String = BgmApiManager.URL_BASE_WEB,
        @Query("type") @TimelineType type: String,
        @Query("page") page: Int? = null,
        @Query("ajax") ajax: Long = 1,
    ): Document

    @GET("https://api.anitabi.cn/bangumi/{mediaId}/lite")
    suspend fun queryMediaTour(@Path("mediaId") mediaId: String): AnimeTourEntity

    @GET("https://api.bilibili.com/x/space/bangumi/follow/list")
    suspend fun queryBilibiliUserAnime(
        @Query("vmid") uid: String,
        @Query("type") type: Int = 1,
        @Query("follow_status") followStatus: Int = 0,
        @Query("pn") pageNumber: Int = 1,
        @Query("ps") pageSize: Int = 30,
        @Query("ts") ts: Long = System.currentTimeMillis(),
    ): AnimeBilibiliEntity

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
        @Query("search_tag") searchTags: String? = null,
        @Query("denied_tags") deniedTags: String? = null,
        @Query("lang") lang: String = "zh_CN",
        @Query("ldate") lDate: String = "0",
        @Query("order_by") orderBy: String = "date",
        @Query("page") page: Int = 0,
    ): ImageGalleryEntity

    @GET("https://safebooru.donmai.us/posts.json")
    suspend fun queryAnimePicture(
        @Query("tags") tags: String? = null,
        @Query("page") page: Int = 1,
    ): ImageBooruEntity

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
    @POST("{bgmUrl}/oauth/access_token")
    suspend fun authToken(
        @Path("bgmUrl", encoded = true) bgmUrl: String = BgmApiManager.URL_BASE_WEB,
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
    @POST("{bgmUrl}/oauth/token_status")
    suspend fun authStatus(
        @Path("bgmUrl", encoded = true) bgmUrl: String = BgmApiManager.URL_BASE_WEB,
        @Field("access_token") accessToken: String,
    ): AuthStatusEntity

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

    @GET("/v0/characters/{personId}")
    suspend fun queryCharacter(@Path("personId") personId: String): ApiCharacterEntity

}