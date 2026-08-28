package com.xiaoyv.bangumi.features.mono.detail.business

import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.collect_cancel_success
import com.xiaoyv.bangumi.core_resource.resources.collect_success
import com.xiaoyv.bangumi.core_resource.resources.mono_detail_tietie_not_available
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.core.mvi.withActionLoading
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.PublishPostType
import com.xiaoyv.bangumi.shared.core.utils.awaitAll
import com.xiaoyv.bangumi.shared.core.utils.mutableStateFlowOf
import com.xiaoyv.bangumi.shared.data.manager.app.PersonalStateStore
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMono
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.reaction.ComposeReaction
import com.xiaoyv.bangumi.shared.data.repository.CacheRepository
import com.xiaoyv.bangumi.shared.data.repository.CollectionRepository
import com.xiaoyv.bangumi.shared.data.repository.ImageRepository
import com.xiaoyv.bangumi.shared.data.repository.MonoRepository
import com.xiaoyv.bangumi.shared.data.repository.readViewModelCache
import com.xiaoyv.bangumi.shared.data.repository.writeViewModelCache
import com.xiaoyv.bangumi.shared.data.usecase.MonoRepoUseCase
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.orbitmvi.orbit.syntax.Syntax

/**
 * [MonoDetailViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class MonoDetailViewModel(
    private val args: Screen.MonoDetail,
    private val monoRepoUseCase: MonoRepoUseCase,
    private val cacheRepository: CacheRepository,
    private val imageRepository: ImageRepository,
    private val monoRepository: MonoRepository,
    private val collectionRepository: CollectionRepository,
    private val personalStateStore: PersonalStateStore,
    private val userManager: UserManager,
) : BaseViewModel<MonoDetailState, MonoDetailSideEffect, MonoDetailEvent.Action>() {

    private val cacheKey = byteArrayPreferencesKey(name = "mono:${args.type}:${args.id}")

    /**
     * 来自 anime-pictures.net 的图片数据
     */
    private val animePicTag = mutableStateFlowOf<String?>(null)
    internal val animePicImages = animePicTag
        .filterNotNull()
        .flatMapLatest { tags ->
            imageRepository.fetchAnimePictures(searchTags = tags).flow
        }
        .cachedIn(viewModelScope)

    /**
     * 来自 pixiv.net 的图片数据
     */
    private val pixivTag = mutableStateFlowOf<String?>(null)
    internal val pixivImages = pixivTag
        .filterNotNull()
        .flatMapLatest { tag ->
            imageRepository.fetchPixivPictures(tag = tag).flow
        }
        .cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            personalStateStore.onMonoUpdated
                .filter { it.id == args.id }
                .collect { event ->
                    intent {
                        reduceData { state.copy(mono = event.data) }
                        saveCache()
                    }
                }
        }

        viewModelScope.launch {
            personalStateStore.publishSuccess
                .filter {
                    it.type == PublishPostType.COMMENT_CHARACTER && args.type == MonoType.CHARACTER && it.publishAttachId.toLongOrNull() == args.id
                            || it.type == PublishPostType.COMMENT_PERSON && args.type == MonoType.PERSON && it.publishAttachId.toLongOrNull() == args.id
                }
                .collect {
                    onRefreshComments()
                }
        }
    }

    override fun initBaseState() = readViewModelCache(
        cacheRepository = cacheRepository,
        cacheKey = cacheKey,
        loadWhenEmpty = true,
        transform = {
            it.copy(mono = it.mono.copy(webInfo = it.mono.webInfo))
        }
    )

    override fun createInitialState() = MonoDetailState(
        id = args.id,
        type = args.type,
    )

    private fun saveCache() = writeViewModelCache(
        cacheRepository = cacheRepository,
        cacheKey = cacheKey,
        saveCondition = { it.mono != ComposeMono.Empty }
    )


    override fun onEvent(event: MonoDetailEvent.Action) {
        when (event) {
            is MonoDetailEvent.Action.OnRefresh -> refresh(event.loading)
            is MonoDetailEvent.Action.OnToggleBookmarkMono -> onToggleBookmarkMono()
            is MonoDetailEvent.Action.OnReactionClick -> onReactionClick(event.comment, event.reaction)
        }
    }

    override suspend fun Syntax<UiState<MonoDetailState>, UiSideEffect<MonoDetailSideEffect>>.refreshSync() {
        if (args.type == MonoType.CHARACTER) {
            awaitAll(
                block1 = { monoRepoUseCase.fetchMonoDetail(args.id, args.type) },
                block2 = { monoRepository.fetchCharacterCasts(args.id) },
                block3 = { monoRepository.fetchMonoComments(args.id, MonoType.CHARACTER) },
            ).onFailure {
                reduceError { it }
            }.onSuccess {
                reduceData {
                    state.copy(
                        mono = it.data1,
                        casts = it.data2.toPersistentList(),
                        comments = it.data3.toPersistentList()
                    )
                }
            }
        } else {
            awaitAll(
                block1 = { monoRepoUseCase.fetchMonoDetail(args.id, args.type) },
                block2 = { monoRepository.fetchPersonCast(args.id, limit = 5) },
                block3 = { monoRepository.fetchMonoComments(args.id, MonoType.PERSON) },
                block4 = { monoRepository.fetchPersonWorks(args.id, limit = 5) },
            ).onFailure {
                reduceError { it }
            }.onSuccess {
                reduceData {
                    state.copy(
                        mono = it.data1,
                        casts = it.data2.toPersistentList(),
                        comments = it.data3.toPersistentList(),
                        works = it.data4.toPersistentList()
                    )
                }
            }
        }

        personalStateStore.emitMonoUpdated(args.id, state.data.mono)

        fetchSearchImageTags(state.data.mono)

        // 获取作品 TAB 下的职位过滤选项的菜单
        if (args.type == MonoType.PERSON) fetchPersonWorkPosition()
    }

    private fun onToggleBookmarkMono() = intent {
        val isBookmarked = state.data.mono.collectedAt > 0
        val toast = if (isBookmarked) getString(Res.string.collect_cancel_success) else getString(Res.string.collect_success)

        withActionLoading { collectionRepository.submitBookmarkOrCancelMono(args.id, args.type, !isBookmarked) }
            .onSuccess {
                postToast { toast }

                // 更新
                personalStateStore.emitMonoUpdated(args.id, state.data.mono.copy(collectedAt = if (it) System.currentTimeMillis() else 0))
            }
    }

    private fun fetchPersonWorkPosition() = intent {
        monoRepository.fetchPersonWorkPosition(args.id)
            .onSuccess {
                reduceData { state.copy(positions = it.toPersistentList()) }
            }
    }

    /**
     * 获取搜索TAG
     */
    private fun fetchSearchImageTags(data: ComposeMono) = intent {
        pixivTag.update { data.name }

        imageRepository.fetchAnimePictureTag(data)
            .onSuccess { tags ->
                animePicTag.update { tags.joinToString("||") }
            }
    }

    private fun onRefreshComments() = intent {
        if (args.type == MonoType.CHARACTER) {
            monoRepository.fetchMonoComments(args.id, MonoType.CHARACTER)
        } else {
            monoRepository.fetchMonoComments(args.id, MonoType.PERSON)
        }.onSuccess {
            reduceData { state.copy(comments = it.toPersistentList()) }
        }
    }


    private fun onReactionClick(comment: ComposeReply, reaction: ComposeReaction) = intent {
        val isLiked = reaction.users.any { it.username == userManager.userInfo.username }
        val self = userManager.userInfo.username

        postToast { getString(Res.string.mono_detail_tietie_not_available) }
    }
}
