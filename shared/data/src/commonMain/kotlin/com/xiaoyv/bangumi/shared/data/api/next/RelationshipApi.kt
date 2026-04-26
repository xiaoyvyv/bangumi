package com.xiaoyv.bangumi.shared.data.api.next

import com.xiaoyv.bangumi.shared.core.types.AppJsonApiDsl
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlocklist
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeStatus
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUserDisplay
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

@AppJsonApiDsl
interface RelationshipApi {
    /**
     * PUT p1/friends/{username}
     * 添加好友
     * @param username
     */
    @PUT("p1/friends/{username}")
    suspend fun addFriend(@Path("username", encoded = true) username: String): ComposeStatus

    /**
     * 与用户绝交
     *
     * @param username
     */
    @PUT("p1/blocklist/{username}")
    suspend fun addUserToBlocklist(@Path("username", encoded = true) username: String): ComposeBlocklist

    /**
     * 获取当前用户的绝交用户列表
     */
    @GET("p1/blocklist")
    suspend fun getBlocklist(): ComposeBlocklist

    /**
     * 获取当前用户的好友 ID 列表
     */
    @GET("p1/friendlist")
    suspend fun getFriendlist(): ComposeBlocklist

    /**
     * 获取当前用户的关注者列表
     *
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/followers")
    suspend fun getMyFollowers(@Query("limit") limit: Int? = 20, @Query("offset") offset: Int? = 0): ComposePage<ComposeUserDisplay>

    /**
     * 获取当前用户的好友列表
     *
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     */
    @GET("p1/friends")
    suspend fun getMyFriends(@Query("limit") limit: Int? = 20, @Query("offset") offset: Int? = 0): ComposePage<ComposeUserDisplay>

    /**
     * 取消好友
     *
     * @param username
     */
    @DELETE("p1/friends/{username}")
    suspend fun removeFriend(@Path("username", encoded = true) username: String): ComposeStatus

    /**
     * 取消与用户绝交
     *
     * @param username
     */
    @DELETE("p1/blocklist/{username}")
    suspend fun removeUserFromBlocklist(@Path("username", encoded = true) username: String): ComposeBlocklist
}
