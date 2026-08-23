@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.api.pixiv

import com.xiaoyv.bangumi.shared.core.types.AppDsl
import com.xiaoyv.bangumi.shared.core.types.pixiv.PixivRankingContentType
import com.xiaoyv.bangumi.shared.core.types.pixiv.PixivRankingMode
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.request.pixiv.PixivAddTagRequest
import com.xiaoyv.bangumi.shared.data.model.request.pixiv.PixivBookmarkRequest
import com.xiaoyv.bangumi.shared.data.model.request.pixiv.PixivDeleteIllustsBookmarkRequest
import com.xiaoyv.bangumi.shared.data.model.request.pixiv.PixivNovelBookmarkRequest
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivAddTagBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivBookmarkAddResponse
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivBookmarkData
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivBookmarkTagsResponse
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivCommentsBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivDiscoveryBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivDiscoveryUsersBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivEmptyArrayResponse
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivFollowLatestBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivIllustDetailBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivIllustRecommendBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivIllustRecommendInitBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivIllustSearchBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivIllustSeriesBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivLikeBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivMyPixivBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivNovelBookmarkStatusBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivNovelDetailBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivNovelRankingBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivNovelRecommendBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivNovelRecommendInitBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivNovelSearchBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivNovelSeriesBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivNovelSeriesContentBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivNovelSeriesTitle
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivPageInfo
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivPostCommentBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivProfileAllBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivProfileIllustsBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivProfileNovelsBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivRankingResponse
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivResponse
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivSearchSuggestionBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivTagInfoBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivTagSuggestBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivUgoiraMetaBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivUserBookmarkIllustsBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivUserBookmarkNovelsBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivUserFollowDetailBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivUserFollowingBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivUserIllustTag
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivUserIllustsByTagBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivUserInfoBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivUserNovelsByTagBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivUserRecommendBody
import com.xiaoyv.bangumi.shared.data.model.response.pixiv.ajax.ComposePixivUserSearchBody
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

/**
 * [PixivAjaxApi]
 *
 * Pixiv WebAjax Ktrofit API 定义接口
 * 整合了插画、小说、用户、收藏、搜索、系列、评论、标签及排行榜等全部 WebAjax 端点
 */
@AppDsl
interface PixivAjaxApi {

    // ==================== 插画 / 漫画 API ====================

    /**
     * 查询插画/漫画详情
     *
     * @param pid 作品ID
     */
    @GET("ajax/illust/{pid}")
    suspend fun getIllustDetail(@Path("pid") pid: Long): ComposePixivResponse<ComposePixivIllustDetailBody>

    /**
     * 查询插画/漫画收藏状态
     *
     * @param pid 作品ID
     */
    @GET("ajax/illust/{pid}/bookmarkData")
    suspend fun getIllustBookmarkData(@Path("pid") pid: Long): ComposePixivResponse<ComposePixivBookmarkData>

    /**
     * 查询多页插画的所有页面详情
     * 用于获取多页作品（漫画）的每一页原图 URL
     *
     * @param pid 作品ID
     * @return 页面列表，每个元素包含该页的各种尺寸图片 URL
     */
    @GET("ajax/illust/{pid}/pages")
    suspend fun getIllustPages(@Path("pid") pid: Long): ComposePixivResponse<List<ComposePixivPageInfo>>

    /**
     * 查询 Ugoira 动图元数据
     *
     * @param pid 动图作品ID
     */
    @GET("ajax/illust/{pid}/ugoira_meta")
    suspend fun getUgoiraMeta(@Path("pid") pid: Long): ComposePixivResponse<ComposePixivUgoiraMetaBody>

