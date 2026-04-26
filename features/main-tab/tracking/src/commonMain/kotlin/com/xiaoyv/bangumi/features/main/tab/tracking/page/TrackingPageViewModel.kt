package com.xiaoyv.bangumi.features.main.tab.tracking.page

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.types.CollectionEpisodeType
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.data.manager.app.PersonalStateStore
import com.xiaoyv.bangumi.shared.data.model.request.CollectionSubjectUpdate
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisode
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.grouped
import com.xiaoyv.bangumi.shared.data.repository.CollectionRepository
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.combine
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.collections.map

@Composable
fun koinTrackingPageViewModel(@SubjectType type: Int): TrackingPageViewModel {
    return koinViewModel(
        key = type.toString(),
        parameters = { parametersOf(type) }
    )
}


class TrackingPageViewModel(
    stateHandle: SavedStateHandle,
    @SubjectType type: Int,
    private val collectionRepository: CollectionRepository,
    private val personalStateStore: PersonalStateStore,
) : BaseViewModel<TrackingPageState, Any, TrackingPageEvent.Action>(stateHandle) {

    private val userCollectionPager = collectionRepository.fetchMyCollectionSubjectPager(
        subjectType = type,
        type = CollectionType.DOING,
        fetchEpisode = true
    )

    val collections = userCollectionPager.flow
        .cachedIn(viewModelScope)
        .combine(personalStateStore.state) { pagingData, personalState ->
            pagingData.map { subject ->
                personalState.subjects[subject.id]?.let { item ->
                    item.copy(
                        episodes = subject.episodes
                            .map { if (it.splitter != null) it else item.episodes.find { ep -> ep.id == it.id } ?: it }
                            .toImmutableList()
                            .grouped()
                    )
                } ?: subject
            }
        }
        .cachedIn(viewModelScope)

    override fun initSate(onCreate: Boolean): TrackingPageState = TrackingPageState()

    override fun onEvent(event: TrackingPageEvent.Action) {
        when (event) {
            is TrackingPageEvent.Action.OnUpdateEpisodeCollection -> onUpdateEpisodeCollectionBatch(
                subject = event.subject,
                episodes = event.episodes,
                type = event.type
            )

            is TrackingPageEvent.Action.OnUpdateSubjectCollection -> onUpdateSubjectCollection(
                subject = event.subject,
                update = event.update
            )
        }
    }

    private fun onUpdateSubjectCollection(
        subject: ComposeSubject,
        update: CollectionSubjectUpdate,
    ) = action {
        withActionLoading {
            collectionRepository.submitUpdateUserSubject(subject.id, update)
        }.onFailure {
            postToast { it.errMsg }
        }.onSuccess {
            personalStateStore.updateCollectionSubject(subject, update)
        }
    }

    private fun onUpdateEpisodeCollectionBatch(
        subject: ComposeSubject,
        episodes: List<ComposeEpisode>,
        @CollectionEpisodeType type: Int,
    ) = action {
        withActionLoading {
            collectionRepository.submitUpdateUserEpisode(subject.id, episodes, type)
        }.onFailure {
            postToast { it.errMsg }
        }.onSuccess {
            personalStateStore.updateCollectionEpisode(subject, episodes.map { it.id }, type)
        }
    }
}