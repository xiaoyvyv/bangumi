package com.xiaoyv.bangumi.features.index.detail.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.collect_cancel_success
import com.xiaoyv.bangumi.core_resource.resources.collect_success
import com.xiaoyv.bangumi.features.index.detail.IndexDetailArguments
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.utils.awaitAll
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.repository.IndexRepository
import org.jetbrains.compose.resources.getString

/**
 * [IndexDetailViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class IndexDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val indexRepository: IndexRepository,
    private val userManager: UserManager,
) : BaseViewModel<IndexDetailState, IndexDetailSideEffect, IndexDetailEvent.Action>(savedStateHandle) {
    private val args = IndexDetailArguments(savedStateHandle)

    override fun initBaseState(): BaseState<IndexDetailState> = BaseState.Loading()

    override fun initSate(onCreate: Boolean) = IndexDetailState()

    override fun onEvent(event: IndexDetailEvent.Action) {
        when (event) {
            is IndexDetailEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is IndexDetailEvent.Action.OnToggleBookmarkIndex -> onToggleBookmarkIndex()
        }
    }

    override suspend fun BaseSyntax<IndexDetailState, IndexDetailSideEffect>.refreshSync() {
        awaitAll(
            block1 = { indexRepository.fetchIndexDetail(args.id) },
            block2 = { if (userManager.isLogin) indexRepository.fetchIndexIsBookmarked(args.id) else Result.success(false) },
        ).onFailure {
            reduceError { it }
        }.onSuccess {
            reduceContent(forceRefresh = true) { state.copy(index = it.data1.copy(isBookmarked = it.data2)) }
        }
    }

    private fun onToggleBookmarkIndex() = action {
        val isBookmarked = stateRaw.index.isBookmarked
        val toast = if (isBookmarked) getString(Res.string.collect_cancel_success) else getString(Res.string.collect_success)

        withActionLoading { indexRepository.submitBookmarkOrCancelIndex(args.id, !isBookmarked) }
            .onFailure { postToast { it.errMsg } }
            .onSuccess {
                reduceContent { state.copy(index = state.index.copy(isBookmarked = it)) }

                postToast { toast }
            }
    }
}