    /**
     * 发现插画
     *
     * @param mode 模式：all, safe, r18
     * @param limit 返回数量（默认 100）
     * @param sampleIllustId 参考作品ID（可选）
     */
    @GET("ajax/discovery/artworks")
    suspend fun getIllustDiscovery(
        @Query("mode") mode: String = "all",
        @Query("limit") limit: Int = 100,
        @Query("sampleIllustId") sampleIllustId: Long? = null
    ): ComposePixivResponse<ComposePixivDiscoveryBody>

    /**
     * 查询推荐插画作品（初始化）
     *
     * @param pid 基准作品ID
     * @param limit 返回数量（默认 18）
     */
    @GET("ajax/illust/{pid}/recommend/init")
    suspend fun getIllustRecommendInit(
        @Path("pid") pid: Long,
        @Query("limit") limit: Int = 18
    ): ComposePixivResponse<ComposePixivIllustRecommendInitBody>

    /**
     * 查询推荐插画作品
     *
     * @param illustIds 基准作品ID列表
     */
    @GET("ajax/illust/recommend/illusts")
    suspend fun getRecommendIllusts(
        @Query("illust_ids[]") illustIds: List<Long>
    ): ComposePixivResponse<ComposePixivIllustRecommendBody>

    /**
     * 点赞插画/漫画
     *
     * @param body 包含 "illust_id" 的映射对象
     */
    @POST("ajax/illusts/like")
    suspend fun postIllustLike(@Body body: Map<String, Long>): ComposePixivResponse<ComposePixivLikeBody>

    // ==================== 收藏 API ====================

    /**
     * 查询用户收藏的插画·漫画
     *
     * @param uid 用户ID
     * @param tag 标签过滤（空字符串表示不过滤）
     * @param offset 偏移量
     * @param limit 返回数量（最大 100）
     * @param rest 公开状态：show(公开), hide(私密)
     */
    @GET("ajax/user/{uid}/illusts/bookmarks")
    suspend fun getUserBookmarkIllusts(
        @Path("uid") uid: Long,
        @Query("tag") tag: String = "",
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 48,
        @Query("rest") rest: String = "show"
    ): ComposePixivResponse<ComposePixivUserBookmarkIllustsBody>

    /**
     * 查询用户收藏的小说
     *
     * @param uid 用户ID
     * @param tag 标签过滤（空字符串表示不过滤）
     * @param offset 偏移量
     * @param limit 返回数量（最大 100）
     * @param rest 公开状态：show(公开), hide(私密)
     */
    @GET("ajax/user/{uid}/novels/bookmarks")
    suspend fun getUserBookmarkNovels(
        @Path("uid") uid: Long,
        @Query("tag") tag: String = "",
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 30,
        @Query("rest") rest: String = "show"
    ): ComposePixivResponse<ComposePixivUserBookmarkNovelsBody>

    /**
     * 收藏插画/漫画
     *
     * @param request 收藏请求参数对象
     */
    @POST("ajax/illusts/bookmarks/add")
    suspend fun addIllustBookmark(@Body request: PixivBookmarkRequest): ComposePixivResponse<ComposePixivBookmarkAddResponse>

    /**
     * 删除插画/漫画收藏
     *
     * @param bookmarkId 收藏ID（从作品的 bookmarkData 字段获取）
     */
    @FormUrlEncoded
    @POST("ajax/illusts/bookmarks/delete")
    suspend fun deleteIllustBookmark(@Field("bookmark_id") bookmarkId: Long): ComposePixivEmptyArrayResponse

    /**
     * 批量删除插画/漫画收藏
     *
     * @param request 批量删除请求参数对象
     */
    @POST("ajax/illusts/bookmarks/remove")
    suspend fun deleteIllustsBookmarks(@Body request: PixivDeleteIllustsBookmarkRequest): ComposePixivEmptyArrayResponse

    /**
     * 获取用户的插画收藏标签列表
     *
     * @param userId 用户ID
     */
    @GET("ajax/user/{userId}/illusts/bookmark/tags")
    suspend fun getIllustBookmarkTags(@Path("userId") userId: Long): ComposePixivResponse<ComposePixivBookmarkTagsResponse>

