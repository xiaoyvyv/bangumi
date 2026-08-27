package com.xiaoyv.bangumi.shared.data.api.next

import com.xiaoyv.bangumi.shared.core.types.AppJsonApiDsl
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.types.TimelineCat
import com.xiaoyv.bangumi.shared.data.model.request.bgm.CreateReportParam
import com.xiaoyv.bangumi.shared.data.model.request.bgm.NextWebLoginParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogEntry
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEmptyBody
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroup
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMono
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndex
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimeline
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeNotice
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUserPrivacy
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

@AppJsonApiDsl
interface UserApi {

    /**
     * Next 站点登录
     *
     * 需要 [turnstile](https://developers.cloudflare.com/turnstile/get-started/client-side-rendering/)  next.bgm.tv 域名对应的 site-key 为 &#x60;0x4AAAAAAABkMYinukE8nzYS&#x60;  dev.bgm38.tv 域名使用测试用的 site-key &#x60;1x00000000000000000000AA&#x60;
     */
    @POST("p1/login")
    suspend fun login(@Body loginRequestBody: NextWebLoginParam): ComposeUser

    /**
     * 获取自己信息
     */
    @GET("p1/me")
    suspend fun getMe(): ComposeUser

    /**
     * 获取用户信息
     *
     * @param username
     */
    @GET("p1/users/{username}")
    suspend fun getUser(@Path("username", encoded = true) username: String): ComposeUser

    /**
     * 获取用户创建的日志
     *
     * @param username
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/users/{username}/blogs")
    suspend fun getUserBlogs(
        @Path("username", encoded = true) username: String,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeBlogEntry>

    /**
     * 获取用户角色收藏
     * @param username
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/users/{username}/collections/characters")
    suspend fun getUserCharacterCollections(
        @Path("username", encoded = true) username: String,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeMono>

    /**
     * 获取用户的关注者列表
     *
     * @param username
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/users/{username}/followers")
    suspend fun getUserFollowers(
        @Path("username", encoded = true) username: String,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeUser>

    /**
     * 获取用户的好友列表
     *
     * @param username
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/users/{username}/friends")
    suspend fun getUserFriends(
        @Path("username", encoded = true) username: String,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeUser>

    /**
     * 获取用户加入的小组
     *
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/users/{username}/groups")
    suspend fun getUserGroups(
        @Path("username", encoded = true) username: String,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeGroup>

    /**
     * 获取用户目录收藏
     *
     * @param username
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/users/{username}/collections/indexes")
    suspend fun getUserIndexCollections(
        @Path("username", encoded = true) username: String,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeIndex>

    /**
     * 获取用户创建的目录
     *
     * @param username
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/users/{username}/indexes")
    suspend fun getUserIndexes(
        @Path("username", encoded = true) username: String,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeIndex>

    /**
     * 获取用户人物收藏
     *
     * @param username
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/users/{username}/collections/persons")
    suspend fun getUserPersonCollections(
        @Path("username", encoded = true) username: String,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeMono>

    /**
     * 获取用户条目收藏
     *
     * @param username
     * @param subjectType  (optional)
     * @param type  (optional)
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/users/{username}/collections/subjects")
    suspend fun getUserSubjectCollections(
        @Path("username", encoded = true) username: String,
        @Query("subjectType") @SubjectType subjectType: Int? = null,
        @Query("type") @CollectionType type: Int? = null,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeSubject>

    /**
     * 获取用户时间胶囊
     *
     * @param username
     * @param limit min 1, max 20 (optional, default to 20)
     * @param until max timeline id to fetch from (optional)
     */
    @GET("p1/users/{username}/timeline")
    suspend fun getUserTimeline(
        @Path("username", encoded = true) username: String,
        @Query("cat") @TimelineCat cat: Int? = null,
        @Query("limit") limit: Int? = 20,
        @Query("until") until: Long? = null,
    ): List<ComposeTimeline>

    /**
     * 报告疑虑
     *
     * @param param
     */
    @POST("p1/report")
    suspend fun createReport(@Body param: CreateReportParam): ComposeEmptyBody

    /**
     * Get current user privacy settings
     */
    @GET("p1/privacy")
    suspend fun getPrivacy(): ComposeUserPrivacy

    /**
     * Update current user privacy settings
     */
    @PATCH("p1/privacy")
    suspend fun patchPrivacy(@Body param: ComposeUserPrivacy): ComposeUserPrivacy

    /**
     * 获取未读通知
     *
     * @param limit max=40
     */
    @GET("p1/notify")
    suspend fun listNotice(
        @Query("unread") unread: Boolean? = null,
        @Query("limit") limit: Int = 40,
    ): ComposePage<ComposeNotice>
}
