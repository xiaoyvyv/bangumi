@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.api

import com.fleeksoft.ksoup.nodes.Document
import com.xiaoyv.bangumi.shared.core.types.AppWebApiDsl
import com.xiaoyv.bangumi.shared.core.types.SubjectWebPath
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeDollarItem
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeFriend
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

    /**
     * 主页
     */
    @GET("/")
    suspend fun fetchMain(): Document

    /**
     * 获取网页登录表单
     */
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
     * 获取用户网络服务设置
     */
    @GET("settings/network_services")
    suspend fun fetchUserEditServicesInfo(): Document

    /**
     * 获取用户通知未读数目
     */
    @GET("json/notify")
    suspend fun fetchUserUnreadNotification(@Query("_") timestamp: Long): Document

    /**
     * 获取用户的会话列表
     */
    @GET("pm/inbox.chii")
    suspend fun fetchUserPmCoversation(@Query("page") page: Int): Document

    /**
     * 获取用户的会话详情
     */
    @GET("pm/conversation/{id}.chii")
    suspend fun fetchUserPmMessage(@Path("id") id: Long, @Query("thread") thread: Long? = null): Document

    /**
     * 获取条目详情
     */
    @GET("subject/{subjectId}")
    suspend fun fetchSubjectDetail(@Path("subjectId") subjectId: Long): Document

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
     * 获取人物详情信息
     */
    @GET("person/{monoId}")
    suspend fun fetchPersonDetail(@Path("monoId") monoId: Long): Document

    /**
     * 获取人物作品的职位过滤项
     */
    @GET("person/{monoId}/works")
    suspend fun fetchPersonWorkPosition(@Path("monoId") monoId: Long): Document


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
     * 小组主页
     */
    @GET("group/discover")
    suspend fun fetchGroupHomepage(): Document

    /**
     * 我的朋友列表
     */
    @GET("ajax/buddy_search")
    suspend fun fetchMyFriends(): List<ComposeFriend>

    /**
     * 用户页面
     */
    @GET("user/{username}")
    suspend fun fetchUserHomepage(@Path("username", encoded = true) username: String): Document

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
        @Field("client_id") fieldClientId: String = WebConstant.APP_ID,
        @Field("submit") submit: String = "授权",
    ): HttpResponse

    /**
     * 保存用户设置
     */
    @POST("settings")
    suspend fun submitUpdateUserInfo(@Body body: MultiPartFormDataContent): Document

    /**
     * 保存用户网络服务设置
     */
    @POST("settings/network_services")
    suspend fun submitUpdateUserServicesInfo(@Body body: MultiPartFormDataContent): Document

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
     * 创建或回复短信
     *
     * - formhash	"a351f454"
     * - msg_receivers	"whystart"
     * - related	"410403"
     * - msg_body	"消息内容"
     * - submit	"发送"
     *
     * 新建话题才传值
     *
     * - new_topic	"1"
     * - msg_title	"话题内容"
     */
    @FormUrlEncoded
    @POST("pm/create.chii")
    suspend fun submitCreateChii(@FieldMap param: Map<String, String>): Document

    /**
     * 删除短信
     */
    @FormUrlEncoded
    @POST("pm/erase/batch")
    suspend fun submitDeleteChii(
        @Field("folder") folder: String,
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
     * 移除时间线
     */
    @GET("erase/tml/{timelineId}")
    suspend fun submitDeleteTimeline(
        @Path("timelineId") timelineId: Long,
        @Query("gh") gh: String,
        @Query("ajax") ajax: Int = 1
    ): Document
}
