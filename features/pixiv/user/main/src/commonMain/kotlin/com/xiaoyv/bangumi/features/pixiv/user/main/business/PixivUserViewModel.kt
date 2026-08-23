package com.xiaoyv.bangumi.features.pixiv.user.main.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
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
    private val pixivRepository: PixivRepository,
) : BaseViewModel<PixivUserState, PixivUserSideEffect, PixivUserEvent.Action>() {

    override fun createInitialState() = PixivUserState(userId = userId)

    override suspend fun Syntax<UiState<PixivUserState>, UiSideEffect<PixivUserSideEffect>>.refreshSync() {
        if (state.data.userId <= 0) return

        pixivRepository.fetchUserInfo(state.data.userId)
            .onFailure { reduceError { it } }
            .onSuccess { userInfo ->
                reduceData {
                    state.copy(userInfo = userInfo)
                }
            }
    }

    override fun onEvent(event: PixivUserEvent.Action) {
        when (event) {
            is PixivUserEvent.Action.OnRefresh -> refresh(loading = event.loading)
        }
    }
}
