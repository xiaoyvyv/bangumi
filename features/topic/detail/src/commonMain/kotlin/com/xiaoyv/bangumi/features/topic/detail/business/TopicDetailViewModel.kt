package com.xiaoyv.bangumi.features.topic.detail.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel

/**
 * [TopicDetailViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class TopicDetailViewModel(savedStateHandle: SavedStateHandle) :
    BaseViewModel<TopicDetailState, TopicDetailSideEffect, TopicDetailEvent.Action>(savedStateHandle) {

    override fun initSate(onCreate: Boolean) = TopicDetailState()

    override fun onEvent(event: TopicDetailEvent.Action) {

    }

}