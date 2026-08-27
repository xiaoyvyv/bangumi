package com.xiaoyv.bangumi.shared.data.repository

import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.MessageBoxType
import com.xiaoyv.bangumi.shared.core.types.ReportReason
import com.xiaoyv.bangumi.shared.core.types.ReportType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.utils.ResultZip2
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import com.xiaoyv.bangumi.shared.data.model.request.bgm.NextWebLoginParam
import com.xiaoyv.bangumi.shared.data.model.request.list.user.ListUserParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeAuthToken
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEmptyBody
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeFriend
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMessage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMessageDetail
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUnRead
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeNotice
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUserDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUserEdit
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUserPrivacy
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUserServicesEdit
import com.xiaoyv.bangumi.shared.data.repository.datasource.MemoryPagingController

/**
 * [UserRepository]
 *
 * @author why
 * @since 2025/1/15
 */
interface UserRepository {
    fun fetchUserMessagePager(@MessageBoxType type: String): MemoryPagingController<ComposeMessage, Long>

    fun fetchUserPager(param: ListUserParam): MemoryPagingController<ComposeUserDisplay, String>


    suspend fun fetchUserInfo(username: String): Result<ComposeUser>

    suspend fun fetchSelfFriends(): Result<List<ComposeFriend>>

    suspend fun fetchUserList(param: ListUserParam): Result<List<ComposeUserDisplay>>

    suspend fun fetchUserListByPage(param: ListUserParam, offset: Int, limit: Int): ComposePage<ComposeUserDisplay>

    suspend fun fetchUserProfile(): Result<ComposeUser>

    suspend fun fetchUserEditInfo(): Result<ResultZip2<ComposeUserEdit, ComposeUserServicesEdit>>

    suspend fun fetchUserPrivacy(): Result<ComposeUserPrivacy>


    suspend fun fetchUserUnreadNotification(): Result<ComposeUnRead>

    suspend fun fetchUserNotify(unread: Boolean?): Result<List<ComposeNotice>>

    suspend fun fetchUserMessageList(@MessageBoxType type: String, page: Int): Result<List<ComposeMessage>>

    suspend fun fetchUserMessageDetail(id: Long): Result<ComposeMessageDetail>

    suspend fun fetchUserCollectionSubject(
        username: String,
        @SubjectType subjectType: Int = 0,
        @CollectionType type: Int = 0,
        offset: Int = 0,
        limit: Int = 20,
    ): Result<List<ComposeSubject>>

    suspend fun submitLogin(param: NextWebLoginParam): Result<ComposeUser>

    suspend fun submitUserPrivacy(privacy: ComposeUserPrivacy): Result<ComposeUserPrivacy>

    suspend fun submitRequestToken(formHash: String): Result<ComposeAuthToken>

    suspend fun submitUserInfoUpdate(
        avatarBytes: ByteArray,
        items: Map<String, String>,
        networkItems: SerializeMap<String, String>
    ): Result<Unit>

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

    suspend fun submitReport(id: Long, @ReportType type: Int, @ReportReason reason: Int, comment: String): Result<ComposeEmptyBody>
}
