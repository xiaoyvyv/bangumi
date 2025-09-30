package com.xiaoyv.bangumi.features.groups.page.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [GroupsPageEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class GroupsPageEvent {
    sealed class UI : GroupsPageEvent() {
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : GroupsPageEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}