package com.xiaoyv.bangumi.features.subject.detail.business

import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.lifecycle.viewModelScope
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.collect_firstly
import com.xiaoyv.bangumi.core_resource.resources.collect_success
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.core.mvi.withActionLoading
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.LoadingState
import com.xiaoyv.bangumi.shared.core.utils.ResultZip2
import com.xiaoyv.bangumi.shared.core.utils.awaitAll
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.data.manager.app.PersonalStateStore
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.model.request.bgm.CollectionSubjectParam
import com.xiaoyv.bangumi.shared.data.model.request.bgm.CollectionSubjectProgressParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisode
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeParade
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectWebInfo
import com.xiaoyv.bangumi.shared.data.model.response.db.ComposeDoubanPhoto
import com.xiaoyv.bangumi.shared.data.repository.CacheRepository
import com.xiaoyv.bangumi.shared.data.repository.CollectionRepository
import com.xiaoyv.bangumi.shared.data.repository.SubjectRepository
import com.xiaoyv.bangumi.shared.data.repository.TopicRepository
import com.xiaoyv.bangumi.shared.data.repository.readViewModelCache
import com.xiaoyv.bangumi.shared.data.repository.writeViewModelCache
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.orbitmvi.orbit.syntax.Syntax

/**
 * [SubjectDetailViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class SubjectDetailViewModel(
    private val args: Screen.SubjectDetail,
    private val subjectRepository: SubjectRepository,
    private val topicRepository: TopicRepository,
    private val cacheRepository: CacheRepository,
    private val collectionRepository: CollectionRepository,
    private val personalStateStore: PersonalStateStore,
    private val userManager: UserManager,
) : BaseViewModel<SubjectDetailState, SubjectDetailSideEffect, SubjectDetailEvent.Action>() {

    private val cacheKey = byteArrayPreferencesKey(name = "subject:" + args.subjectId)

    init {
        // 条目更新
        viewModelScope.launch {
            personalStateStore.onSubjectUpdated
                .filter { it.id == args.subjectId }
                .collect { event ->
                    intent {
                        reduceData { state.copy(subject = event.data) }
                        saveCache()
                    }
                }
        }
    }


    override fun initBaseState() = readViewModelCache(
        cacheRepository = cacheRepository,
        cacheKey = cacheKey,
        loadWhenEmpty = true,
        enable = userManager.settings.ui.cacheState,
        transform = { it.copy(selectedTab = args.selectedTab) }
    )

    override fun createInitialState(): SubjectDetailState {
        return SubjectDetailState(
            id = args.subjectId,
            selectedTab = args.selectedTab,
            subject = ComposeSubject.Empty,
        )
    }

    private fun saveCache() {
        writeViewModelCache(
            cacheRepository = cacheRepository,
            cacheKey = cacheKey,
            saveCondition = { it.subject != ComposeSubject.Empty }
        )
    }

    override fun onEvent(event: SubjectDetailEvent.Action) {
        when (event) {
            is SubjectDetailEvent.Action.OnRefresh -> refresh(event.loading)
            is SubjectDetailEvent.Action.DeleteCollection -> onDeleteCollection()
            is SubjectDetailEvent.Action.OnUpdateEpisodeCollection -> onUpdateEpisodeCollection(event.episodes, event.type)
            is SubjectDetailEvent.Action.OnUpdateSubjectCollection -> onUpdateSubjectCollection(event.update, event.showLoadingDialog)
            is SubjectDetailEvent.Action.OnUpdateSubjectProgress -> onUpdateSubjectProgress(event.update)
        }
    }

    override suspend fun Syntax<UiState<SubjectDetailState>, UiSideEffect<SubjectDetailSideEffect>>.refreshSync() {
        awaitAll(
            block1 = { subjectRepository.fetchSubjectDetail(args.subjectId) },
            block2 = { subjectRepository.fetchSubjectDetailByWeb(args.subjectId).recover { ComposeSubjectWebInfo.Empty } },
            block3 = { subjectRepository.fetchSubjectAllEpisodes(args.subjectId, type = null) },
            block4 = { subjectRepository.fetchSubjectCharacter(args.subjectId, limit = 12) },
            block5 = { subjectRepository.fetchSubjectRelated(args.subjectId) },
        ).onFailure {
            reduceError { it }
        }.onSuccess {
            reduceData {
                state.copy(
                    subject = it.data1.copy(
                        episodes = it.data3.toPersistentList(),
                        webInfo = it.data2
                    ),
                    characters = it.data4.toPersistentList(),
                    related = it.data5.toPersistentList()
                )
            }

            onRefreshParadeAndPhoto()
        }
    }


    /**
     * 刷新豆瓣预览和巡礼图片
     */
    private suspend fun onRefreshParadeAndPhoto() = subIntent {
        val subject = state.data.subject
        reduceData { state.copy(previewLoading = LoadingState.Loading) }

        coroutineScope {
            val parade = async { subjectRepository.fetchSubjectParade(args.subjectId).recover { ComposeParade.Empty } }
            val photo = async { subjectRepository.fetchSubjectPreview(subject).recover { ComposeDoubanPhoto.Empty } }
            val myTags = async { subjectRepository.fetchMySubjectTags(args.subjectId).recover { emptyList() } }

            val preview = photo.await().getOrThrow()
            reduceData {
                state.copy(
                    photo = preview,
                    previewLoading = LoadingState.NotLoading,
                )
            }

            val refreshedParade = parade.await().getOrThrow()
            val refreshedTags = myTags.await().getOrThrow()
            reduceData {
                state.copy(
                    parade = refreshedParade,
                    myTags = refreshedTags.toPersistentList(),
                )
            }
        }

        personalStateStore.emitSubjectUpdated(args.subjectId, subject)
    }


    /**
     * 更新收藏
     */
    private fun onUpdateSubjectCollection(update: CollectionSubjectParam, showLoadingDialog: Boolean) = intent {
        reduceData { state.copy(loading = LoadingState.Loading) }

        withActionLoading(enable = showLoadingDialog) { collectionRepository.submitUpdateSubjectCollection(args.subjectId, update) }
            .onFailure {
                postToast { it.errMsg }

                reduceData { state.copy(loading = LoadingState.Error(it)) }
            }
            .onSuccess {
                postToast { getString(Res.string.collect_success) }
                reduceData { state.copy(loading = LoadingState.NotLoading) }

                personalStateStore.emitSubjectUpdated(
                    id = args.subjectId,
                    data = state.data.subject.copy(interest = state.data.subject.interest.updateFrom(update))
                )
            }
    }

    /**
     * 更新进度
     */
    private fun onUpdateSubjectProgress(update: CollectionSubjectProgressParam) = intent {
        withActionLoading {
            collectionRepository.submitUpdateSubjectProgress(args.subjectId, update)
                .mapCatching { onRefreshTrackingData().getOrThrow() }
        }.onFailure {
            postToast { it.errMsg }
        }.onSuccess {
            reduceData {
                state.copy(
                    subject = state.subject.copy(
                        episodes = it.data2.toPersistentList(),
                        eps = it.data1.eps,
                        volumes = it.data1.volumes,
                        interest = it.data1.interest,
                        collection = it.data1.collection,
                        rating = it.data1.rating,
                    ),
                )
            }

            personalStateStore.emitUpdateTrackingSuccess(args.subjectId)
        }
    }

    /**
     * 更新EP的进度
     */
    private fun onUpdateEpisodeCollection(episodes: List<ComposeEpisode>, type: Int) = intent {
        if (state.data.subject.interest.type == CollectionType.UNKNOWN) {
            postToast { getString(Res.string.collect_firstly) }
            return@intent
        }

        withActionLoading {
            collectionRepository.submitUpdateUserEpisode(args.subjectId, episodes, type)
                .mapCatching { onRefreshTrackingData().getOrThrow() }
        }.onFailure {
            postToast { it.errMsg }
        }.onSuccess {
            reduceData {
                state.copy(
                    subject = state.subject.copy(
                        episodes = it.data2.toPersistentList(),
                        eps = it.data1.eps,
                        volumes = it.data1.volumes,
                        interest = it.data1.interest,
                        collection = it.data1.collection,
                        rating = it.data1.rating,
                    ),
                )
            }

            personalStateStore.emitUpdateTrackingSuccess(args.subjectId)
        }
    }

    /**
     * 删除收藏
     */
    private fun onDeleteCollection() = intent {
        withActionLoading { collectionRepository.submitRemoveSubjectCollection(args.subjectId) }
            .onFailure { postToast { it.errMsg } }
            .onSuccess {
                personalStateStore.emitSubjectUpdated(
                    id = args.subjectId,
                    data = state.data.run {
                        subject.copy(interest = subject.interest.copy(type = CollectionType.UNKNOWN))
                    }
                )
            }
    }

    private suspend fun onRefreshTrackingData(): Result<ResultZip2<ComposeSubject, List<ComposeEpisode>>> {
        return awaitAll(
            block1 = { subjectRepository.fetchSubjectDetail(args.subjectId) },
            block2 = { subjectRepository.fetchSubjectAllEpisodes(args.subjectId, type = null) }
        )
    }
}
