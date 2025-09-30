package com.xiaoyv.bangumi.features.timeline.page.web

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
fun koinTimelineWebPageViewModel(param: ListTimelineParam): TimelineWebPageViewModel {
    return koinViewModel<TimelineWebPageViewModel>(
        key = param.uniqueKey,
        parameters = { parametersOf(param) }
    )
}

class TimelineWebPageViewModel(
    savedStateHandle: SavedStateHandle,
    param: ListTimelineParam,
    ugcRepository: UgcRepository,
) : BaseViewModel<Any, Any, Any>(savedStateHandle) {
    private val timelinePager = ugcRepository.fetchTimelinePager(
        target = param.browserWeb.target,
        type = param.browserWeb.type,
        username = param.browserWeb.username
    )

    internal val timelineFlow = timelinePager.flow.cachedIn(viewModelScope)

    override fun initSate(onCreate: Boolean) = Any()

    override fun onEvent(event: Any) {

    }
}