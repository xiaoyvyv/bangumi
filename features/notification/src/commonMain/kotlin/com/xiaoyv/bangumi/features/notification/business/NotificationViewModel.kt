package com.xiaoyv.bangumi.features.notification.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeNotification
import com.xiaoyv.bangumi.shared.data.repository.UserRepository
import kotlinx.collections.immutable.toPersistentList

/**
 * [NotificationViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class NotificationViewModel(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
) : BaseViewModel<NotificationState, NotificationSideEffect, NotificationEvent.Action>(savedStateHandle) {
    override fun initBaseState(): BaseState<NotificationState> = BaseState.Loading()

    override fun initSate(onCreate: Boolean) = NotificationState()

    override fun onEvent(event: NotificationEvent.Action) {
        when (event) {
            is NotificationEvent.Action.OnRefresh -> refresh(event.loading)
            is NotificationEvent.Action.OnMarkRead -> onMarkRead(event.item)
            is NotificationEvent.Action.OnAgreeFriendRequest -> onAgreeFriendRequest(event.item)
        }
    }

    override suspend fun BaseSyntax<NotificationState, NotificationSideEffect>.refreshSync() {
        userRepository.fetchUserAllNotification()
            .onFailure { reduceError { it } }
            .onSuccess { reduceContent { state.copy(notifications = it.toPersistentList()) } }
    }

    private fun onAgreeFriendRequest(item: ComposeNotification) = action {
        withActionLoading {
            userRepository.submitMarkNotificationRead(item.id)
            userRepository.fetchUserAllNotification()
        }.onFailure {
            postToast { it.errMsg }
        }.onSuccess {
            reduceContent { state.copy(notifications = it.toPersistentList()) }
            postEffect { NotificationSideEffect.OnRefreshNotificationCount }
        }
    }

    private fun onMarkRead(item: ComposeNotification) = action {
        withActionLoading {
            userRepository.submitMarkNotificationRead(item.id)
            userRepository.fetchUserAllNotification()
        }.onFailure {
            postToast { it.errMsg }
        }.onSuccess {
            reduceContent { state.copy(notifications = it.toPersistentList()) }
            postEffect { NotificationSideEffect.OnRefreshNotificationCount }
        }
    }
}