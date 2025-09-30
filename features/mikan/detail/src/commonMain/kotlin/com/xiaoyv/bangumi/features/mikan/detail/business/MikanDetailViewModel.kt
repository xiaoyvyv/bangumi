package com.xiaoyv.bangumi.features.mikan.detail.business

import androidx.compose.ui.util.fastForEach
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_copy_success
import com.xiaoyv.bangumi.features.mikan.detail.MikanStudioArguments
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.repository.CacheRepository
import com.xiaoyv.bangumi.shared.data.repository.MikanRepository
import com.xiaoyv.bangumi.shared.data.repository.readViewModelCache
import com.xiaoyv.bangumi.shared.data.repository.writeViewModelCache
import org.jetbrains.compose.resources.getString

/**
 * [MikanDetailViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class MikanDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val mikanRepository: MikanRepository,
    private val cacheRepository: CacheRepository,
) : BaseViewModel<MikanDetailState, MikanDetailSideEffect, MikanDetailEvent.Action>(savedStateHandle) {

    private val args = MikanStudioArguments(savedStateHandle)
    private val cacheKey =
        stringPreferencesKey(name = "mikan_detail_" + args.mikanId + "_" + args.groupId)

    override fun initBaseState(): BaseState<MikanDetailState> = readViewModelCache(
        cacheRepository = cacheRepository,
        cacheKey = cacheKey,
        loadWhenEmpty = true,
        transform = { it.copy(checkMode = false, checkItems = emptyList()) }
    )

    override fun initSate(onCreate: Boolean) = MikanDetailState(
        groupName = args.groupName
    )

    override suspend fun BaseSyntax<MikanDetailState, MikanDetailSideEffect>.refreshSync() {
        mikanRepository.fetchMikanResources(args.mikanId, args.groupId, args.groupName)
            .onFailure {
                reduceError { it }
            }
            .onSuccess {
                reduceContent { state.copy(resources = it) }
                writeViewModelCache(cacheRepository, cacheKey)
            }

    }

    override fun onEvent(event: MikanDetailEvent.Action) {
        when (event) {
            is MikanDetailEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is MikanDetailEvent.Action.OnToggleCheckMode -> onToggleCheckList()
            is MikanDetailEvent.Action.OnToggleItem -> onToggleItem(event.index)
            is MikanDetailEvent.Action.OnCopy -> onShareOrCopyItem(false)
            is MikanDetailEvent.Action.OnShare -> onShareOrCopyItem(true)
            is MikanDetailEvent.Action.OnDownload -> onDownloadItem()
            is MikanDetailEvent.Action.OnToggleSelectAll -> onToggleSelectAllItem()
        }
    }


    private fun onToggleItem(index: Int) = action {
        reduceContent {
            if (state.checkItems.contains(index)) {
                state.copy(checkItems = state.checkItems - index)
            } else {
                state.copy(checkItems = state.checkItems + index)
            }
        }
    }

    private fun onToggleCheckList() = action {
        reduceContent {
            state.copy(checkMode = !state.checkMode)
        }
    }

    private fun onDownloadItem() = action {
        val checkItems = state.content.checkItems
        if (checkItems.isEmpty()) return@action
        val items = checkItems.mapNotNull {
            state.content.resources.getOrNull(it)
        }

        postEffect { MikanDetailSideEffect.OnOpenUri(items.first().magnet.orEmpty()) }
    }

    private fun onShareOrCopyItem(share: Boolean) = action {
        val checkItems = state.content.checkItems
        if (checkItems.isEmpty()) return@action
        val items = checkItems.mapNotNull {
            state.content.resources.getOrNull(it)
        }

        val text = buildString {
            items.fastForEach {
                appendLine()
                append(it.magnet.orEmpty())
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

        reduceContent {
            state.copy(checkMode = false)
        }
    }

    private fun onToggleSelectAllItem() = action {
        val items = state.content.resources
        val checkItems = state.content.checkItems
        if (checkItems.size == items.size) {
            reduceContent { state.copy(checkItems = emptyList()) }
        } else {
            reduceContent { state.copy(checkItems = items.mapIndexed { index, _ -> index }) }
        }
    }
}