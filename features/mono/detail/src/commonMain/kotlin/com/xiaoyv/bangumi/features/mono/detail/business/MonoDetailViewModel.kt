package com.xiaoyv.bangumi.features.mono.detail.business

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.collect_cancel_success
import com.xiaoyv.bangumi.core_resource.resources.collect_success
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.utils.awaitAll
import com.xiaoyv.bangumi.shared.core.utils.mutableStateFlowOf
import com.xiaoyv.bangumi.shared.data.manager.app.PersonalStateStore
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMono
import com.xiaoyv.bangumi.shared.data.repository.CacheRepository
import com.xiaoyv.bangumi.shared.data.repository.CollectionRepository
import com.xiaoyv.bangumi.shared.data.repository.ImageRepository
import com.xiaoyv.bangumi.shared.data.repository.MonoRepository
import com.xiaoyv.bangumi.shared.data.repository.readViewModelCache
import com.xiaoyv.bangumi.shared.data.repository.writeViewModelCache
import com.xiaoyv.bangumi.shared.data.usecase.MonoRepoUseCase
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.getString

/**
 * [MonoDetailViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class MonoDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val args: Screen.MonoDetail,
    private val monoRepoUseCase: MonoRepoUseCase,
    private val cacheRepository: CacheRepository,
    private val imageRepository: ImageRepository,
    private val monoRepository: MonoRepository,
    private val collectionRepository: CollectionRepository,
    private val personalStateStore: PersonalStateStore,
) : BaseViewModel<MonoDetailState, MonoDetailSideEffect, MonoDetailEvent.Action>(savedStateHandle) {

    private val cacheKey = stringPreferencesKey(name = "mono_detail_${args.type}_" + args.id)

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
        personalStateStore.state
            .drop(1)
            .onEach {
                val mono = it.monos[args.id]
                if (mono != null) action {
                    reduceContent { state.copy(mono = mono) }
                    saveCache()
                }
            }
            .launchIn(viewModelScope)
    }

    override fun initBaseState() = readViewModelCache(
        cacheRepository = cacheRepository,
        cacheKey = cacheKey,
        loadWhenEmpty = true,
        transform = {
            it.copy(mono = it.mono.copy(webInfo = it.mono.webInfo.restore()))
        }
    )

    override fun initSate(onCreate: Boolean) = MonoDetailState(
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
        }
    }

    override suspend fun BaseSyntax<MonoDetailState, MonoDetailSideEffect>.refreshSync() {
        if (args.type == MonoType.CHARACTER) {
            awaitAll(
                block1 = { monoRepoUseCase.fetchMonoDetail(args.id, args.type) },
                block2 = { monoRepository.fetchCharacterCasts(args.id) },
            ).onFailure {
                reduceError { it }
            }.onSuccess {
                reduceContent {
                    state.copy(
                        mono = it.data1,
                        casts = it.data2.toPersistentList()
                    )
                }
            }
        } else {
            awaitAll(
                block1 = { monoRepoUseCase.fetchMonoDetail(args.id, args.type) },
                block2 = { monoRepository.fetchPersonCast(args.id, limit = 5) },
                block3 = { monoRepository.fetchPersonWorks(args.id, limit = 5) },
            ).onFailure {
                reduceError { it }
            }.onSuccess {
                reduceContent {
                    state.copy(
                        mono = it.data1,
                        casts = it.data2.toPersistentList(),
                        works = it.data3.toPersistentList()
                    )
                }
            }
        }

        personalStateStore.updateMono(args.id, stateRaw.mono)

        fetchSearchImageTags(stateRaw.mono)

        if (args.type == MonoType.PERSON) fetchPersonWorkPosition()
    }

    private fun onToggleBookmarkMono() = action {
        val isBookmarked = stateRaw.mono.collectedAt > 0
        val toast = if (isBookmarked) getString(Res.string.collect_cancel_success) else getString(Res.string.collect_success)

        withActionLoading { collectionRepository.submitBookmarkOrCancelMono(args.id, args.type, !isBookmarked) }
            .onSuccess {
                postToast { toast }

                // 更新
                personalStateStore.updateMono(args.id, stateRaw.mono.copy(collectedAt = if (it) System.currentTimeMillis() else 0))
            }
    }

    private fun fetchPersonWorkPosition() = action {
        monoRepository.fetchPersonWorkPosition(args.id)
            .onSuccess {
                reduceContent { state.copy(positions = it.toPersistentList()) }
            }
    }

    /**
     * 获取搜索TAG
     */
    private fun fetchSearchImageTags(data: ComposeMono) = action {
        pixivTag.update { data.name }

        imageRepository.fetchAnimePictureTag(data)
            .onSuccess { tags ->
                animePicTag.update { tags.joinToString("||") }
            }
    }
}