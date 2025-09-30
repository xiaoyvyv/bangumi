package com.xiaoyv.bangumi.features.mono.page.business

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.ListMonoParam
import com.xiaoyv.bangumi.shared.data.repository.MonoRepository
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf


@Composable
fun koinMonoPageViewModel(param: ListMonoParam): MonoPageViewModel {
    return koinViewModel<MonoPageViewModel>(
        key = param.uniqueKey,
        parameters = { parametersOf(param) }
    )
}

/**
 * [MonoPageViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class MonoPageViewModel(
    savedStateHandle: SavedStateHandle,
    monoRepository: MonoRepository,
    val param: ListMonoParam,
) : BaseViewModel<MonoPageState, MonoPageSideEffect, MonoPageEvent.Action>(savedStateHandle) {

    private val monoPager = monoRepository.fetchMonoListPager(param)

    val monos = monoPager.flow.cachedIn(viewModelScope)

    override fun initSate(onCreate: Boolean) = MonoPageState(param = param)

    override fun onEvent(event: MonoPageEvent.Action) {

    }
}