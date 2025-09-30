package com.xiaoyv.bangumi.features.index.page.dialog

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.index_add_related_success
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
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
    override fun initBaseState(): BaseState<IndexDialogState> = BaseState.Loading()

    override fun initSate(onCreate: Boolean) = IndexDialogState()

    override fun onEvent(event: IndexDialogEvent.Action) {
        when (event) {
            is IndexDialogEvent.Action.OnRefresh -> refresh(event.loading)
            is IndexDialogEvent.Action.OnSaveToCollection -> onSaveToCollection(event.indexId)
            IndexDialogEvent.Action.OnRefreshCollection -> onRefreshCollection()
        }
    }

    override suspend fun BaseSyntax<IndexDialogState, IndexDialogSideEffect>.refreshSync() {
        indexRepository.fetchUserCreatedIndex(userManager.userInfo.username)
            .onFailure { reduceError { it } }
            .onSuccess {
                reduceContent { state.copy(indexList = it.toPersistentList()) }
            }
    }

    private fun onRefreshCollection() = action {
        indexRepository.fetchUserCreatedIndex(userManager.userInfo.username)
            .onFailure { reduceError { it } }
            .onSuccess {
                reduceContent { state.copy(indexList = it.toPersistentList()) }
            }
    }

    private fun onSaveToCollection(indexId: Long) = action {
        withActionLoading { indexRepository.submitIndexAddRelated(indexId, target) }
            .onSuccess {
                postToast { getString(Res.string.index_add_related_success) }

                postEffect { IndexDialogSideEffect.OnSaveSuccess }
            }
    }
}