    /**
     * 收藏小说
     *
     * @param request 小说收藏请求对象
     */
    @POST("ajax/novels/bookmarks/add")
    suspend fun addNovelBookmark(@Body request: PixivNovelBookmarkRequest): ComposePixivResponse<String>

    /**
     * 删除小说收藏
     *
     * @param bookId 收藏ID
     * @param del 删除标记（默认 "1"）
     */
    @FormUrlEncoded
    @POST("ajax/novels/bookmarks/delete")
    suspend fun deleteNovelBookmark(
        @Field("book_id") bookId: Long,
        @Field("del") del: String = "1"
    ): ComposePixivEmptyArrayResponse

    /**
     * 批量删除小说收藏
     *
     * @param request 批量删除对象
     */
    @POST("ajax/novels/bookmarks/remove")
    suspend fun deleteNovelsBookmarks(@Body request: PixivDeleteIllustsBookmarkRequest): ComposePixivEmptyArrayResponse

    /**
     * 获取用户的小说收藏标签列表
     *
     * @param userId 用户ID
     */
    @GET("ajax/user/{userId}/novels/bookmark/tags")
    suspend fun getNovelBookmarkTags(@Path("userId") userId: Long): ComposePixivResponse<ComposePixivBookmarkTagsResponse>

    // ==================== 用户 API ====================

    /**
     * 查询用户信息
     *
     * @param uid 用户ID
     * @param full 是否获取完整信息（1=是，0=否）
     */
    @GET("ajax/user/{uid}")
    suspend fun getUserInfo(
        @Path("uid") uid: Long,
        @Query("full") full: Int = 1
    ): ComposePixivResponse<ComposePixivUserInfoBody>

    /**
     * 查询用户作品概况（包括插画、漫画、小说和精选集）
     *
     * @param uid 用户ID
     */
    @GET("ajax/user/{uid}/profile/all")
    suspend fun getUserProfileAll(@Path("uid") uid: Long): ComposePixivResponse<ComposePixivProfileAllBody>

    /**
     * 查询用户的插画作品列表
     *
     * @param uid 用户ID
     * @param ids 作品ID列表
     * @param workCategory 作品类型：illust(插画), manga(漫画), illustManga(混合)
     * @param isFirstPage 是否为第一页（1=是，0=否）
     */
    @GET("ajax/user/{uid}/profile/illusts")
    suspend fun getUserProfileIllusts(
        @Path("uid") uid: Long,
        @Query("ids[]") ids: List<Long>,
        @Query("work_category") workCategory: String = "illustManga",
        @Query("is_first_page") isFirstPage: Int = 0
    ): ComposePixivResponse<ComposePixivProfileIllustsBody>

    /**
     * 查询用户的小说作品列表
     *
     * @param uid 用户ID
     * @param ids 小说ID列表（从 getUserProfileAll 获取）
     */
    @GET("ajax/user/{uid}/profile/novels")
    suspend fun getUserProfileNovels(
        @Path("uid") uid: Long,
        @Query("ids[]") ids: List<Long>
    ): ComposePixivResponse<ComposePixivProfileNovelsBody>

    /**
     * 获取用户关注列表
     *
     * @param uid 用户ID
     * @param offset 偏移量
     * @param limit 返回数量（最大 100）
     * @param rest 公开状态：show(公开), hide(私密)
     * @param tag 标签过滤（空字符串表示不过滤）
     * @param acceptingRequests 是否只显示正在接稿的用户（0=否，1=是）
     */
    @GET("ajax/user/{uid}/following")
    suspend fun getUserFollowing(
        @Path("uid") uid: Long,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 24,
        @Query("rest") rest: String = "show",
        @Query("tag") tag: String = "",
        @Query("acceptingRequests") acceptingRequests: Int = 0
    ): ComposePixivResponse<ComposePixivUserFollowingBody>

