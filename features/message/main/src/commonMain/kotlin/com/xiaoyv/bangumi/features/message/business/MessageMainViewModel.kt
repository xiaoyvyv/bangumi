package com.xiaoyv.bangumi.features.message.business

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_friend
import com.xiaoyv.bangumi.core_resource.resources.global_inbox
import com.xiaoyv.bangumi.features.message.TAB_FRIEND
import com.xiaoyv.bangumi.features.message.TAB_INBOX
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.repository.UserRepository
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf

/**
 * [MessageMainViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class MessageMainViewModel(
    userRepository: UserRepository,
) : BaseViewModel<MessageMainState, MessageMainSideEffect, MessageMainEvent.Action>() {

    private val messageInboxPager = userRepository.fetchUserPmConversationPager()

    val messageInbox = messageInboxPager.flow.cachedIn(viewModelScope)

    override fun createInitialState() = MessageMainState(
        tabs = persistentListOf(
            ComposeTextTab(TAB_INBOX, Res.string.global_inbox),
            ComposeTextTab(TAB_FRIEND, Res.string.global_friend),
        )
    )

    override fun onEvent(event: MessageMainEvent.Action) {
        when (event) {
            is MessageMainEvent.Action.OnRefresh -> refresh(event.loading)
        }
    }
}