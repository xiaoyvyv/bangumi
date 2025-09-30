package com.xiaoyv.bangumi.features.mikan.studio.business

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.features.mikan.studio.MikanStudioArguments
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.utils.printTrace
import com.xiaoyv.bangumi.shared.data.repository.CacheRepository
import com.xiaoyv.bangumi.shared.data.repository.MikanRepository
import com.xiaoyv.bangumi.shared.data.repository.readViewModelCache
import com.xiaoyv.bangumi.shared.data.repository.writeViewModelCache

/**
 * [MikanStudioViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class MikanStudioViewModel(
    savedStateHandle: SavedStateHandle,
    private val mikanRepository: MikanRepository,
    private val cacheRepository: CacheRepository,
) : BaseViewModel<MikanStudioState, MikanStudioSideEffect, MikanStudioEvent.Action>(savedStateHandle) {
    private val args = MikanStudioArguments(savedStateHandle)
    private val cacheKey = stringPreferencesKey(name = "mikan_studio_" + args.id)

    override fun initBaseState() = readViewModelCache(
        cacheRepository = cacheRepository,
        cacheKey = cacheKey,
        loadWhenEmpty = true
    )

    override fun initSate(onCreate: Boolean) = MikanStudioState(
        mikanId = args.mikanId
    )

    override suspend fun BaseSyntax<MikanStudioState, MikanStudioSideEffect>.refreshSync() {
        mikanRepository.fetchMikanGroup(args.mikanId)
            .onFailure {
                it.printTrace()
                reduceError { it }
            }
            .onSuccess {
                reduceContent { state.copy(groupInfo = it) }

                writeViewModelCache(
                    cacheRepository = cacheRepository,
                    cacheKey = cacheKey
                )
            }
    }

    override fun onEvent(event: MikanStudioEvent.Action) {
        when (event) {
            is MikanStudioEvent.Action.OnRefresh -> refresh(loading = event.loading)
        }
    }

}