    /**
     * 获取用户粉丝列表
     *
     * @param uid 用户ID
     * @param offset 偏移量
     * @param limit 返回数量（最大 100）
     */
    @GET("ajax/user/{uid}/followers")
    suspend fun getUserFollowers(
        @Path("uid") uid: Long,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 24
    ): ComposePixivResponse<ComposePixivUserFollowingBody>

    /**
     * 推荐用户（针对特定用户）
     * 根据指定用户推荐相似用户
     *
     * @param uid 用户ID
     * @param userNum 推荐用户数量
     * @param workNum 每个用户附带的作品数量
     * @param isR18 是否包含 R18
     */
    @GET("ajax/user/{uid}/recommends")
    suspend fun getRecommendUsers(
        @Path("uid") uid: Long,
        @Query("userNum") userNum: Int = 20,
        @Query("workNum") workNum: Int = 3,
        @Query("isR18") isR18: Boolean = true
    ): ComposePixivResponse<ComposePixivUserRecommendBody>

    /**
     * 发现用户（总体推荐）
     * 获取推荐给当前登录账户的用户，不针对特定用户
     *
     * @param limit 返回数量（默认 20）
     */
    @GET("ajax/discovery/users")
    suspend fun getDiscoveryUsers(
        @Query("limit") limit: Int = 20
    ): ComposePixivResponse<ComposePixivDiscoveryUsersBody>

    /**
     * 获取用户关注详情
     * 查询指定用户的关注状态（公开/悄悄关注）
     * ⚠️ 注意：此接口只能查询自己关注的用户
     *
     * @param userId 用户ID
     */
    @GET("ajax/following/user/details")
    suspend fun getUserFollowDetail(
        @Query("user_id") userId: Long
    ): ComposePixivResponse<ComposePixivUserFollowDetailBody>

    /**
     * 获取好 P 友列表（MyPixiv）
     * 获取用户的好 P 友（互相关注的好友）列表
     *
     * @param uid 用户ID
     * @param offset 偏移量
     * @param limit 返回数量（最大 24）
     */
    @GET("ajax/user/{uid}/mypixiv")
    suspend fun getMyPixiv(
        @Path("uid") uid: Long,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 24
    ): ComposePixivResponse<ComposePixivMyPixivBody>

    /**
     * 获取用户插画的全部标签
     * 获取指定用户所有插画作品的标签列表，包括标签名称、翻译和作品数量
     *
     * @param uid 用户ID
     * @param all 获取全部标签（固定为 1）
     */
    @GET("ajax/user/{uid}/illusts/tags")
    suspend fun getUserIllustTags(
        @Path("uid") uid: Long,
        @Query("all") all: Int = 1
    ): ComposePixivResponse<List<ComposePixivUserIllustTag>>

    /**
     * 获取用户指定标签的插画作品
     * 根据标签筛选获取用户的插画作品列表
     *
     * @param uid 用户ID
     * @param tag 标签名称
     * @param offset 偏移量
     * @param limit 返回数量（最大 48）
     * @param sensitiveFilterMode 敏感内容过滤模式，默认 "userSetting"
     */
    @GET("ajax/user/{uid}/illusts/tag")
    suspend fun getUserIllustsByTag(
        @Path("uid") uid: Long,
        @Query("tag") tag: String,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 48,
        @Query("sensitiveFilterMode") sensitiveFilterMode: String = "userSetting"
    ): ComposePixivResponse<ComposePixivUserIllustsByTagBody>

    /**
     * 获取用户小说的全部标签
     * 获取指定用户所有小说作品的标签列表，包括标签名称、翻译和作品数量
     *
     * @param uid 用户ID
     * @param all 获取全部标签（固定为 1）
     */
    @GET("ajax/user/{uid}/novels/tags")
    suspend fun getUserNovelTags(
        @Path("uid") uid: Long,
        @Query("all") all: Int = 1
    ): ComposePixivResponse<List<ComposePixivUserIllustTag>>

