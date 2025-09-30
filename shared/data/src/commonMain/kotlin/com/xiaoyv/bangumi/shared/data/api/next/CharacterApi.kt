package com.xiaoyv.bangumi.shared.data.api.next

import com.xiaoyv.bangumi.shared.core.types.AppJsonApiDsl
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.data.model.request.CreateBlogCommentRequest
import com.xiaoyv.bangumi.shared.data.model.request.UpdateContent
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeCommentReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMono
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeStatus
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUserDisplay
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

@AppJsonApiDsl
interface CharacterApi {
    /**
     * 创建角色的吐槽
     *
     * @param characterID
     * @param createBlogCommentRequest  (optional)
     */
    @POST("p1/characters/{characterID}/comments")
    suspend fun createCharacterComment(
        @Path("characterID") characterID: Int,
        @Body createBlogCommentRequest: CreateBlogCommentRequest? = null,
    ): ComposeCommentReply

    /**
     * 删除角色的吐槽
     *
     * @param commentID
     */
    @DELETE("p1/characters/-/comments/{commentID}")
    suspend fun deleteCharacterComment(@Path("commentID") commentID: Int): ComposeCommentReply

    /**
     * 获取角色
     *
     * @param characterID
     */
    @GET("p1/characters/{characterID}")
    suspend fun getCharacter(@Path("characterID") characterID: Long): ComposeMono

    /**
     * 获取角色出演作品
     *
     * @param characterID
     * @param subjectType  (optional)
     * @param type 角色出场类型: 主角，配角，客串 (optional)
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/characters/{characterID}/casts")
    suspend fun getCharacterCasts(
        @Path("characterID") characterID: Long,
        @Query("subjectType") @SubjectType subjectType: Int? = null,
        @Query("type") type: Int? = null,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeMonoInfo>

    /**
     * 获取角色的收藏用户
     *
     * @param characterID
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/characters/{characterID}/collects")
    suspend fun getCharacterCollects(
        @Path("characterID") characterID: Long,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeUserDisplay>

    /**
     * 获取角色的吐槽箱
     *
     * @param characterID
     */
    @GET("p1/characters/{characterID}/comments")
    suspend fun getCharacterComments(@Path("characterID") characterID: Int): ComposeCommentReply

    /**
     * 编辑角色的吐槽
     *
     * @param commentID
     * @param updateContent  (optional)
     */
    @PUT("p1/characters/-/comments/{commentID}")
    suspend fun updateCharacterComment(@Path("commentID") commentID: Int, @Body updateContent: UpdateContent? = null): ComposeStatus
}
