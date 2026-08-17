package com.xiaoyv.bangumi.features.mikan.studio.business

import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.SavedStateHandle
import org.orbitmvi.orbit.syntax.Syntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.utils.printTrace
import com.xiaoyv.bangumi.shared.data.repository.CacheRepository
import com.xiaoyv.bangumi.shared.data.repository.MikanRepository
import com.xiaoyv.bangumi.shared.data.repository.readViewModelCache
import com.xiaoyv.bangumi.shared.data.repository.writeViewModelCache
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [MikanStudioViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class MikanStudioViewModel(
    savedStateHandle: SavedStateHandle,
    private val args: Screen.MikanStudio,
    private val mikanRepository: MikanRepository,
    private val cacheRepository: CacheRepository,
) : BaseViewModel<MikanStudioState, MikanStudioSideEffect, MikanStudioEvent.Action>(savedStateHandle) {
    private val cacheKey = stringPreferencesKey(name = "mikan_studio_" + args.subjectId)

    override fun initBaseState() = readViewModelCache(
        cacheRepository = cacheRepository,
        cacheKey = cacheKey,
        loadWhenEmpty = true
    )

    override fun createInitialState() = MikanStudioState(
        mikanId = args.mikanId
    )

    override suspend fun Syntax<UiState<MikanStudioState>, UiSideEffect<MikanStudioSideEffect>>.refreshSync() {
        mikanRepository.fetchMikanGroup(args.mikanId)
            .onFailure {
                it.printTrace()
                reduceError { it }
            }
            .onSuccess {
                reduceData { state.copy(groupInfo = it) }

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