package com.xiaoyv.bangumi.features.topic.page.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [TopicPageEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class TopicPageEvent {
    sealed class UI : TopicPageEvent() {
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : TopicPageEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}