    /**
     * 获取用户指定标签的小说作品
     * 根据标签筛选获取用户的小说作品列表
     *
     * @param uid 用户ID
     * @param tag 标签名称
     * @param offset 偏移量
     * @param limit 返回数量（最大 30）
     */
    @GET("ajax/user/{uid}/novels/tag")
    suspend fun getUserNovelsByTag(
        @Path("uid") uid: Long,
        @Query("tag") tag: String,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 30
    ): ComposePixivResponse<ComposePixivUserNovelsByTagBody>

    // ==================== 搜索 API ====================

    /**
     * 搜索插画 + 漫画 + 动图
     *
     * @param keyword 关键词
     * @param word 搜索文本
     * @param searchMode 搜索模式：s_tag(部分一致), s_tag_full(完全一致), s_tc(标题说明)
     * @param order 排序：date_d(最新), date(最旧)
     * @param mode 模式：all, safe, r18
     * @param page 页码
     * @param aiType AI作品过滤：1(隐藏AI), null(显示AI)
     * @param scd 起始日期 (yyyy-MM-dd)
     * @param ecd 结束日期 (yyyy-MM-dd)
     */
    @GET("ajax/search/artworks/{keyword}")
    suspend fun searchIllust(
        @Path("keyword", encoded = true) keyword: String,
        @Query("word") word: String,
        @Query("s_mode") searchMode: String = "s_tag_full",
        @Query("order") order: String = "date_d",
        @Query("mode") mode: String = "all",
        @Query("p") page: Int = 1,
        @Query("ai_type") aiType: Int? = null,
        @Query("scd") scd: String? = null,
        @Query("ecd") ecd: String? = null
    ): ComposePixivResponse<ComposePixivIllustSearchBody>

    /**
     * 搜索小说
     *
     * @param keyword 关键词
     * @param word 搜索文本
     * @param searchMode 搜索模式：s_tag_only(部分一致), s_tag_full(完全一致), s_tc(正文)
     * @param order 排序：date_d(最新), date(最旧)
     * @param mode 模式：all, safe, r18
     * @param page 页码
     * @param aiType AI作品过滤：1(隐藏AI), null(显示AI)
     * @param scd 起始日期 (yyyy-MM-dd)
     * @param ecd 结束日期 (yyyy-MM-dd)
     * @param workLang 语言过滤
     */
    @GET("ajax/search/novels/{keyword}")
    suspend fun searchNovel(
        @Path("keyword", encoded = true) keyword: String,
        @Query("word") word: String,
        @Query("s_mode") searchMode: String = "s_tag_full",
        @Query("order") order: String = "date_d",
        @Query("mode") mode: String = "all",
        @Query("p") page: Int = 1,
        @Query("ai_type") aiType: Int? = null,
        @Query("scd") scd: String? = null,
        @Query("ecd") ecd: String? = null,
        @Query("work_lang") workLang: String? = null
    ): ComposePixivResponse<ComposePixivNovelSearchBody>

    /**
     * 搜索用户
     *
     * @param keyword 用户昵称关键词
     * @param searchMode 搜索模式：s_usr(部分一致), s_usr_full(完全一致)
     * @param hasWork 是否只搜索有投稿作品的用户：1(是), 0(否)
     * @param page 页码
     */
    @GET("ajax/search/users")
    suspend fun searchUser(
        @Query("nick") keyword: String,
        @Query("s_mode") searchMode: String = "s_usr",
        @Query("i") hasWork: Int = 1,
        @Query("p") page: Int = 1
    ): ComposePixivResponse<ComposePixivUserSearchBody>

    // ==================== 小说 API ====================

