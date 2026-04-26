package com.xiaoyv.bangumi.shared.data.repository.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.component.toPinYin
import com.xiaoyv.bangumi.shared.core.exception.ApiHttpException
import com.xiaoyv.bangumi.shared.core.types.EditInfoType
import com.xiaoyv.bangumi.shared.core.types.MessageBoxType
import com.xiaoyv.bangumi.shared.core.types.list.ListUserType
import com.xiaoyv.bangumi.shared.core.utils.awaitAll
import com.xiaoyv.bangumi.shared.core.utils.fromJson
import com.xiaoyv.bangumi.shared.core.utils.runResult
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.api.client.BgmApiClient
import com.xiaoyv.bangumi.shared.data.manager.app.PreferenceStore
import com.xiaoyv.bangumi.shared.data.model.request.list.user.ListUserParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeAuthToken
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeFriend
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMessage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMessageDetail
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeNotification
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUnRead
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUserDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUserEdit
import com.xiaoyv.bangumi.shared.data.model.response.bgm.loadAllData
import com.xiaoyv.bangumi.shared.data.model.response.bgm.transform
import com.xiaoyv.bangumi.shared.data.parser.bgm.NotificationParser
import com.xiaoyv.bangumi.shared.data.parser.bgm.UserParser
import com.xiaoyv.bangumi.shared.data.repository.UserRepository
import com.xiaoyv.bangumi.shared.data.repository.datasource.createNetworkOffsetLimitPagingPager
import com.xiaoyv.bangumi.shared.data.repository.datasource.createNetworkPageLimitPagingPager
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

/**
 * [UserRepositoryImpl]
 *
 * @author why
 * @since 2025/1/15
 */
