@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.api

import com.fleeksoft.ksoup.nodes.Document
import com.xiaoyv.bangumi.shared.core.types.AppWebApiDsl
import com.xiaoyv.bangumi.shared.core.types.CollectionWebPath
import com.xiaoyv.bangumi.shared.core.types.CollectionWebSortType
import com.xiaoyv.bangumi.shared.core.types.CommentType
import com.xiaoyv.bangumi.shared.core.types.IndexCatWebTabType
import com.xiaoyv.bangumi.shared.core.types.MessageBoxType
import com.xiaoyv.bangumi.shared.core.types.TopicDetailType
import com.xiaoyv.bangumi.shared.core.types.RakuenTab
import com.xiaoyv.bangumi.shared.core.types.ReportType
import com.xiaoyv.bangumi.shared.core.types.ReportValueType
import com.xiaoyv.bangumi.shared.core.types.SubjectSortBrowserType
import com.xiaoyv.bangumi.shared.core.types.SubjectWebPath
import com.xiaoyv.bangumi.shared.core.types.TimelineTab
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeAuthToken
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeDollarItem
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeFriend
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeNewReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeStatus
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUploadImage
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FieldMap
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.ReqBuilder
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.statement.HttpResponse
import io.ktor.util.date.getTimeMillis

/**
 * [BgmWebApi]
 *
 * @author why
 * @since 2025/1/14
 */
@AppWebApiDsl
interface BgmWebApi {
    @GET("https://bgm.tv")
    suspend fun fetchMainPage(): Document

    @GET("login")
    suspend fun fetchLoginForm(): Document

    /**
     * 验证码
     */
    @GET("signup/captcha")
    suspend fun fetchVerifyCodeImage(@QueryMap map: Map<String, String>): HttpResponse


    /**
     * 获取用户设置
     */
    @GET("settings")
    suspend fun fetchUserEditInfo(): Document

    /**
     * 获取用户通知未读数目
     */
    @GET("json/notify")
    suspend fun fetchUserUnreadNotification(@Query("_") timestamp: Long): Document

    /**
     * 获取用户短信未读数目
     */
    @GET("pm/inbox.chii")
    suspend fun fetchUserUnreadMessage(): Document

    /**
     * 获取用户最新的未读通知
     */
    @GET("notify")
    suspend fun fetchUserNotificationNewest(): Document

    /**
     * 获取用户全部通知
     */
    @GET("notify/all")
    suspend fun fetchUserNotificationHistory(): Document

    /**
     * 获取用户的短信消息
     */
    @GET("/pm/{type}.chii")
    suspend fun fetchUserMessageList(
        @Path("type", encoded = true) @MessageBoxType type: String,
        @Query("page") page: Int,
    ): Document

    /**
     * 获取用户的短信消息详情
     */
    @GET("pm/view/{id}.chii")
    suspend fun fetchUserMessageDetail(@Path("id", encoded = true) id: Long): Document

    /**
     * 浏览条目
     */
    @GET("{type}/browser")
    suspend fun fetchBrowser(
        @Path("type") @SubjectWebPath type: String,
        @Query("sort") @SubjectSortBrowserType sort: String,
    ): Document

    @GET("subject/{subjectId}")
    suspend fun fetchSubjectDetail(@Path("subjectId") subjectId: Long): Document

    /**
     * 获取条目吐槽
     */
    @GET("subject/{subjectId}/comments")
    suspend fun fetchSubjectComment(
        @Path("subjectId") subjectId: Long,
        @Query("page") page: Int = 1,
    ): Document


    /**
     * 获取条目角色信息
     */
    @GET("subject/{subjectId}/characters")
    suspend fun fetchSubjectCharacter(@Path("subjectId") subjectId: Long): Document


    /**
     * 获取条目人物信息
     */
    @GET("subject/{subjectId}/persons")
    suspend fun fetchSubjectPerson(@Path("subjectId") subjectId: Long): Document


