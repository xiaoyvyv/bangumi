package com.xiaoyv.bangumi.shared.data.manager.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xiaoyv.bangumi.shared.core.utils.awaitAll
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.repository.MikanRepository
import com.xiaoyv.bangumi.shared.data.repository.UserRepository
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.container


/**
 * [SharedViewModel]
 *
 * @author why
 * @since 2025/1/15
 */
class SharedViewModel(
    private val userManager: UserManager,
    private val mikanRepository: MikanRepository,
    private val userRepository: UserRepository,
) : ContainerHost<SharedState, SharedEvent>, ViewModel() {

    override val container: Container<SharedState, SharedEvent> by lazy {
        viewModelScope.container(
            initialState = initAppState(),
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
                delay(10000)
            }
        }
    }

    private fun initAppState() = SharedState(
        user = userManager.userInfo,
        settings = userManager.settings,
    )

    private suspend fun onCreate() {
        coroutineScope {
            launch { onRefreshMikanIds() }
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
        awaitAll(
            block1 = { userRepository.fetchUserUnreadNotification() },
            block2 = { userRepository.fetchUserUnreadMessage() },
        ).onSuccess {
            reduce {
                state.copy(
                    unreadNotification = it.data1.count ?: 0,
                    unreadMessage = it.data2.count ?: 0
                )
            }
        }
    }
}