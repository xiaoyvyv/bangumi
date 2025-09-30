package com.xiaoyv.bangumi.features.groups.detail.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [GroupsDetailEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class GroupsDetailEvent {
    sealed class UI : GroupsDetailEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : GroupsDetailEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data object OnToggleJoinGroup : Action()
    }
}