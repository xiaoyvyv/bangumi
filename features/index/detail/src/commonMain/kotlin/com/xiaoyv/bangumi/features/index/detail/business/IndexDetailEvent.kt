package com.xiaoyv.bangumi.features.index.detail.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [IndexDetailEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class IndexDetailEvent {
    sealed class UI : IndexDetailEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : IndexDetailEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data object OnToggleBookmarkIndex : Action()
    }
}