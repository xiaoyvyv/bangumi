package com.xiaoyv.bangumi.features.mikan.detail.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [MikanDetailEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class MikanDetailEvent {
    sealed class UI : MikanDetailEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : MikanDetailEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data object OnToggleSelectAll : Action()
        data object OnToggleCheckMode : Action()
        data object OnShare : Action()
        data object OnCopy : Action()
        data object OnDownload : Action()
        data class OnToggleItem(val index: Int) : Action()
    }
}