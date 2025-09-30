package com.xiaoyv.bangumi.features.mikan.studio.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [MikanStudioEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class MikanStudioEvent {
    sealed class UI : MikanStudioEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : MikanStudioEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}