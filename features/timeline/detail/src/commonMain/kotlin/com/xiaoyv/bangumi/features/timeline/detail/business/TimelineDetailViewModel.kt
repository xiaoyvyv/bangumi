package com.xiaoyv.bangumi.features.timeline.detail.business

import androidx.lifecycle.viewModelScope
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.PageStatus
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.postEffect
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.core.mvi.withActionLoading
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.data.manager.app.PersonalStateStore
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.model.response.bgm.reaction.ComposeReaction
import com.xiaoyv.bangumi.shared.data.model.response.bgm.reaction.refreshReaction
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimeline
import com.xiaoyv.bangumi.shared.data.repository.TimelineRepository
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    private val personalStateStore: PersonalStateStore,
    private val userManager: UserManager,
) : BaseViewModel<TimelineDetailState, TimelineDetailSideEffect, TimelineDetailEvent.Action>() {

    init {
        personalStateStore.state
            .drop(1)
            .onEach { personalState ->
                personalState.timelines[args.timeline.id]?.let { timeline ->
                    intent { reduceData { state.copy(timeline = timeline) } }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun initBaseState(): UiState<TimelineDetailState> = UiState(
        data = createInitialState(),
        status = PageStatus.Loading,
    )

    override fun createInitialState() = TimelineDetailState(timeline = args.timeline)

    override fun onEvent(event: TimelineDetailEvent.Action) {
        when (event) {
            is TimelineDetailEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is TimelineDetailEvent.Action.OnClickReaction -> onClickReaction(event.timeline, event.reaction)
            is TimelineDetailEvent.Action.OnDeleteTimeline -> onDeleteTimeline(event.timeline)
            TimelineDetailEvent.Action.OnAppendComment -> onAppendComment()
        }
    }

    private fun onClickReaction(timeline: ComposeTimeline, reaction: ComposeReaction) = intent {
        val isLiked = reaction.users.any { it.username == userManager.userInfo.username }

        withActionLoading {
            timelineRepository.submitTimelineReaction(timeline.id, if (isLiked) null else reaction.value)
        }.onFailure {
            postToast { it.errMsg }
        }.onSuccess {
            personalStateStore.updateTimeline(timeline.id, timeline.refreshReaction(userManager, reaction))
        }
    }

    private fun onDeleteTimeline(timeline: ComposeTimeline) = intent {
        withActionLoading {
            timelineRepository.submitDeleteTimeline(timeline.id)
        }.onFailure {
            postToast { it.errMsg }
        }.onSuccess {
            personalStateStore.deleteTimeline(timeline.id)
            postEffect { TimelineDetailSideEffect.OnNavUp }
        }
    }

    private fun onAppendComment() = intent {
        timelineRepository.fetchTimelineReplies(args.timeline.id)
            .onFailure { postToast { it.errMsg } }
            .onSuccess { replies ->
                val updatedTimeline = state.data.timeline.copy(replies = state.data.timeline.replies + 1)
                personalStateStore.updateTimeline(updatedTimeline.id, updatedTimeline)
                reduceData { state.copy(replies = replies.toImmutableList()) }
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
