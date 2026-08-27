package com.xiaoyv.bangumi.shared.data.api.next

import com.xiaoyv.bangumi.shared.core.types.AppJsonApiDsl
import com.xiaoyv.bangumi.shared.core.types.TimelineCat
import com.xiaoyv.bangumi.shared.core.types.TimelineTarget
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.data.model.request.bgm.CreateCommentParam
import com.xiaoyv.bangumi.shared.data.model.request.bgm.LikeCommentParam
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeId
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimeline
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse

@AppJsonApiDsl
interface TimelineApi {
    /**
     * 创建时间线回复
     *
     * @param timelineID
     * @param param (optional)
     */
    @POST("p1/timeline/{timelineID}/replies")
    suspend fun createTimelineReply(
        @Path("timelineID") timelineID: Int,
        @Body param: CreateCommentParam? = null,
    ): ComposeId

    /**
     * 发送时间线吐槽
     *
     * @param param  (optional)
     */
    @POST("p1/timeline")
    suspend fun createTimelineSay(@Body param: CreateCommentParam): ComposeId

    /**
     * 删除时间线
     *
     * @param timelineID
     */
    @DELETE("p1/timeline/{timelineID}")
    suspend fun deleteTimeline(@Path("timelineID") timelineID: Long): HttpResponse

    /**
     * 获取时间线
     *
     * @param mode 登录时默认为 friends, 未登录或没有好友时始终为 all (optional)
     * @param limit min 1, max 20 (optional, default to 20)
     * @param until max timeline id to fetch from (optional)
     */
    @GET("p1/timeline")
    suspend fun getTimeline(
        @Query("mode") @TimelineTarget mode: String? = null,
        @Query("cat") @TimelineCat cat: Int? = null,
        @Query("limit") limit: Int? = 20,
        @Query("until") until: Long? = null,
    ): List<ComposeTimeline>

    /**
     * 获取时间线 - Web 解析版本
     *
     * @param type 网页版本的时间线对应的类型
     * @param mode 登录时默认为 friends, 未登录或没有好友时始终为 all (optional)
     */
    @GET("${WebConstant.URL_BGM_PROXY}p1/timeline")
    suspend fun getTimelineWebApi(
        @Query("mode") @TimelineTarget mode: String,
        @Query("type") type: String? = null,
        @Query("username") username: String? = null,
        @Query("page") page: Int = 1,
    ): List<ComposeTimeline>

    /**
     * 获取时间线回复
     *
     * @param timelineID
     */
    @GET("p1/timeline/{timelineID}/replies")
    suspend fun getTimelineReplies(@Path("timelineID") timelineID: Int): List<ComposeReply>


    /**
     * 给时间线吐槽点赞
     *
     * @param timelineID
     * @param param
     */
    @PUT("p1/timeline/{timelineID}/like")
    suspend fun likeTimeline(@Path("timelineID") timelineID: Long, @Body param: LikeCommentParam): HttpResponse

    /**
     * 取消时间线吐槽点赞
     *
     * @param timelineID
     */
    @DELETE("p1/timeline/{timelineID}/like")
    suspend fun unlikeTimeline(@Path("timelineID") timelineID: Long): HttpResponse
}
