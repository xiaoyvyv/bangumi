package com.xiaoyv.bangumi.features.topic.page.business

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.topic.ListTopicParam
import com.xiaoyv.bangumi.shared.data.repository.TopicRepository
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun rememberTopicPageViewModel(param: ListTopicParam): TopicPageViewModel {
    return koinViewModel<TopicPageViewModel>(
        key = param.uniqueKey,
        parameters = { parametersOf(param) }
    )
}

/**
 * [TopicPageViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class TopicPageViewModel(
    savedStateHandle: SavedStateHandle,
    private val param: ListTopicParam,
    private val topicRepository: TopicRepository,
) : BaseViewModel<TopicPageState, TopicPageSideEffect, TopicPageEvent.Action>(savedStateHandle) {
    private val topicPager = topicRepository.fetchTopicPager(param)
    val topic = topicPager.flow.cachedIn(viewModelScope)

    override fun initSate(onCreate: Boolean) = TopicPageState(param)

    override fun onEvent(event: TopicPageEvent.Action) {

    }
}