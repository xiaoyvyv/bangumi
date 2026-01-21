package com.xiaoyv.bangumi.shared.data.api

import com.xiaoyv.bangumi.shared.core.types.AppJsonApiDsl
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.data.model.request.CollectionEpisodeUpdate
import com.xiaoyv.bangumi.shared.data.model.request.CollectionSubjectUpdate
import com.xiaoyv.bangumi.shared.data.model.request.IndexBasicInfo
import com.xiaoyv.bangumi.shared.data.model.request.IndexSubjectEditInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeIndex
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeParade
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUser
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse
import org.openapitools.client.models.IndexSubjectAddInfo

/**
 * [BgmJsonApi]
 *
 * @author why
 * @since 2025/1/14
 */
@AppJsonApiDsl
interface BgmJsonApi {
    /**
     * 获取自己信息
     */
    @GET("v0/me")
    suspend fun fetchUserProfile(): ComposeUser

    /**
     * 获取条目巡礼
     */
    @GET("https://api.anitabi.cn/bangumi/{subjectId}/lite")
    suspend fun fetchSubjectParade(@Path("subjectId") subjectId: Long): ComposeParade

    /**
     * Get Index By ID
     *
     * @param indexId 目录 ID
     */
    @GET("v0/indices/{index_id}")
    suspend fun fetchIndexById(@Path("index_id") indexId: Long): ComposeIndex

    /**
     * Get Index Subjects
     *
     * @param indexId 目录 ID
     * @param type 条目类型 (optional)
     * @param limit 分页参数 (optional, default to 30)
     * @param offset 分页参数 (optional, default to 0)
     */
    @GET("v0/indices/{index_id}/subjects")
    suspend fun fetchIndexSubjectsByIndexId(
        @Path("index_id") indexId: Long,
        @Query("type") @SubjectType type: Int? = null,
        @Query("limit") limit: Int? = 30,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeSubject>

    /**
     * Add a subject to Index
     *
     * @param indexId 目录 ID
     * @param indexSubjectAddInfo  (optional)
     */
    @POST("v0/indices/{index_id}/subjects")
    suspend fun submitSubjectToIndexByIndexId(
        @Path("index_id") indexId: Long,
        @Body indexSubjectAddInfo: IndexSubjectAddInfo? = null,
    ): HttpResponse

    /**
     * Delete a subject from a Index
     *
     * @param indexId 目录 ID
     * @param subjectId 条目 ID
     */
    @DELETE("v0/indices/{index_id}/subjects/{subject_id}")
    suspend fun submitDeleteSubjectFromIndexByIndexIdAndSubjectID(
        @Path("index_id") indexId: Long,
        @Path("subject_id") subjectId: Int,
    ): HttpResponse

    /**
     * 为当前用户收藏一条目录
     *
     * @param indexId 目录 ID
     */
    @POST("v0/indices/{index_id}/collect")
    suspend fun submitBookmarkAddIndex(@Path("index_id") indexId: Long): HttpResponse

    /**
     * 为当前用户取消收藏一条目录
     *
     * @param indexId 目录 ID
     */
    @DELETE("v0/indices/{index_id}/collect")
    suspend fun submitBookmarkRemoveIndex(@Path("index_id") indexId: Long): HttpResponse

    /**
     * Edit index's information
     *
     * @param indexId 目录 ID
     * @param indexBasicInfo  (optional)
     */
    @PUT("v0/indices/{index_id}")
    suspend fun submitEditIndexById(
        @Path("index_id") indexId: Long,
        @Body indexBasicInfo: IndexBasicInfo? = null,
    ): HttpResponse

    /**
     * Edit subject information in a index
     * 如果条目不存在于目录，会创建该条目
     *
     * @param indexId 目录 ID
     * @param subjectId 条目 ID
     * @param indexSubjectEditInfo  (optional)
     */
    @PUT("v0/indices/{index_id}/subjects/{subject_id}")
    suspend fun submitEditIndexSubjectsByIndexIdAndSubjectID(
        @Path("index_id") indexId: Long,
        @Path("subject_id") subjectId: Long,
        @Body indexSubjectEditInfo: IndexSubjectEditInfo? = null,
    ): HttpResponse


    /**
     * 批量 章节收藏信息
     * 同时会重新计算条目的完成度
     *
     * @param subjectId 条目 ID
     * @param body  (optional)
     */
    @PATCH("v0/users/-/collections/{subject_id}/episodes")
    suspend fun submitUpdateUserEpisodeBatch(
        @Path("subject_id") subjectId: Long,
        @Body body: CollectionEpisodeUpdate? = null,
    ): HttpResponse

    /**
     * 更新单个 章节收藏信息
     *
     * @param episodeId 章节 ID
     * @param body  (optional)
     */
    @PUT("v0/users/-/collections/-/episodes/{episode_id}")
    suspend fun submitUpdateUserEpisode(
        @Path("episode_id") episodeId: Long,
        @Body body: CollectionEpisodeUpdate? = null,
    ): HttpResponse

    /**
     * 新增或修改用户单个条目收藏
     *
     * 修改条目收藏状态, 如果不存在则创建，如果存在则修改
     * 由于直接修改剧集条目的完成度可能会引起意料之外效果，只能用于修改书籍类条目的完成度。
     *
     * 方法的所有请求体字段均可选
     *
     * @param subjectId 条目 ID
     * @param body  (optional)
     */
    @POST("v0/users/-/collections/{subject_id}")
    suspend fun submitUserSubjectCollection(
        @Path("subject_id") subjectId: Long,
        @Body body: CollectionSubjectUpdate? = null,
    ): HttpResponse
}