    /**
     * 获取条目日志信息
     */
    @GET("subject/{subjectId}/reviews/{page}.html")
    suspend fun fetchSubjectBlog(
        @Path("subjectId") subjectId: Long,
        @Path("page") page: Int,
    ): Document

    /**
     * 获取条目帖子信息
     */
    @GET("subject/{subjectId}/board")
    suspend fun fetchSubjectTopic(@Path("subjectId") subjectId: Long): Document

    /**
     * 获取条目关联目录
     */
    @GET("subject/{subjectId}/index")
    suspend fun fetchSubjectIndex(@Path("subjectId") subjectId: Long, @Query("page") page: Int): Document

    /**
     * 获取条目透视
     */
    @GET("subject/{subjectId}/stats")
    suspend fun fetchSubjectStats(@Path("subjectId") subjectId: Long): Document

    /**
     * 搜索条目Tag
     */
    @GET("search/tag/{type}/{keyword}")
    suspend fun fetchSearchSubjectTags(
        @Path("type") @SubjectWebPath type: String,
        @Path("keyword") keyword: String,
    ): Document

    /**
     * 浏览条目全部Tag
     */
    @GET("{type}/tag")
    suspend fun fetchBorwserSubjectTags(
        @Path("type") @SubjectWebPath type: String,
        @Query("page") page: Int,
    ): Document

    /**
     * 获取角色详情信息
     */
    @GET("character/{monoId}")
    suspend fun fetchCharacterDetail(@Path("monoId") monoId: Long): Document

    /**
     * 获取角色相册
     */
    @GET("character/{monoId}/album")
    suspend fun fetchCharacterAlbum(@Path("monoId") monoId: Long, @Query("page") page: Int): Document

    /**
     * 获取角色关联目录
     */
    @GET("character/{monoId}/indices")
    suspend fun fetchCharacterIndices(@Path("monoId") monoId: Long, @Query("page") page: Int): Document

    /**
     * 获取人物详情信息
     */
    @GET("person/{monoId}")
    suspend fun fetchPersonDetail(@Path("monoId") monoId: Long): Document

    /**
     * 获取人物职位
     */
    @GET("person/{monoId}/works")
    suspend fun fetchPersonWorkPosition(@Path("monoId") monoId: Long): Document

    /**
     * 获取人物关联目录
     */
    @GET("person/{monoId}/indices")
    suspend fun fetchPersonIndices(@Path("monoId") monoId: Long, @Query("page") page: Int): Document

    /**
     * 按条目分类浏览全部日志
     */
    @GET("{subject_type}/blog")
    suspend fun fetchBrowserBlog(
        @Path("subject_type") @SubjectWebPath type: String,
        @Query("page") page: Int,
    ): Document

    /**
     * 浏览全部日志
     */
    @GET("blog")
    suspend fun fetchBrowserBlog(@Query("page") page: Int): Document

    /**
     * 人物首页数据
     */
    @GET("mono")
    suspend fun fetchMonoHomepage(): Document

    /**
     * 浏览人物
     */
    @GET("person")
    suspend fun fetchBrowserMonoPerson(
        @Query("page") page: Int,
        @Query("type") type: String? = null,
        @Query("gender") gender: String? = null,
        @Query("bloodtype") bloodtype: String? = null,
        @Query("month") month: String? = null,
        @Query("day") day: String? = null,
        @Query("orderby") sort: String? = null,
    ): Document

    /**
     * 浏览角色
     */
    @GET("character")
    suspend fun fetchBrowserMonoCharacter(
        @Query("page") page: Int,
        @Query("type") type: String? = null,
        @Query("gender") gender: String? = null,
        @Query("bloodtype") bloodtype: String? = null,
        @Query("month") month: String? = null,
        @Query("day") day: String? = null,
        @Query("orderby") sort: String? = null,
    ): Document

