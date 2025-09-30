package com.xiaoyv.bangumi.features.preivew.main.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.features.preivew.main.PreviewMainArguments
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import kotlinx.collections.immutable.toPersistentList

/**
 * [PreviewMainViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class PreviewMainViewModel(savedStateHandle: SavedStateHandle) :
    BaseViewModel<PreviewMainState, PreviewMainSideEffect, PreviewMainEvent.Action>(savedStateHandle) {
    private val args = PreviewMainArguments(savedStateHandle)

    override fun initSate(onCreate: Boolean) = PreviewMainState(
        items = args.items.toPersistentList(),
        index = args.index,
    )

    override fun onEvent(event: PreviewMainEvent.Action) {
        when (event) {
            is PreviewMainEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is PreviewMainEvent.Action.OnPageSelected -> onPageSelected(event.index)
        }
    }

    private fun onPageSelected(index: Int) = action {
        reduceContent { state.copy(index = index) }
    }
}