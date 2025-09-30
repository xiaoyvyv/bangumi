package com.xiaoyv.bangumi.shared.data.repository

import androidx.paging.Pager
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.MessageBoxType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.request.list.user.ListUserParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeAuthToken
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeFriend
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMessage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMessageDetail
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeNotification
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUnRead
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUser
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUserDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUserEdit

/**
 * [UserRepository]
 *
 * @author why
 * @since 2025/1/15
 */
interface UserRepository {
    fun fetchUserMessagePager(@MessageBoxType type: String): Pager<Int, ComposeMessage>

    fun fetchUserPager(param: ListUserParam): Pager<Int, ComposeUserDisplay>

    suspend fun fetchUserInfo(username: String): Result<ComposeUser>

    suspend fun fetchSelfFriends(): Result<List<ComposeFriend>>

    suspend fun fetchUserList(param: ListUserParam): Result<List<ComposeUserDisplay>>

    suspend fun fetchUserListByPage(param: ListUserParam, offset: Int, limit: Int): ComposePage<ComposeUserDisplay>

    suspend fun fetchUserProfile(): Result<ComposeUser>

    suspend fun fetchUserEditInfo(): Result<ComposeUserEdit>

    suspend fun fetchUserUnreadNotification(): Result<ComposeUnRead>

    suspend fun fetchUserUnreadMessage(): Result<ComposeUnRead>

    suspend fun fetchUserMessageList(@MessageBoxType type: String, page: Int): Result<List<ComposeMessage>>

    suspend fun fetchUserMessageDetail(id: Long): Result<ComposeMessageDetail>

    suspend fun fetchUserAllNotification(): Result<List<ComposeNotification>>

    suspend fun fetchUserCollectionSubject(
        username: String,
        @SubjectType subjectType: Int = 0,
        @CollectionType type: Int = 0,
        offset: Int = 0,
        limit: Int = 20,
    ): Result<List<ComposeSubject>>

    suspend fun submitRequestToken(formHash: String): Result<ComposeAuthToken>

    suspend fun submitRefreshToken(refreshToken: String): Result<ComposeAuthToken>

    suspend fun submitUserInfoUpdate(avatarBytes: ByteArray, parts: Map<String, String>): Result<Unit>

    suspend fun submitMarkNotificationRead(notificationId: Long): Result<Unit>

    suspend fun submitMarkAllNotificationRead(): Result<Unit>

    suspend fun submitSendMessage(
        relatedId: String,
        currentMsgId: String,
        username: String,
        title: String,
        text: String,
        newChat: Boolean,
    ): Result<ComposeMessageDetail>

    suspend fun submitDeleteMessage(ids: SerializeList<Long>, @MessageBoxType type: String): Result<Unit>
}