    /**
     * 全站闲聊
     *
     * https://bgm.tv/dollars?since_id=1756805557&_=1756806092131
     */
    @GET("dollars")
    suspend fun fetchDollarChat(
        @Query("since_id") sinceId: Long = 0,
        @Query("_") timestamp: Long = getTimeMillis(),
    ): List<ComposeDollarItem>

    /**
     * 时间胶囊-指定用户
     *
     * @param ajax 仅返回嵌套的 html
     */
    @GET("user/{username}/timeline")
    suspend fun fetchTimelineForUser(
        @Path("username", encoded = true) username: String,
        @Query("type") @TimelineTab type: String,
        @Query("page") page: Int? = null,
        @Query("ajax") ajax: Long = 1,
    ): Document

    /**
     * 隐私设置页面
     */
    @GET("settings/privacy")
    suspend fun fetchUserPrivacy(): Document

    /**
     * 时间胶囊-全站
     */
    @GET("timeline")
    suspend fun fetchTimelineForWhole(
        @Query("type") @TimelineTab type: String,
        @Query("page") page: Int? = null,
        @Query("ajax") ajax: Long = 1,
    ): Document

    /**
     * 时间胶囊-吐槽回复内容
     */
    @GET("user/{username}/timeline/status/{timelineId}")
    suspend fun fetchTimelineReply(
        @Path("username", encoded = true) username: String,
        @Path("timelineId", encoded = true) timelineId: String,
    ): Document

    /**
     * 小组-全站
     *
     * @param category 全部传 `all`
     */
    @GET("group/category/{category}")
    suspend fun fetchAllGroup(
        @Path("category", encoded = true) category: String,
        @Query("page") page: Int = 1,
    ): Document

    /**
     * 用户加入的小组
     */
    @GET("user/{username}/groups")
    suspend fun fetchUserGroup(@Path("username", encoded = true) username: String): Document

    /**
     * 小组主页
     */
    @GET("group/discover")
    suspend fun fetchGroupHomepage(): Document

    /**
     * 查询指定用户的日志
     */
    @GET("user/{username}/blog")
    suspend fun fetchUserBlogList(
        @Path("username", encoded = true) username: String,
        @Query("page") page: Int = 1,
    ): Document

    /**
     * 查询指定用户创建的目录
     */
    @GET("user/{username}/index")
    suspend fun fetchUserIndexCreateList(
        @Path("username", encoded = true) username: String,
        @Query("page") page: Int = 1,
        @Query("ajax") ajax: Long = 1,
    ): Document

    /**
     * 查询指定用户收藏的目录
     */
    @GET("user/{username}/index/collect")
    suspend fun fetchUserIndexCollectionList(
        @Path("username", encoded = true) username: String,
        @Query("page") page: Int = 1,
        @Query("ajax") ajax: Long = 1,
    ): Document

    /**
     * 查询全站目录
     */
    @GET("index/browser")
    suspend fun fetchIndexBorwser(
        @Query("orderby") orderby: String,
        @Query("page") page: Int = 1,
    ): Document

    /**
     * 查询目录首页
     */
    @GET("index")
    suspend fun fetchIndexHomepage(): Document

    /**
     * 目录详情
     */
    @GET("index/{id}")
    suspend fun fetchIndexDetail(@Path("id") id: Long, @Query("cat") @IndexCatWebTabType cat: String): Document

    /**
     * 超展开
     */
    @GET("rakuen/topiclist")
    suspend fun fetchRakuenTopic(
        @Query("type") @RakuenTab type: String,
        @Query("filter") filter: String? = null,
    ): Document

    /**
     * Topic 详情
     *
     * 直接使用超展开接口
     */
    @GET("rakuen/topic/{type}/{id}")
    suspend fun fetchRakuenTopicDetail(
        @Path("id") id: Long,
        @Path("type", encoded = true) @TopicDetailType type: String,
        @ReqBuilder ext: HttpRequestBuilder.() -> Unit = {},
    ): Document

