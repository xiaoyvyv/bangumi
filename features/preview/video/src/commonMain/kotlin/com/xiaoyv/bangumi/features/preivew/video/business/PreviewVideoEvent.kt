package com.xiaoyv.bangumi.features.preivew.video.business

import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [PreviewVideoEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class PreviewVideoEvent {
    sealed class UI : PreviewVideoEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : PreviewVideoEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
    }
}