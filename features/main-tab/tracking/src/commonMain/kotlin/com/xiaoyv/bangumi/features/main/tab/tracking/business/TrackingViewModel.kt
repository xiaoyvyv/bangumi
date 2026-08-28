package com.xiaoyv.bangumi.features.main.tab.tracking.business

import androidx.lifecycle.viewModelScope
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_anime
import com.xiaoyv.bangumi.core_resource.resources.global_book
import com.xiaoyv.bangumi.core_resource.resources.global_real
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.core.mvi.withActionLoading
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.manager.app.PersonalStateStore
import com.xiaoyv.bangumi.shared.data.model.request.bgm.CollectionSubjectParam
import com.xiaoyv.bangumi.shared.data.model.request.bgm.CollectionSubjectProgressParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisode
import com.xiaoyv.bangumi.shared.data.model.response.bgm.home.ComposeHomeProgress
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.repository.CollectionRepository
import com.xiaoyv.bangumi.shared.data.repository.UserRepository
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.syntax.Syntax

/**
 * [TrackingViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class TrackingViewModel(
    private val userRepository: UserRepository,
    private val collectionRepository: CollectionRepository,
    private val personalStateStore: PersonalStateStore,
) : BaseViewModel<TrackingState, TrackingSideEffect, TrackingEvent.Action>() {

    init {
        viewModelScope.launch {
            personalStateStore.updateTrackingSuccess.collectLatest {
                onRefreshTrackingData()
            }
        }
    }

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
            is TrackingEvent.Action.OnUpdateEpisode -> onUpdateEpisode(event.subject, event.eps, event.type)
            is TrackingEvent.Action.OnUpdateSubjectCollection -> onUpdateSubjectCollection(event.subject, event.param)
            is TrackingEvent.Action.OnUpdateSubjectProgress -> onUpdateSubjectProgress(event.subject, event.param)
        }
    }

    override suspend fun Syntax<UiState<TrackingState>, UiSideEffect<TrackingSideEffect>>.refreshSync() {
        onRefreshTrackingData()
    }

    private suspend fun onRefreshTrackingData() = subIntent {
        queryTrackingData()
            .onFailure { reduceError { it } }
            .onSuccess { updateTrackingState(it) }
    }

    private fun onUpdateSubjectCollection(subject: ComposeSubject, param: CollectionSubjectParam) = intent {
        withActionLoading {
            collectionRepository.submitUpdateSubjectCollection(subject.id, param)
                .mapCatching { queryTrackingData().getOrThrow() }
        }.onFailure {
            postToast { it.errMsg }
        }.onSuccess {
            updateTrackingState(it)
            personalStateStore.emitUpdateTrackingSuccess(subject.id)
        }
    }

    private fun onUpdateSubjectProgress(subject: ComposeSubject, param: CollectionSubjectProgressParam) = intent {
        withActionLoading {
            collectionRepository.submitUpdateSubjectProgress(subject.id, param)
                .mapCatching { queryTrackingData().getOrThrow() }
        }.onFailure {
            postToast { it.errMsg }
        }.onSuccess {
            updateTrackingState(it)
            personalStateStore.emitUpdateTrackingSuccess(subject.id)
        }
    }


    private fun onUpdateEpisode(subject: ComposeSubject, episodes: List<ComposeEpisode>, type: Int) = intent {
        withActionLoading {
            collectionRepository.submitUpdateUserEpisode(subject.id, episodes, type)
                .mapCatching { queryTrackingData().getOrThrow() }
        }.onFailure {
            postToast { it.errMsg }
        }.onSuccess {
            updateTrackingState(it)
            personalStateStore.emitUpdateTrackingSuccess(subject.id)
        }
    }

    private suspend fun Syntax<UiState<TrackingState>, UiSideEffect<TrackingSideEffect>>.updateTrackingState(
        data: Triple<ImmutableList<ComposeHomeProgress>, ImmutableList<ComposeHomeProgress>, ImmutableList<ComposeHomeProgress>>,
    ) {
        reduceData(forceRefresh = true) {
            // 对每一类数据进行稳定排序，保证条目位置不会因状态更新而乱跳
            val (anime, animeOrder) = data.first.stableSort(state.animeOrder)
            val (book, bookOrder) = data.second.stableSort(state.bookOrder)
            val (real, realOrder) = data.third.stableSort(state.realOrder)

            state.copy(
                progressAnime = anime,
                progressBook = book,
                progressReal = real,
                animeOrder = animeOrder,
                bookOrder = bookOrder,
                realOrder = realOrder,
            )
        }
    }

    /**
     * 稳定排序逻辑：
     * 1. 首次加载时（oldOrder 为空），完全遵循服务端返回的顺序。
     * 2. 后续刷新时，已存在的条目固定在原位，新条目追加到列表末尾。
     */
    private fun List<ComposeHomeProgress>.stableSort(
        oldOrder: List<Long>,
    ): Pair<ImmutableList<ComposeHomeProgress>, SerializeList<Long>> {
        val currentIds = map { it.subject.id }
        // 过滤出当前列表里不在旧顺序中的新 ID
        val newIds = currentIds.filter { it !in oldOrder }
        // 合并顺序：旧顺序 + 新条目 ID
        val newOrder = (oldOrder + newIds).toImmutableList()

        // 构建 ID 到索引的映射，用于快速排序
        val idToIndex = newOrder.withIndex().associate { it.value to it.index }
        val sortedList = sortedBy { idToIndex[it.subject.id] ?: Int.MAX_VALUE }.toImmutableList()

        return sortedList to newOrder
    }

    private suspend fun queryTrackingData(): Result<Triple<ImmutableList<ComposeHomeProgress>, ImmutableList<ComposeHomeProgress>, ImmutableList<ComposeHomeProgress>>> {
        return userRepository.fetchUserHomeInfo().map {
            val progressAnime = it.progress
                .filter { progress -> progress.subject.type == SubjectType.ANIME }
                .toImmutableList()
            val progressBook = it.progress
                .filter { progress -> progress.subject.type == SubjectType.BOOK }
                .toImmutableList()
            val progressReal = it.progress
                .filter { progress -> progress.subject.type == SubjectType.REAL }
                .toImmutableList()
            Triple(progressAnime, progressBook, progressReal)
        }
    }
}