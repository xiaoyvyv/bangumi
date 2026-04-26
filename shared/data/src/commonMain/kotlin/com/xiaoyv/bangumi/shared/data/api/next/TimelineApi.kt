package com.xiaoyv.bangumi.shared.data.api.next

import com.xiaoyv.bangumi.shared.core.types.AppJsonApiDsl
import com.xiaoyv.bangumi.shared.core.types.TimelineTarget
import com.xiaoyv.bangumi.shared.core.types.TimelineCat
import com.xiaoyv.bangumi.shared.data.model.request.CreateBlogCommentRequest
import com.xiaoyv.bangumi.shared.data.model.request.CreateTimelineSayRequest
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimeline
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse

@AppJsonApiDsl
interface TimelineApi {
    /**
     * 创建时间线回复
     *
     * @param timelineID
     * @param createBlogCommentRequest  (optional)
     */
    @POST("p1/timeline/{timelineID}/replies")
    suspend fun createTimelineReply(
        @Path("timelineID") timelineID: Int,
        @Body createBlogCommentRequest: CreateBlogCommentRequest? = null,
    ): HttpResponse

    /**
     * 发送时间线吐槽
     *
     * @param createTimelineSayRequest  (optional)
     */
    @POST("p1/timeline")
    suspend fun createTimelineSay(@Body createTimelineSayRequest: CreateTimelineSayRequest? = null): HttpResponse

    /**
     * 删除时间线
     *
     * @param timelineID
     */
    @DELETE("p1/timeline/{timelineID}")
    suspend fun deleteTimeline(@Path("timelineID") timelineID: Int): HttpResponse

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
     * 获取时间线回复
     *
     * @param timelineID
     */
    @GET("p1/timeline/{timelineID}/replies")
    suspend fun getTimelineReplies(@Path("timelineID") timelineID: Int): List<ComposeReply>
}
