package com.xiaoyv.bangumi.shared.data.manager.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xiaoyv.bangumi.shared.AppVersion
import com.xiaoyv.bangumi.shared.core.exception.ApiHttpException
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.model.response.chore.ComposeAppRelease
import com.xiaoyv.bangumi.shared.data.repository.ChoreRepository
import com.xiaoyv.bangumi.shared.data.repository.MikanRepository
import com.xiaoyv.bangumi.shared.data.repository.UserRepository
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.OrbitContainer
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.orbitContainer
import kotlin.time.Duration.Companion.milliseconds

/**
 * [SharedViewModel]
 *
 * @author why
 * @since 2025/1/15
 */
class SharedViewModel(
    private val userManager: UserManager,
    private val choreRepository: ChoreRepository,
    private val mikanRepository: MikanRepository,
    private val userRepository: UserRepository,
) : OrbitContainerHost<SharedState, SharedState, SharedEvent>, ViewModel() {

    override val container: OrbitContainer<SharedState, SharedState, SharedEvent> by lazy {
        viewModelScope.orbitContainer(
            initialState = initAppState(),
            transformState = { it },
            onCreate = { onCreate() }
        )
    }

    init {
        // 用户数据变化了，直接更新
        userManager.notification
            .onEach {
                intent {
                    reduce {
                        state.copy(
                            user = userManager.userInfo,
                            pixivToken = userManager.pixivToken,
                            settings = userManager.settings
                        )
                    }
                }
            }
            .launchIn(viewModelScope)

        // 消息通知轮询
        viewModelScope.launch {
            while (isActive) {
                if (userManager.isLogin) onRefreshUserUnreadNotification()
                delay(10000.milliseconds)
            }
        }
    }

    private fun initAppState() = SharedState(
        user = userManager.userInfo,
        pixivToken = userManager.pixivToken,
        settings = userManager.settings,
    )

    private suspend fun onCreate() {
        coroutineScope {
            launch { onRefreshAppRelease() }
            launch { onRefreshMikanIds() }
        }
    }

    /**
     * 根据当前更新渠道刷新应用发布信息。
     */
    suspend fun onRefreshAppRelease() = subIntent {
        choreRepository.fetchAppRelease(state.settings.network.updateChannel).onSuccess { release ->
            if (release.isNewerThan(currentVersionCode = AppVersion.versionCode)) {
                reduce { state.copy(appRelease = release) }
                postSideEffect(SharedEvent.OnShowAppUpdate)
            } else {
                reduce { state.copy(appRelease = ComposeAppRelease.Empty) }
            }
        }
    }

    /**
     * 请求展示当前可用的应用更新。
     */
    fun onShowAppUpdate() = intent {
        if (state.appRelease != ComposeAppRelease.Empty) {
            postSideEffect(SharedEvent.OnShowAppUpdate)
        }
    }

    /**
     * 蜜柑映射数据
     */
    private suspend fun onRefreshMikanIds() = subIntent {
        mikanRepository.fetchMikanIdMapByEmbed().onSuccess { localMap ->
            reduce { state.copy(mikanIdMap = localMap) }

            mikanRepository.fetchMikanIdMapByJsdelivr()
                .onFailure {
                    mikanRepository.fetchMikanIdMapByGithub()
                        .onSuccess {
                            reduce { state.copy(mikanIdMap = (localMap + it).toImmutableMap()) }
                        }
                }
                .onSuccess {
                    reduce { state.copy(mikanIdMap = (localMap + it).toImmutableMap()) }
                }
        }
    }

    fun onRefreshUserUnreadNotification() = intent {
        userRepository.fetchUserUnreadNotification()
            .onFailure {
                if (it is ApiHttpException && it.code == 401) {
                    userManager.logout()
                }
            }.onSuccess {
                reduce { state.copy(unread = it) }
            }
    }
}
