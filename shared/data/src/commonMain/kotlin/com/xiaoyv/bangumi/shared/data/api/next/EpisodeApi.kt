package com.xiaoyv.bangumi.shared.data.api.next

import com.xiaoyv.bangumi.shared.core.types.AppJsonApiDsl
import com.xiaoyv.bangumi.shared.data.model.request.bgm.CreateCommentParam
import com.xiaoyv.bangumi.shared.data.model.request.bgm.LikeCommentParam
import com.xiaoyv.bangumi.shared.data.model.request.bgm.UpdateContent
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeId
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisode
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.client.statement.HttpResponse

@AppJsonApiDsl
interface EpisodeApi {
    /**
     * 创建条目的剧集吐槽
     *
     * @param episodeID
     * @param param (optional)
     */
    @POST("p1/episodes/{episodeID}/comments")
    suspend fun createEpisodeComment(
        @Path("episodeID") episodeID: Long,
        @Body param: CreateCommentParam? = null,
    ): ComposeId

    /**
     * 删除条目的剧集吐槽
     *
     * @param commentID
     */
    @DELETE("p1/episodes/-/comments/{commentID}")
    suspend fun deleteEpisodeComment(@Path("commentID") commentID: Int): HttpResponse

    /**
     * 获取剧集信息
     *
     * @param episodeID
     */
    @GET("p1/episodes/{episodeID}")
    suspend fun getEpisode(@Path("episodeID") episodeID: Long): ComposeEpisode

    /**
     * 获取条目的剧集吐槽箱
     *
     * @param episodeID
     */
    @GET("p1/episodes/{episodeID}/comments")
    suspend fun getEpisodeComments(@Path("episodeID") episodeID: Long): List<ComposeReply>

    /**
     * 给条目的剧集吐槽点赞
     *
     * @param commentID
     * @param param
     */
    @PUT("p1/episodes/-/comments/{commentID}/like")
    suspend fun likeEpisodeComment(
        @Path("commentID") commentID: Long,
        @Body param: LikeCommentParam,
    ): HttpResponse

    /**
     * 取消条目的剧集吐槽点赞
     *
     * @param commentID
     */
    @DELETE("p1/episodes/-/comments/{commentID}/like")
    suspend fun unlikeEpisodeComment(@Path("commentID") commentID: Long): HttpResponse

    /**
     * 编辑条目的剧集吐槽
     *
     * @param commentID
     * @param updateContent  (optional)
     */
    @PUT("p1/episodes/-/comments/{commentID}")
    suspend fun updateEpisodeComment(
        @Path("commentID") commentID: Int,
        @Body updateContent: UpdateContent? = null,
    ): HttpResponse
}
