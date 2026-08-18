package com.xiaoyv.bangumi.features.preivew.main.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import kotlinx.collections.immutable.toPersistentList

/**
 * [PreviewMainViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class PreviewMainViewModel(
    private val args: Screen.PreviewMain,
) : BaseViewModel<PreviewMainState, PreviewMainSideEffect, PreviewMainEvent.Action>() {

    override fun createInitialState() = PreviewMainState(
        items = args.items.toPersistentList(),
        index = args.index,
    )

    override fun onEvent(event: PreviewMainEvent.Action) {
        when (event) {
            is PreviewMainEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is PreviewMainEvent.Action.OnPageSelected -> onPageSelected(event.index)
        }
    }

    private fun onPageSelected(index: Int) = intent {
        reduceData { state.copy(index = index) }
    }
}