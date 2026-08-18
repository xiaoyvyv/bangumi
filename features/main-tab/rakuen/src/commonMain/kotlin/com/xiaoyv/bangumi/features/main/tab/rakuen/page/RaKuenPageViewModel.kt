package com.xiaoyv.bangumi.features.main.tab.rakuen.page

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xiaoyv.bangumi.features.main.tab.rakuen.business.RaKuenEvent
import com.xiaoyv.bangumi.features.main.tab.rakuen.business.RaKuenSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.types.RakuenType
import com.xiaoyv.bangumi.shared.data.repository.UgcRepository
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun koinRaKuenPageViewModel(
    @RakuenType type: String,
): RaKuenPageViewModel {
    return koinViewModel<RaKuenPageViewModel>(
        key = type,
        parameters = { parametersOf(type) }
    )
}

class RaKuenPageViewModel(
    ugcRepository: UgcRepository,
    @field:RakuenType private val type: String,
) : BaseViewModel<RaKuenPageState, RaKuenSideEffect, RaKuenEvent>() {
    private val topicPager = ugcRepository.fetchRaKuenPager(type = type)

    internal val topicFlow = topicPager.flow.cachedIn(viewModelScope)

    override fun createInitialState() = RaKuenPageState(
        type = type,
    )

    override fun onEvent(event: RaKuenEvent) {

    }
}