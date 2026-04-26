package com.xiaoyv.bangumi.features.timeline.page.business

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.timeline.ListTimelineParam
import com.xiaoyv.bangumi.shared.data.repository.UgcRepository
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
    ugcRepository: UgcRepository,
) : BaseViewModel<TimelinePageState, TimelinePageSideEffect, TimelinePageEvent.Action>(savedStateHandle) {
    private val timelinePager = ugcRepository.fetchTimelineDisplayPager(
        target = param.timlineMode,
        type = param.timelineCat,
        username = param.username
    )

    internal val timelines = timelinePager.flow.cachedIn(viewModelScope)

    override fun initSate(onCreate: Boolean) = TimelinePageState()

    override fun onEvent(event: TimelinePageEvent.Action) {
        when (event) {
            is TimelinePageEvent.Action.OnRefresh -> refresh(loading = event.loading)
        }
    }

}