package com.xiaoyv.bangumi.shared.data.api.next

import com.xiaoyv.bangumi.shared.core.types.AppJsonApiDsl
import com.xiaoyv.bangumi.shared.data.model.request.CreateBlogCommentRequest
import com.xiaoyv.bangumi.shared.data.model.request.UpdateContent
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogEntry
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse

@AppJsonApiDsl
interface BlogApi {
    /**
     * 创建日志的吐槽
     */
    @POST("p1/blogs/{entryID}/comments")
    suspend fun createBlogComment(
        @Path("entryID") entryID: Long,
        @Body createBlogCommentRequest: CreateBlogCommentRequest? = null
    ): HttpResponse

    /**
     * 删除日志的吐槽
     */
    @DELETE("p1/blogs/-/comments/{commentID}")
    suspend fun deleteBlogComment(@Path("commentID") commentID: Long): HttpResponse

    /**
     * 获取日志的吐槽箱
     */
    @GET("p1/blogs/{entryID}/comments")
    suspend fun getBlogComments(@Path("entryID") entryID: Long): HttpResponse

    /**
     * 获取日志详情
     */
    @GET("p1/blogs/{entryID}")
    suspend fun getBlogEntry(@Path("entryID") entryID: Long): ComposeBlogEntry

    /**
     * 获取日志的图片
     */
    @GET("p1/blogs/{entryID}/photos")
    suspend fun getBlogPhotos(
        @Path("entryID") entryID: Long,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0
    ): HttpResponse

    /**
     * 获取日志的关联条目
     */
    @GET("p1/blogs/{entryID}/subjects")
    suspend fun getBlogRelatedSubjects(@Path("entryID") entryID: Long): List<ComposeSubject>

    /**
     * 编辑日志的吐槽
     */
    @PUT("p1/blogs/-/comments/{commentID}")
    suspend fun updateBlogComment(
        @Path("commentID") commentID: Long,
        @Body updateContent: UpdateContent? = null
    ): HttpResponse
}