    /**
     * 日志详情
     */
    @GET("blog/{id}")
    suspend fun fetchRakuenBlogDetail(@Path("id") id: Long): Document

    /**
     * 我的朋友列表
     */
    @GET("ajax/buddy_search")
    suspend fun fetchMyFriends(): List<ComposeFriend>

    /**
     * 朋友列表
     */
    @GET("user/{username}/friends")
    suspend fun fetchUserFriends(@Path("username", encoded = true) username: String): Document

    /**
     * 用户页面
     */
    @GET("user/{username}")
    suspend fun fetchUserHomepage(@Path("username", encoded = true) username: String): Document

    /**
     * 用户收藏查询
     */
    @GET("{subjectType}/list/{username}/{type}")
    suspend fun fetchUserCollection(
        @Path("username", encoded = true) username: String,
        @Path("subjectType", encoded = true) @SubjectWebPath subjectType: String,
        @Path("type", encoded = true) @CollectionWebPath type: String,
        @Query("orderby") @CollectionWebSortType sortType: String? = null,
        @Query("tag") tag: String? = null,
        @Query("page") page: Int = 1,
    ): Document

    /**
     * 登录地址
     */
    @FormUrlEncoded
    @POST("FollowTheRabbit")
    suspend fun sendLogin(
        @Header("Referer") referer: String,
        @FieldMap param: Map<String, String>,
    ): Document

    /**
     * OAuth 授权
     */
    @FormUrlEncoded
    @POST("oauth/authorize")
    suspend fun sendAuthJsonApi(
        @Query("client_id") clientId: String = WebConstant.APP_ID,
        @Query("response_type") responseType: String = "code",
        @Query("redirect_uri", encoded = true) redirectUri: String = WebConstant.APP_CALLBACK,
        @Field("formhash") formhash: String,
        @Field("redirect_uri", encoded = true) filedRedirectUri: String = WebConstant.APP_CALLBACK,
        @Field("client_id") fieldClientId: String = WebConstant.APP_ID,
        @Field("submit") submit: String = "授权",
    ): HttpResponse

    /**
     * 获取 Token
     */
    @FormUrlEncoded
    @POST("oauth/access_token")
    suspend fun sendAuthJsonApiToken(
        @Field("code") code: String? = null,
        @Field("grant_type") grantType: String,
        @Field("refresh_token") refreshToken: String? = null,
        @Field("redirect_uri") redirectUri: String = WebConstant.APP_CALLBACK,
        @Field("state") state: String = getTimeMillis().toString(),
        @Field("client_id") clientId: String = WebConstant.APP_ID,
        @Field("client_secret") clientSecret: String = WebConstant.APP_SECRET,
    ): ComposeAuthToken

    /**
     * 贴贴
     */
    @GET("like")
    suspend fun submitReaction(
        @Query("type") @CommentType type: Int,
        @Query("main_id") mainId: Long,
        @Query("id") id: String,
        @Query("value") value: String,
        @Query("gh") gh: String,
        @Query("ajax") ajax: Int = 1,
    ): HttpResponse

    /**
     * 举报
     */
    @GET("/report")
    suspend fun submitReport(
        @Query("id") targetId: Long,
        @Query("type") @ReportType type: Int,
        @Query("value") @ReportValueType value: Int,
        @Query("comment") comment: String,
        @Query("formhash") formhash: String,
        @Query("update") update: String = "报告疑虑",
    ): Document

    /**
     * 保存用户设置
     */
    @POST("settings")
    suspend fun submitUpdateUserInfo(@Body body: MultiPartFormDataContent): Document

    /**
     * 发表评论
     */
    @POST("{action}")
    @FormUrlEncoded
    suspend fun submitNewReply(
        @Path("action", encoded = true) action: String,
        @FieldMap params: Map<String, Any>,
        @Query("ajax") ajax: Int = 1,
    ): ComposeNewReply

