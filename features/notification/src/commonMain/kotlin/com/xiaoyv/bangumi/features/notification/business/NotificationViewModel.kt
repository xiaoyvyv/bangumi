package com.xiaoyv.bangumi.features.notification.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.PageStatus
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.postEffect
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.core.mvi.withActionLoading
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeNotification
import com.xiaoyv.bangumi.shared.data.repository.UserRepository
import kotlinx.collections.immutable.toPersistentList
import org.orbitmvi.orbit.syntax.Syntax

/**
 * [NotificationViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class NotificationViewModel(
    private val userRepository: UserRepository,
) : BaseViewModel<NotificationState, NotificationSideEffect, NotificationEvent.Action>() {
    override fun initBaseState(): UiState<NotificationState> = UiState(data = createInitialState(), status = PageStatus.Loading)

    override fun createInitialState() = NotificationState()

    override fun onEvent(event: NotificationEvent.Action) {
        when (event) {
            is NotificationEvent.Action.OnRefresh -> refresh(event.loading)
            is NotificationEvent.Action.OnMarkRead -> onMarkRead(event.item)
            is NotificationEvent.Action.OnAgreeFriendRequest -> onAgreeFriendRequest(event.item)
        }
    }

    override suspend fun Syntax<UiState<NotificationState>, UiSideEffect<NotificationSideEffect>>.refreshSync() {
        userRepository.fetchUserAllNotification()
            .onFailure { reduceError { it } }
            .onSuccess { reduceData { state.copy(notifications = it.toPersistentList()) } }
    }

    private fun onAgreeFriendRequest(item: ComposeNotification) = intent {
        withActionLoading {
            userRepository.submitMarkNotificationRead(item.id)
            userRepository.fetchUserAllNotification()
        }.onFailure {
            postToast { it.errMsg }
        }.onSuccess {
            reduceData { state.copy(notifications = it.toPersistentList()) }
            postEffect { NotificationSideEffect.OnRefreshNotificationCount }
        }
    }

    private fun onMarkRead(item: ComposeNotification) = intent {
        withActionLoading {
            userRepository.submitMarkNotificationRead(item.id)
            userRepository.fetchUserAllNotification()
        }.onFailure {
            postToast { it.errMsg }
        }.onSuccess {
            reduceData { state.copy(notifications = it.toPersistentList()) }
            postEffect { NotificationSideEffect.OnRefreshNotificationCount }
        }
    }
}