package com.xiaoyv.bangumi.features.main.tab.tracking.business

import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_anime
import com.xiaoyv.bangumi.core_resource.resources.global_book
import com.xiaoyv.bangumi.core_resource.resources.global_real
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.data.repository.UserRepository
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.orbitmvi.orbit.syntax.Syntax

/**
 * [TrackingViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class TrackingViewModel(private val userRepository: UserRepository) : BaseViewModel<TrackingState, TrackingSideEffect, TrackingEvent.Action>() {
    override fun initBaseState(): UiState<TrackingState> = initBaseLoadingState()

    override fun createInitialState() = TrackingState(
        tabs = persistentListOf(
            ComposeTextTab(SubjectType.ANIME, Res.string.global_anime),
            ComposeTextTab(SubjectType.BOOK, Res.string.global_book),
            ComposeTextTab(SubjectType.REAL, Res.string.global_real),
        )
    )

    override fun onEvent(event: TrackingEvent.Action) {
        when (event) {
            is TrackingEvent.Action.OnRefresh -> refresh(contentLoading = event.loading)
        }
    }

    override suspend fun Syntax<UiState<TrackingState>, UiSideEffect<TrackingSideEffect>>.refreshSync() {
        onRefreshTrackingData()
    }

    private suspend fun onRefreshTrackingData() = subIntent {
        userRepository.fetchUserHomeInfo()
            .onFailure { reduceError { it } }
            .onSuccess {
                reduceData(forceRefresh = true) {
                    state.copy(
                        progressAnime = it.progress
                            .filter { progress -> progress.subject.type == SubjectType.ANIME }
                            .toImmutableList(),
                        progressBook = it.progress
                            .filter { progress -> progress.subject.type == SubjectType.BOOK }
                            .toImmutableList(),
                        progressReal = it.progress
                            .filter { progress -> progress.subject.type == SubjectType.REAL }
                            .toImmutableList(),
                    )
                }
            }
    }
}