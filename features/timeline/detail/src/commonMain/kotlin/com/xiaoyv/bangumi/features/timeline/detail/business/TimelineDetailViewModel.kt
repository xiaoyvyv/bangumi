package com.xiaoyv.bangumi.features.timeline.detail.business

import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.PageStatus
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.data.repository.TimelineRepository
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import kotlinx.collections.immutable.toImmutableList
import org.orbitmvi.orbit.syntax.Syntax

/**
 * [TimelineDetailViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class TimelineDetailViewModel(
    private val args: Screen.TimelineDetail,
    private val timelineRepository: TimelineRepository,
) : BaseViewModel<TimelineDetailState, TimelineDetailSideEffect, TimelineDetailEvent.Action>() {

    override fun initBaseState(): UiState<TimelineDetailState> = UiState(
        data = createInitialState(),
        status = PageStatus.Loading,
    )

    override fun createInitialState() = TimelineDetailState(timeline = args.timeline)

    override fun onEvent(event: TimelineDetailEvent.Action) {
        when (event) {
            is TimelineDetailEvent.Action.OnRefresh -> refresh(loading = event.loading)
        }
    }

    override suspend fun Syntax<UiState<TimelineDetailState>, UiSideEffect<TimelineDetailSideEffect>>.refreshSync() {
        timelineRepository.fetchTimelineReplies(args.timeline.id)
            .onFailure { reduceError { it } }
            .onSuccess { replies ->
                reduceData { state.copy(replies = replies.toImmutableList()) }
            }
    }

}
