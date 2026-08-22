package com.xiaoyv.bangumi.features.mono.page.business

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.manager.app.PersonalStateStore
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.ListMonoParam
import com.xiaoyv.bangumi.shared.data.repository.MonoRepository
import com.xiaoyv.bangumi.shared.data.repository.datasource.bindMonoDisplayPersonalState
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
    monoRepository: MonoRepository,
    personalStateStore: PersonalStateStore,
    val param: ListMonoParam,
) : BaseViewModel<MonoPageState, MonoPageSideEffect, MonoPageEvent.Action>() {

    private val monoPager = monoRepository.fetchMonoListPager(param)

    val monos = monoPager.cachedIn(viewModelScope)

    init {
        monoPager.bindMonoDisplayPersonalState(viewModelScope, personalStateStore)
    }

    override fun createInitialState() = MonoPageState(param = param)

    override fun onEvent(event: MonoPageEvent.Action) {

    }
}
