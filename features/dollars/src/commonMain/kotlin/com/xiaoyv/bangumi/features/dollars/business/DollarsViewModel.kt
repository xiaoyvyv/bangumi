package com.xiaoyv.bangumi.features.dollars.business

import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.PageStatus
import org.orbitmvi.orbit.syntax.Syntax
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

    override fun initBaseState(): UiState<DollarsState> = UiState(data = createInitialState(), status = PageStatus.Loading)

    override fun createInitialState() = DollarsState()

    override fun onEvent(event: DollarsEvent.Action) {
        when (event) {
            is DollarsEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is DollarsEvent.Action.OnValueChange -> onValueChange(event.value)
            DollarsEvent.Action.OnSendMessage -> onSendMessage()
        }
    }

    override suspend fun Syntax<UiState<DollarsState>, UiSideEffect<DollarsSideEffect>>.refreshSync() {
        refreshChats()
    }

    private suspend fun refreshChats() = subIntent {
        ugcRepository.fetchDollarsChat()
            .onFailure { reduceError { it } }
            .onSuccess { reduceData { state.copy(items = it.toPersistentList()) } }
    }

    private fun onValueChange(value: TextFieldValue) = intent {
        reduceData { state.copy(value = value.limit(1000)) }
    }

    private fun onSendMessage() = intent {
        reduceData { state.copy(sending = LoadingState.Loading) }

        ugcRepository.summitDollarsChat(state.data.value.text.trim())
            .onFailure {
                postToast { it.errMsg }
                reduceData { state.copy(sending = LoadingState.NotLoading) }
            }
            .onSuccess {
                reduceData {
                    state.copy(
                        value = TextFieldValue(),
                        sending = LoadingState.NotLoading
                    )
                }

                refreshChats()
            }
    }
}