    /**
     * 查询小说详情
     *
     * @param novelId 小说ID
     */
    @GET("ajax/novel/{novelId}")
    suspend fun getNovelDetail(@Path("novelId") novelId: Long): ComposePixivResponse<ComposePixivNovelDetailBody>

    /**
     * 查询小说收藏状态
     *
     * @param novelId 小说ID
     */
    @GET("ajax/novel/{novelId}/bookmarkData")
    suspend fun getNovelBookmarkData(@Path("novelId") novelId: Long): ComposePixivResponse<ComposePixivNovelBookmarkStatusBody>

    /**
     * 发现小说
     *
     * @param mode 模式：all, safe, r18
     * @param limit 返回数量（默认 100）
     * @param sampleNovelId 参考小说ID（可选）
     */
    @GET("ajax/discovery/novels")
    suspend fun getNovelDiscovery(
        @Query("mode") mode: String = "all",
        @Query("limit") limit: Int = 100,
        @Query("sampleNovelId") sampleNovelId: Long? = null
    ): ComposePixivResponse<ComposePixivDiscoveryBody>

    /**
     * 查询推荐小说（初始化）
     *
     * @param novelId 基准小说ID
     * @param limit 返回数量（默认 9）
     */
    @GET("ajax/novel/{novelId}/recommend/init")
    suspend fun getNovelRecommendInit(
        @Path("novelId") novelId: Long,
        @Query("limit") limit: Int = 9
    ): ComposePixivResponse<ComposePixivNovelRecommendInitBody>

    /**
     * 查询推荐小说
     *
     * @param novelIds 基准小说ID列表
     */
    @GET("ajax/novel/recommend/novels")
    suspend fun getRecommendNovels(
        @Query("novelIds[]") novelIds: List<Long>
    ): ComposePixivResponse<ComposePixivNovelRecommendBody>

    // ==================== 系列 API ====================

    /**
     * 查询插画/漫画系列详情
     *
     * @param seriesId 系列ID
     * @param page 页码
     */
    @GET("ajax/series/{seriesId}")
    suspend fun getIllustSeriesDetail(
        @Path("seriesId") seriesId: Long,
        @Query("p") page: Int = 1
    ): ComposePixivResponse<ComposePixivIllustSeriesBody>

    /**
     * 加入插画/漫画系列追更列表
     *
     * @param seriesId 系列ID
     */
    @POST("ajax/illust/series/{seriesId}/watch")
    suspend fun watchIllustSeries(@Path("seriesId") seriesId: Long): ComposePixivResponse<List<String>>

    /**
     * 移除插画/漫画系列追更
     *
     * @param seriesId 系列ID
     */
    @POST("ajax/illust/series/{seriesId}/unwatch")
    suspend fun unwatchIllustSeries(@Path("seriesId") seriesId: Long): ComposePixivResponse<List<String>>

    /**
     * 查询小说系列详情
     *
     * @param seriesId 系列ID
     */
    @GET("ajax/novel/series/{seriesId}")
    suspend fun getNovelSeriesDetail(@Path("seriesId") seriesId: Long): ComposePixivResponse<ComposePixivNovelSeriesBody>

    /**
     * 查询小说系列中作品的基础信息
     *
     * @param seriesId 系列ID
     * @param limit 返回数量（默认 30）
     * @param lastOrder 最后一个作品的序号（用于分页）
     * @param orderBy 排序方式：asc(升序), desc(降序)
     */
    @GET("ajax/novel/series_content/{seriesId}")
    suspend fun getNovelSeriesContents(
        @Path("seriesId") seriesId: Long,
        @Query("limit") limit: Int = 30,
        @Query("last_order") lastOrder: Int? = null,
        @Query("order_by") orderBy: String = "asc"
    ): ComposePixivResponse<ComposePixivNovelSeriesContentBody>

    /**
     * 查询小说系列的各篇标题列表
     *
     * @param seriesId 系列ID
     */
    @GET("ajax/novel/series/{seriesId}/content_titles")
    suspend fun getNovelSeriesTitles(@Path("seriesId") seriesId: Long): ComposePixivResponse<List<ComposePixivNovelSeriesTitle>>

