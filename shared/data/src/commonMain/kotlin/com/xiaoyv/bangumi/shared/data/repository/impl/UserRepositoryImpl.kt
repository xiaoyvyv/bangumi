package com.xiaoyv.bangumi.shared.data.repository.impl

import androidx.paging.PagingConfig
import com.fleeksoft.ksoup.Ksoup
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.component.toPinYin
import com.xiaoyv.bangumi.shared.core.exception.ApiHttpException
import com.xiaoyv.bangumi.shared.core.types.EditInfoType
import com.xiaoyv.bangumi.shared.core.types.list.ListUserType
import com.xiaoyv.bangumi.shared.core.utils.ResultZip2
import com.xiaoyv.bangumi.shared.core.utils.awaitAll
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.core.utils.fromJson
import com.xiaoyv.bangumi.shared.core.utils.requireNoError
import com.xiaoyv.bangumi.shared.core.utils.runResult
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import com.xiaoyv.bangumi.shared.data.api.client.ApiClient
import com.xiaoyv.bangumi.shared.data.manager.app.PreferenceStore
import com.xiaoyv.bangumi.shared.data.model.request.bgm.ClearNoticeRequest
import com.xiaoyv.bangumi.shared.data.model.request.bgm.CreateReportParam
import com.xiaoyv.bangumi.shared.data.model.request.bgm.NextWebLoginParam
import com.xiaoyv.bangumi.shared.data.model.request.list.user.ListUserParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeAuthToken
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEmptyBody
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeFriend
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUnRead
import com.xiaoyv.bangumi.shared.data.model.response.bgm.home.ComposeHome
import com.xiaoyv.bangumi.shared.data.model.response.bgm.loadAllData
import com.xiaoyv.bangumi.shared.data.model.response.bgm.pm.ComposePmConversation
import com.xiaoyv.bangumi.shared.data.model.response.bgm.pm.ComposePmMessageDetail
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectRelation
import com.xiaoyv.bangumi.shared.data.model.response.bgm.transform
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeNotice
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUserDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUserEdit
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUserPrivacy
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUserServicesEdit
import com.xiaoyv.bangumi.shared.data.parser.bgm.UserParser
import com.xiaoyv.bangumi.shared.data.repository.UserRepository
import com.xiaoyv.bangumi.shared.data.repository.datasource.MemoryPagingController
import com.xiaoyv.bangumi.shared.data.repository.datasource.createMemoryOffsetLimitPagingController
import com.xiaoyv.bangumi.shared.data.repository.datasource.createMemoryPageLimitPagingController
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.collections.immutable.persistentListOf

/**
 * [UserRepositoryImpl]
 *
 * @author why
 * @since 2025/1/15
 */
