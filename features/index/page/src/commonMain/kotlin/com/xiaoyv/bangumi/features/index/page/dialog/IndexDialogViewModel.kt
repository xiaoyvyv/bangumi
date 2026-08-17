package com.xiaoyv.bangumi.features.index.page.dialog

import com.xiaoyv.bangumi.shared.core.mvi.withActionLoading
import com.xiaoyv.bangumi.shared.core.mvi.postEffect
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.index_add_related_success
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.PageStatus
import org.orbitmvi.orbit.syntax.Syntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.model.request.IndexTarget
import com.xiaoyv.bangumi.shared.data.repository.IndexRepository
import kotlinx.collections.immutable.toPersistentList
import org.jetbrains.compose.resources.getString
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun koinIndexDialogViewModel(
    target: IndexTarget,
): IndexDialogViewModel {
    return koinViewModel(
        key = target.uniqueKey,
        parameters = { parametersOf(target) }
    )
}

/**
 * [IndexDialogViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class IndexDialogViewModel(
    savedStateHandle: SavedStateHandle,
    private val indexRepository: IndexRepository,
    private val target: IndexTarget,
    private val userManager: UserManager,
) : BaseViewModel<IndexDialogState, IndexDialogSideEffect, IndexDialogEvent.Action>(savedStateHandle) {
    override fun initBaseState(): UiState<IndexDialogState> = UiState(data = createInitialState(), status = PageStatus.Loading)

    override fun createInitialState() = IndexDialogState()

    override fun onEvent(event: IndexDialogEvent.Action) {
        when (event) {
            is IndexDialogEvent.Action.OnRefresh -> refresh(event.loading)
            is IndexDialogEvent.Action.OnSaveToCollection -> onSaveToCollection(event.indexId)
            IndexDialogEvent.Action.OnRefreshCollection -> onRefreshCollection()
        }
    }

    override suspend fun Syntax<UiState<IndexDialogState>, UiSideEffect<IndexDialogSideEffect>>.refreshSync() {
        indexRepository.fetchUserCreatedIndex(userManager.userInfo.username)
            .onFailure { reduceError { it } }
            .onSuccess {
                reduceData { state.copy(indexList = it.toPersistentList()) }
            }
    }

    private fun onRefreshCollection() = intent {
        indexRepository.fetchUserCreatedIndex(userManager.userInfo.username)
            .onFailure { reduceError { it } }
            .onSuccess {
                reduceData { state.copy(indexList = it.toPersistentList()) }
            }
    }

    private fun onSaveToCollection(indexId: Long) = intent {
        withActionLoading { indexRepository.submitIndexAddRelated(indexId, target) }
            .onSuccess {
                postToast { getString(Res.string.index_add_related_success) }

                postEffect { IndexDialogSideEffect.OnSaveSuccess }
            }
    }
}