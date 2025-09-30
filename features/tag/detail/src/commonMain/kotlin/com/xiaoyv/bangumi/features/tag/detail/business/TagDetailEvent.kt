package com.xiaoyv.bangumi.features.tag.detail.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [TagDetailEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class TagDetailEvent {
    sealed class UI : TagDetailEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : TagDetailEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}