    /**
     * 加入小说系列追更列表
     *
     * @param seriesId 系列ID
     */
    @POST("ajax/novel/series/{seriesId}/watch")
    suspend fun watchNovelSeries(@Path("seriesId") seriesId: Long): ComposePixivResponse<List<String>>

    /**
     * 移除小说系列追更
     *
     * @param seriesId 系列ID
     */
    @POST("ajax/novel/series/{seriesId}/unwatch")
    suspend fun unwatchNovelSeries(@Path("seriesId") seriesId: Long): ComposePixivResponse<List<String>>

    // ==================== 评论 API ====================

    /**
     * 获取插画/漫画评论根楼层列表
     *
     * @param illustId 作品ID
     * @param offset 偏移量
     * @param limit 返回数量
     */
    @GET("ajax/illusts/comments/roots")
    suspend fun getIllustCommentRoots(
        @Query("illust_id") illustId: Long,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 20
    ): ComposePixivResponse<ComposePixivCommentsBody>

    /**
     * 获取插画/漫画评论的回复列表
     *
     * @param commentId 评论ID
     * @param page 页码
     */
    @GET("ajax/illusts/comments/replies")
    suspend fun getIllustCommentReplies(
        @Query("comment_id") commentId: Long,
        @Query("page") page: Int = 1
    ): ComposePixivResponse<ComposePixivCommentsBody>

    /**
     * 发表插画/漫画评论
     *
     * @param illustId 作品ID
     * @param userId 作者/发送者用户ID
     * @param type 评论类型 ("comment" 或 "stamp")
     * @param comment 评论文字
     * @param stampId 表情/贴图ID
     * @param parentCommentId 父评论ID（回复时使用）
     */
    @FormUrlEncoded
    @POST("rpc/post_comment.php")
    suspend fun postIllustComment(
        @Field("illust_id") illustId: Long,
        @Field("author_user_id") userId: Long,
        @Field("type") type: String,
        @Field("comment") comment: String? = null,
        @Field("stamp_id") stampId: Int? = null,
        @Field("parent_id") parentCommentId: Long? = null
    ): ComposePixivPostCommentBody

    /**
     * 获取小说评论根楼层列表
     *
     * @param novelId 小说ID
     * @param offset 偏移量
     * @param limit 返回数量
     */
    @GET("ajax/novels/comments/roots")
    suspend fun getNovelCommentRoots(
        @Query("novel_id") novelId: Long,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 20
    ): ComposePixivResponse<ComposePixivCommentsBody>

    /**
     * 获取小说评论的回复列表
     *
     * @param commentId 评论ID
     * @param page 页码
     */
    @GET("ajax/novels/comments/replies")
    suspend fun getNovelCommentReplies(
        @Query("comment_id") commentId: Long,
        @Query("page") page: Int = 1
    ): ComposePixivResponse<ComposePixivCommentsBody>

    // ==================== 标签 API ====================

    /**
     * 查询标签建议（添加标签或搜索时使用）
     *
     * @param keyword 关键字
     */
    @GET("ajax/tags/suggest_by_word")
    suspend fun getTagSuggestByWord(@Query("word") keyword: String): ComposePixivResponse<ComposePixivTagSuggestBody>

    /**
     * 获取搜索框推荐内容（点击搜索框时触发）
     * 返回热门标签、推荐标签等
     *
     * @param mode 模式（all: 全部作品, r18: R18作品）
     */
    @GET("ajax/search/suggestion")
    suspend fun getSearchRecommendations(
        @Query("mode") mode: String = "all"
    ): ComposePixivResponse<ComposePixivSearchSuggestionBody>

