package com.xiaoyv.bangumi.features.dollars.business

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.types.LoadingState
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.core.utils.limit
import com.xiaoyv.bangumi.shared.data.repository.UgcRepository
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * [DollarsViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class DollarsViewModel(
    savedStateHandle: SavedStateHandle,
    private val ugcRepository: UgcRepository,
) : BaseViewModel<DollarsState, DollarsSideEffect, DollarsEvent.Action>(savedStateHandle) {

    init {
        viewModelScope.launch {
            while (isActive) {
                delay(3000)
                refreshChats()
            }
        }
    }

    override fun initBaseState(): BaseState<DollarsState> = BaseState.Loading()

    override fun initSate(onCreate: Boolean) = DollarsState()

    override fun onEvent(event: DollarsEvent.Action) {
        when (event) {
            is DollarsEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is DollarsEvent.Action.OnValueChange -> onValueChange(event.value)
            DollarsEvent.Action.OnSendMessage -> onSendMessage()
        }
    }

    override suspend fun BaseSyntax<DollarsState, DollarsSideEffect>.refreshSync() {
        refreshChats()
    }

    private suspend fun refreshChats() = subAction {
        ugcRepository.fetchDollarsChat()
            .onFailure { reduceError { it } }
            .onSuccess { reduceContent { state.copy(items = it.toPersistentList()) } }
    }

    private fun onValueChange(value: TextFieldValue) = action {
        reduceContent { state.copy(value = value.limit(1000)) }
    }

    private fun onSendMessage() = action {
        reduceContent { state.copy(sending = LoadingState.Loading) }

        ugcRepository.summitDollarsChat(stateRaw.value.text.trim())
            .onFailure {
                postToast { it.errMsg }
                reduceContent { state.copy(sending = LoadingState.NotLoading) }
            }
            .onSuccess {
                reduceContent {
                    state.copy(
                        value = TextFieldValue(),
                        sending = LoadingState.NotLoading
                    )
                }

                refreshChats()
            }
    }
}