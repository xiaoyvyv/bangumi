package com.xiaoyv.bangumi.shared.data.api.next

import com.xiaoyv.bangumi.shared.core.types.AppJsonApiDsl
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.data.model.request.CollectSubject
import com.xiaoyv.bangumi.shared.data.model.request.UpdateEpisodeProgress
import com.xiaoyv.bangumi.shared.data.model.request.UpdateSubjectProgress
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeCollection
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEmptyBody
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse

@AppJsonApiDsl
interface CollectionApi {
    /**
     * 新增角色收藏
     *
     * @param characterID
     */
    @PUT("p1/collections/characters/{characterID}")
    suspend fun addCharacterCollection(
        @Path("characterID") characterID: Long,
        @Body body: ComposeEmptyBody = ComposeEmptyBody.Empty,
    ): HttpResponse

    /**
     * 新增目录收藏
     *
     * @param indexID
     */
    @PUT("p1/collections/indexes/{indexID}")
    suspend fun addIndexCollection(@Path("indexID") indexID: Int): HttpResponse

    /**
     * 新增人物收藏
     *
     *
     * @param personID
     */
    @PUT("p1/collections/persons/{personID}")
    suspend fun addPersonCollection(
        @Path("personID") personID: Long,
        @Body body: ComposeEmptyBody = ComposeEmptyBody.Empty,
    ): HttpResponse

    /**
     * 删除角色收藏
     *
     * @param characterID
     */
    @DELETE("p1/collections/characters/{characterID}")
    suspend fun deleteCharacterCollection(
        @Path("characterID") characterID: Long,
        @Body body: ComposeEmptyBody = ComposeEmptyBody.Empty,
    ): HttpResponse

    /**
     * 删除目录收藏
     *
     * @param indexID
     */
    @DELETE("p1/collections/indexes/{indexID}")
    suspend fun deleteIndexCollection(@Path("indexID") indexID: Int): HttpResponse

    /**
     * 删除人物收藏
     *
     * @param personID
     */
    @DELETE("p1/collections/persons/{personID}")
    suspend fun deletePersonCollection(
        @Path("personID") personID: Long,
        @Body body: ComposeEmptyBody = ComposeEmptyBody.Empty,
    ): HttpResponse

    /**
     * 获取当前用户的角色收藏
     *
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/collections/characters")
    suspend fun getMyCharacterCollections(
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeCollection>

    /**
     * 获取当前用户的目录收藏
     *
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/collections/indexes")
    suspend fun getMyIndexCollections(
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeCollection>

    /**
     * 获取当前用户的人物收藏
     *
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/collections/persons")
    suspend fun getMyPersonCollections(
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeCollection>

    /**
     * 获取当前用户的条目收藏
     *
     * @param subjectType  (optional)
     * @param type  (optional)
     * @param since 起始时间戳 (optional)
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/collections/subjects")
    suspend fun getMySubjectCollections(
        @Query("subjectType") @SubjectType subjectType: Int? = null,
        @Query("type") @CollectionType type: Int? = null,
        @Query("since") since: Int? = null,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeSubject>

    /**
     * 更新章节进度
     *
     * @param episodeID
     * @param updateEpisodeProgress  (optional)
     */
    @PATCH("p1/collections/episodes/{episodeID}")
    suspend fun updateEpisodeProgress(
        @Path("episodeID") episodeID: Long,
        @Body updateEpisodeProgress: UpdateEpisodeProgress? = null,
    ): HttpResponse

    /**
     * 新增或修改条目收藏
     *
     * @param subjectID
     * @param collectSubject  (optional)
     */
    @PUT("p1/collections/subjects/{subjectID}")
    suspend fun updateSubjectCollection(
        @Path("subjectID") subjectID: Int,
        @Body collectSubject: CollectSubject? = null,
    ): HttpResponse

    /**
     * 更新条目进度
     *
     * @param subjectID
     * @param updateSubjectProgress  (optional)
     */
    @PATCH("p1/collections/subjects/{subjectID}")
    suspend fun updateSubjectProgress(
        @Path("subjectID") subjectID: Int,
        @Body updateSubjectProgress: UpdateSubjectProgress? = null,
    ): HttpResponse
}