    /**
     * 查询标签信息及翻译
     *
     * @param tag 标签名称
     * @param lang 语言（可选）
     */
    @GET("ajax/tag/info")
    suspend fun getTagInfo(
        @Query("tag") tag: String,
        @Query("lang") lang: String? = null
    ): ComposePixivResponse<ComposePixivTagInfoBody>

    /**
     * 为插画添加标签
     *
     * @param illustId 作品ID
     * @param body 添加标签请求体
     */
    @POST("ajax/tags/illust/{illustId}/add")
    suspend fun addIllustTag(
        @Path("illustId") illustId: Long,
        @Body body: PixivAddTagRequest
    ): ComposePixivResponse<ComposePixivAddTagBody>

    /**
     * 删除插画标签
     *
     * @param illustId 作品ID
     * @param body 删除标签请求体
     */
    @POST("ajax/tags/illust/{illustId}/delete")
    suspend fun deleteIllustTag(
        @Path("illustId") illustId: Long,
        @Body body: PixivAddTagRequest
    ): ComposePixivResponse<ComposePixivAddTagBody>

    /**
     * 为小说添加标签
     *
     * @param novelId 小说ID
     * @param body 添加标签请求体
     */
    @POST("ajax/tags/novel/{novelId}/add")
    suspend fun addNovelTag(
        @Path("novelId") novelId: Long,
        @Body body: PixivAddTagRequest
    ): ComposePixivResponse<ComposePixivAddTagBody>

    /**
     * 删除小说标签
     *
     * @param novelId 小说ID
     * @param body 删除标签请求体
     */
    @POST("ajax/tags/novel/{novelId}/delete")
    suspend fun deleteNovelTag(
        @Path("novelId") novelId: Long,
        @Body body: PixivAddTagRequest
    ): ComposePixivResponse<ComposePixivAddTagBody>

    // ==================== 关注 & 排行榜 API ====================

    /**
     * 查询关注作者的最新插画
     *
     * @param mode 模式：all, r18
     * @param page 页码
     */
    @GET("ajax/follow_latest/illust")
    suspend fun getFollowLatestIllust(
        @Query("mode") mode: String = "all",
        @Query("p") page: Int = 1
    ): ComposePixivResponse<ComposePixivFollowLatestBody>

    /**
     * 查询关注作者的最新小说
     *
     * @param mode 模式：all, r18
     * @param page 页码
     */
    @GET("ajax/follow_latest/novel")
    suspend fun getFollowLatestNovel(
        @Query("mode") mode: String = "all",
        @Query("p") page: Int = 1
    ): ComposePixivResponse<ComposePixivFollowLatestBody>

    /**
     * 查询插画/漫画排行榜
     *
     * @param mode 排行榜模式（如 daily, weekly, monthly, rookie, daily_r18 等）
     * @param page 页码
     * @param content 内容类型（all, illust, manga, ugoira）
     * @param date 日期（格式：yyyyMMdd，可选）
     * @param format 返回格式（默认 json）
     */
    @GET("ranking.php")
    suspend fun getIllustRanking(
        @Query("mode") @PixivRankingMode mode: String = PixivRankingMode.DAILY,
        @Query("p") page: Int = 1,
        @Query("content") @PixivRankingContentType content: String = PixivRankingContentType.ALL,
        @Query("date") date: String? = null,
        @Query("format") format: String = "json"
    ): ComposePixivRankingResponse

    /**
     * 查询小说排行榜（JSON 接口）
     *
     * @param mode 排行榜模式（如 daily, weekly, rookie, male, female, daily_r18 等）
     * @param content 内容类型（novel）
     * @param page 页码
     * @param date 日期（格式：yyyyMMdd，可选）
     */
    @GET("ajax/ranking/novel")
    suspend fun getNovelRankingJson(
        @Query("mode") mode: String = "daily",
        @Query("content") content: String = "novel",
        @Query("p") page: Int = 1,
        @Query("date") date: String? = null
    ): ComposePixivResponse<ComposePixivNovelRankingBody>
}