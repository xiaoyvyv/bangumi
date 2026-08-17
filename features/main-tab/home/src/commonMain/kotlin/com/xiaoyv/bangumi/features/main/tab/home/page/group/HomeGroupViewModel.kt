package com.xiaoyv.bangumi.features.main.tab.home.page.group

import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.PageStatus
import org.orbitmvi.orbit.syntax.Syntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.repository.UgcRepository

class HomeGroupViewModel(
    stateHandle: SavedStateHandle,
    private val ugcRepository: UgcRepository,
) : BaseViewModel<HomeGroupState, Any, Any>(stateHandle) {
    override fun initBaseState(): UiState<HomeGroupState> = UiState(data = createInitialState(), status = PageStatus.Loading)

    override fun createInitialState(): HomeGroupState {
        return HomeGroupState()
    }

    override fun onEvent(event: Any) {
    }

    override suspend fun Syntax<UiState<HomeGroupState>, UiSideEffect<Any>>.refreshSync() {
        refreshGroupHomepage()
    }

    private suspend fun refreshGroupHomepage() = subIntent {
        ugcRepository.fetchGroupHomepage()
            .onFailure { reduceError { it } }
            .onSuccess {
                reduceData(forceRefresh = true) {
                    state.copy(
                        hotGroups = it.hotGroups,
                        newestGroups = it.newestGroups,
                        newestTopics = it.newestTopics
                    )
                }
            }
    }
}