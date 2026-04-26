package com.xiaoyv.bangumi.shared.data.manager.app

import androidx.compose.runtime.Stable
import com.xiaoyv.bangumi.shared.core.exception.ApiHttpException
import com.xiaoyv.bangumi.shared.core.utils.debugLog
import com.xiaoyv.bangumi.shared.data.api.client.cookie.BgmCookieStorage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeAuthToken
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeFriend
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSetting
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.data.repository.UserRepository
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

@Stable
class UserManager(
    private val userRepository: UserRepository,
    private val preferenceStore: PreferenceStore,
    private val cookieStorage: BgmCookieStorage,
) : CoroutineScope by MainScope() {
    private val seq: Long
        get() = Clock.System.now().let { it.nanosecondsOfSecond + it.epochSeconds }

    val isLogin get() = preferenceStore.userInfo != ComposeUser.Empty

    /**
     * 本地缓存数据接口委托
     */
    var isUserFirstUse by preferenceStore::isUserFirstUse
    var isAgreePrivacy by preferenceStore::isAgreePrivacy
    var settings by preferenceStore::settings

    val friends = persistentListOf<ComposeFriend>()

    /**
     * 用户信息
     */
    val userInfo: ComposeUser get() = preferenceStore.userInfo
    val userToken: ComposeAuthToken get() = preferenceStore.userToken

    private val _notification = MutableStateFlow(seq)
    val notification = _notification.asStateFlow()

    init {
        launch {
            if (isLogin) updateUserInfo()
        }
    }

    /**
     * 登录成功
     */
    fun login(userInfo: ComposeUser, token: ComposeAuthToken) {
        preferenceStore.userInfo = userInfo
        preferenceStore.userToken = token
        notificationChanged()
    }

    fun setToken(token: ComposeAuthToken) {
        preferenceStore.userToken = token
    }

    /**
     * 注销登录
     */
    suspend fun logout(): Result<Boolean> {
        preferenceStore.userToken = ComposeAuthToken.Empty
        preferenceStore.userInfo = ComposeUser.Empty
        cookieStorage.removeAll()
        notificationChanged()
        return Result.success(true)
    }

    /**
     * 更新用户信息，登录信息失效会清空
     */
    suspend fun updateUserInfo() {
        updateUserInfoImpl()
        notificationChanged()
    }

    /**
     * 刷新用户信息
     */
    private suspend fun updateUserInfoImpl() {
        // 通过通知接口检测是否Web登录信息是否失效
        userRepository.fetchUserUnreadNotification().fold(
            onFailure = {
                if (it is ApiHttpException && it.code == 401) {
                    preferenceStore.userToken = ComposeAuthToken.Empty
                    preferenceStore.userInfo = ComposeUser.Empty
                }
            },
            onSuccess = {
                // 拉取个人信息，顺便检查 Json 授权是否失效
                userRepository.fetchUserProfile()
                    .onSuccess {
                        debugLog { "更新用户信息: ${it.copy(formHash = userInfo.formHash)}" }

                        // 这里需要保留 userInfo 登录中填充的 Hash 字段
                        preferenceStore.userInfo = it.copy(formHash = userInfo.formHash)
                    }
            }
        )
    }

    /**
     * 更新设置
     */
    fun updateSettings(block: (ComposeSetting) -> ComposeSetting) {
        val newSettings = block(settings)
        preferenceStore.settings = newSettings
        notificationChanged()
    }

    /**
     * 通知刷新状态
     */
    private fun notificationChanged() {
        _notification.update { seq }
    }
}
