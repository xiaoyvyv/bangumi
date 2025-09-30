package com.xiaoyv.bangumi.features.timeline.page.business

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.timeline.ListTimelineParam
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun koinTimelinePageViewModel(param: ListTimelineParam) = koinViewModel<TimelinePageViewModel>(
    key = param.uniqueKey,
    parameters = { parametersOf(param) }
)

/**
 * [TimelinePageViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class TimelinePageViewModel(
    savedStateHandle: SavedStateHandle,
    param: ListTimelineParam,
) : BaseViewModel<TimelinePageState, TimelinePageSideEffect, TimelinePageEvent.Action>(savedStateHandle) {

    override fun initSate(onCreate: Boolean) = TimelinePageState()

    override fun onEvent(event: TimelinePageEvent.Action) {
        when (event) {
            is TimelinePageEvent.Action.OnRefresh -> refresh(loading = event.loading)
        }
    }

}