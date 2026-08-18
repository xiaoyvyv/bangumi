package com.xiaoyv.bangumi.features.main.tab.rakuen.page

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xiaoyv.bangumi.features.main.tab.rakuen.business.RaKuenEvent
import com.xiaoyv.bangumi.features.main.tab.rakuen.business.RaKuenSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.types.RakuenTab
import com.xiaoyv.bangumi.shared.data.repository.UgcRepository
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun koinTopicPageViewModel(
    @RakuenTab type: String,
): TopicPageViewModel {
    return koinViewModel<TopicPageViewModel>(
        key = type,
        parameters = { parametersOf(type) }
    )
}

class TopicPageViewModel(
    ugcRepository: UgcRepository,
    @field:RakuenTab private val type: String,
) : BaseViewModel<RaKuenPageState, RaKuenSideEffect, RaKuenEvent>() {
    private val topicPager = ugcRepository.fetchTopicPager(type = type)

    internal val topicFlow = topicPager.flow.cachedIn(viewModelScope)

    override fun createInitialState() = RaKuenPageState(
        type = type,
    )

    override fun onEvent(event: RaKuenEvent) {

    }
}