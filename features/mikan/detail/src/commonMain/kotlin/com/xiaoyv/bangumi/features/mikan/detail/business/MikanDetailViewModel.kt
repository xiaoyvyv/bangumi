package com.xiaoyv.bangumi.features.mikan.detail.business

import androidx.compose.ui.util.fastForEach
import androidx.datastore.preferences.core.stringPreferencesKey
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_copy_success
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.postEffect
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.core.utils.parseAsHtml
import com.xiaoyv.bangumi.shared.data.repository.CacheRepository
import com.xiaoyv.bangumi.shared.data.repository.MikanRepository
import com.xiaoyv.bangumi.shared.data.repository.readViewModelCache
import com.xiaoyv.bangumi.shared.data.repository.writeViewModelCache
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import org.jetbrains.compose.resources.getString
import org.orbitmvi.orbit.syntax.Syntax

/**
 * [MikanDetailViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class MikanDetailViewModel(
    private val args: Screen.MikanResources,
    private val mikanRepository: MikanRepository,
    private val cacheRepository: CacheRepository,
) : BaseViewModel<MikanDetailState, MikanDetailSideEffect, MikanDetailEvent.Action>() {

    private val cacheKey =
        stringPreferencesKey(name = "mikan_detail_" + args.mikanId + "_" + args.groupId)

    override fun initBaseState(): UiState<MikanDetailState> = readViewModelCache(
        cacheRepository = cacheRepository,
        cacheKey = cacheKey,
        loadWhenEmpty = true,
        transform = {
            it.copy(
                checkMode = false,
                checkItems = emptyList(),
                resources = it.resources.map { resource ->
                    resource.copy(titleHtml = resource.title.parseAsHtml())
                }
            )
        }
    )

    override fun createInitialState() = MikanDetailState(
        groupName = args.groupName
    )

    override suspend fun Syntax<UiState<MikanDetailState>, UiSideEffect<MikanDetailSideEffect>>.refreshSync() {
        mikanRepository.fetchMikanResources(args.mikanId, args.groupId, args.groupName)
            .onFailure {
                reduceError { it }
            }
            .onSuccess {
                reduceData { state.copy(resources = it) }
                writeViewModelCache(cacheRepository, cacheKey)
            }

    }

    override fun onEvent(event: MikanDetailEvent.Action) {
        when (event) {
            is MikanDetailEvent.Action.OnRefresh -> refresh(contentLoading = event.loading)
            is MikanDetailEvent.Action.OnToggleCheckMode -> onToggleCheckList()
            is MikanDetailEvent.Action.OnToggleItem -> onToggleItem(event.index)
            is MikanDetailEvent.Action.OnCopy -> onShareOrCopyItem(false)
            is MikanDetailEvent.Action.OnShare -> onShareOrCopyItem(true)
            is MikanDetailEvent.Action.OnDownload -> onDownloadItem()
            is MikanDetailEvent.Action.OnToggleSelectAll -> onToggleSelectAllItem()
        }
    }


    private fun onToggleItem(index: Int) = intent {
        reduceData {
            if (state.checkItems.contains(index)) {
                state.copy(checkItems = state.checkItems - index)
            } else {
                state.copy(checkItems = state.checkItems + index)
            }
        }
    }

    private fun onToggleCheckList() = intent {
        reduceData {
            state.copy(checkMode = !state.checkMode)
        }
    }

    private fun onDownloadItem() = intent {
        val checkItems = state.data.checkItems
        if (checkItems.isEmpty()) return@intent
        val items = checkItems.mapNotNull {
            state.data.resources.getOrNull(it)
        }

        postEffect { MikanDetailSideEffect.OnOpenUri(items.first().magnet.orEmpty()) }
    }

    private fun onShareOrCopyItem(share: Boolean) = intent {
        val checkItems = state.data.checkItems
        if (checkItems.isEmpty()) return@intent
        val items = checkItems.mapNotNull {
            state.data.resources.getOrNull(it)
        }

        val text = buildString {
            items.fastForEach {
                appendLine()
                append(it.magnet)
                appendLine()
            }
        }

        if (share) {
            postEffect { MikanDetailSideEffect.OnCopyText(text.trim()) }
            System.shareText(text.trim())
        } else {
            postEffect { MikanDetailSideEffect.OnCopyText(text.trim()) }
            postToast { getString(Res.string.global_copy_success) }
        }

        reduceData {
            state.copy(checkMode = false)
        }
    }

    private fun onToggleSelectAllItem() = intent {
        val items = state.data.resources
        val checkItems = state.data.checkItems
        if (checkItems.size == items.size) {
            reduceData { state.copy(checkItems = emptyList()) }
        } else {
            reduceData { state.copy(checkItems = items.mapIndexed { index, _ -> index }) }
        }
    }
}
