package com.xiaoyv.bangumi.features.main.tab.home.page.mono

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.repository.MonoRepository
import kotlinx.collections.immutable.toPersistentList

class HomeMonoViewModel(
    stateHandle: SavedStateHandle,
    private val monoRepository: MonoRepository,
) : BaseViewModel<HomeMonoState, Any, Any>(stateHandle) {
    override fun initBaseState(): BaseState<HomeMonoState> = BaseState.Loading()

    override fun initSate(onCreate: Boolean): HomeMonoState {
        return HomeMonoState()
    }

    override fun onEvent(event: Any) {

    }

    override suspend fun BaseSyntax<HomeMonoState, Any>.refreshSync() {
        refreshMonoHomepage()
    }

    private suspend fun refreshMonoHomepage() = subAction {
        monoRepository.fetchMonoHomepage()
            .onFailure { reduceError { it } }
            .onSuccess {
                reduceContent(forceRefresh = true) {
                    state.copy(sections = it.toPersistentList())
                }
            }
    }
}