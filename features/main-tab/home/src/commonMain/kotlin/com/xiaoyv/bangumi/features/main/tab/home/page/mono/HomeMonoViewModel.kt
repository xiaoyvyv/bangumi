package com.xiaoyv.bangumi.features.main.tab.home.page.mono

import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.PageStatus
import org.orbitmvi.orbit.syntax.Syntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.repository.MonoRepository
import kotlinx.collections.immutable.toPersistentList

class HomeMonoViewModel(
    stateHandle: SavedStateHandle,
    private val monoRepository: MonoRepository,
) : BaseViewModel<HomeMonoState, Any, Any>(stateHandle) {
    override fun initBaseState(): UiState<HomeMonoState> = UiState(data = createInitialState(), status = PageStatus.Loading)

    override fun createInitialState(): HomeMonoState {
        return HomeMonoState()
    }

    override fun onEvent(event: Any) {

    }

    override suspend fun Syntax<UiState<HomeMonoState>, UiSideEffect<Any>>.refreshSync() {
        refreshMonoHomepage()
    }

    private suspend fun refreshMonoHomepage() = subIntent {
        monoRepository.fetchMonoHomepage()
            .onFailure { reduceError { it } }
            .onSuccess {
                reduceData(forceRefresh = true) {
                    state.copy(sections = it.toPersistentList())
                }
            }
    }
}