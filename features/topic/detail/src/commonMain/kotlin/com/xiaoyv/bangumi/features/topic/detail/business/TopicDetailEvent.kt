package com.xiaoyv.bangumi.features.topic.detail.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [TopicDetailEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class TopicDetailEvent {
    sealed class UI : TopicDetailEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : TopicDetailEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}