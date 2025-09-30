package com.xiaoyv.bangumi.features.message.business

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_friend
import com.xiaoyv.bangumi.core_resource.resources.global_inbox
import com.xiaoyv.bangumi.core_resource.resources.global_outbox
import com.xiaoyv.bangumi.features.message.TAB_FRIEND
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.types.MessageBoxType
import com.xiaoyv.bangumi.shared.data.repository.UserRepository
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

/**
 * [MessageMainViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class MessageMainViewModel(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
) : BaseViewModel<MessageMainState, MessageMainSideEffect, MessageMainEvent.Action>(savedStateHandle) {

    private val messageInboxPager = userRepository.fetchUserMessagePager(MessageBoxType.TYPE_INBOX)
    private val messageOutboxPager = userRepository.fetchUserMessagePager(MessageBoxType.TYPE_OUTBOX)

    val messageInbox = messageInboxPager.flow.cachedIn(viewModelScope)
    val messageOutbox = messageOutboxPager.flow.cachedIn(viewModelScope)

    override fun initSate(onCreate: Boolean) = MessageMainState(
        tabs = persistentListOf(
            ComposeTextTab(MessageBoxType.TYPE_INBOX, Res.string.global_inbox),
            ComposeTextTab(MessageBoxType.TYPE_OUTBOX, Res.string.global_outbox),
            ComposeTextTab(TAB_FRIEND, Res.string.global_friend),
        )
    )

    override fun onEvent(event: MessageMainEvent.Action) {
        when (event) {
            is MessageMainEvent.Action.OnRefresh -> refresh(event.loading)
            is MessageMainEvent.Action.OnToggleEditMode -> onToggleEditMode()
            is MessageMainEvent.Action.OnItemCheckChanged -> onItemCheckChanged(event.type, event.id, event.checked)
            is MessageMainEvent.Action.OnDeleteMessage -> onDeleteMessage(event.type)
            is MessageMainEvent.Action.OnTabSelected -> onTabSelected(event.type)
        }
    }

    private fun onTabSelected(type: String) = action {
        reduceContent { state.copy(selectedTabType = type) }
    }

    private fun onDeleteMessage(@MessageBoxType type: String) = action {
        val ids = if (type == MessageBoxType.TYPE_INBOX) stateRaw.selectedInboxIds else stateRaw.selectedOutboxIds

        withActionLoading { userRepository.submitDeleteMessage(ids = ids, type = type) }
            .onSuccess {
                reduceContent {
                    if (type == MessageBoxType.TYPE_INBOX) {
                        state.copy(
                            selectedInboxIds = persistentListOf(),
                            editMode = false
                        )
                    } else {
                        state.copy(
                            selectedOutboxIds = persistentListOf(),
                            editMode = false
                        )
                    }
                }
                postEffect { MessageMainSideEffect.OnRefreshList(type) }
            }
    }

    private fun onItemCheckChanged(@MessageBoxType type: String, id: Long, checked: Boolean) = action {
        if (type == MessageBoxType.TYPE_INBOX) {
            if (checked) {
                reduceContent { state.copy(selectedInboxIds = state.selectedInboxIds.plus(id).toPersistentList()) }
            } else {
                reduceContent { state.copy(selectedInboxIds = state.selectedInboxIds.minus(id).toPersistentList()) }
            }
        } else {
            if (checked) {
                reduceContent { state.copy(selectedOutboxIds = state.selectedOutboxIds.plus(id).toPersistentList()) }
            } else {
                reduceContent { state.copy(selectedOutboxIds = state.selectedOutboxIds.minus(id).toPersistentList()) }
            }
        }
    }

    private fun onToggleEditMode() = action {
        reduceContent { state.copy(editMode = !state.editMode) }
    }
}