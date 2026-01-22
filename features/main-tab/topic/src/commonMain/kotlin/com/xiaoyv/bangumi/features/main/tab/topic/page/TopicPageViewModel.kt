package com.xiaoyv.bangumi.features.main.tab.topic.page

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xiaoyv.bangumi.features.main.tab.topic.business.TopicEvent
import com.xiaoyv.bangumi.features.main.tab.topic.business.TopicSideEffect
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
    savedStateHandle: SavedStateHandle,
    ugcRepository: UgcRepository,
    @field:RakuenTab private val type: String,
) : BaseViewModel<TopicPageState, TopicSideEffect, TopicEvent>(savedStateHandle) {
    private val topicPager = ugcRepository.fetchTopicPager(type = type)

    internal val topicFlow = topicPager.flow.cachedIn(viewModelScope)

    override fun initSate(onCreate: Boolean) = TopicPageState(
        type = type,
    )

    override fun onEvent(event: TopicEvent) {

    }
}