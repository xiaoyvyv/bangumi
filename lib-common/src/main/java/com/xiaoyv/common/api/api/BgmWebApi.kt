package com.xiaoyv.common.api.api

import com.xiaoyv.common.api.request.CreateTokenParam
import com.xiaoyv.common.api.response.BgmStatusEntity
import com.xiaoyv.common.api.response.NotifyEntity
import com.xiaoyv.common.api.response.ReplyResultEntity
import com.xiaoyv.common.api.response.UploadResultEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.BrowserSortType
import com.xiaoyv.common.config.annotation.MediaDetailType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.annotation.MessageBoxType
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
    suspend fun doLogin(@FieldMap query: Map<String, String>): Document

    /**
     * 查询时间胶囊
     *
     * @param ajax 仅返回嵌套的 html
     */
    @GET("/user/{userId}/timeline")
    suspend fun queryTimeline(
        @Path("userId", encoded = true) userId: String,
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

    @GET("/subject/{mediaId}{mediaDetailType}")
    suspend fun queryMediaDetail(
        @Path("mediaId", encoded = true) mediaId: String,
        @Path("mediaDetailType", encoded = true) @MediaDetailType type: String,
        @Query("page") page: Int? = null,
    ): Document

    /**
     * 日志详情
     */
    @GET("/blog/{blogId}")
    suspend fun queryBlogDetail(@Path("blogId", encoded = true) blogId: String): Document

    /**
     * Topic 详情
     *
     * 直接使用超展开接口
     */
    @GET("/rakuen/topic/{topicType}/{topicId}")
    suspend fun queryTopicDetail(
        @Path("topicId", encoded = true) topicId: String,
        @Path("topicType", encoded = true) topicType: String
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
    suspend fun postNewReply(
        @Path("action", encoded = true) submitAction: String,
        @FieldMap param: Map<String, String>,
        @Query("ajax") ajax: Int = 1
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

    @GET("/blog/create")
    suspend fun queryCreateBlogForm(@Query("review") review: String?): Document

    @GET("/erase/entry/{blogId}")
    suspend fun deleteBlog(
        @Path("blogId", encoded = true) blogId: String,
        @Query("gh") hash: String
    ): Document

    @FormUrlEncoded
    @POST("/subject/{mediaId}/interest/update")
    suspend fun updateInterest(
        @FieldMap map: Map<String, String>,
        @Path("mediaId", encoded = true) mediaId: String,
        @Query("gh") gh: String
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
        @Query("page") page: Int? = null,
    ): Document

    @FormUrlEncoded
    @POST("/group/{groupId}/bye")
    suspend fun postExitGroup(
        @Header("Referer") referer: String,
        @Path("groupId", encoded = true) groupId: String,
        @Query("gh") gh: String,
        @Field("action") action: String = "join-bye"
    ): Document

    @FormUrlEncoded
    @POST("/group/{groupId}/join")
    suspend fun postJoinGroup(
        @Header("Referer") referer: String,
        @Path("groupId", encoded = true) groupId: String,
        @Query("gh") gh: String,
        @Field("action") action: String = "join-bye"
    ): Document

    @GET("/notify/all")
    suspend fun queryNotifyAll(): Document

    /**
     * 绝交用户
     */
    @FormUrlEncoded
    @POST("/settings/privacy?ajax=1")
    suspend fun postIgnoreUser(
        @Field("ignore_user") ignoreUser: String,
        @Field("formhash") formHash: String,
        @Field("submit_ignore") submitIgnore: String = "submit_ignore",
        @Query("ajax") ajax: Int = 1
    ): BgmStatusEntity

    @GET("/pm/{type}.chii")
    suspend fun queryMessageList(
        @Path("type", encoded = true) @MessageBoxType type: String,
        @Query("page") page: Int
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
    suspend fun postMessage(
        @Header("Referer") referer: String,
        @FieldMap map: Map<String, String>
    ): Document
}