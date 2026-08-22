package com.xiaoyv.bangumi.features.main.tab.tracking.page

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.withActionLoading
import com.xiaoyv.bangumi.shared.core.types.CollectionEpisodeType
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.data.manager.app.PersonalStateStore
import com.xiaoyv.bangumi.shared.data.model.request.CollectionSubjectUpdate
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisode
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.repository.CollectionRepository
import com.xiaoyv.bangumi.shared.data.repository.datasource.bindCollectionSubjectPersonalState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun koinTrackingPageViewModel(@SubjectType type: Int): TrackingPageViewModel {
    return koinViewModel(
        key = type.toString(),
        parameters = { parametersOf(type) }
    )
}


class TrackingPageViewModel(
    @SubjectType type: Int,
    private val collectionRepository: CollectionRepository,
    private val personalStateStore: PersonalStateStore,
) : BaseViewModel<TrackingPageState, Any, TrackingPageEvent.Action>() {

    private val userCollectionController = collectionRepository.fetchMyCollectionSubjectPager(
        subjectType = type,
        type = CollectionType.DOING,
        fetchEpisode = true
    )

    val collections = userCollectionController.cachedIn(viewModelScope)

    init {
        userCollectionController.bindCollectionSubjectPersonalState(viewModelScope, personalStateStore)
    }

    override fun createInitialState(): TrackingPageState = TrackingPageState()

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
    ) = intent {
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
    ) = intent {
        withActionLoading {
            collectionRepository.submitUpdateUserEpisode(subject.id, episodes, type)
        }.onFailure {
            postToast { it.errMsg }
        }.onSuccess {
            personalStateStore.updateCollectionEpisode(subject, episodes.map { it.id }, type)
        }
    }
}
