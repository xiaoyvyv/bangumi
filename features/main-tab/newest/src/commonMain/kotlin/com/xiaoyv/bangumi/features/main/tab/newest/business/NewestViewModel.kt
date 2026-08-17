package com.xiaoyv.bangumi.features.main.tab.newest.business

import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.utils.currentTime
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.toImmutableList

/**
 * [NewestViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class NewestViewModel(savedStateHandle: SavedStateHandle) :
    BaseViewModel<NewestState, NewestSideEffect, NewestEvent.Action>(savedStateHandle) {

    override fun createInitialState() = NewestState(
        tabs = createTabs(currentTime().year),
        year = currentTime().year,
        defaultMonth = currentTime().month.ordinal + 1,
    )

    override fun onEvent(event: NewestEvent.Action) {
        when (event) {
            is NewestEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is NewestEvent.Action.OnChangeYear -> onChangeYear(event.year)
        }
    }

    private fun onChangeYear(year: Int) = intent {
        reduceData {
            state.copy(tabs = createTabs(year), year = year)
        }
    }

    private fun createTabs(year: Int): SerializeList<ComposeTextTab<Int>> {
        return List(12) {
            val month = it + 1
            ComposeTextTab(type = month, labelText = "$year-${month.toString().padStart(2, '0')}")
        }.toImmutableList()
    }
}