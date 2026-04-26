package com.xiaoyv.bangumi.shared.data.api.next

import com.xiaoyv.bangumi.shared.core.types.AppJsonApiDsl
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.EpisodeType
import com.xiaoyv.bangumi.shared.core.types.ModeType
import com.xiaoyv.bangumi.shared.core.types.SubjectSortBrowserType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.data.model.request.CreateGroupTopicRequest
import com.xiaoyv.bangumi.shared.data.model.request.LikeEpisodeCommentRequest
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlogDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisode
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeHomeSection
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeStatus
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.topic.ComposeTopic
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUserDisplay
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.serialization.json.JsonObject
import kotlin.jvm.JvmSuppressWildcards

@AppJsonApiDsl
interface SubjectApi {
    /**
     * 日历
     */
    @GET("p1/calendar")
    suspend fun getCalendar(): Map<String, List<ComposeHomeSection>>

    /**
     * 获取热门条目
     */
    @GET("p1/trending/subjects")
    suspend fun getTrends(
        @Query("type") @SubjectType type: Int,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 20,
    ): ComposePage<ComposeHomeSection>

    /**
     * 创建条目讨论
     *
     * @param subjectID
     * @param createGroupTopicRequest  (optional)
     */
    @POST("p1/subjects/{subjectID}/topics")
    suspend fun createSubjectTopic(
        @Path("subjectID") subjectID: Int,
        @Body createGroupTopicRequest: CreateGroupTopicRequest? = null,
    ): ComposeTopic

    /**
     * 获取最新的条目讨论
     *
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/subjects/-/topics")
    suspend fun getRecentSubjectTopics(
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeTopic>

    /**
     * 获取条目
     *
     * @param subjectID
     */
    @GET("p1/subjects/{subjectID}")
    suspend fun getSubject(@Path("subjectID") subjectID: Long): ComposeSubject

    /**
     * 获取条目的角色
     *
     * @param subjectID
     * @param type 角色出场类型: 主角，配角，客串 (optional)
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/subjects/{subjectID}/characters")
    suspend fun getSubjectCharacters(
        @Path("subjectID") subjectID: Long,
        @Query("type") type: Int? = null,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeMonoInfo>

    /**
     * 获取条目的收藏用户
     *
     * @param subjectID
     * @param type  (optional)
     * @param mode 默认为 all, 未登录或没有好友时始终为 all (optional)
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/subjects/{subjectID}/collects")
    suspend fun getSubjectCollects(
        @Path("subjectID") subjectID: Long,
        @Query("type") @CollectionType type: Int? = null,
        @Query("mode") @ModeType mode: String? = null,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeUserDisplay>

    /**
     * 获取条目的吐槽箱
     *
     * @param subjectID
     * @param type  (optional)
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/subjects/{subjectID}/comments")
    suspend fun getSubjectComments(
        @Path("subjectID") subjectID: Int,
        @Query("type") type: CollectionType? = null,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeReply>

    /**
     * 获取条目的剧集
     *
     * @param subjectID
     * @param type  (optional)
     * @param limit max 1000 (optional, default to 100)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/subjects/{subjectID}/episodes")
    suspend fun getSubjectEpisodes(
        @Path("subjectID") subjectID: Long,
        @Query("type") @EpisodeType type: Int? = null,
        @Query("limit") limit: Int? = 100,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeEpisode>

    /**
     * 获取条目的推荐
     *
     * @param subjectID
     * @param limit max 10 (optional, default to 10)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/subjects/{subjectID}/recs")
    suspend fun getSubjectRecs(
        @Path("subjectID") subjectID: Int,
        @Query("limit") limit: Int? = 10,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeSubject>

    /**
     * 获取条目的关联条目
     *
     * @param subjectID
     * @param type  (optional)
     * @param offprint 是否单行本 (optional, default to false)
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/subjects/{subjectID}/relations")
    suspend fun getSubjectRelations(
        @Path("subjectID") subjectID: Long,
        @Query("type") @SubjectType type: Int? = null,
        @Query("offprint") offprint: Boolean? = false,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeSubjectDisplay>

    /**
     * 获取条目的评论
     *
     * @param subjectID
     * @param limit max 20 (optional, default to 5)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/subjects/{subjectID}/reviews")
    suspend fun getSubjectReviews(
        @Path("subjectID") subjectID: Long,
        @Query("limit") limit: Int? = 5,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeBlogDisplay>

    /**
     * 获取条目的制作人员
     *
     * @param subjectID
     * @param position 人物职位: 监督，原案，脚本,.. (optional)
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/subjects/{subjectID}/staffs/persons")
    suspend fun getSubjectStaffPersons(
        @Path("subjectID") subjectID: Long,
        @Query("position") position: Long? = null,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeMonoInfo>

    /**
     * 获取条目的制作人员职位
     *
     * @param subjectID
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/subjects/{subjectID}/staffs/positions")
    suspend fun getSubjectStaffPositions(
        @Path("subjectID") subjectID: Int,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<JsonObject>

    /**
     * 获取条目讨论版
     *
     * @param subjectID
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/subjects/{subjectID}/topics")
    suspend fun getSubjectTopics(
        @Path("subjectID") subjectID: Long,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeTopic>

    /**
     * 获取条目列表
     *
     * @param type
     * @param sort  (default to rank)
     * @param page min 1 (optional, default to 1)
     * @param cat 每种条目类型分类不同，具体参考 https://github.com/bangumi/common 的 subject_platforms.yaml (optional)
     * @param series 是否为系列，仅对书籍类型的条目有效 (optional)
     * @param year 年份 (optional)
     * @param month 月份 (optional)
     * @param tags  (optional)
     */
    @GET("p1/subjects")
    suspend fun getSubjects(
        @Query("type") @SubjectType type: Int,
        @Query("sort") @SubjectSortBrowserType sort: String = SubjectSortBrowserType.RANK,
        @Query("page") page: Int? = 1,
        @Query("cat") cat: Int? = null,
        @Query("series") series: Boolean? = null,
        @Query("year") year: Int? = null,
        @Query("month") month: Int? = null,
        @Query("tags") tags: @JvmSuppressWildcards List<String>? = null,
    ): ComposePage<ComposeSubject>

    /**
     * 给条目收藏点赞
     *
     * @param collectID
     * @param likeEpisodeCommentRequest
     */
    @PUT("p1/subjects/-/collects/{collectID}/like")
    suspend fun likeSubjectCollect(
        @Path("collectID") collectID: Int,
        @Body likeEpisodeCommentRequest: LikeEpisodeCommentRequest,
    ): ComposeStatus

    /**
     * 取消条目收藏点赞
     *
     * @param collectID
     */
    @DELETE("p1/subjects/-/collects/{collectID}/like")
    suspend fun unlikeSubjectCollect(@Path("collectID") collectID: Int): ComposeStatus
}
