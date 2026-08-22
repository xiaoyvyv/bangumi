package com.xiaoyv.bangumi.features.timeline.page.business

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.withActionLoading
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.data.manager.app.PersonalStateStore
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.model.request.list.timeline.ListTimelineParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.reaction.ComposeReaction
import com.xiaoyv.bangumi.shared.data.model.response.bgm.reaction.refreshReaction
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimeline
import com.xiaoyv.bangumi.shared.data.repository.TimelineRepository
import com.xiaoyv.bangumi.shared.data.repository.datasource.bindTimelinePersonalState
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
    param: ListTimelineParam,
    private val timelineRepository: TimelineRepository,
    private val personalStateStore: PersonalStateStore,
    private val userManager: UserManager,
) : BaseViewModel<TimelinePageState, TimelinePageSideEffect, TimelinePageEvent.Action>() {

    private val timelineController = timelineRepository.fetchTimelineDisplayPager(
        target = param.timelineMode,
        type = param.timelineCat,
        username = param.username
    )

    internal val timelines = timelineController.cachedIn(viewModelScope)

    init {
        timelineController.bindTimelinePersonalState(viewModelScope, personalStateStore)
    }

    override fun createInitialState() = TimelinePageState()

    override fun onEvent(event: TimelinePageEvent.Action) {
        when (event) {
            is TimelinePageEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is TimelinePageEvent.Action.OnClickRecation -> onClickRecation(event.timeline, event.reaction)
            is TimelinePageEvent.Action.OnDeleteTimeline -> onDeleteTimeline(event.timeline)
        }
    }

    private fun onClickRecation(timeline: ComposeTimeline, reaction: ComposeReaction) = intent {
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
        }
    }
}
