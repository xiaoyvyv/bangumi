package com.xiaoyv.bangumi.features.main.tab.topic.business

import com.xiaoyv.bangumi.shared.core.types.RakuenTab
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [TopicEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class TopicEvent {
    sealed class UI : TopicEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : TopicEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data class OnChangeType(@field:RakuenTab val type: String) : Action()
    }
}