class UserRepositoryImpl(
    private val client: ApiClient,
    private val userParser: UserParser,
    private val preferenceStore: PreferenceStore,
    private val pagingConfig: PagingConfig,
) : UserRepository {

    override fun fetchUserPmConversationPager(): MemoryPagingController<ComposePmConversation, Long> {
        return createMemoryPageLimitPagingController(
            idSelector = { it.id },
            pagingConfig = pagingConfig,
            onLoadData = { fetchUserPmConversation(it).getOrThrow() }
        )
    }

    override fun fetchUserPager(param: ListUserParam): MemoryPagingController<ComposeUserDisplay, String> {
        return createMemoryOffsetLimitPagingController(
            pagingConfig = pagingConfig,
            idSelector = { it.user.username },
            onLoadData = {
                fetchUserListByPage(param, it, pagingConfig.pageSize).result
            }
        )
    }

    override suspend fun fetchUserHomeInfo(): Result<ComposeHome> = client.requestNextHomeApi {
        getHome()
    }

    override suspend fun submitReport(
        id: Long,
        type: Int,
        reason: Int,
        comment: String
    ): Result<ComposeEmptyBody> = client.requestNextUserApi {
        createReport(CreateReportParam(id = id, type = type, reason = reason, comment = comment))
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
                client.nextRelationshipApi.getBlocklist().blocklist
                    .map { id -> ComposeUserDisplay(user = ComposeUser(id = id, nickname = "ID:$id")) }
                    .let { ComposePage(result = it, total = it.size) }
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

    override suspend fun fetchUserProfile(): Result<ComposeUser> = client.requestNextUserApi {
        getMe()
    }

    override suspend fun fetchUserEditInfo(): Result<ResultZip2<ComposeUserEdit, ComposeUserServicesEdit>> {
        return awaitAll(
            block1 = {
                Result.success(with(userParser) {
                    client.bgmWebApi.fetchUserEditInfo()
                        .fetchUserEditInfoConverted()
                })
            },
            block2 = {
                Result.success(with(userParser) {
                    client.bgmWebApi.fetchUserEditServicesInfo()
                        .fetchUserEditServicesInfoConverted()
                })
            }
        )
    }

    override suspend fun fetchUserUnreadNotification(): Result<ComposeUnRead> = client.requestWebApi {
        fetchUserUnreadNotification(System.currentTimeMillis()).let {
            val info = requireNotNull(it.text().fromJson<ComposeUnRead>())
            if (info.count == null) throw ApiHttpException(code = 401)
            info
        }
    }

    override suspend fun fetchUserPrivacy(): Result<ComposeUserPrivacy> = client.requestNextUserApi {
        getPrivacy()
    }

    override suspend fun submitUserPrivacy(privacy: ComposeUserPrivacy): Result<ComposeUserPrivacy> = client.requestNextUserApi {
        patchPrivacy(privacy)
    }

    override suspend fun fetchUserNotify(unread: Boolean?): Result<List<ComposeNotice>> = client.requestNextUserApi {
        listNotice(unread = unread).result.map { it.normalized() }
    }

    override suspend fun fetchUserPmConversation(page: Int): Result<List<ComposePmConversation>> =
        client.requestWebApi {
            with(userParser) {
                fetchUserPmCoversation(page = page)
                    .fetchUserPmCoversationConverted()
            }
        }

    override suspend fun fetchUserPmMessage(id: Long, thread: Long): Result<ComposePmMessageDetail> =
        client.requestWebApi {
            with(userParser) {
                fetchUserPmMessage(id, thread.takeIf { it != 0L })
                    .fetchUserPmMessageConverted()
            }
        }

    override suspend fun fetchUserCollectionSubject(
        username: String,
        subjectType: Int,
        type: Int,
        offset: Int,
        limit: Int,
    ): Result<List<ComposeSubjectRelation>> = client.requestNextUserApi {
        getUserSubjectCollections(
            username = username,
            subjectType = subjectType,
            type = type,
            offset = offset,
            limit = limit
        ).result.map { ComposeSubjectRelation(subject = it.normalized()) }
    }

    override suspend fun submitLogin(param: NextWebLoginParam): Result<ComposeUser> = client.requestNextUserApi {
        login(param)
    }

    override suspend fun submitUserInfoUpdate(
        avatarBytes: ByteArray,
        items: Map<String, String>,
        networkItems: SerializeMap<String, String>,
    ): Result<Unit> = runResult {
        // 个人信息
        if (items.isNotEmpty()) {
            val data = items.toMutableMap()
            data[EditInfoType.TYPE_FORM_HASH] = preferenceStore.userInfo.formHash
            data[EditInfoType.TYPE_SUBMIT] = "submit"

            val multipart = MultiPartFormDataContent(formData {
                data.forEach { append(it.key, it.value) }
                if (avatarBytes.isNotEmpty()) {
                    append(EditInfoType.TYPE_AVATAR, avatarBytes, Headers.build {
                        append(HttpHeaders.ContentType, "image/png")
                        append(HttpHeaders.ContentDisposition, "filename=\"avatar.png\"")
                    })
                }
            })

            with(userParser) {
                client.bgmWebApi
                    .submitUpdateUserInfo(body = multipart)
                    .sendUpdateUserInfoConverted()
            }
        }

        // 网络服务信息
        if (networkItems.isNotEmpty()) {
            val data = networkItems.toMutableMap()
            data[EditInfoType.TYPE_FORM_HASH] = preferenceStore.userInfo.formHash
            data[EditInfoType.TYPE_SUBMIT_NETWORK_SERVICES] = "submit"

            val multipart = MultiPartFormDataContent(formData {
                data.forEach { append(it.key, it.value) }
            })

            with(userParser) {
                client.bgmWebApi
                    .submitUpdateUserServicesInfo(body = multipart)
                    .sendUpdateUserInfoConverted()
            }
        }
    }

    override suspend fun submitMarkNotificationRead(notificationId: Long): Result<Unit> = client.requestNextUserApi {
        clearNotice(ClearNoticeRequest(persistentListOf(notificationId)))
    }

    override suspend fun submitMarkAllNotificationRead(): Result<Unit> = client.requestNextUserApi {
        clearNotice(ClearNoticeRequest())
    }

    override suspend fun submitDeleteMessage(ids: SerializeList<Long>): Result<Unit> =
        client.requestWebApi(disableRedirect = true) {
            submitDeleteChii(
                ids = ids,
                folder = "inbox",
                formhash = preferenceStore.userInfo.formHash,
            )
        }

    override suspend fun submitSendMessage(text: String, topic: String?, inputs: Map<String, String>): Result<Unit> = client.requestWebApi(disableRedirect = true) {
        val params = inputs.toMutableMap()
        if (topic.isNullOrBlank()) {
            params.remove("new_topic")
            params.remove("msg_title")
        } else {
            params["new_topic"] = "1"
            params["msg_title"] = topic
        }
        params["msg_body"] = text
        submitCreateChii(params)
    }

    override suspend fun submitRequestToken(formHash: String): Result<ComposeAuthToken> = client.createBgmToken(formHash)

    companion object {
        suspend fun ApiClient.createBgmToken(formHash: String) = runResult {
            val response = bgmWebApiNoRedirect.sendAuthJsonApi(formhash = formHash)
            if (response.status.value == 200) {
                Ksoup.parse(response.bodyAsText()).requireNoError()
            }

            val location = response.headers["Location"].orEmpty()
            val code = location
                .substringAfter("code=")
                .substringBefore("=")

            require(code.isNotBlank()) { "授权失败" }

            // 返回授权结果
            val tokenEntity = authApi.sendBgmAuthToken(
                code = code,
                grantType = "authorization_code"
            )

            require(tokenEntity.accessToken.isNotBlank())
            require(tokenEntity.refreshToken.isNotBlank())

            debugLog { "AuthToken ：${tokenEntity}" }

            tokenEntity
        }
    }
}
