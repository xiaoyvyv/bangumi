package com.xiaoyv.bangumi.shared.data.api.next

import com.xiaoyv.bangumi.shared.core.types.AppJsonApiDsl
import com.xiaoyv.bangumi.shared.data.model.request.CreateBlogCommentRequest
import com.xiaoyv.bangumi.shared.data.model.request.LikeEpisodeCommentRequest
import com.xiaoyv.bangumi.shared.data.model.request.UpdateContent
import com.xiaoyv.bangumi.shared.data.model.request.UpdateSubjectTopicRequest
import com.xiaoyv.bangumi.shared.data.model.request.UpdateTopic
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.topic.ComposeTopic
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.client.statement.HttpResponse

@AppJsonApiDsl
interface TopicApi {

    /**
     * 创建小组话题回复
     */
    @POST("p1/groups/-/topics/{topicID}/replies")
    suspend fun createGroupReply(
        @Path("topicID") topicID: Long,
        @Body createBlogCommentRequest: CreateBlogCommentRequest? = null
    ): HttpResponse

    /**
     * 创建条目讨论回复
     */
    @POST("p1/subjects/-/topics/{topicID}/replies")
    suspend fun createSubjectReply(
        @Path("topicID") topicID: Long,
        @Body createBlogCommentRequest: CreateBlogCommentRequest? = null
    ): HttpResponse

    /**
     * 删除小组话题回复
     */
    @DELETE("p1/groups/-/posts/{postID}")
    suspend fun deleteGroupPost(@Path("postID") postID: Long): HttpResponse

    /**
     * 删除条目讨论回复
     */
    @DELETE("p1/subjects/-/posts/{postID}")
    suspend fun deleteSubjectPost(@Path("postID") postID: Long): HttpResponse

    /**
     * 编辑小组话题回复
     */
    @PUT("p1/groups/-/posts/{postID}")
    suspend fun editGroupPost(
        @Path("postID") postID: Long,
        @Body updateContent: UpdateContent? = null
    ): HttpResponse

    /**
     * 编辑小组话题
     */
    @PUT("p1/groups/-/topics/{topicID}")
    suspend fun editGroupTopic(
        @Path("topicID") topicID: Long,
        @Body updateTopic: UpdateTopic? = null
    ): HttpResponse

    /**
     * 编辑条目讨论回复
     */
    @PUT("p1/subjects/-/posts/{postID}")
    suspend fun editSubjectPost(
        @Path("postID") postID: Long,
        @Body updateContent: UpdateContent? = null
    ): HttpResponse

    /**
     * 获取小组话题回复详情
     */
    @GET("p1/groups/-/posts/{postID}")
    suspend fun getGroupPost(@Path("postID") postID: Long): ComposeReply

    /**
     * 获取小组话题详情
     */
    @GET("p1/groups/-/topics/{topicID}")
    suspend fun getGroupTopic(@Path("topicID") topicID: Long): ComposeTopic

    /**
     * 获取条目讨论回复详情
     */
    @GET("p1/subjects/-/posts/{postID}")
    suspend fun getSubjectPost(@Path("postID") postID: Long): ComposeReply

    /**
     * 获取条目讨论详情
     */
    @GET("p1/subjects/-/topics/{topicID}")
    suspend fun getSubjectTopic(@Path("topicID") topicID: Long): ComposeTopic

    /**
     * 给小组话题回复点赞
     */
    @PUT("p1/groups/-/posts/{postID}/like")
    suspend fun likeGroupPost(
        @Path("postID") postID: Long,
        @Body likeEpisodeCommentRequest: LikeEpisodeCommentRequest
    ): HttpResponse

    /**
     * 给条目讨论回复点赞
     */
    @PUT("p1/subjects/-/posts/{postID}/like")
    suspend fun likeSubjectPost(
        @Path("postID") postID: Long,
        @Body likeEpisodeCommentRequest: LikeEpisodeCommentRequest
    ): HttpResponse

    /**
     * 取消小组话题回复点赞
     */
    @DELETE("p1/groups/-/posts/{postID}/like")
    @Headers("Content-Type: */*")
    suspend fun unlikeGroupPost(@Path("postID") postID: Long): HttpResponse

    /**
     * 取消条目讨论回复点赞
     */
    @DELETE("p1/subjects/-/posts/{postID}/like")
    @Headers("Content-Type: */*")
    suspend fun unlikeSubjectPost(@Path("postID") postID: Long): HttpResponse

    /**
     * 编辑自己创建的条目讨论
     */
    @PUT("p1/subjects/-/topics/{topicID}")
    suspend fun updateSubjectTopic(
        @Path("topicID") topicID: Long,
        @Body updateSubjectTopicRequest: UpdateSubjectTopicRequest
    ): HttpResponse
}
