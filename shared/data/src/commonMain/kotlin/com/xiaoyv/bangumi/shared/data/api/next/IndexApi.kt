package com.xiaoyv.bangumi.shared.data.api.next

import com.xiaoyv.bangumi.shared.core.types.AppJsonApiDsl
import com.xiaoyv.bangumi.shared.core.types.IndexCatType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.data.model.request.CreateCommentParam
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeId
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndex
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndexRelated
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

@AppJsonApiDsl
interface IndexApi {
    /**
     * 创建目录的评论
     */
    @POST("p1/indexes/{indexID}/comments")
    suspend fun createIndexComment(
        @Path("indexID") indexID: Long,
        @Body param: CreateCommentParam? = null
    ): ComposeId

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
}
