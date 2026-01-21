package com.xiaoyv.bangumi.features.subject.detail.business

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.collect_firstly
import com.xiaoyv.bangumi.core_resource.resources.collect_success
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.LoadingState
import com.xiaoyv.bangumi.shared.core.utils.awaitAll
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.data.manager.app.PersonalStateStore
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.model.request.CollectionSubjectUpdate
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisode
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeParade
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSubjectWebInfo
import com.xiaoyv.bangumi.shared.data.model.response.db.ComposeDoubanPhoto
import com.xiaoyv.bangumi.shared.data.repository.CacheRepository
import com.xiaoyv.bangumi.shared.data.repository.CollectionRepository
import com.xiaoyv.bangumi.shared.data.repository.SubjectRepository
import com.xiaoyv.bangumi.shared.data.repository.readViewModelCache
import com.xiaoyv.bangumi.shared.data.repository.writeViewModelCache
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.resources.getString

/**
 * [SubjectDetailViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class SubjectDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val args: Screen.SubjectDetail,
    private val subjectRepository: SubjectRepository,
    private val cacheRepository: CacheRepository,
    private val collectionRepository: CollectionRepository,
    private val personalStateStore: PersonalStateStore,
    private val userManager: UserManager,
) : BaseViewModel<SubjectDetailState, SubjectDetailSideEffect, SubjectDetailEvent.Action>(savedStateHandle) {
    private val subjectCommentPager = subjectRepository.fetchSubjectCommentPager(args.subjectId)

    internal val subjectComments = subjectCommentPager.flow
        .cachedIn(viewModelScope)

    private val cacheKey = stringPreferencesKey(name = "subject_detail_" + args.subjectId)

    init {
        personalStateStore.state
            .drop(1)
            .onEach {
                val subject = it.subjects[args.subjectId]
                if (subject != null) action {
                    reduceContent { state.copy(subject = subject) }
                    saveCache()
                }
            }
            .launchIn(viewModelScope)
    }

    override fun initBaseState() = readViewModelCache(
        cacheRepository = cacheRepository,
        cacheKey = cacheKey,
        loadWhenEmpty = true,
        enable = userManager.settings.ui.cacheState,
        transform = { it.copy(subject = it.subject.restoreHtml()) }
    )

    override fun initSate(onCreate: Boolean): SubjectDetailState {
        return SubjectDetailState(
            id = args.subjectId,
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
            is SubjectDetailEvent.Action.OnUpdateSubjectCollection -> onUpdateSubjectCollection(event.update, event.showLoadingDialog)
            is SubjectDetailEvent.Action.OnUpdateEpisodeCollection -> onUpdateEpisodeCollection(event.episodes, event.type)
        }
    }

    override suspend fun BaseSyntax<SubjectDetailState, SubjectDetailSideEffect>.refreshSync() {
        awaitAll(
            block1 = { subjectRepository.fetchSubjectDetail(args.subjectId) },
            block2 = { subjectRepository.fetchSubjectDetailByWeb(args.subjectId).recover { ComposeSubjectWebInfo.Empty } },
            block3 = { subjectRepository.fetchSubjectEpisodes(args.subjectId, type = null) },
            block4 = { subjectRepository.fetchSubjectCharacter(args.subjectId, limit = 12) },
            block5 = { subjectRepository.fetchSubjectRelated(args.subjectId) },
        ).onFailure {
            reduceError { it }
        }.onSuccess {
            reduceContent {
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
    private suspend fun onRefreshParadeAndPhoto() = subAction {
        awaitAll(
            block1 = { subjectRepository.fetchSubjectParade(args.subjectId).recover { ComposeParade.Empty } },
            block2 = { subjectRepository.fetchSubjectPreview(stateRaw.subject).recover { ComposeDoubanPhoto.Empty } },
            block3 = { subjectRepository.fetchMySubjectTags(args.subjectId).recover { emptyList() } },
        ).onSuccess {
            reduceContent {
                state.copy(
                    parade = it.data1,
                    photo = it.data2,
                    myTags = it.data3.toPersistentList(),
                )
            }

            personalStateStore.updateSubject(args.subjectId, stateRaw.subject)
        }
    }

    private fun onUpdateEpisodeCollection(episodes: List<ComposeEpisode>, type: Int) = action {
        if (stateRaw.subject.interest.type == CollectionType.UNKNOWN) {
            postToast { getString(Res.string.collect_firstly) }
            return@action
        }

        withActionLoading { collectionRepository.submitUpdateUserEpisode(args.subjectId, episodes, type) }
            .onFailure { postToast { it.errMsg } }
            .onSuccess {
                personalStateStore.updateCollectionEpisode(stateRaw.subject, episodes.map { it.id }, type)
            }
    }

    private fun onUpdateSubjectCollection(update: CollectionSubjectUpdate, showLoadingDialog: Boolean) = action {
        reduceContent { state.copy(loading = LoadingState.Loading) }

        withActionLoading(showLoading = showLoadingDialog) { collectionRepository.submitUpdateUserSubject(args.subjectId, update) }
            .onFailure {
                postToast { it.errMsg }

                reduceContent { state.copy(loading = LoadingState.Error(it)) }
            }
            .onSuccess {
                postToast { getString(Res.string.collect_success) }
                reduceContent { state.copy(loading = LoadingState.NotLoading) }

                personalStateStore.updateSubject(args.subjectId, stateRaw.run {
                    subject.copy(interest = subject.interest.updateFrom(update))
                })

            }
    }

    private fun onDeleteCollection() = action {
        withActionLoading { collectionRepository.submitRemoveSubjectCollection(args.subjectId) }
            .onSuccess {
                personalStateStore.updateSubject(args.subjectId, stateRaw.run {
                    subject.copy(interest = subject.interest.copy(type = CollectionType.UNKNOWN))
                })
            }
    }

}