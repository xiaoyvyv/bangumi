package com.xiaoyv.bangumi.features.timeline.page.business

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.withActionLoading
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.data.manager.app.PersonalStateStore
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.model.request.list.timeline.ListTimelineParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.reaction.ComposeReaction
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimeline
import com.xiaoyv.bangumi.shared.data.repository.TimelineRepository
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.combine
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
    private val userManager: UserManager,
    private val personalStateStore: PersonalStateStore,
) : BaseViewModel<TimelinePageState, TimelinePageSideEffect, TimelinePageEvent.Action>() {

    private val timelinePager = timelineRepository.fetchTimelineDisplayPager(
        target = param.timelineMode,
        type = param.timelineCat,
        username = param.username
    )

    internal val timelines = timelinePager.flow
        .cachedIn(viewModelScope)
        .combine(personalStateStore.state) { pagingData, personalState ->
            pagingData
                .filter { it.id !in personalState.deletedTimelineIds }
                .map { personalState.timelines[it.id] ?: it }
        }
        .cachedIn(viewModelScope)

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
        val self = userManager.userInfo.username

        withActionLoading {
            timelineRepository.submitTimelineReaction(timeline.id, if (isLiked) null else reaction.value)
        }.onFailure {
            postToast { it.errMsg }
        }.onSuccess {
            // 先从全部的贴贴移除自己
            val reactions = timeline.reactions
                .map { it.copy(users = it.users.filter { user -> user.username != self }.toImmutableList()) }
                .toMutableList()

            // 时间线没有该贴贴直接添加一个
            val newReactions = if (reactions.find { it.value == reaction.value } == null) {
                reactions.add(reaction.copy(users = persistentListOf(userManager.userInfo)))
                reactions
            } else {
                // 添加
                if (!isLiked) {
                    reactions.map {
                        if (it.value == reaction.value) {
                            val users = it.users.toMutableList()
                            users.add(userManager.userInfo)
                            it.copy(users = users.toImmutableList())
                        } else {
                            it
                        }
                    }
                } else {
                    reactions
                }
            }

            val updatedTimeline = timeline.copy(
                reactions = newReactions.filter { it.users.isNotEmpty() }.toImmutableList()
            )
            personalStateStore.updateTimeline(timeline.id, updatedTimeline)
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