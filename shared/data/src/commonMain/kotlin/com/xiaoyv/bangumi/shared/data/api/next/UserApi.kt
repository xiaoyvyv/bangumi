package com.xiaoyv.bangumi.shared.data.api.next

import com.xiaoyv.bangumi.shared.core.types.AppJsonApiDsl
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogEntry
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroup
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeIndex
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMono
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUser
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.serialization.json.JsonElement

@AppJsonApiDsl
interface UserApi {
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
    ): ComposePage<JsonElement>

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
        @Query("limit") limit: Int? = 20,
        @Query("until") until: Int? = null,
    ): ComposePage<JsonElement>
}