class UserRepositoryImpl(
    private val client: BgmApiClient,
    private val userParser: UserParser,
    private val notificationParser: NotificationParser,
    private val preferenceStore: PreferenceStore,
    private val pagingConfig: PagingConfig,
) : UserRepository {

    override fun fetchUserMessagePager(@MessageBoxType type: String): Pager<Int, ComposeMessage> {
        return createNetworkPageLimitPagingPager(
            keySelector = { it.id },
            pagingConfig = pagingConfig,
            onLoadData = { fetchUserMessageList(type, it).getOrThrow() }
        )
    }


    override fun fetchUserPager(param: ListUserParam): Pager<Int, ComposeUserDisplay> {
        return createNetworkOffsetLimitPagingPager(
            pagingConfig = pagingConfig,
            keySelector = { it.user.username },
            onLoadData = {
                fetchUserListByPage(param, it, pagingConfig.pageSize).result
            }
        )
    }

    override suspend fun fetchUserListByPage(
        param: ListUserParam,
        offset: Int,
        limit: Int,
    ): ComposePage<ComposeUserDisplay> {
        return when (param.type) {
            ListUserType.USER_FRIEND -> {
                client.nextUserApi.getUserFriends(
                    username = param.username,
                    limit = limit,
                    offset = offset
                ).transform { ComposeUserDisplay(user = it) }
            }

            ListUserType.USER_FOLLOWER -> {
                client.nextUserApi.getUserFollowers(
                    username = param.username,
                    limit = limit,
                    offset = offset
                ).transform { ComposeUserDisplay(user = it) }
            }

            ListUserType.USER_BLOCKLIST -> {
                with(userParser) {
                    client.bgmWebApi.fetchUserPrivacy()
                        .fetchUserPrivacyConverted().blocklist
                        .map { user -> ComposeUserDisplay(user = user) }
                        .let { ComposePage(result = it, total = it.size) }
                }
//                client.nextRelationshipApi.getBlocklist().blocklist
//                    .map { id -> ComposeUserDisplay(user = ComposeUser(id = id, nickname = "ID:$id")) }
//                    .let { ComposePage(result = it, total = it.size) }
            }

            ListUserType.GROUP_MEMBER -> {
                client.nextGroupApi.getGroupMembers(
                    groupName = param.groupName,
                    role = param.groupRole,
                    limit = limit,
                    offset = offset
                )
            }

            ListUserType.CHARACTER_COLLECT -> {
                client.nextCharacterApi.getCharacterCollects(
                    characterID = param.characterID,
                    limit = limit,
                    offset = offset
                )
            }

            ListUserType.PERSON_COLLECT -> {
                client.nextPersonApi.getPersonCollects(
                    personID = param.personID,
                    limit = limit,
                    offset = offset
                )
            }

            ListUserType.SUBJECT_COLLECT -> {
                client.nextSubjectApi.getSubjectCollects(
                    subjectID = param.subjectID,
                    limit = limit,
                    offset = offset
                )
            }

            else -> error("暂不支持该类型")
        }
    }

    override suspend fun fetchUserInfo(username: String): Result<ComposeUser> = client.requestNextUserApi {
        awaitAll(
            block1 = { client.requestNextUserApi { getUser(username) } },
            block2 = {
                client.requestWebApi {
                    with(userParser) {
                        fetchUserHomepage(username)
                            .fetchUserHomepageConverted()
                    }
                }
            }
        ).map { it.data1.copy(stats = it.data1.stats.copy(rating = it.data2)) }
            .getOrThrow()
    }

    override suspend fun fetchSelfFriends(): Result<List<ComposeFriend>> = client.requestWebApi {
        fetchMyFriends()
    }

    override suspend fun fetchUserList(param: ListUserParam): Result<List<ComposeUserDisplay>> = runResult {
        loadAllData { offset, limit -> fetchUserListByPage(param, offset, limit) }
            .distinctBy { it.user.username }
            .map { it.copy(pinyin = it.user.nickname.toPinYin()) }
            .apply { require(isNotEmpty()) { "这里没有人哦~" } }
    }

    override suspend fun fetchUserProfile(): Result<ComposeUser> = client.requestJsonApi {
        fetchUserProfile()
    }

    override suspend fun fetchUserEditInfo(): Result<ComposeUserEdit> = client.requestWebApi {
        with(userParser) {
            fetchUserEditInfo()
                .fetchUserEditInfoConverted()
        }
    }

    override suspend fun fetchUserUnreadNotification(): Result<ComposeUnRead> = client.requestWebApi {
        fetchUserUnreadNotification(System.currentTimeMillis()).let {
            val info = requireNotNull(it.text().fromJson<ComposeUnRead>())
            if (info.count == null) throw ApiHttpException(code = 401)
            info
        }
    }

    override suspend fun fetchUserUnreadMessage(): Result<ComposeUnRead> = client.requestWebApi {
        with(notificationParser) {
            fetchUserUnreadMessage()
                .fetchUserUnreadMessageConverted()
        }
    }

    override suspend fun fetchUserMessageList(@MessageBoxType type: String, page: Int): Result<List<ComposeMessage>> =
        client.requestWebApi {
            with(userParser) {
                fetchUserMessageList(type = type, page = page)
                    .fetchUserMessageListConverted(type)
            }
        }

    override suspend fun fetchUserMessageDetail(id: Long): Result<ComposeMessageDetail> = client.requestWebApi {
        with(userParser) {
            fetchUserMessageDetail(id)
                .fetchUserMessageDetailConverted()
        }
    }


    override suspend fun fetchUserAllNotification(): Result<List<ComposeNotification>> = client.requestWebApi {
        with(notificationParser) {
            val newest = fetchUserNotificationNewest()
                .fetchUserNotificationConverted(checkEmpty = false)
            val newestIds = newest.map { it.id }

            fetchUserNotificationHistory()
                .fetchUserNotificationConverted(checkEmpty = true)
                .map { notification ->
                    notification.copy(
                        unread = newestIds.contains(notification.id),
                        count = newest.find { it.id == notification.id }?.count
                    )
                }
        }
    }

    override suspend fun fetchUserCollectionSubject(
        username: String,
        subjectType: Int,
        type: Int,
        offset: Int,
        limit: Int,
    ): Result<List<ComposeSubject>> = client.requestNextUserApi {
        getUserSubjectCollections(
            username = username,
            subjectType = subjectType,
            type = type,
            offset = offset,
            limit = limit
        ).result
    }

    override suspend fun submitUserInfoUpdate(
        avatarBytes: ByteArray,
        parts: Map<String, String>,
    ): Result<Unit> = runResult {
        val multipart = MultiPartFormDataContent(formData {
            parts.forEach {
                append(it.key, it.value)
            }
            append(EditInfoType.TYPE_AVATAR, avatarBytes, Headers.build {
                append(HttpHeaders.ContentType, "image/png")
                append(HttpHeaders.ContentDisposition, "filename=\"avatar.png\"")
            })
        })

        with(userParser) {
            client.bgmWebApi
                .submitUpdateUserInfo(body = multipart)
                .sendUpdateUserInfoConverted()
        }
    }

    override suspend fun submitMarkNotificationRead(notificationId: Long): Result<Unit> = client.requestWebApi {
        submitMarkNotificationRead(
            notification = notificationId.toString(),
            gh = preferenceStore.userInfo.formHash,
        )
    }

    override suspend fun submitMarkAllNotificationRead(): Result<Unit> = client.requestWebApi {
        submitMarkNotificationRead(
            notification = "all",
            gh = preferenceStore.userInfo.formHash,
        )
    }

    override suspend fun submitDeleteMessage(ids: SerializeList<Long>, @MessageBoxType type: String): Result<Unit> =
        client.requestWebApi(disableRedirect = true) {
            with(userParser) {
                submitDeleteChii(
                    ids = ids,
                    folder = type,
                    formhash = preferenceStore.userInfo.formHash,
                )
            }
        }

    override suspend fun submitSendMessage(
        relatedId: String,
        currentMsgId: String,
        username: String,
        title: String,
        text: String,
        newChat: Boolean,
    ): Result<ComposeMessageDetail> = client.requestWebApi(disableRedirect = true) {
        with(userParser) {
            submitCreateChii(
                related = relatedId,
                currentMsgId = currentMsgId,
                msgReceivers = username,
                msgTitle = title,
                msgBody = text,
                chat = if (newChat) null else "on",
                formhash = preferenceStore.userInfo.formHash,
            )
            fetchUserMessageDetail(currentMsgId.toLong())
                .fetchUserMessageDetailConverted()
        }
    }

    override suspend fun submitRequestToken(formHash: String): Result<ComposeAuthToken> = client.createBgmToken(formHash)

    override suspend fun submitRefreshToken(refreshToken: String): Result<ComposeAuthToken> = runResult {
        client.bgmWebApiNoRedirect.sendAuthJsonApiToken(
            refreshToken = refreshToken,
            grantType = "refresh_token"
        )
    }


}