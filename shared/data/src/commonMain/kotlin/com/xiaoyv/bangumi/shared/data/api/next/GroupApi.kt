package com.xiaoyv.bangumi.shared.data.api.next

import com.xiaoyv.bangumi.shared.core.types.AppJsonApiDsl
import com.xiaoyv.bangumi.shared.data.model.emnu.GroupFilterMode
import com.xiaoyv.bangumi.shared.data.model.emnu.GroupMemberRole
import com.xiaoyv.bangumi.shared.data.model.emnu.GroupSortType
import com.xiaoyv.bangumi.shared.data.model.request.CreateGroupTopicRequest
import com.xiaoyv.bangumi.shared.data.model.request.GroupTopicFilter
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroup
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeStatus
import com.xiaoyv.bangumi.shared.data.model.response.bgm.topic.ComposeTopic
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUserDisplay
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

@AppJsonApiDsl
interface GroupApi {
    /**
     * 创建小组话题
     *
     * @param groupName
     * @param createGroupTopicRequest  (optional)
     */
    @POST("p1/groups/{groupName}/topics")
    suspend fun createGroupTopic(
        @Path("groupName") groupName: String,
        @Body createGroupTopicRequest: CreateGroupTopicRequest? = null,
    ): ComposeStatus

    /**
     * 获取小组详情
     *
     * @param groupName
     */
    @GET("p1/groups/{groupName}")
    suspend fun getGroup(@Path("groupName") groupName: String): ComposeGroup

    /**
     * 获取小组成员列表
     *
     * @param groupName
     * @param role  (optional)
     * @param limit  (optional, default to 20)
     * @param offset  (optional, default to 0)
     */
    @GET("p1/groups/{groupName}/members")
    suspend fun getGroupMembers(
        @Path("groupName") groupName: String,
        @Query("role") role: GroupMemberRole? = null,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeUserDisplay>

    /**
     * 获取小组话题列表
     *
     * @param groupName
     * @param limit  (optional, default to 20)
     * @param offset  (optional, default to 0)
     */
    @GET("p1/groups/{groupName}/topics")
    suspend fun getGroupTopics(
        @Path("groupName") groupName: String,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeTopic>

    /**
     * 获取小组列表
     *
     * @param sort  (default to created)
     * @param mode  (optional)
     * @param limit  (optional, default to 20)
     * @param offset  (optional, default to 0)
     */
    @GET("p1/groups")
    suspend fun getGroups(
        @Query("sort") @GroupSortType sort: String = GroupSortType.CREATED,
        @Query("mode") mode: GroupFilterMode? = null,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeGroup>

    /**
     * 获取最新的小组话题
     *
     * @param mode 登录时默认为 joined, 未登录或没有加入小组时始终为 all
     * @param limit  (optional, default to 20)
     * @param offset  (optional, default to 0)
     */
    @GET("p1/groups/-/topics")
    suspend fun getRecentGroupTopics(
        @Query("mode") @GroupTopicFilter mode: String,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
    ): ComposePage<ComposeTopic>
}
