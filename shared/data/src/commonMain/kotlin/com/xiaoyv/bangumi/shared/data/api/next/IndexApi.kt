package com.xiaoyv.bangumi.shared.data.api.next

import com.xiaoyv.bangumi.shared.core.types.AppJsonApiDsl
import com.xiaoyv.bangumi.shared.core.types.IndexCatType
import com.xiaoyv.bangumi.shared.core.types.IndexOrderType
import com.xiaoyv.bangumi.shared.core.types.IndexType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.data.model.request.bgm.CreateCommentParam
import com.xiaoyv.bangumi.shared.data.model.request.bgm.IndexCreateParam
import com.xiaoyv.bangumi.shared.data.model.request.bgm.UpdateIndexRelatedParam
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeId
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndex
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndexRelated
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse

@AppJsonApiDsl
interface IndexApi {
    /**
     * GET p1/indexes
     * 获取目录列表
     * 全站公开目录列表，支持排序、分页和类型过滤
     *
     * @param order 排序方式：hot&#x3D;按收藏数，latest&#x3D;按创建时间 (optional, default to Order.latest)
     * @param type  (optional)
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/indexes")
    suspend fun getIndexes(
        @Query("order") @IndexOrderType order: String? = null,
        @Query("type") @IndexType type: Int? = null,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0
    ): ComposePage<ComposeIndex>

    /**
     * 创建目录的评论
     */
    @POST("p1/indexes/{indexID}/comments")
    suspend fun createIndexComment(
        @Path("indexID") indexID: Long,
        @Body param: CreateCommentParam? = null
    ): ComposeId

    /**
     * 删除目录的评论
     */
    @DELETE("p1/indexes/-/comments/{commentID}")
    suspend fun deleteIndexComment(@Path("commentID") commentID: Long): HttpResponse

    /**
     * 删除目录
     */
    @DELETE("p1/indexes/{indexID}")
    suspend fun deleteIndex(@Path("indexID") indexID: Long): HttpResponse

    /**
     * 删除目录关联内容
     */
    @DELETE("p1/indexes/{indexID}/related/{id}")
    suspend fun deleteIndexRelated(
        @Path("indexID") indexID: Long,
        @Path("id") id: Long
    ): HttpResponse


    /**
     * 获取目录详情
     *
     * @param indexID
     */
    @GET("p1/indexes/{indexID}")
    suspend fun getIndex(@Path("indexID") indexID: Long): ComposeIndex

    /**
     * 获取目录的评论
     */
    @GET("p1/indexes/{indexID}/comments")
    suspend fun getIndexComments(@Path("indexID") indexID: Long): List<ComposeReply>

    /**
     * 获取目录的关联内容
     *
     * @param indexID
     * @param cat  (optional)
     * @param type  (optional)
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/indexes/{indexID}/related")
    suspend fun getIndexRelated(
        @Path("indexID") indexID: Long,
        @Query("cat") @IndexCatType cat: Int? = null,
        @Query("type") @SubjectType type: Int? = null,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeIndexRelated>

    /**
     * 更新目录关联内容
     */
    @PATCH("p1/indexes/{indexID}/related/{id}")
    suspend fun patchIndexRelated(
        @Path("indexID") indexID: Long,
        @Path("id") id: Long,
        @Body param: UpdateIndexRelatedParam
    ): HttpResponse

    /**
     * 添加目录关联内容
     */
    @PUT("p1/indexes/{indexID}/related")
    suspend fun putIndexRelated(
        @Path("indexID") indexID: Long,
        @Body param: IndexCreateParam
    ): ComposeId
}