    /**
     * 发表Dollars
     */
    @POST("dollars")
    @FormUrlEncoded
    suspend fun summitDollarsChat(
        @Field("message") message: String,
        @Query("ajax") ajax: Int = 1,
    ): ComposeStatus

    /**
     * 上传图片
     */
    @POST("blog/upload_photo")
    suspend fun submitUploadImage(@Body body: MultiPartFormDataContent): ComposeUploadImage

    /**
     * 标记通知已读，
     *
     * @param notification all 则为全部已读，具体 id 就是单条已读
     */
    @GET("erase/notify/{notification}")
    suspend fun submitMarkNotificationRead(
        @Path("notification") notification: String,
        @Query("gh") gh: String,
        @Query("ajax") ajax: Int = 1,
    ): ComposeStatus

    /**
     * 创建或回复短信
     */
    @FormUrlEncoded
    @POST("pm/create.chii")
    suspend fun submitCreateChii(
        @Field("related") related: String,
        @Field("msg_receivers") msgReceivers: String,
        @Field("current_msg_id") currentMsgId: String,
        @Field("formhash") formhash: String,
        @Field("msg_title") msgTitle: String,
        @Field("msg_body") msgBody: String,
        @Field("chat") chat: String? = null,
        @Field("submit") submit: String = "回复",
    ): Document

    /**
     * 删除短信
     */
    @FormUrlEncoded
    @POST("pm/erase/batch")
    suspend fun submitDeleteChii(
        @Field("folder") @MessageBoxType folder: String,
        @Field("erase_pm[]") ids: List<Long>,
        @Query("gh") formhash: String,
    ): Document

    /**
     * 加入小组
     */
    @FormUrlEncoded
    @POST("group/{groupName}/join")
    suspend fun submitJoinGroup(
        @Path("groupName") groupName: String,
        @Query("gh") formHash: String,
        @Field("action") action: String = "join-bye",
    ): Document

    /**
     * 退出小组
     */
    @FormUrlEncoded
    @POST("group/{groupName}/bye")
    suspend fun submitExitGroup(
        @Path("groupName") groupName: String,
        @Query("gh") formHash: String,
        @Field("action") action: String = "join-bye",
    ): Document

    /**
     * 添加目录收藏
     */
    @GET("index/{indexId}/collect")
    suspend fun submitCollectionIndexAdd(
        @Path("indexId") indexId: Long,
        @Query("gh") formHash: String,
    ): HttpResponse

    /**
     * 移除目录收藏
     */
    @GET("index/{indexId}/erase_collect")
    suspend fun submitCollectionIndexRemove(
        @Path("indexId") indexId: Long,
        @Query("gh") formHash: String,
    ): HttpResponse

    /**
     * 移除条目收藏
     */
    @GET("subject/{subjectId}/remove")
    suspend fun submitCollectionSubjectRemove(
        @Path("subjectId") subjectId: Long,
        @Query("gh") formHash: String,
    ): HttpResponse

    /**
     * 移除人物收藏
     */
    @GET("person/{personId}/erase_collect")
    suspend fun submitCollectionPersonRemove(
        @Path("personId") personId: Long,
        @Query("gh") formHash: String,
    ): HttpResponse

    /**
     * 移除角色收藏
     */
    @GET("character/{characterId}/erase_collect")
    suspend fun submitCollectionCharacterRemove(
        @Path("characterId") personId: Long,
        @Query("gh") formHash: String,
    ): HttpResponse

    /**
     * 向目录添加内容
     */
    @GET("index/{indexId}/add_related")
    suspend fun submitIndexRelatedAdd(
        @Path("indexId") indexId: Long,
        @Query("add_related") addRelated: Long,
        @Query("cat") @IndexCatWebTabType cat: Int,
        @Query("gh") formHash: String,
        @Query("referer") referer: String = "ajax",
    ): HttpResponse

}