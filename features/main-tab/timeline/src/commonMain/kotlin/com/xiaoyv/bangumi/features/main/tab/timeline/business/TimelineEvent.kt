package com.xiaoyv.bangumi.features.main.tab.timeline.business

import com.xiaoyv.bangumi.shared.core.types.TimelineTab
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [TimelineEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class TimelineEvent {
    sealed class UI : TimelineEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : TimelineEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data class OnChangeTimeline(@TimelineTab val mode: Int) : Action()
    }
}