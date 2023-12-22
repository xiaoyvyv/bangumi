package com.xiaoyv.common.api.api

import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.LikeEntity
import com.xiaoyv.common.api.request.CreateTokenParam
import com.xiaoyv.common.api.response.BgmStatusEntity
import com.xiaoyv.common.api.response.NotifyEntity
import com.xiaoyv.common.api.response.ReplyResultEntity
import com.xiaoyv.common.api.response.UploadResultEntity
import com.xiaoyv.common.api.response.base.BgmActionResponse
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.BrowserSortType
import com.xiaoyv.common.config.annotation.EpCollectType
import com.xiaoyv.common.config.annotation.IndexAttachCatType
import com.xiaoyv.common.config.annotation.IndexTabCatType
import com.xiaoyv.common.config.annotation.LikeType
import com.xiaoyv.common.config.annotation.MagiType
import com.xiaoyv.common.config.annotation.MediaDetailType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.annotation.MessageBoxType
import com.xiaoyv.common.config.annotation.ReportType
import com.xiaoyv.common.config.annotation.SearchCatType
import com.xiaoyv.common.config.annotation.SuperType
import com.xiaoyv.common.config.annotation.TimelineType
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import org.jsoup.nodes.Document
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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

    @GET("/json/notify")
    suspend fun notify(@Query("_") timestamp: Long): NotifyEntity

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
    suspend fun doLogin(
        @Header("Referer") referer: String = BgmApiManager.URL_BASE_WEB,
        @FieldMap param: Map<String, String>,
    ): Document

    /**
     * 查询指定好友时间胶囊
     *
     * @param ajax 仅返回嵌套的 html
     */
    @GET("/user/{userId}/timeline")
    suspend fun queryUserTimeline(
        @Path("userId", encoded = true) userId: String,
        @Query("type") @TimelineType type: String,
        @Query("ajax") ajax: Long = 1,
    ): Document

    /**
     * 查询登录状态下，全部好友时间胶囊

     * @param ajax 仅返回嵌套的 html
     */
    @GET("/timeline")
    suspend fun queryFriendTimeline(
        @Query("type") @TimelineType type: String,
        @Query("ajax") ajax: Long = 1,
    ): Document

    /**
     * 超展开
     */
    @GET("/rakuen/topiclist")
    suspend fun querySuperTopic(
        @Query("type") @SuperType type: String,
        @Query("filter") filter: String? = null,
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
        @Query("page") page: Int = 1,
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
        @Query("page") page: Int = 1,
    ): Document

    @GET("/subject/{mediaId}{mediaDetailType}")
    suspend fun queryMediaDetail(
        @Path("mediaId", encoded = true) mediaId: String,
        @Path("mediaDetailType", encoded = true) @MediaDetailType type: String,
        @Query("page") page: Int? = null,
    ): Document

    @FormUrlEncoded
    @POST("/subject/ep/{epId}/status/{epCollectType}")
    suspend fun postEpCollect(
        @Header("Referer") referer: String,
        @Path("epId", encoded = true) epId: String,
        @Path("epCollectType", encoded = true) @EpCollectType epCollectType: String,
        @Query("gh") gh: String,
        @Field("ep_id") watchedEpId: Array<String>? = null,
    ): Document

    @FormUrlEncoded
    @POST("/subject/set/watched/{mediaId}")
    suspend fun updateMediaProgress(
        @Path("mediaId") mediaId: String,
        @Field("watchedeps") watch: String,
        @Field("referer") referer: String = "subject",
        @Field("submit") submit: String = "更新",
    ): Document

    /**
     * 日志详情
     */
    @GET("/blog/{blogId}")
    suspend fun queryBlogDetail(@Path("blogId", encoded = true) blogId: String): Document

    /**
     * 目录详情
     */
    @GET("/index/{indexId}")
    suspend fun queryIndexDetail(
        @Path("indexId", encoded = true) indexId: String,
        @Query("cat") @IndexTabCatType cat: String? = null,
    ): Document

    @FormUrlEncoded
    @POST("/index/{indexId}/erase")
    suspend fun deleteIndex(
        @Header("Referer") referer: String = BgmApiManager.URL_BASE_WEB,
        @Path("indexId", encoded = true) indexId: String,
        @FieldMap map: Map<String, String>,
    ): Document

    @FormUrlEncoded
    @POST("/index/create")
    suspend fun createIndex(
        @Header("Referer") referer: String = BgmApiManager.URL_BASE_WEB,
        @Field("formhash") formHash: String,
        @Field("title") title: String,
        @Field("desc") desc: String,
        @Field("submit") submit: String = "创建目录",
    ): Document

    @FormUrlEncoded
    @POST("/index/{indexId}/add_related")
    suspend fun addMediaToIndex(
        @Header("Referer") referer: String = BgmApiManager.URL_BASE_WEB,
        @Path("indexId", encoded = true) indexId: String,
        @Field("formhash") formHash: String,
        @Field("cat") @IndexAttachCatType cat: String,
        @Field("add_related") targetId: String,
        @Field("submit") submit: String = "添加关联",
    ): Document

    /**
     * Topic 详情
     *
     * 直接使用超展开接口
     */
    @GET("/rakuen/topic/{topicType}/{topicId}")
    suspend fun queryTopicDetail(
        @Path("topicId", encoded = true) topicId: String,
        @Path("topicType", encoded = true) topicType: String,
    ): Document


    /**
     * 发表回复
     *
     * - New
     * ```
     * content	"\n台长?\n"
     * related_photo	"0"
     * lastview	"1701450913"
     * formhash	"275b091c"
     * submit	"submit"
     * ```
     *
     * Sub
     * ```
     * topic_id	"327295"
     * related	"195202"
     * sub_reply_uid	"837364"
     * post_uid	"539713"
     * content	"Re:zero+"
     * related_photo	"0"
     * lastview	"1701492611"
     * formhash	"275b091c"
     * submit	"submit"
     * ```
     */
    @FormUrlEncoded
    @POST("{action}")
    suspend fun postReply(
        @Path("action", encoded = true) submitAction: String,
        @FieldMap param: Map<String, String>,
        @Query("ajax") ajax: Int = 1,
    ): ReplyResultEntity

    /**
     * 上传图片
     */
    @Multipart
    @POST("/blog/upload_photo")
    suspend fun uploadBlogImage(@Part file: MultipartBody.Part): UploadResultEntity

    /**
     * 发表文章
     * ```
     * -----------------------------7435713640328518672821874407
     * Content-Disposition: form-data; name="formhash"
     *
     * 275b091c
     * -----------------------------7435713640328518672821874407
     * Content-Disposition: form-data; name="title"
     *
     * 浅浅发个日志测试一下
     * -----------------------------7435713640328518672821874407
     * Content-Disposition: form-data; name="content"
     *
     * 浅浅发个日志测试一下
     * -----------------------------7435713640328518672821874407
     * Content-Disposition: form-data; name="tags"
     *
     * 动画
     * -----------------------------7435713640328518672821874407
     * Content-Disposition: form-data; name="public"
     *
     * 0
     * -----------------------------7435713640328518672821874407
     * Content-Disposition: form-data; name="submit"
     *
     * 加上去
     * -----------------------------7435713640328518672821874407
     * Content-Disposition: form-data; name="related_subject[]"
     *
     * 2907
     * -----------------------------7435713640328518672821874407--
     *
     * ```
     */
    @POST("/blog/create")
    suspend fun postCreateBlog(@Body body: MultipartBody): Document

    @GET("/blog/{blogId}/edit")
    suspend fun queryBlogEditInfo(@Path("blogId") blogId: String): Document

    @POST("/blog/{blogId}/edit")
    suspend fun postEditBlog(@Path("blogId") blogId: String, @Body body: MultipartBody): Document

    /**
     * 发布一个话题
     *
     * @param targetType 发布话题的目标类型 group|subject
     * @param targetId 发布话题的目标类型的ID
     */
    @FormUrlEncoded
    @POST("/{targetType}/{targetId}/topic/new")
    suspend fun postCreateTopic(
        @Path("targetType", encoded = true) targetType: String,
        @Path("targetId", encoded = true) targetId: String,
        @FieldMap map: Map<String, String>,
    ): Document

    @GET("/subject/{subjectId}/remove")
    suspend fun deleteSubjectCollect(
        @Path("subjectId", encoded = true) subjectId: String,
        @Query("gh") hash: String,
    ): Document

    @GET("/erase/group/topic/{topicId}")
    suspend fun deleteGrpTopic(
        @Path("topicId", encoded = true) topicId: String,
        @Query("gh") hash: String,
    ): Document

    @GET("/erase/subject/topic/{topicId}")
    suspend fun deleteSubjectTopic(
        @Path("topicId", encoded = true) topicId: String,
        @Query("gh") hash: String,
    ): Document

    @GET("/erase/club/topic/{topicId}")
    suspend fun deleteClubTopic(
        @Path("topicId", encoded = true) topicId: String,
        @Query("gh") hash: String,
    ): Document

    @GET("/erase/tml/{tmlId}")
    suspend fun deleteTimeline(
        @Path("tmlId", encoded = true) tmlId: String,
        @Query("gh") hash: String,
        @Query("ajax") ajax: Int = 1,
    ): Document

    @GET("/erase/entry/{blogId}")
    suspend fun deleteBlog(
        @Path("blogId", encoded = true) blogId: String,
        @Query("gh") hash: String,
    ): Document

    @GET("/disconnect/{frdId}")
    suspend fun disconnectFriend(
        @Header("Referer") referer: String,
        @Path("frdId", encoded = true) frdId: String,
        @Query("gh") hash: String,
        @Query("ajax") ajax: Int = 1,
    ): Document

    @GET("/connect/{frdId}")
    suspend fun connectFriend(
        @Header("Referer") referer: String,
        @Path("frdId", encoded = true) frdId: String,
        @Query("gh") hash: String,
        @Query("ajax") ajax: Int = 1,
    )

    @FormUrlEncoded
    @POST("/subject/{mediaId}/interest/update")
    suspend fun updateInterest(
        @FieldMap map: Map<String, String>,
        @Path("mediaId", encoded = true) mediaId: String,
        @Query("gh") gh: String,
    ): Document

    /**
     * 用户详情
     */
    @GET("/user/{userId}")
    suspend fun queryUserInfo(@Path("userId") userId: String): Document

    /**
     * 人物详情
     */
    @GET("/person/{personId}")
    suspend fun queryPersonInfo(@Path("personId") personId: String): Document

    @GET("/{type}/{id}/collect")
    suspend fun postAddCollect(
        @Header("Referer") referer: String,
        @Path("type", encoded = true) @BgmPathType type: String,
        @Path("id", encoded = true) id: String,
        @Query("gh") gh: String,
    ): Document

    @GET("/{type}/{id}/erase_collect")
    suspend fun postRemoveCollect(
        @Header("Referer") referer: String,
        @Path("type", encoded = true) @BgmPathType type: String,
        @Path("id", encoded = true) id: String,
        @Query("gh") gh: String,
    ): Document

    /**
     * 虚拟人物详情
     */
    @GET("/character/{characterId}")
    suspend fun queryCharacterInfo(@Path("characterId") characterId: String): Document

    @GET("/person/{personId}/collections?page=1")
    suspend fun queryPersonCollectUser(
        @Path("personId", encoded = true) personId: String,
        @Query("page") page: Int? = null,
    ): Document

    @GET("/character/{personId}/collections?page=1")
    suspend fun queryCharacterCollectUser(
        @Path("personId", encoded = true) personId: String,
        @Query("page") page: Int? = null,
    ): Document

    @GET("/person/{personId}/collabs")
    suspend fun queryPersonCooperate(
        @Path("personId", encoded = true) personId: String,
        @Query("page") page: Int? = null,
    ): Document

    @GET("/person/{personId}/works")
    suspend fun queryPersonWorks(
        @Path("personId", encoded = true) personId: String,
        @Query("page") page: Int? = null,
    ): Document

    @GET("/person/{personId}/works/voice")
    suspend fun queryPersonWorkVoices(@Path("personId", encoded = true) personId: String): Document

    @GET("/group/{groupId}")
    suspend fun queryGroupDetail(@Path("groupId", encoded = true) groupId: String): Document

    @GET("/group/discover")
    suspend fun queryGroupIndex(): Document

    @GET("/group/{groupId}/forum")
    suspend fun queryGroupTopicList(
        @Path("groupId", encoded = true) groupId: String,
        @Query("page") page: Int,
    ): Document

    @GET("/group/{type}")
    suspend fun queryUserTopicList(
        @Path("type", encoded = true) type: String,
        @Query("page") page: Int,
    ): Document

    @GET("/group/category/{category}")
    suspend fun queryGroupList(
        @Path("category", encoded = true) category: String,
        @Query("page") page: Int,
    ): Document

    @FormUrlEncoded
    @POST("/group/{groupId}/bye")
    suspend fun postExitGroup(
        @Header("Referer") referer: String,
        @Path("groupId", encoded = true) groupId: String,
        @Query("gh") gh: String,
        @Field("action") action: String = "join-bye",
    ): Document

    @FormUrlEncoded
    @POST("/group/{groupId}/join")
    suspend fun postJoinGroup(
        @Header("Referer") referer: String,
        @Path("groupId", encoded = true) groupId: String,
        @Query("gh") gh: String,
        @Field("action") action: String = "join-bye",
    ): Document

    @GET("/notify/all")
    suspend fun queryNotifyAll(): Document

    /**
     * 标记全部通知已读
     */
    @GET("/erase/notify/all")
    suspend fun markNotifyRead(
        @Header("Referer") referer: String = BgmApiManager.URL_BASE_WEB,
        @Query("gh") formHash: String,
    ): Document

    /**
     * 绝交用户
     */
    @FormUrlEncoded
    @POST("/settings/privacy?ajax=1")
    suspend fun postIgnoreUser(
        @Header("Referer") referer: String,
        @Field("ignore_user") ignoreUser: String,
        @Field("formhash") formHash: String,
        @Field("submit_ignore") submitIgnore: String = "submit_ignore",
        @Query("ajax") ajax: Int = 1,
    ): BgmStatusEntity

    @GET("/pm/{type}.chii")
    suspend fun queryMessageList(
        @Path("type", encoded = true) @MessageBoxType type: String,
        @Query("page") page: Int,
    ): Document

    @GET("/pm/view/{messageId}.chii")
    suspend fun queryMessageBox(@Path("messageId", encoded = true) messageId: String): Document


    /**
     * 发送短信
     *
     * ```
     * related	"320667"
     * msg_receivers	"837364"
     * current_msg_id	"320667"
     * formhash	"6a93ef91"
     * msg_title	"Re:hello?"
     * msg_body	"22222"
     * chat	"on"
     * submit	"回复"
     * ```
     */
    @FormUrlEncoded
    @POST("/pm/create.chii")
    suspend fun postMessage(@FieldMap map: Map<String, String>): Document

    @FormUrlEncoded
    @POST("/pm/erase/batch")
    suspend fun postClearMessageBox(
        @Field("folder") @MessageBoxType folder: String,
        @Field("erase_pm[]") erasePm: List<String>,
        @Field("chkall") chkall: String,
        @Query("gh") gh: String,
    ): Document

    /**
     * 搜索人物、条目
     */
    @GET("/{pathType}/{keyword}")
    suspend fun querySearchMedia(
        @Path("pathType") pathType: String,
        @Path("keyword") keyword: String,
        @Query("cat") @SearchCatType cat: String,
        @Query("page") page: Int,
        @Query("legacy") legacy: Int = 1,
    ): Document

    /**
     * 搜索标签
     */
    @GET("/search/tag/{mediaType}/{keyword}")
    suspend fun querySearchTag(
        @Path("mediaType") mediaType: String,
        @Path("keyword") keyword: String,
    ): Document

    /**
     * 标签详细内容
     *
     * time 格式
     * - /airtime/2024
     * - /airtime/2024-12
     */
    @GET("/{mediaType}/tag/{tag}{time}")
    suspend fun queryTagDetail(
        @Path("mediaType") mediaType: String,
        @Path("tag") tag: String,
        @Path("time", encoded = true) time: String,
        @Query("sort") @BrowserSortType sortType: String? = null,
        @Query("page") page: Int,
    ): Document


    @GET("/magi")
    suspend fun queryMagi(@Query("cat") @MagiType mediaType: String): Document

    @GET("/magi/rank")
    suspend fun queryMagiRank(): Document

    @GET("/magi/self")
    suspend fun queryMagiHistory(): Document

    @GET("/magi/q/{id}")
    suspend fun queryMagiDetail(@Path("id") id: String): Document

    @FormUrlEncoded
    @POST("/magi/answer")
    suspend fun postMagiAnswer(
        @Header("Referer") referer: String = BgmApiManager.URL_BASE_WEB + "/magi",
        @FieldMap params: Map<String, String>,
    ): Document

    @GET("/index")
    suspend fun queryIndexPage(): Document

    @GET("/index/browser")
    suspend fun queryIndexList(
        @Query("orderby") orderBy: String? = null,
        @Query("page") page: Int,
    ): Document

    @GET("/user/{userId}/index{collect}")
    suspend fun queryUserIndex(
        @Path("userId", encoded = true) userId: String,
        @Path("collect", encoded = true) collect: String = "",
        @Query("page") page: Int,
    ): Document

    @GET("/user/{userId}/groups")
    suspend fun queryUserGroup(@Path("userId", encoded = true) userId: String): Document

    @GET("/user/{userId}/friends")
    suspend fun queryUserFriends(@Path("userId", encoded = true) userId: String): Document

    @GET("/report")
    suspend fun queryReportForm(
        @Query("id") userId: String,
        @Query("type") @ReportType reportType: String,
    ): Document

    @FormUrlEncoded
    @POST("/report")
    suspend fun postReport(
        @Field("comment") comment: String,
        @Field("value") value: String,
        @FieldMap map: Map<String, String>,
    ): Document

    /**
     * 贴贴或取消
     *
     * @param type 类型
     * @param mainId 主题ID
     * @param commendId 贴贴的目标评论ID
     * @param likeValue Like 的值
     */
    @GET("like")
    suspend fun toggleLike(
        @Query("type") @LikeType type: String,
        @Query("main_id") mainId: String,
        @Query("id") commendId: String,
        @Query("value") likeValue: String,
        @Query("gh") gh: String,
        @Query("ajax") ajax: Int = 1,
    ): BgmActionResponse<LikeEntity>

    @FormUrlEncoded
    @POST("/group/new_group")
    suspend fun newGroup(
        @Field("formhash") formhash: String,
        @Field("name") name: String,
        @Field("title") title: String,
        @Field("category") category: String,
        @Field("desc") desc: String,
        @Field("accessible") accessible: Int,
        @Field("submit") submit: String,
    ): Document

    @GET("/settings/privacy")
    suspend fun queryPrivacy(
        @Query("ignore_reset") numberId: String? = null,
        @Query("gh") formHash: String? = null,
    ): Document

    @FormUrlEncoded
    @POST("/settings/privacy")
    suspend fun postPrivacy(
        @Header("Referer") referer: String = BgmApiManager.URL_BASE_WEB,
        @FieldMap param: Map<String, String>,
    ): Document

    @GET("/mono")
    suspend fun queryMonoIndex(): Document

    @GET("/user/{userId}/mono")
    suspend fun queryUserMono(@Path("userId") userId: String): Document

    @GET("/{monoType}")
    suspend fun queryMonoList(
        @Path("monoType", encoded = true) monoType: String,
        @Query("orderby") orderByType: String?,
        @Query("page") page: Int,
        @QueryMap param: Map<String, String>,
    ): Document

    /**
     * 吐个槽
     */
    @FormUrlEncoded
    @POST("/update/user/say?ajax=1")
    suspend fun postTimelineSay(
        @Field("say_input") sayInput: String,
        @Field("formhash") gh: String,
        @Field("submit") submit: String = "submit",
        @Query("ajax") ajax: Int = 1,
    )
}





