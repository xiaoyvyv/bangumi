package com.xiaoyv.bangumi.features.pixiv.user.main.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.PageStatus
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.data.manager.app.PreferenceStore
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.repository.PixivRepository
import org.orbitmvi.orbit.syntax.Syntax

/**
 * [PixivUserViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class PixivUserViewModel(
    private val userId: Long,
    private val preferenceStore: PreferenceStore,
    private val userManager: UserManager,
    private val pixivRepository: PixivRepository,
) : BaseViewModel<PixivUserState, PixivUserSideEffect, PixivUserEvent.Action>() {

    private val isCurrentUser: Boolean
        get() = userId > 0 && userId == preferenceStore.pixivTokenData.currentUser.id.toLongOrNull()

    override fun initBaseState(): UiState<PixivUserState> =
        UiState(data = createInitialState(), status = PageStatus.Loading)

    override fun createInitialState() = PixivUserState(
        userId = userId,
        isCurrentUser = isCurrentUser,
    )

    override suspend fun Syntax<UiState<PixivUserState>, UiSideEffect<PixivUserSideEffect>>.refreshSync() {
        if (state.data.userId <= 0) return

        pixivRepository.fetchUserInfo(state.data.userId)
            .onFailure { reduceError { it } }
            .onSuccess { userInfo ->
                reduceData {
                    state.copy(
                        isCurrentUser = isCurrentUser,
                        userInfo = userInfo,
                    )
                }
            }
    }

    override fun onEvent(event: PixivUserEvent.Action) {
        when (event) {
            is PixivUserEvent.Action.OnRefresh -> refresh(loading = event.loading)
            PixivUserEvent.Action.OnLogout -> onLogout()
        }
    }

    private fun onLogout() = intent {
        userManager.clearPixivToken()
        reduceData { state.copy(isCurrentUser = false) }
    }
}
