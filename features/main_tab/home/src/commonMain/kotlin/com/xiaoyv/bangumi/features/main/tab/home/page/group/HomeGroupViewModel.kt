package com.xiaoyv.bangumi.features.main.tab.home.page.group

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.repository.UgcRepository

class HomeGroupViewModel(
    stateHandle: SavedStateHandle,
    private val ugcRepository: UgcRepository,
) : BaseViewModel<HomeGroupState, Any, Any>(stateHandle) {
    override fun initBaseState(): BaseState<HomeGroupState> = BaseState.Loading()

    override fun initSate(onCreate: Boolean): HomeGroupState {
        return HomeGroupState()
    }

    override fun onEvent(event: Any) {
    }

    override suspend fun BaseSyntax<HomeGroupState, Any>.refreshSync() {
        refreshGroupHomepage()
    }

    private suspend fun refreshGroupHomepage() = subAction {
        ugcRepository.fetchGroupHomepage()
            .onFailure { reduceError { it } }
            .onSuccess {
                reduceContent(forceRefresh = true) {
                    state.copy(
                        hotGroups = it.hotGroups,
                        newestGroups = it.newestGroups,
                        newestTopics = it.newestTopics
                    )
                }
            }
    }
}