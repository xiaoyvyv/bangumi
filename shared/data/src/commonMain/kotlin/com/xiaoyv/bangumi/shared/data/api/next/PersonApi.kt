package com.xiaoyv.bangumi.shared.data.api.next

import com.xiaoyv.bangumi.shared.core.types.AppJsonApiDsl
import com.xiaoyv.bangumi.shared.core.types.PersonPositionType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.data.model.request.CreateBlogCommentRequest
import com.xiaoyv.bangumi.shared.data.model.request.UpdateContent
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeCommentReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMono
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeStatus
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSubjectDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUserDisplay
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

@AppJsonApiDsl
interface PersonApi {
    /**
     * 创建人物的吐槽
     *
     * @param personID
     * @param createBlogCommentRequest  (optional)
     */
    @POST("p1/persons/{personID}/comments")
    suspend fun createPersonComment(
        @Path("personID") personID: Int,
        @Body createBlogCommentRequest: CreateBlogCommentRequest? = null,
    ): ComposeCommentReply

    /**
     * 删除人物的吐槽
     *
     * @param commentID
     */
    @DELETE("p1/persons/-/comments/{commentID}")
    suspend fun deletePersonComment(@Path("commentID") commentID: Int): ComposeStatus

    /**
     * 获取人物
     *
     * @param personID
     */
    @GET("p1/persons/{personID}")
    suspend fun getPerson(@Path("personID") personID: Long): ComposeMono

    /**
     * 获取人物的出演角色
     *
     * @param personID
     * @param subjectType  (optional)
     * @param type 角色出场类型: 主角，配角，客串 (optional)
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/persons/{personID}/casts")
    suspend fun getPersonCasts(
        @Path("personID") personID: Long,
        @Query("subjectType") @SubjectType subjectType: Int? = null,
        @Query("type") type: Int? = null,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeMonoInfo>

    /**
     * 获取人物的收藏用户
     *
     * @param personID
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/persons/{personID}/collects")
    suspend fun getPersonCollects(
        @Path("personID") personID: Long,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeUserDisplay>

    /**
     * 获取人物的吐槽箱
     *
     * @param personID
     */
    @GET("p1/persons/{personID}/comments")
    suspend fun getPersonComments(@Path("personID") personID: Int): List<ComposeCommentReply>

    /**
     * 获取人物的参与作品
     *
     * @param personID
     * @param subjectType  (optional)
     * @param position 职位 (optional)
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/persons/{personID}/works")
    suspend fun getPersonWorks(
        @Path("personID") personID: Long,
        @Query("subjectType") @SubjectType subjectType: Int? = null,
        @Query("position") @PersonPositionType position: Long? = null,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeSubjectDisplay>

    /**
     * 编辑人物的吐槽
     *
     * @param commentID
     * @param updateContent  (optional)
     */
    @PUT("p1/persons/-/comments/{commentID}")
    suspend fun updatePersonComment(@Path("commentID") commentID: Int, @Body updateContent: UpdateContent? = null): ComposeStatus

}
